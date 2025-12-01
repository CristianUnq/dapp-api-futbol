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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MatchService {

    private final MatchRepository matchRepository;
    private final TeamRepository teamRepository;
    private final MatchPredictionCalculator predictionCalculator;

    public MatchService(MatchRepository matchRepository, TeamRepository teamRepository, MatchPredictionCalculator predictionCalculator) {
        this.matchRepository = matchRepository;
        this.teamRepository = teamRepository;
        this.predictionCalculator = predictionCalculator;
    }

    /**
     * Devuelve los próximos partidos de un equipo (status == TIMED) buscando
     * por coincidencia parcial del nombre en homeTeamName o awayTeamName.
     * Retorna un ResponseObject con los datos, mensaje y status.
     */
    public ResponseObject getNextMatchesOf(String teamName) {
        if (teamName == null || teamName.trim().isEmpty()) {
            return new ResponseObject("Nombre de equipo inválido", HttpStatus.BAD_REQUEST.value());
        }

        String name = teamName.toLowerCase().trim();
        List<Match> nextMatches = matchRepository.findUpcomingByTeamNameAndStatus(name, "TIMED");

        return new ResponseObject(nextMatches, "Próximos partidos encontrados exitosamente", HttpStatus.OK.value());
    }

    /**
     * Genera una predicción para un partido dado su ID, comparando las estadísticas de los equipos.
     * @param idMatch El ID del partido para el cual generar la predicción.
     * @return Un MatchPredictionDTO con las probabilidades de resultado.
     * @throws MatchNotFoundException si el partido no se encuentra.
     * @throws TeamNotFoundException si alguno de los equipos del partido no se encuentra.
     */
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