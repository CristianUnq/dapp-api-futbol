package com.dapp.api_futbol.controller;

import com.dapp.api_futbol.dto.AdvancedMetricsDTO;
import com.dapp.api_futbol.service.TeamMetricsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdvancedMetricsController {

    private final TeamMetricsService teamMetricsService;

    public AdvancedMetricsController(TeamMetricsService teamMetricsService) {
        this.teamMetricsService = teamMetricsService;
    }

    // User-facing advanced team metrics (moved out of /actuator).
    // This endpoint is for application users; actuator endpoints remain under /actuator/**.
    @GetMapping("/advanced-metrics")
    public ResponseEntity<AdvancedMetricsDTO> getAdvancedMetrics() {
        AdvancedMetricsDTO metrics = teamMetricsService.getAdvancedMetrics();
        return ResponseEntity.ok(metrics);
    }
}
