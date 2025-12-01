package com.dapp.api_futbol.service;

import com.dapp.api_futbol.dto.ComparisonResultDTO;
import com.dapp.api_futbol.dto.TeamMetricDTO;
import com.dapp.api_futbol.metrics.ComparisonCalculation;
import com.dapp.api_futbol.model.Team;
import com.dapp.api_futbol.repository.TeamRepository;
import com.dapp.api_futbol.exception.TeamNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class TeamComparisonService {

    private final TeamRepository teamRepository;
    private final ComparisonCalculation comparisonCalculation;

    public TeamComparisonService(TeamRepository teamRepository, ComparisonCalculation comparisonCalculation) {
        this.teamRepository = teamRepository;
        this.comparisonCalculation = comparisonCalculation;
    }

    public ComparisonResultDTO compareTeamsByName(String nameA, String nameB) {
        Team a = teamRepository.findByNameIgnoreCase(nameA).orElseThrow(() -> new TeamNotFoundException(nameA));
        Team b = teamRepository.findByNameIgnoreCase(nameB).orElseThrow(() -> new TeamNotFoundException(nameB));

        ComparisonResultDTO out = new ComparisonResultDTO();
        out.setTeamAName(a.getName());
        out.setTeamBName(b.getName());

        TeamMetricDTO teamADto = new TeamMetricDTO();
        teamADto.setId(Long.valueOf(a.getId()));
        teamADto.setName(a.getName());
        teamADto.setPuntos(a.getPuntos());
        teamADto.setGolesAFavor(a.getGolesAFavor());
        teamADto.setGolesEnContra(a.getGolesEnContra());
        teamADto.setDiferenciaDeGoles(a.getDiferenciaDeGoles());

        TeamMetricDTO teamBDto = new TeamMetricDTO();
        teamBDto.setId(Long.valueOf(b.getId()));
        teamBDto.setName(b.getName());
        teamBDto.setPuntos(b.getPuntos());
        teamBDto.setGolesAFavor(b.getGolesAFavor());
        teamBDto.setGolesEnContra(b.getGolesEnContra());
        teamBDto.setDiferenciaDeGoles(b.getDiferenciaDeGoles());

        out.setTeamA(teamADto);
        out.setTeamB(teamBDto);

        out.setHeadToHeadSummary(comparisonCalculation.computeHeadToHead(a, b));
        double[] probs = comparisonCalculation.computeWinProbabilities(a, b);
        out.setProbabilityTeamAWin(probs[0]);
        out.setProbabilityTeamBWin(probs[1]);
        out.setProbabilityDraw(probs[2]);
        out.setExpectedGoalDifference(comparisonCalculation.computeExpectedGoalDifference(a, b));

        return out;
    }
}
