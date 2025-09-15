package com.dapp.api_futbol.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/players")
public class PlayerController {

    @Operation(summary = "Get a sample player", description = "Returns a sample player object for demo")
    @GetMapping("/sample")
    public ResponseEntity<PlayerDto> sample() {
        PlayerDto p = new PlayerDto();
        p.setId(1L);
        p.setName("Lionel Messi");
        p.setTeam("PSG");
        return ResponseEntity.ok(p);
    }

    public static class PlayerDto {
        private Long id;
        private String name;
        private String team;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getTeam() { return team; }
        public void setTeam(String team) { this.team = team; }
    }
}
