package com.dapp.api_futbol.service;

import com.dapp.api_futbol.dto.PlayerDTO;
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
import java.util.List;

@Service
public class ScraperService {

    private static final String BASE_URL = "https://es.whoscored.com";
    private static final String SEARCH_URL_TEMPLATE = BASE_URL + "/Search/?t=";

    public List<PlayerDTO> scrapePlayers(String teamName) {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36");

        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        List<PlayerDTO> players = new ArrayList<>();

        try {
            String encodedTeamName = URLEncoder.encode(teamName, StandardCharsets.UTF_8);
            driver.get(SEARCH_URL_TEMPLATE + encodedTeamName);

            // --- INICIO: CÓDIGO MEJORADO PARA ACEPTAR COOKIES ---
            try {
                // 1. Esperar y cambiar al iframe del banner de cookies
                wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.cssSelector("iframe[id*='sp_message_iframe']")));
                System.out.println("Cambiado al iframe de cookies.");

                // 2. Hacer clic en el botón de aceptar dentro del iframe
                String cookieButtonSelector = "button.qc-cmp2-summary-buttons-accept-all";
                WebElement acceptButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(cookieButtonSelector)));
                acceptButton.click();
                System.out.println("Banner de cookies aceptado.");

                // 3. Volver al contenido principal de la página
                driver.switchTo().defaultContent();
                System.out.println("Regresando al contenido principal.");

            } catch (Exception e) {
                System.out.println("No se encontró el banner de cookies o falló el proceso, continuando...");
                driver.switchTo().defaultContent(); // Asegurarse de volver al contenido principal en caso de error
            }
            // --- FIN: CÓDIGO MEJORADO ---

            WebElement searchResult = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#search-result ul li a")));
            searchResult.click();
            System.out.println("Clic en el primer resultado de búsqueda.");

            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("player-table-statistics-body")));
            
            Document teamPage = Jsoup.parse(driver.getPageSource());
            
            Elements playerRows = teamPage.select("#player-table-statistics-body tr");

            for (Element row : playerRows) {
                Element playerLink = row.selectFirst("a.player-link");
                if (playerLink != null) {
                    String playerName = playerLink.text();
                    PlayerDTO player = new PlayerDTO();
                    player.setName(playerName);
                    player.setTeam(teamName);
                    players.add(player);
                }
            }
        } catch (Exception e) {
            System.out.println("Ocurrió un error, guardando captura de pantalla...");
            File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            try {
                Path destination = Paths.get("failure-screenshot.png");
                Files.copy(scrFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Captura de pantalla guardada en: " + destination.toAbsolutePath());
            } catch (IOException ioException) {
                System.err.println("Error al guardar la captura de pantalla: " + ioException.getMessage());
            }
            throw new RuntimeException(e);
        } finally {
            driver.quit();
        }
        return players;
    }
}