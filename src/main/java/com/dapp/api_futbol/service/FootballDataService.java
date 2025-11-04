package com.dapp.api_futbol.service;

import com.dapp.api_futbol.model.Match;
import com.dapp.api_futbol.repository.MatchRepository;
import com.dapp.api_futbol.response.ResponseObject;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FootballDataService {

    private final MatchRepository matchRepository;

    public FootballDataService(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
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
}