package com.dapp.api_futbol.config;

import com.dapp.api_futbol.model.Match;
import com.dapp.api_futbol.model.Player;
import com.dapp.api_futbol.model.Team;
import com.dapp.api_futbol.repository.MatchRepository;
import com.dapp.api_futbol.repository.PlayerRepository;
import com.dapp.api_futbol.repository.TeamRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Simple DataLoader for development/e2e profiles.
 * Seeds minimal teams, players and a match without calling external scrapers.
 */
@Component
@ConditionalOnProperty(name = "app.data-loader.enabled", havingValue = "true", matchIfMissing = false)
public class DataLoader implements CommandLineRunner {

    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;
    private final MatchRepository matchRepository;

    public DataLoader(TeamRepository teamRepository, PlayerRepository playerRepository, MatchRepository matchRepository) {
        this.teamRepository = teamRepository;
        this.playerRepository = playerRepository;
        this.matchRepository = matchRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Clear in safe order
        matchRepository.deleteAll();
        playerRepository.deleteAll();
        teamRepository.deleteAll();

        // Seed two teams
        Team teamA = new Team();
        teamA.setName("Team A");
        teamRepository.save(teamA);

        Team teamB = new Team();
        teamB.setName("Team B");
        teamRepository.save(teamB);

    // Seed players for teamA
    Player p1 = new Player();
    p1.setName("Player One");
    p1.setTeam(teamA);
    p1.setGoals("5");
    p1.setAssists("2");
    p1.setRating("7.1");
    playerRepository.save(p1);

    Player p2 = new Player();
    p2.setName("Player Two");
    p2.setTeam(teamA);
    p2.setGoals("3");
    p2.setAssists("1");
    p2.setRating("6.8");
    playerRepository.save(p2);

    // Seed players for teamB
    Player p3 = new Player();
    p3.setName("Player Three");
    p3.setTeam(teamB);
    p3.setGoals("4");
    p3.setAssists("3");
    p3.setRating("7.3");
    playerRepository.save(p3);

    // Seed a sample upcoming match (store team names per Match model)
    Match m = new Match();
    m.setHomeTeamName(teamA.getName());
    m.setAwayTeamName(teamB.getName());
    m.setMatchDate(LocalDateTime.now().plusDays(3));
    matchRepository.save(m);

        System.out.println("DataLoader: seeded teams, players and a sample match.");
    }
}