package com.dapp.api_futbol.service;

import com.dapp.api_futbol.model.Match;
import com.dapp.api_futbol.model.Team;
import com.dapp.api_futbol.repository.MatchRepository;
import com.dapp.api_futbol.repository.TeamRepository;
import com.dapp.api_futbol.response.ResponseObject;
import com.dapp.api_futbol.dto.MatchPredictionDTO;
import com.dapp.api_futbol.metrics.MatchPredictionCalculator;
import com.dapp.api_futbol.exception.MatchNotFoundException;
import com.dapp.api_futbol.exception.TeamNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MatchService {

    private final MatchRepository matchRepository;
    private final TeamRepository teamRepository;
    private final MatchPredictionCalculator predictionCalculator;

    private static final Logger logger = LoggerFactory.getLogger(MatchService.class);

    public MatchService(MatchRepository matchRepository, TeamRepository teamRepository, MatchPredictionCalculator predictionCalculator) {
        this.matchRepository = matchRepository;
        this.teamRepository = teamRepository;
        this.predictionCalculator = predictionCalculator;
    }

    public ResponseObject getNextMatchesOf(String teamName) {
        if (teamName == null || teamName.trim().isEmpty()) {
            logger.error("Es Invalido el nombre del equipo: {}", teamName);
            return new ResponseObject("Nombre de equipo inválido", HttpStatus.BAD_REQUEST.value());
        }

        String name = teamName.toLowerCase().trim();
        List<Match> nextMatches = matchRepository.findUpcomingByTeamNameAndStatus(name, "TIMED");

        logger.info("Se encontraron {} próximos partidos para el equipo: {}", nextMatches.size(), teamName);
        return new ResponseObject(nextMatches, "Próximos partidos encontrados exitosamente", HttpStatus.OK.value());
    }

    public MatchPredictionDTO getPredictionFrom(Long idMatch) {
        Match match = matchRepository.findById(idMatch)
                .orElseThrow(() -> new MatchNotFoundException(idMatch.toString()));

        String homeTeamName = match.getHomeTeamName();
        String awayTeamName = match.getAwayTeamName();

        // Buscar los equipos con sus estadísticas completas
        Team localTeam = teamRepository.findByNameOrContainsName(homeTeamName)
                .orElseThrow(() -> new TeamNotFoundException("Equipo local no encontrado: " + homeTeamName));
        Team visitorTeam = teamRepository.findByNameOrContainsName(awayTeamName)
                .orElseThrow(() -> new TeamNotFoundException("Equipo visitante no encontrado: " + awayTeamName));

        // Calcular la predicción usando la clase MatchPredictionCalculator
        return predictionCalculator.calculatePrediction(localTeam, visitorTeam);
    }
}