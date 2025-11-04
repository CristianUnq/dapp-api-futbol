package com.dapp.api_futbol.service;

import com.dapp.api_futbol.dto.PlayerPerformanceDTO;
import com.dapp.api_futbol.exception.TeamNotFoundException;
import com.dapp.api_futbol.model.Match;
import com.dapp.api_futbol.model.Player;
import com.dapp.api_futbol.model.PlayerStats;
import com.dapp.api_futbol.model.Team;
import com.dapp.api_futbol.repository.PlayerRepository;
import com.dapp.api_futbol.repository.PlayerStatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PlayerPerformanceService {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private PlayerStatsRepository playerStatsRepository;

    private static final int LAST_MATCHES_COUNT = 10;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Transactional(readOnly = true)
    public PlayerPerformanceDTO getPlayerPerformance(Long playerId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new TeamNotFoundException("Player not found with ID: " + playerId));

        List<PlayerStats> lastMatches = playerStatsRepository.findLastMatchesByPlayer(
                player, PageRequest.of(0, LAST_MATCHES_COUNT));

        Double averageRating = Optional.ofNullable(playerStatsRepository.getAverageRating(player))
                .orElse(0.0);
        Integer totalGoals = Optional.ofNullable(playerStatsRepository.getTotalGoals(player))
                .orElse(0);
        Integer totalAssists = Optional.ofNullable(playerStatsRepository.getTotalAssists(player))
                .orElse(0);

        PlayerPerformanceDTO dto = new PlayerPerformanceDTO();
        dto.setPlayerId(player.getId());
        dto.setPlayerName(player.getName());
        dto.setTeamName(player.getTeam().getName());
        dto.setAverageRating(averageRating);
        dto.setTotalGoals(totalGoals);
        dto.setTotalAssists(totalAssists);

        dto.setLastMatches(lastMatches.stream()
                .map(this::convertToMatchPerformanceDTO)
                .collect(Collectors.toList()));

        return dto;
    }

    private PlayerPerformanceDTO.MatchPerformanceDTO convertToMatchPerformanceDTO(PlayerStats stats) {
        PlayerPerformanceDTO.MatchPerformanceDTO dto = new PlayerPerformanceDTO.MatchPerformanceDTO();
        dto.setMatchId(stats.getMatch().getId());
        dto.setOpponent(getOpponentName(stats));
        dto.setDate(stats.getMatch().getDate().format(DATE_FORMATTER));
        dto.setGoals(stats.getGoals());
        dto.setAssists(stats.getAssists());
        dto.setRating(stats.getRating());
        return dto;
    }

    private String getOpponentName(PlayerStats stats) {
        Match match = stats.getMatch();
        Team playerTeam = stats.getPlayer().getTeam();
        String playerTeamName = playerTeam.getName();
        
        // Since our Match model doesn't have actual Team objects, use names
        boolean isHome = playerTeamName.equals(match.getHomeTeamName());
        return isHome ? match.getAwayTeamName() : match.getHomeTeamName();
    }
}