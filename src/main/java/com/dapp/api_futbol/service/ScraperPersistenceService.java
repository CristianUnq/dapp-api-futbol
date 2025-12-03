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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.nio.charset.StandardCharsets;
import java.io.File;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
// Note: avoid aliasing imports; reference java.util.concurrent.TimeoutException fully if needed

@Service
public class ScraperPersistenceService {

    private static final Logger logger = LoggerFactory.getLogger(ScraperPersistenceService.class);
    private static final ExecutorService regexExecutor = Executors.newSingleThreadExecutor();

    @Value("${who.scored.base_url}")
    private String BASE_URL;
    @Value("${who.scored.champions_league_teams_url}")
    private String CHAMPIONS_LEAGUE_TEAMS_URL;
    
    private final String SEARCH_URL_TEMPLATE = BASE_URL + "/Search/?t=";

    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;

    public ScraperPersistenceService(TeamRepository teamRepository, PlayerRepository playerRepository) {
        this.teamRepository = teamRepository;
        this.playerRepository = playerRepository;
    }

    @Scheduled(initialDelay = 0, fixedRate = 24 * 60 * 60 * 1000)
    @Transactional
    public void scrapeAndSaveChampionsLeagueTeamsAndPlayers() {
        System.out.println("Iniciando scraping de equipos y jugadores de la Champions League...");
        WebDriver driver = setupWebDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        Integer limitTeams = 5;
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
                limitTeams--;
                if (limitTeams < 0) break;
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
        
        // Inicia el scraping de estadisticas de equipos despues de tener los equipos base.
        scrapeAndSaveTeamStats();
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

                // helper to find cell by header variants (used for KG and other numeric stats)
                    java.util.function.Function<String[], String> cellByHeaders = (variants) -> {
                        for (int i = 0; i < headers.size(); i++) {
                            String h = headers.get(i);
                            for (String v : variants) {
                                if (h.contains(v)) {
                                    if (i < cells.size()) return cells.get(i).text();
                                }
                            }
                        }
                        return "";
                    };

                    // Find the link and the cell that contains the player's name
                    Element playerLinkElement = row.selectFirst("a[href*='/Players/'], a[href*='/players/']");
                    if (playerLinkElement == null) continue;

                    int nameCellIndex = -1;
                    for (int i = 0; i < cells.size(); i++) {
                        if (!cells.get(i).select("a[href*='/Players/'], a[href*='/players/']").isEmpty()) {
                            nameCellIndex = i;
                            break;
                        }
                    }
                    Element nameCell = nameCellIndex >= 0 ? cells.get(nameCellIndex) : cells.first();

                    // Clean and normalize the player name from the anchor text
                    String rawName = safeReplace(playerLinkElement.text(), "^\\d+\\s*", "").trim();
                    // SonarCloud: Mitigating ReDoS by using a safe replacement helper with timeout.
                    // The regex `\\s*-\\s*.*$` is simplified by finding the first ` - ` and taking the substring.
                    int dashIndex = rawName.indexOf(" - ");
                    if (dashIndex != -1) {
                        rawName = rawName.substring(0, dashIndex);
                    }
                    // The regex `\\s*\\(.*\\)\\s*$` is potentially vulnerable. We use a safer, timeout-based approach.
                    rawName = safeReplace(rawName, "\\s{0,10}\\(.*\\)\\s{0,10}$", "").trim();
                    if (rawName.isEmpty()) continue;

                    String playerName = rawName;
                    Optional<Player> existingPlayerOpt = playerRepository.findByNameAndTeam(playerName, team);
                    Player player = existingPlayerOpt.orElseGet(Player::new);
                    // If new player, set name and team now; if existing, we'll update fields
                    if (existingPlayerOpt.isEmpty()) {
                        player.setName(playerName);
                        player.setTeam(team);
                    }

                    // --- Extract age and positions deterministically from the same name cell ---
                    String nameCellText = nameCell.text();
                    int extractedAge = 0;
                    String extractedPositions = null;

                    // Prefer the format where the details follow the link, e.g. "32, MP(C),DL"
                    // Remove the first occurrence of the anchor text without using regex
                    String afterLink;
                    String anchorText = playerLinkElement.text();
                    int idx = nameCellText.indexOf(anchorText);
                    if (idx != -1) {
                        afterLink = nameCellText.substring(0, idx) + nameCellText.substring(idx + anchorText.length());
                    } else {
                        afterLink = nameCellText;
                    }
                    afterLink = afterLink.trim();
                    // Remove leading commas, NBSPs and whitespace without regex
                    int start = 0;
                    while (start < afterLink.length()) {
                        char c = afterLink.charAt(start);
                        if (c == ',' || c == '\u00A0' || Character.isWhitespace(c)) start++; else break;
                    }
                    if (start > 0) afterLink = afterLink.substring(start).trim();
                    if (!afterLink.isEmpty()) {
                        // split by first comma: first token often is the age
                        String[] parts = afterLink.split("\\,\\s*", 2);
                        // If split failed due to escaping, fallback to simple comma split
                        if (parts.length == 1 && afterLink.contains(",")) parts = afterLink.split(",", 2);
                        String firstToken = parts.length > 0 ? parts[0].trim() : "";
                        // Try to parse a pure numeric first token as age
                        Matcher ageOnly = Pattern.compile("^(\\d{1,2})$").matcher(firstToken);
                        if (ageOnly.find()) {
                            try { extractedAge = Integer.parseInt(ageOnly.group(1)); } catch (NumberFormatException ex) { extractedAge = 0; }
                        } else {
                            // If first token contains digits at start (e.g. '32 '), extract leading digits
                            Matcher leadingDigits = Pattern.compile("^(\\d{1,2})\\b").matcher(firstToken);
                            if (leadingDigits.find()) {
                                try { extractedAge = Integer.parseInt(leadingDigits.group(1)); } catch (NumberFormatException ex) { extractedAge = 0; }
                            }
                        }

                        // Determine positions: if age was parsed and there's a remainder, that's positions
                        if (extractedAge > 0) {
                            if (parts.length > 1) extractedPositions = parts[1].trim();
                        } else {
                            // No numeric age in first token -> treat the whole afterLink as positions
                            extractedPositions = afterLink;
                        }
                    }

                    // Fallback heuristics if deterministic parsing didn't yield an age
                    if (extractedAge == 0) {
                        Pattern[] agePatterns = new Pattern[] {
                            Pattern.compile("\\b(\\d{1,2})\\s*[\\u00A0\\s]*a\\u00F1os?\\b", Pattern.CASE_INSENSITIVE),
                            Pattern.compile("\\b(\\d{1,2})\\s*(?:years?|yrs?)\\b", Pattern.CASE_INSENSITIVE),
                            Pattern.compile("\\b(\\d{1,2})\\b")
                        };
                        for (Pattern p : agePatterns) {
                            Matcher m = p.matcher(nameCellText);
                            if (m.find()) {
                                try { extractedAge = Integer.parseInt(m.group(1)); } catch (NumberFormatException ex) { extractedAge = 0; }
                                if (extractedAge > 0 && extractedAge < 60) break;
                            }
                        }
                    }

                    // Clean and set parsed values
                    player.setAge(extractedAge);
                    if (extractedAge == 0) System.out.println("[Scraper Verificación] edad no encontrada para: " + playerName + " -- nameCell='" + nameCellText + "'");

                    if (extractedPositions != null && !extractedPositions.isEmpty()) {
                        String cleaned = safeReplace(extractedPositions, "\\b(\\d{1,2})\\s*[\\u00A0\\s]*a\\u00F1os?\\b", "").trim();
                        cleaned = safeReplace(cleaned, "\\(\\d{1,2}-\\d{1,2}-\\d{2,4}\\)", "").trim();
                        cleaned = safeReplace(cleaned, "^[\\-:\\s]+|[\\-:\\s]+$", "").trim();
                        if (!cleaned.isEmpty()) player.setPosition(cleaned);
                    }

                    // Verification log for troubleshooting: name, age, position and raw cell
                    System.out.println("[Scraper Verificación] Nombre: '" + playerName + "', Edad: " + extractedAge + ", Posición: '" + (player.getPosition() != null ? player.getPosition() : "") + "' -- raw='" + nameCellText + "'");

                // Additional stats
                player.setHeight(cellByHeaders.apply(new String[]{"cm", "altura", "height"}));
                player.setWeight(cellByHeaders.apply(new String[]{"kg", "kilos", "peso", "weight"}));
                player.setAppearances(cellByHeaders.apply(new String[]{"apps", "appearances", "partidos", "pj"}));
                player.setMinsPlayed(cellByHeaders.apply(new String[]{"mins", "minutes", "minutos", "min"}));
                player.setGoals(cellByHeaders.apply(new String[]{"goals", "goles", "gls"}));
                player.setAssists(cellByHeaders.apply(new String[]{"asist", "asistencias", "ast"}));
                player.setYellowCards(cellByHeaders.apply(new String[]{"yellow", "amarilla", "amarillas", "amar"}));
                player.setRedCards(cellByHeaders.apply(new String[]{"red", "roja", "rojas"}));
                player.setShotsPerGame(cellByHeaders.apply(new String[]{"shots", "disparos", "shots per game", "tpp"}));
                player.setPassSuccess(cellByHeaders.apply(new String[]{"pass", "pases", "%", "ap%"}));
                player.setAerialsWon(cellByHeaders.apply(new String[]{"aerial", "aéreos", "aerials"}));
                player.setManOfTheMatch(cellByHeaders.apply(new String[]{"motm", "mom", "man of the match", "jugador del partido", "jdelp"}));
                player.setRating(cellByHeaders.apply(new String[]{"rating", "media", "avg"}));

                // Some additional metadata: set canonical player URL if available
                if (playerLinkElement != null) {
                    String href = playerLinkElement.attr("href");
                    if (href != null && !href.isEmpty()) {
                        if (href.startsWith("http")) player.setUrl(href);
                        else player.setUrl(BASE_URL + href);
                    }
                }

                player.setTeam(team);
                playerRepository.save(player);
            }
        } catch (Exception e) {
            System.err.println("Error al scrapear jugadores para el equipo " + team.getName() + ": " + e.getMessage());
        }
    }


    public void scrapeAndSaveTeamStats() {
        WebDriver driver = setupWebDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        try {
            System.out.println("[Scraper] Iniciando scraping de estadísticas de equipos...");
            scrapeStandings(driver, wait);
            scrapeTeamPerformanceStats(driver, wait);
            System.out.println("[Scraper] Finalizado scraping de estadísticas de equipos.");
        } catch (Exception e) {
            System.err.println("Error al scrapear estadísticas de equipos: " + e.getMessage());
            handleScraperError(driver, e);
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }

    private void scrapeStandings(WebDriver driver, WebDriverWait wait) {
        String url = "https://es.whoscored.com/regions/250/tournaments/12/seasons/10903/stages/24796/show/europa-champions-league-2025-2026";
        System.out.println("[Scraper] Obteniendo datos de clasificación desde: " + url);
        driver.get(url);
        acceptCookies(driver, wait);
        // Esperamos por un elemento estable; usar presenceOf para evitar fallos por visibilidad/overlays.
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("h2.tournament-tables-header")));
        } catch (TimeoutException te) {
            System.err.println("[Scraper] Timeout esperando el header de las clasificaciones, intentando continuacion de forma tolerante...");
        }

        // Intentamos parsear la página y buscar la tabla con múltiples selectores de fallback.
        Document doc = Jsoup.parse(driver.getPageSource());
        Element table = null;
        String matchedSelector = null;

        String[] selectors = new String[] {
            "div[id^='standings-'] table",
            "table[id^='standings-']",
            "#standings-24796 table",
            "table.grid.with-centered-columns",
            "#tournament-tables div table",
            "table"
        };

        // Hacemos un pequeño bucle de reintentos porque la página puede tardar en renderizar partes vía JS.
        for (int attempt = 0; attempt < 3 && table == null; attempt++) {
            for (String sel : selectors) {
                table = doc.selectFirst(sel);
                if (table != null) {
                    matchedSelector = sel;
                    break;
                }
            }
            if (table == null) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {}
                doc = Jsoup.parse(driver.getPageSource());
            }
        }

        if (table == null) {
            System.err.println("[Scraper] No se pudo encontrar el elemento <table> dentro de la sección de clasificaciones. Revisar el HTML guardado.");
            return;
        } else {
            System.out.println("[Scraper] Tabla de clasificaciones encontrada usando selector: " + matchedSelector);
        }

        for (Element row : table.select("tbody tr")) {
            Elements cells = row.select("td");
            // Si una fila no tiene suficientes celdas, la saltamos.
            if (cells.size() < 9) continue; 

            // El nombre del equipo está en la celda 0 dentro de un enlace
            String teamName = cells.first().select("a.team-link").text().trim();
            if (teamName.isEmpty()) continue;

            Team team = teamRepository.findByName(teamName).orElse(new Team());
            team.setName(teamName);

            // Accedemos a los datos por el índice de la columna, que es más robusto.
            // J=1, G=2, E=3, P=4, GF=5, GC=6, DG=7, Pts=8 (índices basados en 0)
            team.setMatchsPlayed(parseInt(cells.get(1).text()));
            team.setMatchsWon(parseInt(cells.get(2).text()));
            team.setMatchsDrew(parseInt(cells.get(3).text()));
            team.setMatchsLost(parseInt(cells.get(4).text()));
            team.setGoalsInFavor(parseInt(cells.get(5).text()));
            team.setGoalsAgainst(parseInt(cells.get(6).text()));
            team.setGoalsDifference(parseInt(cells.get(7).text()));
            team.setPoints(parseInt(cells.get(8).text()));

            teamRepository.save(team);
        }
        System.out.println("[Scraper] Datos de clasificación procesados.");
    }

    private void scrapeTeamPerformanceStats(WebDriver driver, WebDriverWait wait) {
        String url = "https://es.whoscored.com/regions/250/tournaments/12/seasons/10903/stages/24796/teamstatistics/europa-champions-league-2025-2026";
        System.out.println("[Scraper] Obteniendo estadísticas de rendimiento desde: " + url);
        driver.get(url);
        acceptCookies(driver, wait);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("top-team-stats-summary-grid")));

        Document doc = Jsoup.parse(driver.getPageSource());
        Element table = doc.selectFirst("#top-team-stats-summary-grid");
        if (table == null) {
            System.err.println("[Scraper] No se pudo encontrar la tabla de estadísticas de equipos.");
            return;
        }

        Map<String, Integer> headerMap = getHeaderMap(table.select("thead th"));
        for (Element row : table.select("tbody tr")) {
            String teamName = safeReplace(row.select("a.team-link").text(), "^\\d+\\.\\s*", "").trim();
            if (teamName.isEmpty()) continue;

            teamRepository.findByName(teamName).ifPresent(team -> {
                team.setShotsPerMatch(getCellText(row, headerMap, "tiros pp"));
                team.setPossesion(getCellText(row, headerMap, "posesion%"));
                team.setPassAccuracy(getCellText(row, headerMap, "aciertopase%"));
                team.setAerialDuels(getCellText(row, headerMap, "aéreos"));
                team.setRating(getCellText(row, headerMap, "rating"));
                teamRepository.save(team);
            });
        }
        System.out.println("[Scraper] Estadísticas de rendimiento procesadas.");
    }

    private Map<String, Integer> getHeaderMap(Elements headers) {
        Map<String, Integer> headerMap = new HashMap<>();
        for (int i = 0; i < headers.size(); i++) {
            headerMap.put(headers.get(i).text().toLowerCase().trim(), i);
        }
        return headerMap;
    }

    private String getCellText(Element row, Map<String, Integer> headerMap, String headerName) {
        Integer index = headerMap.get(headerName);
        if (index != null && row.select("td").size() > index) {
            return row.select("td").get(index).text();
        }
        return null;
    }

    private Integer parseInt(String value) {
        if (value == null || value.trim().isEmpty() || value.equals("-")) return 0;
        try {
            String cleaned = digitsAndHyphen(value);
            if (cleaned.isEmpty()) return 0;
            return Integer.parseInt(cleaned);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static String digitsAndHyphen(String value) {
        if (value == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (Character.isDigit(c) || c == '-') sb.append(c);
        }
        return sb.toString();
    }

    /**
     * Safely run a regex-based replaceAll with a timeout to mitigate ReDoS.
     * If the operation times out or fails, returns the original input.
     */
    private static String safeReplace(String input, String regex, String replacement) {
        if (input == null || input.isEmpty()) return input;
        Callable<String> task = () -> {
            try {
                return input.replaceAll(regex, replacement);
            } catch (Throwable t) {
                return input;
            }
        };

        Future<String> future = regexExecutor.submit(task);
        try {
            return future.get(100, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException e) {
            future.cancel(true);
            logger.warn("safeReplace failed for regex {}: {}", regex, e.toString());
            return input;
        } catch (java.util.concurrent.TimeoutException e) {
            future.cancel(true);
            logger.warn("safeReplace timed out for regex {}", regex);
            return input;
        }
    }

   /*  private String parseDouble(String value) {
        if (value == null || value.trim().isEmpty() || value.equals("-")) return 0.0;
        try {
            String cleanedValue = value
                .replace(',', '.')
                .replaceAll("[^\\d.]", "");

            if (cleanedValue.isEmpty()) {
                return 0.0;
            }

            double parsedValue = Double.parseDouble(cleanedValue);
            
            DecimalFormat df = new DecimalFormat("#.#");
            //String valorFormateado = df.format(14.600000); // Resultado: "14.6"

            // Si necesitas mantenerlo como número
            return df.format(parsedValue);

        } catch (NumberFormatException e) {
            System.err.println("Error al parsear el double del valor: '" + value + "'");
            return 0.0;
        }
    }*/

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
        // Aumentamos la robustez intentando con varios selectores comunes para banners de cookies.
        // El wait corto es para no penalizar el rendimiento si el banner no aparece.
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
        
        // Lista de selectores a intentar en orden de probabilidad.
        List<By> selectors = List.of(
            By.xpath("//button[contains(., 'Aceptar todo')]") ,
            By.xpath("//button[contains(., 'Accept All')]") ,
            By.cssSelector(".qc-cmp2-summary-buttons button[mode='primary']"),
            By.id("accept-cookies-button"),
            By.className("accept-cookies")
        );

        for (By selector : selectors) {
            try {
                WebElement consentButton = shortWait.until(ExpectedConditions.elementToBeClickable(selector));
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", consentButton);
                
                // Esperamos un momento a que el banner desaparezca para evitar problemas de sincronización.
                wait.until(ExpectedConditions.invisibilityOf(consentButton));
                
                System.out.println("Banner de cookies aceptado con el selector: " + selector);
                return; // Salimos del método si tuvimos éxito.
            } catch (TimeoutException e) {
                // No hacemos nada, simplemente probamos el siguiente selector.
            }
        }
        
        System.out.println("No se encontró ningún banner de cookies conocido o ya fue aceptado.");
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
            Path destination = Path.of("failure-screenshot.png");
            Files.copy(source.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Screenshot guardado en: " + destination.toAbsolutePath());

            Path htmlPath = Path.of("failure-page.html");
            Files.write(htmlPath, driver.getPageSource().getBytes(StandardCharsets.UTF_8));
            System.out.println("HTML de la página guardado en: " + htmlPath.toAbsolutePath());
        } catch (IOException ex) {
            System.err.println("No se pudo guardar el screenshot o el HTML: " + ex.getMessage());
        }
    }

}
