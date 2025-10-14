package com.dapp.api_futbol.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dapp.api_futbol.response.ResponsePlayers;
import com.dapp.api_futbol.service.FootballDataService;

import io.micrometer.core.ipc.http.HttpSender.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/footballData")
public class FootballDataController {
    
    private static final Logger logger = LoggerFactory.getLogger(TeamsPlayersController.class);
    
    @Autowired
    private FootballDataService footballDataService;

    @Operation(summary = "Obtiene los jugadores de un equipo haciendo web scraping", description = "Retorna los datos de los jugadores de un equipo desde es.whoscored.com")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Jugadores encontrados exitosamente"),
        @ApiResponse(responseCode = "400", description = "Nombre de equipo inválido"),
        @ApiResponse(responseCode = "404", description = "No se encontraron jugadores para el equipo"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("nextMatches/{teamName}")
    public ResponseEntity<?> getNextMatchesFromTeam(@PathVariable String teamName) {
        logger.info("Solicitando proximos partidos del equipo: {}", teamName);
        ResponsePlayers responseNextMatches = footballDataService.getNextMatchesOf(teamName);
        return ResponseEntity.ok(responseNextMatches);
    }
}
