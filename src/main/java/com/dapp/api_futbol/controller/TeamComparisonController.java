package com.dapp.api_futbol.controller;

import com.dapp.api_futbol.dto.ComparisonResultDTO;
import com.dapp.api_futbol.response.ResponseObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import com.dapp.api_futbol.service.TeamComparisonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TeamComparisonController {

    private final TeamComparisonService teamComparisonService;

    public TeamComparisonController(TeamComparisonService teamComparisonService) {
        this.teamComparisonService = teamComparisonService;
    }

    @Operation(summary = "Compara dos equipos por nombre", description = "Compara estadísticas avanzadas entre dos equipos. Se requieren los parámetros teamA y teamB.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Comparación generada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Parámetros faltantes o inválidos"),
        @ApiResponse(responseCode = "404", description = "Uno o ambos equipos no encontrados"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/compare-teams")
    public ResponseEntity<ComparisonResultDTO> compareTeams(@RequestParam("teamA") String teamA,
                                                            @RequestParam("teamB") String teamB) {
        ComparisonResultDTO result = teamComparisonService.compareTeamsByName(teamA.trim(), teamB.trim());
        return ResponseEntity.ok(result);
    }
}
