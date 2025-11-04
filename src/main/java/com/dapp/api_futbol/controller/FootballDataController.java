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
import com.dapp.api_futbol.service.FootballDataService;
import com.dapp.api_futbol.service.QueryHistoryService;
import com.dapp.api_futbol.service.UserService;
import com.dapp.api_futbol.model.User;

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

    @Autowired
    private QueryHistoryService queryHistoryService;

    @Autowired
    private UserService userService;

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

        User user = null;
        if (principal != null) {
            user = userService.findByUsername(principal.getName()).orElse(null);
            if (user != null) {
                queryHistoryService.recordQuery(user, "GET_NEXT_MATCHES", "team=" + teamName);
            }
        }

        ResponseObject responseNextMatches = footballDataService.getNextMatchesOf(teamName);
        return ResponseEntity.ok(responseNextMatches);
    }
}
