package com.dapp.api_futbol.controller;

import com.dapp.api_futbol.dto.PlayerDTO;
import com.dapp.api_futbol.service.ScraperService;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class TeamsPlayersController {

    private final ScraperService scraperService;

    public TeamsPlayersController(ScraperService scraperService) {
        this.scraperService = scraperService;
    }

    @Operation(summary = "Get players of a team by scraping", description = "Returns the data of players of a team from es.whoscored.com")
    @GetMapping("players/{teamName}")
    public ResponseEntity<List<PlayerDTO>> getPlayersOfTeam(@PathVariable String teamName) {
        try {
            List<PlayerDTO> players = scraperService.scrapePlayers(teamName);
            if (players.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(players);
        } catch (Exception e) { // Cambiado de IOException a Exception
            // Es buena idea loggear el error
            e.printStackTrace();
            return ResponseEntity.status(500).body(null); // Error interno del servidor
        }
    }
}