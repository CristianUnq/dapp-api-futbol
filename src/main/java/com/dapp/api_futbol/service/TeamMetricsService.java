package com.dapp.api_futbol.service;

import com.dapp.api_futbol.dto.AdvancedMetricsDTO;
import com.dapp.api_futbol.dto.TeamMetricDTO;
import com.dapp.api_futbol.model.Team;
import com.dapp.api_futbol.repository.TeamRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

        List<Integer> puntos = new ArrayList<>();
        List<Integer> golesAFavor = new ArrayList<>();
        List<Integer> golesEnContra = new ArrayList<>();
        List<Integer> diferencia = new ArrayList<>();

        List<TeamMetricDTO> summary = teams.stream().map(t -> {
            Integer p = t.getPuntos();
            Integer gf = t.getGolesAFavor();
            Integer ga = t.getGolesEnContra();
            Integer diff = t.getDiferenciaDeGoles();
            if (p != null) puntos.add(p);
            if (gf != null) golesAFavor.add(gf);
            if (ga != null) golesEnContra.add(ga);
            if (diff != null) diferencia.add(diff);
            TeamMetricDTO dto = new TeamMetricDTO();
            dto.setId(Long.valueOf(t.getId()));
            dto.setName(t.getName());
            dto.setPuntos(p);
            dto.setGolesAFavor(gf);
            dto.setGolesEnContra(ga);
            dto.setDiferenciaDeGoles(diff);
            return dto;
        }).collect(Collectors.toList());

        AdvancedMetricsDTO result = new AdvancedMetricsDTO();

        result.setPuntos(metricsCalculation.computeStats(puntos));
        result.setGolesAFavor(metricsCalculation.computeStats(golesAFavor));
        result.setGolesEnContra(metricsCalculation.computeStats(golesEnContra));
        result.setDiferenciaDeGoles(metricsCalculation.computeStats(diferencia));

        // compute z-score for points per team
        double meanPoints = result.getPuntos() != null ? result.getPuntos().getMean() : 0.0;
        double stdPoints = result.getPuntos() != null ? result.getPuntos().getStddev() : 0.0;
        for (TeamMetricDTO dto : summary) {
            dto.setzScorePuntos(metricsCalculation.computeZScore(dto.getPuntos(), meanPoints, stdPoints));
        }
        result.setTeams(summary);
        return result;
    }

}
