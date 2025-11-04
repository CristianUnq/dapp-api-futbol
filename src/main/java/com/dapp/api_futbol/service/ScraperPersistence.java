package com.dapp.api_futbol.service;

import com.dapp.api_futbol.model.Player;
import com.dapp.api_futbol.model.Team;
import com.dapp.api_futbol.repository.PlayerRepository;
import com.dapp.api_futbol.repository.TeamRepository;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.nio.charset.StandardCharsets;
import java.io.File;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

@Service
public class ScraperPersistence {

    private static final String BASE_URL = "https://es.whoscored.com";
    private static final String CHAMPIONS_LEAGUE_TEAMS_URL = "https://es.whoscored.com/regions/250/tournaments/12/europa-champions-league";
    private static final String SEARCH_URL_TEMPLATE = BASE_URL + "/Search/?t=";

    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;

    public ScraperPersistence(TeamRepository teamRepository, PlayerRepository playerRepository) {
        this.teamRepository = teamRepository;
        this.playerRepository = playerRepository;
    }

    // Use a fixedRate for testing so the first run happens shortly after startup
    // and next runs are scheduled every 60_000ms. When moving to production you
    // can switch back to a cron expression like "0 0/1 * * * ?" or a daily cron.
    // Ejecuta cada 90 segundos (1.5 minutos). fixedRate mide en milisegundos.
    @Scheduled(fixedRate = 90000, initialDelay = 5000)
    @Transactional
    public void scrapeAndSaveChampionsLeagueTeamsAndPlayers() {
        System.out.println("Iniciando scraping de equipos y jugadores de la Champions League...");
        WebDriver driver = setupWebDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        try {
            driver.get(CHAMPIONS_LEAGUE_TEAMS_URL);
            acceptCookies(driver, wait);

            // Wait for the tournament header (more stable) instead of the dynamic container id
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("h2.tournament-tables-header")));

            Document page = Jsoup.parse(driver.getPageSource());
            // Try multiple selectors (fallbacks) to find team links
            Elements teamLinks = page.select("#tournament-tables div[id^='standings-'] a.team-link, #standings-1-content a.team-link, h2.tournament-tables-header ~ div a.team-link, a.team-link");

            if (teamLinks.isEmpty()) {
                System.err.println("No se encontraron enlaces de equipos en la página de clasificaciones.");
                return;
            }

            for (Element teamLink : teamLinks) {
                String teamName = teamLink.text();
                String teamUrl = teamLink.attr("href");

                if (teamName == null || teamName.trim().isEmpty()) continue;

                Optional<Team> existingTeam = teamRepository.findByName(teamName);
                Team team = existingTeam.orElseGet(Team::new);
                team.setName(teamName);
                teamRepository.save(team);

                // Use search-based navigation (more stable) to get the canonical team page when href is relative or missing
                String finalTeamUrl = (teamUrl == null || teamUrl.isEmpty()) ? findTeamUrl(driver, wait, teamName) : teamUrl;
                if (finalTeamUrl == null) {
                    System.err.println("No se pudo determinar la URL del equipo: " + teamName);
                    continue;
                }

                scrapeAndSavePlayersForTeam(driver, wait, team, BASE_URL + finalTeamUrl);
            }
        } catch (Exception e) {
            handleScraperError(driver, e);
        } finally {
            if (driver != null) driver.quit();
        }
        System.out.println("Scraping de equipos y jugadores completado.");
    }

    private void scrapeAndSavePlayersForTeam(WebDriver driver, WebDriverWait wait, Team team, String teamUrl) {
        try {
            driver.get(teamUrl);
            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("top-player-stats-summary-grid")));
            Document teamPage = Jsoup.parse(driver.getPageSource());

            // Build header->index map so we can extract columns by name (robust to column order changes)
            Elements headerEls = teamPage.select("#top-player-stats-summary-grid thead th");
            List<String> headers = new ArrayList<>();
            for (Element h : headerEls) headers.add(h.text().toLowerCase().trim());

            Elements rows = teamPage.select("#top-player-stats-summary-grid tbody tr, #top-player-stats-summary-grid tr");
            for (Element row : rows) {
                Elements cells = row.select("td");
                if (cells.isEmpty()) continue;

                // helper to find cell by header variants
                java.util.function.Function<String[], String> cellByHeaders = (variants) -> {
                    for (int i = 0; i < headers.size(); i++) {
                        String h = headers.get(i);
                        for (String v : variants) {
                            if (h.contains(v)) {
                                if (i < cells.size()) return cells.get(i).text();
                            }
                        }
                    }
                    // fallback: try some common fixed indices
                    for (String v : variants) {
                        if (v.equals("name") && cells.size() > 2) return cells.get(2).text();
                        if (v.equals("age") && cells.size() > 3) return cells.get(3).text();
                        if (v.equals("position") && cells.size() > 4) return cells.get(4).text();
                    }
                    return "";
                };

                String playerName = cellByHeaders.apply(new String[]{"name", "jugador", "player", "nombre"}).trim();
                if (playerName.isEmpty()) continue;

                Optional<Player> existingPlayer = playerRepository.findByNameAndTeam(playerName, team);
                if (existingPlayer.isPresent()) continue; // don't overwrite for now

                Player player = new Player();
                player.setName(playerName);

                String ageStr = cellByHeaders.apply(new String[]{"age", "edad"}).trim();
                try {
                    player.setAge(Integer.parseInt(ageStr));
                } catch (NumberFormatException ex) {
                    player.setAge(0);
                }

                player.setPosition(cellByHeaders.apply(new String[]{"position", "posicion", "pos", "posiciones"}));

                // Additional stats
                player.setHeight(cellByHeaders.apply(new String[]{"CM", "altura", "height"}));
                player.setWeight(cellByHeaders.apply(new String[]{"KG", "KiloGramos", "weight", "peso"}));
                player.setAppearances(cellByHeaders.apply(new String[]{"apps", "appearances", "partidos", "pj", "AP%"}));
                player.setMinsPlayed(cellByHeaders.apply(new String[]{"mins", "minutes", "minutos", "min"}));
                player.setGoals(cellByHeaders.apply(new String[]{"goals", "goles", "gls"}));
                player.setAssists(cellByHeaders.apply(new String[]{"asist", "asistencias", "ast"}));
                player.setYellowCards(cellByHeaders.apply(new String[]{"yellow", "amarilla", "yellow cards", "amarillas", "amar"}));
                player.setRedCards(cellByHeaders.apply(new String[]{"red", "roja", "red cards", "rojas"}));
                player.setShotsPerGame(cellByHeaders.apply(new String[]{"shots", "disparos", "shots per game", "TpP"}));
                player.setPassSuccess(cellByHeaders.apply(new String[]{"pass", "pases", "%"}));
                player.setAerialsWon(cellByHeaders.apply(new String[]{"aerial", "aéreos", "aerials"}));
                player.setManOfTheMatch(cellByHeaders.apply(new String[]{"motm", "mom", "man of the match", "jugador del partido", "JdelP"}));
                player.setRating(cellByHeaders.apply(new String[]{"rating", "media", "avg"}));

                // Some additional metadata
                // try to get url if available
                Element link = row.selectFirst("a[href*='/Player/']");
                if (link != null) player.setUrl(link.attr("href"));

                player.setTeam(team);
                playerRepository.save(player);
            }
        } catch (Exception e) {
            System.err.println("Error al scrapear jugadores para el equipo " + team.getName() + ": " + e.getMessage());
        }
    }


    private String findTeamUrl(WebDriver driver, WebDriverWait wait, String teamName) {
        try {
            String encodedTeamName = URLEncoder.encode(teamName, StandardCharsets.UTF_8);
            driver.get(SEARCH_URL_TEMPLATE + encodedTeamName);
            // small wait for search results
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("#search-result")));
            Document searchPage = Jsoup.parse(driver.getPageSource());
            Elements teamLinks = searchPage.select("a[href^='/Teams/']");
            if (!teamLinks.isEmpty()) {
                return teamLinks.first().attr("href");
            }
        } catch (Exception e) {
            System.err.println("Error al buscar la URL del equipo " + teamName + ": " + e.getMessage());
        }
        return null;
    }

    private WebDriver setupWebDriver() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36");
        return new ChromeDriver(options);
    }

    private void acceptCookies(WebDriver driver, WebDriverWait wait) {
        try {
            By consentButtonBy = By.cssSelector(".qc-cmp2-summary-buttons button[mode='primary']");
            WebElement consentButton = wait.until(ExpectedConditions.elementToBeClickable(consentButtonBy));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", consentButton);
            wait.until(ExpectedConditions.invisibilityOf(consentButton));
        } catch (TimeoutException e) {
            System.out.println("No se encontró el banner de cookies o ya fue aceptado.");
        }
    }

    private void handleScraperError(WebDriver driver, Exception e) {
        System.err.println("Ocurrió un error durante el scraping: " + e.getMessage());
        e.printStackTrace();
        if (driver != null) {
            takeScreenshot(driver);
        }
    }

    private void takeScreenshot(WebDriver driver) {
        try {
            TakesScreenshot ts = (TakesScreenshot) driver;
            File source = ts.getScreenshotAs(OutputType.FILE);
            Path destination = Paths.get("failure-screenshot.png");
            Files.copy(source.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Screenshot guardado en: " + destination.toAbsolutePath());

            Path htmlPath = Paths.get("failure-page.html");
            Files.write(htmlPath, driver.getPageSource().getBytes(StandardCharsets.UTF_8));
            System.out.println("HTML de la página guardado en: " + htmlPath.toAbsolutePath());
        } catch (IOException ex) {
            System.err.println("No se pudo guardar el screenshot o el HTML: " + ex.getMessage());
        }
    }
}