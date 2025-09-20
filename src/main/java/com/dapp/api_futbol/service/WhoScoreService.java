package com.dapp.api_futbol.service;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import com.dapp.api_futbol.model.Team;

@Service
@Slf4j
public class WhoScoreService {

    WebDriver driver = new ChromeDriver();
    WebElement dataFromWhoScore;

    private void getDataFromWhoScore() {
        try{
            driver.get("https://es.whoscored.com/");
            dataFromWhoScore = driver.findElement(By.className("fixtures"));
        }catch (Exception e) {
            log.error("Error obteniendo informacion de WhoScored: {}", e.getMessage(), e);
            throw new RuntimeException("Error al obtener partidos del día", e);
        }

    }

    public WhoScoreService() {
    }

    public void getPlayersOfTeam(Team team) {

    }
}
