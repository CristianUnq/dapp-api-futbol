package com.dapp.api_futbol.dto;

import java.time.LocalDateTime;

public class MatchDTO {

    private TeamDTO homeTeam;
    private TeamDTO awayTeam;
    private LocalDateTime matchDate;
    private Integer homeScore;
    private Integer awayScore;

    public void setHomeTeam(TeamDTO teamA) {
        homeTeam = teamA;
    }
    public void setAwayTeam(TeamDTO teamB) {
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
    public TeamDTO getHomeTeam() {
        return homeTeam;
    }
    public TeamDTO getAwayTeam() {
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
