package com.dapp.api_futbol.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Team homeTeam;

    @ManyToOne
    private Team awayTeam;

    private LocalDateTime matchDate;

    private Integer homeScore;
    private Integer awayScore;

    public void setHomeTeam(Team teamA) {
        homeTeam = teamA;
    }
    public void setAwayTeam(Team teamB) {
        awayTeam = teamB;
    }
    public void setMatchDate(LocalDateTime now) {
        matchDate = now;
    }
    public void setAwayScore(int i) {
        awayScore = i;
    }
    public void setHomeScore(int i) {
        homeScore = i;
    }
    public String getId() {
        return id.toString();
    }
    public Team getHomeTeam() {
        return homeTeam;
    }
    public Team getAwayTeam() {
        return awayTeam;
    }
    public String getHomeScore() {
        return homeScore.toString();
    }
    public String getAwayScore() {
        return awayScore.toString();
    }
    public LocalDateTime getMatchTime() {
        return matchDate;
    }
}