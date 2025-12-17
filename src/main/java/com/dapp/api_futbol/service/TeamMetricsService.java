package com.dapp.api_futbol.service;

import com.dapp.api_futbol.dto.AdvancedMetricsDTO;
import com.dapp.api_futbol.dto.TeamMetricDTO;
import com.dapp.api_futbol.model.Team;
import com.dapp.api_futbol.repository.TeamRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TeamMetricsService {

    private final TeamRepository teamRepository;
    private final com.dapp.api_futbol.metrics.AdvancedMetricsCalculation metricsCalculation;

    private static final Logger logger = LoggerFactory.getLogger(TeamMetricsService.class);

    public TeamMetricsService(TeamRepository teamRepository, com.dapp.api_futbol.metrics.AdvancedMetricsCalculation metricsCalculation) {
        this.teamRepository = teamRepository;
        this.metricsCalculation = metricsCalculation;
    }

    public AdvancedMetricsDTO getAdvancedMetrics() {
        logger.info("buscando equipos en la base de datos para calcular métricas");
        List<Team> teams = teamRepository.findAll();
        AdvancedMetricsDTO result = new AdvancedMetricsDTO();
        logger.info("calculando métricas avanzadas de los equipos");
        List<TeamMetricDTO> metrics = metricsCalculation.computeTeamMetrics(teams);
        result.setTeams(metrics);
        return result;
    }

}
