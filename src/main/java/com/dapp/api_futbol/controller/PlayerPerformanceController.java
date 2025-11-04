package com.dapp.api_futbol.controller;

import com.dapp.api_futbol.dto.PlayerPerformanceDTO;
import com.dapp.api_futbol.service.PlayerPerformanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/players")
@Tag(name = "Players", description = "Player performance and statistics endpoints")
public class PlayerPerformanceController {

    @Autowired
    private PlayerPerformanceService playerPerformanceService;

    @GetMapping("/performance/{playerId}")
    @Operation(
        summary = "Get player performance stats",
        description = "Retrieves a player's performance statistics including their last 10 matches, " +
                     "average rating, total goals, and assists.",
        security = {
            @SecurityRequirement(name = "bearer-jwt"),
            @SecurityRequirement(name = "api-key")
        }
    )
    public ResponseEntity<PlayerPerformanceDTO> getPlayerPerformance(
            @Parameter(description = "ID of the player", required = true)
            @PathVariable Long playerId) {
        return ResponseEntity.ok(playerPerformanceService.getPlayerPerformance(playerId));
    }
}