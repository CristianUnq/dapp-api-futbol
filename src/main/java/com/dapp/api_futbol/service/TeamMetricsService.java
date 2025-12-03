package com.dapp.api_futbol.service;

import com.dapp.api_futbol.dto.AdvancedMetricsDTO;
import com.dapp.api_futbol.dto.TeamMetricDTO;
import com.dapp.api_futbol.model.Team;
import com.dapp.api_futbol.repository.TeamRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TeamMetricsService {

    private final TeamRepository teamRepository;
    private final com.dapp.api_futbol.metrics.AdvancedMetricsCalculation metricsCalculation;

    public TeamMetricsService(TeamRepository teamRepository, com.dapp.api_futbol.metrics.AdvancedMetricsCalculation metricsCalculation) {
        this.teamRepository = teamRepository;
        this.metricsCalculation = metricsCalculation;
    }

    public AdvancedMetricsDTO getAdvancedMetrics() {
        List<Team> teams = teamRepository.findAll();
        AdvancedMetricsDTO result = new AdvancedMetricsDTO();
        List<TeamMetricDTO> metrics = metricsCalculation.computeTeamMetrics(teams);
        result.setTeams(metrics);
        return result;
    }

}
