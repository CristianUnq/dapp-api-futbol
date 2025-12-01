package com.dapp.api_futbol.controller;

import com.dapp.api_futbol.response.ResponseObject;
import com.dapp.api_futbol.service.TeamsPlayersService;
import com.dapp.api_futbol.service.QueryHistoryService;
import com.dapp.api_futbol.service.UserService;
import com.dapp.api_futbol.model.User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api")
public class TeamsPlayersController {

    private static final Logger logger = LoggerFactory.getLogger(TeamsPlayersController.class);
    
    @Autowired
    private final TeamsPlayersService teamsPlayersService;
    private final QueryHistoryService queryHistoryService;
    private final UserService userService;

    public TeamsPlayersController(TeamsPlayersService teamsPlayersService, 
                                QueryHistoryService queryHistoryService,
                                UserService userService) {
        this.teamsPlayersService = teamsPlayersService;
        this.queryHistoryService = queryHistoryService;
        this.userService = userService;
    }

    @Operation(summary = "Obtiene los jugadores de un equipo haciendo web scraping", description = "Retorna los datos de los jugadores de un equipo desde es.whoscored.com")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Jugadores encontrados exitosamente"),
        @ApiResponse(responseCode = "400", description = "Nombre de equipo inválido"),
        @ApiResponse(responseCode = "404", description = "No se encontraron jugadores para el equipo"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("players/{teamName}")
    public ResponseEntity<?> getPlayersOfTeam(@PathVariable String teamName, java.security.Principal principal) {
        logger.info("Solicitando jugadores para el equipo: {}", teamName);
        ResponseObject responsePlayers = teamsPlayersService.getPlayersByTeam(teamName);
        return ResponseEntity.ok(responsePlayers);
    }
}