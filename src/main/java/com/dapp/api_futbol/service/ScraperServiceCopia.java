/*package com.dapp.api_futbol.service;

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
public class ScraperServiceCopia {

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

        Document searchPage = Jsoup.parse(driver.getPageSource());
        Elements teamLinks = searchPage.select("a[href^=/Teams/]");
        if (!teamLinks.isEmpty()) {
            return teamLinks.first().attr("href");
        }
        return null;
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

    private List<PlayerDTO> parsePlayersFromTeamPage(Document doc, String teamName) {
        List<PlayerDTO> players = new ArrayList<>();
        Elements rows = doc.select("#top-player-stats-summary-grid tr");
        for (Element row : rows) {
            Elements cells = row.select("td");
            if (cells.size() > 5) {
                PlayerDTO player = new PlayerDTO();
                player.setName(cells.get(2).text());
                player.setAge(Integer.parseInt(cells.get(3).text()));
                player.setPosition(cells.get(4).text());
                player.setTeamName(teamName);
                players.add(player);
            }
        }
        return players;
    }

    private List<TeamDTO> parseTeamsFromSearchPage(Document doc) {
        List<TeamDTO> teams = new ArrayList<>();
        Elements rows = doc.select("#search-result table tbody tr");
        for (Element row : rows) {
            Elements cells = row.select("td");
            if (!cells.isEmpty()) {
                TeamDTO team = new TeamDTO();
                team.setName(cells.get(0).text());
                teams.add(team);
            }
        }
        return teams;
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
}*/
