package com.dapp.api_futbol.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.dapp.api_futbol.dto.PlayerDTO;

@RestController
@RequestMapping("/api")
public class TeamsPlayersController {

    @Operation(summary = "Get players of a team", description = "Returns the data of players of a team")
    @GetMapping("players/{teamName}")
    public ResponseEntity<PlayerDTO> getPlayersOfTeam(@RequestBody teamName) {
        PlayerDto p = new PlayerDto();
   /*      p.setId(1L);
        p.setName("Lionel Messi");
        p.setTeam("PSG");*/
        return ResponseEntity.ok(p);
    }
}