package com.dapp.api_futbol.service;

import com.dapp.api_futbol.dto.PlayerDTO;
import com.dapp.api_futbol.dto.TeamDTO;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ScraperService {

    private static final String BASE_URL = "https://es.whoscored.com";
    private static final String SEARCH_URL_TEMPLATE = BASE_URL + "/Search/?t=";

    public List<PlayerDTO> scrapePlayersByTeam(String teamName) {
        WebDriver driver = setupWebDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        List<PlayerDTO> players = new ArrayList<>();

        try {
            String teamUrl = findTeamUrl(driver, wait, teamName);
            if (teamUrl == null) {
                System.out.println("No se encontró el equipo '" + teamName + "'.");
                return players;
            }

            driver.get(BASE_URL + teamUrl);
            System.out.println("Navegando a la página del equipo: " + driver.getCurrentUrl());
            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("top-player-stats-summary-grid")));

            Document teamPage = Jsoup.parse(driver.getPageSource());
            players = parsePlayersFromTeamPage(teamPage, teamName);

        } catch (Exception e) {
            handleScraperError(driver, e);
        } finally {
            driver.quit();
        }
        return players;
    }

    public List<TeamDTO> scrapeTeams(String teamName) {
        WebDriver driver = setupWebDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        List<TeamDTO> teams = new ArrayList<>();

        try {
            String encodedTeamName = URLEncoder.encode(teamName, StandardCharsets.UTF_8);
            driver.get(SEARCH_URL_TEMPLATE + encodedTeamName);
            acceptCookies(driver, wait);

            Document searchPage = Jsoup.parse(driver.getPageSource());
            teams = parseTeamsFromSearchPage(searchPage);

        } catch (Exception e) {
            handleScraperError(driver, e);
        } finally {
            driver.quit();
        }
        return teams;
    }

    private WebDriver setupWebDriver() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36");
        return new ChromeDriver(options);
    }

    private String findTeamUrl(WebDriver driver, WebDriverWait wait, String teamName) {
        String encodedTeamName = URLEncoder.encode(teamName, StandardCharsets.UTF_8);
        driver.get(SEARCH_URL_TEMPLATE + encodedTeamName);
        acceptCookies(driver, wait);

        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//h2[text()='Equipos:']/following-sibling::table[1]//tbody")));
            WebElement teamLink = driver.findElement(By.xpath("//div[@class='search-result']//h2[text()='Equipos:']/following-sibling::table[1]//a[normalize-space(text())='" + teamName + "']"));
            return teamLink.getAttribute("href").replace(BASE_URL, "");
        } catch (NoSuchElementException e) {
            System.out.println("No se encontró un enlace para el equipo: " + teamName);
            return null;
        }
    }

    private List<TeamDTO> parseTeamsFromSearchPage(Document searchPage) {
        List<TeamDTO> teams = new ArrayList<>();
        Elements teamRows = searchPage.select(".search-result h2:contains(Equipos:) + table tbody tr");
        System.out.println("Encontrados " + teamRows.size() + " equipos.");

        for (Element row : teamRows) {
            Element linkElement = row.selectFirst("a");
            Element countryElement = row.selectFirst("td span");
            if (linkElement != null && countryElement != null) {
                String name = linkElement.text().trim();
                String url = linkElement.attr("href");
                String country = countryElement.ownText().trim();
                teams.add(new TeamDTO(name, country, url));
            }
        }
        return teams;
    }

    private List<PlayerDTO> parsePlayersFromTeamPage(Document teamPage, String teamName) {
        List<PlayerDTO> players = new ArrayList<>();
        Element table = teamPage.getElementById("top-player-stats-summary-grid");
        if (table == null) {
            System.out.println("No se encontró la tabla de estadísticas, la página podría haber cambiado.");
            return players;
        }

        Elements headers = table.select("thead th");
        Map<String, Integer> headerMap = getHeaderMap(headers);
        
        Elements playerRows = table.select("tbody tr");
        System.out.println("Encontrados " + playerRows.size() + " jugadores para el equipo " + teamName + ".");

        for (Element row : playerRows) {
            Elements cells = row.select("td, th");
            if (cells.size() < 10) continue;

            try {
                PlayerDTO player = new PlayerDTO();
                player.setTeam(teamName);

                Element playerCell = cells.get(headerMap.get("Jugador"));
                Element playerLink = playerCell.selectFirst("a.player-link");
                if (playerLink != null) {
                    player.setName(playerLink.text().trim());
                    String playerUrl = "https://es.whoscored.com" + playerLink.attr("href");
                    player.setUrl(playerUrl);
                } else {
                    player.setName("N/A");
                }

                player.setHeight(getStatFromCell(cells, headerMap, "CM"));
                player.setWeight(getStatFromCell(cells, headerMap, "KG"));
                player.setAppearances(getStatFromCell(cells, headerMap, "Jgdos"));
                player.setMinsPlayed(getStatFromCell(cells, headerMap, "Mins"));
                player.setGoals(getStatFromCell(cells, headerMap, "Goles"));
                player.setAssists(getStatFromCell(cells, headerMap, "Asist"));
                player.setYellowCards(getStatFromCell(cells, headerMap, "Amar"));
                player.setRedCards(getStatFromCell(cells, headerMap, "Roja"));
                player.setShotsPerGame(getStatFromCell(cells, headerMap, "TpP"));
                player.setPassSuccess(getStatFromCell(cells, headerMap, "AP%"));
                player.setAerialsWon(getStatFromCell(cells, headerMap, "Aéreos"));
                player.setManOfTheMatch(getStatFromCell(cells, headerMap, "JdelP"));
                player.setRating(getStatFromCell(cells, headerMap, "Rating"));

                players.add(player);

            } catch (Exception e) {
                System.err.println("Error al parsear fila de jugador: " + e.getMessage());
            }
        }
        return players;
    }

    private Map<String, Integer> getHeaderMap(Elements headers) {
        Map<String, Integer> headerMap = new HashMap<>();
        for (int i = 0; i < headers.size(); i++) {
            headerMap.put(headers.get(i).text().trim(), i);
        }
        return headerMap;
    }

    private String getStatFromCell(Elements cells, Map<String, Integer> headerMap, String headerName) {
        if (headerMap.containsKey(headerName)) {
            int index = headerMap.get(headerName);
            if (index < cells.size()) {
                String value = cells.get(index).text().trim();
                return value.equals("-") ? "0" : value;
            }
        }
        return "N/A";
    }

    private void acceptCookies(WebDriver driver, WebDriverWait wait) {
        try {
            List<WebElement> iframes = driver.findElements(By.tagName("iframe"));
            for (WebElement iframe : iframes) {
                driver.switchTo().frame(iframe);
                List<WebElement> buttons = driver.findElements(By.xpath(
                        "//button[contains(text(),'Accept') or contains(text(),'Aceptar')]"
                ));
                if (!buttons.isEmpty()) {
                    buttons.get(0).click();
                    System.out.println("Cookies aceptadas.");
                    driver.switchTo().defaultContent();
                    return;
                }
                driver.switchTo().defaultContent();
            }
        } catch (Exception e) {
            System.out.println("No se pudo aceptar cookies: " + e.getMessage());
            driver.switchTo().defaultContent();
        }
    }

    private void handleScraperError(WebDriver driver, Exception e) {
        System.out.println("Ocurrió un error, guardando captura de pantalla y HTML...");
        try {
            File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Path destination = Paths.get("failure-screenshot.png");
            Files.copy(scrFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Captura de pantalla guardada en: " + destination.toAbsolutePath());
        } catch (IOException ioException) {
            System.err.println("Error al guardar la captura: " + ioException.getMessage());
        }
        try {
            Path htmlFile = Paths.get("failure-page.html");
            Files.writeString(htmlFile, driver.getPageSource(), StandardCharsets.UTF_8);
            System.out.println("HTML de la página guardado en: " + htmlFile.toAbsolutePath());
        } catch (IOException ioException) {
            System.err.println("Error al guardar el HTML: " + ioException.getMessage());
        }
        throw new RuntimeException(e);
    }
}