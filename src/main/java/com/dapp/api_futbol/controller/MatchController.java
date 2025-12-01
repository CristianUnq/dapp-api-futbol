package com.dapp.api_futbol.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dapp.api_futbol.response.ResponseObject;
import com.dapp.api_futbol.service.MatchService;
import com.dapp.api_futbol.service.QueryHistoryService;
import com.dapp.api_futbol.service.UserService;
import com.dapp.api_futbol.model.User;
import com.dapp.api_futbol.dto.MatchPredictionDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/footballData")
public class MatchController {
    
    private static final Logger logger = LoggerFactory.getLogger(TeamsPlayersController.class);
    
    @Autowired
    private MatchService footballDataService;

    @Operation(summary = "Obtiene los proximos partidos de un equipo", description = "Retorna los proximos partidos de un equipo desde FootballData.com")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Proximos partidos encontrados exitosamente"),
        @ApiResponse(responseCode = "400", description = "Nombre de equipo inválido"),
        @ApiResponse(responseCode = "404", description = "No se encontraron partidos para el equipo"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("nextMatches/{teamName}")
    public ResponseEntity<?> getNextMatchesFromTeam(@PathVariable String teamName, java.security.Principal principal) {
        logger.info("Solicitando proximos partidos del equipo: {}", teamName);
        ResponseObject responseNextMatches = footballDataService.getNextMatchesOf(teamName);
        return ResponseEntity.ok(responseNextMatches);
    }

    @Operation(summary = "Obtiene una predicción de resultado para un partido", description = "Retorna las probabilidades de victoria del equipo local, visitante o empate para un partido específico.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Predicción generada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Partido o equipos no encontrados"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("prediction/{idMatch}")
    public ResponseEntity<?> getMatchPrediction(@PathVariable Long idMatch, java.security.Principal principal) {
        logger.info("Solicitando predicción para el partido con ID: {}", idMatch);
        // El servicio MatchService se inyecta como 'footballDataService'
        MatchPredictionDTO prediction = footballDataService.getPredictionFrom(idMatch);
        return ResponseEntity.ok(prediction);
    }
}
