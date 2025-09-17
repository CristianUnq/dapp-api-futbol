package com.dapp.api_futbol.config;

import com.dapp.api_futbol.model.Match;
import com.dapp.api_futbol.model.Team;
import com.dapp.api_futbol.repository.MatchRepository;
import com.dapp.api_futbol.repository.TeamRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataLoader implements CommandLineRunner {

    private final TeamRepository teamRepository;
    private final MatchRepository matchRepository;

    public DataLoader(TeamRepository teamRepository, MatchRepository matchRepository) {
        this.teamRepository = teamRepository;
        this.matchRepository = matchRepository;
    }

    @Override
    public void run(String... args) {
        Team teamA = new Team();
        teamA.setName("FC Barcelona");
        Team teamB = new Team();
        teamB.setName("Real Madrid");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Match match = new Match();
        match.setHomeTeam(teamA);
        match.setAwayTeam(teamB);
        match.setMatchDate(LocalDateTime.now());
        match.setHomeScore(2);
        match.setAwayScore(1);
        matchRepository.save(match);

        // Print all teams
        System.out.println("Teams in DB:");
        teamRepository.findAll().forEach(t -> System.out.println(t.getId() + " - " + t.getName()));

        // Print all matches
        System.out.println("Matches in DB:");
        matchRepository.findAll().forEach(m -> System.out.println(
            m.getId() + ": " + m.getHomeTeam().getName() + " vs " +
            m.getAwayTeam().getName() + " (" + m.getHomeScore() + "-" + m.getAwayScore() + ")"
        ));
    }
}