package com.dapp.api_futbol.controller;

import com.dapp.api_futbol.dto.ComparisonResultDTO;
import com.dapp.api_futbol.service.TeamComparisonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TeamComparisonController {

    private final TeamComparisonService teamComparisonService;

    public TeamComparisonController(TeamComparisonService teamComparisonService) {
        this.teamComparisonService = teamComparisonService;
    }

    @GetMapping("/compare-teams")
    public ResponseEntity<ComparisonResultDTO> compareTeams(@RequestParam("teamA") String teamA,
                                                            @RequestParam("teamB") String teamB) {
        ComparisonResultDTO result = teamComparisonService.compareTeamsByName(teamA, teamB);
        return ResponseEntity.ok(result);
    }
}
