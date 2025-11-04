package com.dapp.api_futbol.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Store team names as plain text (no relation to Team entity)
    private String homeTeamName;
    private String awayTeamName;

    private LocalDateTime matchDate;

    private Integer homeScore;
    private Integer awayScore;
    private Integer footballDataId;
    private String status;

    public void setHomeTeamName(String name) {
        this.homeTeamName = name;
    }

    public void setAwayTeamName(String name) {
        this.awayTeamName = name;
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

    public Integer getFootballDataId() {
        return footballDataId;
    }

    public void setFootballDataId(Integer footballDataId) {
        this.footballDataId = footballDataId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getHomeTeamName() {
        return homeTeamName;
    }

    public String getAwayTeamName() {
        return awayTeamName;
    }

    public Integer getHomeScore() {
        return homeScore;
    }

    public Integer getAwayScore() {
        return awayScore;
    }

    public LocalDateTime getMatchTime() {
        return matchDate;
    }
}