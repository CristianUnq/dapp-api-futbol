package com.dapp.api_futbol.service;

import com.dapp.api_futbol.dto.ComparisonResultDTO;
import com.dapp.api_futbol.dto.TeamStatsDTO;
import com.dapp.api_futbol.exception.TeamNotFoundException;
import com.dapp.api_futbol.metrics.ComparisonCalculation;
import com.dapp.api_futbol.model.Player;
import com.dapp.api_futbol.model.Team;
import com.dapp.api_futbol.repository.PlayerRepository;
import com.dapp.api_futbol.repository.TeamRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional
public class TeamComparisonService {

    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;
    private final ComparisonCalculation comparisonCalculation;

    private static final Logger logger = LoggerFactory.getLogger(TeamComparisonService.class);

    public TeamComparisonService(TeamRepository teamRepository, PlayerRepository playerRepository, ComparisonCalculation comparisonCalculation) {
        this.teamRepository = teamRepository;
        this.playerRepository = playerRepository;
        this.comparisonCalculation = comparisonCalculation;
    }

    public ComparisonResultDTO compareTeamsByName(String nameA, String nameB) {
        logger.info("Comparando equipos: '{}' vs '{}'", nameA, nameB);
        Team teamA = teamRepository.findByNameIgnoreCase(nameA).orElseThrow(() -> new TeamNotFoundException(nameA));
        Team teamB = teamRepository.findByNameIgnoreCase(nameB).orElseThrow(() -> new TeamNotFoundException(nameB));

        // Calculate metrics for each team
        double avgAgeA = calculateAverageAge(teamA);
        double avgAgeB = calculateAverageAge(teamB);
        double avgRatingA = calculateAverageRating(teamA);
        double avgRatingB = calculateAverageRating(teamB);
        double winRateA = calculateWinRate(teamA);
        double winRateB = calculateWinRate(teamB);
        Player bestPlayerA = findBestPlayer(teamA);
        Player bestPlayerB = findBestPlayer(teamB);

        // Create DTOs with descriptive strings
        TeamStatsDTO teamADto = new TeamStatsDTO(Long.valueOf(teamA.getId()), teamA.getName());
        teamADto.setAverageAgeDescription(formatComparison("average age", avgAgeA, avgAgeB, " (", " vs ", ")"));
        teamADto.setAverageRatingDescription(formatComparison("average rating", avgRatingA, avgRatingB, " (", " vs ", ")"));
        teamADto.setWinRateDescription(formatComparison("win rate", winRateA, winRateB, " (", " vs ", ")%"));
        teamADto.setBestPlayerName(bestPlayerA != null ? bestPlayerA.getName() : "N/A");

        TeamStatsDTO teamBDto = new TeamStatsDTO(Long.valueOf(teamB.getId()), teamB.getName());
        teamBDto.setAverageAgeDescription(formatComparison("average age", avgAgeB, avgAgeA, " (", " vs ", ")"));
        teamBDto.setAverageRatingDescription(formatComparison("average rating", avgRatingB, avgRatingA, " (", " vs ", ")"));
        teamBDto.setWinRateDescription(formatComparison("win rate", winRateB, winRateA, " (", " vs ", ")%"));
        teamBDto.setBestPlayerName(bestPlayerB != null ? bestPlayerB.getName() : "N/A");

        ComparisonResultDTO out = new ComparisonResultDTO();
        out.setTeamAName(teamA.getName());
        out.setTeamBName(teamB.getName());
        out.setTeamA(teamADto);
        out.setTeamB(teamBDto);
        


        return out;
    }

    private double calculateAverageAge(Team team) {
        List<Player> players = playerRepository.findByTeam(team);
        if (players.isEmpty()) {
            return 0.0;
        }
        return players.stream()
                .mapToInt(Player::getAge)
                .average()
                .orElse(0.0);
    }

    private double calculateAverageRating(Team team) {
        List<Player> players = playerRepository.findByTeam(team);
        if (players.isEmpty()) {
            return 0.0;
        }
        return players.stream()
                .mapToDouble(player -> {
                    try {
                        return Double.parseDouble(player.getRating());
                    } catch (NumberFormatException e) {
                        return 0.0; // Or some other default value
                    }
                })
                .average()
                .orElse(0.0);
    }

    private double calculateWinRate(Team team) {
        if (team.getMatchsPlayed() == null || team.getMatchsPlayed() == 0) {
            return 0.0;
        }
        return ((double) (team.getMatchsWon() != null ? team.getMatchsWon() : 0) / team.getMatchsPlayed()) * 100;
    }

    private Player findBestPlayer(Team team) {
        List<Player> players = playerRepository.findByTeam(team);
        return players.stream()
                .max(Comparator.comparing(player -> {
                    try {
                        return Double.parseDouble(player.getRating());
                    } catch (NumberFormatException e) {
                        return 0.0;
                    }
                }))
                .orElse(null);
    }

    private String formatComparison(String metricName, double valueA, double valueB, String prefix, String separator, String suffix) {
        DecimalFormat df = new DecimalFormat("#.#");
        String formattedA = df.format(valueA);
        String formattedB = df.format(valueB);
        // Treat values as equal when their formatted representations match (avoids floating-point precision issues)
        if (formattedA.equals(formattedB)) {
            return "Same " + metricName + prefix + formattedA + separator + formattedB + suffix;
        }
        String comparison = valueA > valueB ? "Higher" : "Lower";
        return comparison + " " + metricName + prefix + formattedA + separator + formattedB + suffix;
    }
}
