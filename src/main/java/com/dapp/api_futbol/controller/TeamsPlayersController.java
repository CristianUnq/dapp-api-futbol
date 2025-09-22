package com.dapp.api_futbol.controller;

import com.dapp.api_futbol.dto.PlayerDTO;
import com.dapp.api_futbol.service.ScraperService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TeamsPlayersController {

    private final ScraperService scraperService;

    public TeamsPlayersController(ScraperService scraperService) {
        this.scraperService = scraperService;
    }

    @Operation(summary = "Obtiene los jugadores de un equipo haciendo web scraping", description = "Retorna los datos de los jugadores de un equipo desde es.whoscored.com")
    @GetMapping("players/{teamName}")
    public ResponseEntity<?> getPlayersOfTeam(@PathVariable String teamName) {
        try {
            List<PlayerDTO> players = scraperService.scrapePlayersByTeam(teamName);
            
            if (players.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                     .body("No se encontraron jugadores para el equipo: " + teamName + ". Por favor, verifica que el nombre sea correcto.");
            }
            return ResponseEntity.ok(players);
            
        } catch (Exception e) {
            System.err.println("Error en la solicitud para el equipo '" + teamName + "': " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Ocurrió un error inesperado al procesar la solicitud.");
        }
    }
}