package com.dapp.api_futbol.service;

import com.dapp.api_futbol.dto.MatchDTO;
import com.dapp.api_futbol.model.Match;
import com.dapp.api_futbol.repository.MatchRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MatchService {

    private final MatchRepository matchRepository;

    public MatchService(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    public List<MatchDTO> getUpcomingMatchesByTeam(String teamName) {
        if (teamName == null || teamName.trim().isEmpty()) {
            throw new IllegalArgumentException("teamName is required");
        }

        String name = teamName.trim().toLowerCase();
        List<Match> matches = matchRepository.findUpcomingByTeamName(name, LocalDateTime.now());

        return matches.stream().map(m -> new MatchDTO(
                m.getId(),
                m.getHomeTeamName(),
                m.getAwayTeamName(),
                m.getMatchTime(),
                m.getStatus(),
                m.getHomeScore(),
                m.getAwayScore()
        )).collect(Collectors.toList());
    }
}
