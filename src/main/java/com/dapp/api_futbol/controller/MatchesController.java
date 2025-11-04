package com.dapp.api_futbol.controller;

import com.dapp.api_futbol.dto.MatchDTO;
import com.dapp.api_futbol.service.MatchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/matches")
public class MatchesController {

    private final MatchService matchService;

    public MatchesController(MatchService matchService) {
        this.matchService = matchService;
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<MatchDTO>> getUpcomingMatches(@RequestParam(name = "team") String team) {
        List<MatchDTO> list = matchService.getUpcomingMatchesByTeam(team);
        return ResponseEntity.ok(list);
    }
}
