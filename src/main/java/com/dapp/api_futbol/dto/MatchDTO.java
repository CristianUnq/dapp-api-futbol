package com.dapp.api_futbol.dto;

import java.time.LocalDateTime;
public class MatchDTO {
    private Long id;
    private String homeTeamName;
    private String awayTeamName;
    private LocalDateTime matchDate;
    private String status;
    private Integer homeScore;
    private Integer awayScore;

    public MatchDTO() {}

    public MatchDTO(Long id, String homeTeamName, String awayTeamName, LocalDateTime matchDate, String status, Integer homeScore, Integer awayScore) {
        this.id = id;
        this.homeTeamName = homeTeamName;
        this.awayTeamName = awayTeamName;
        this.matchDate = matchDate;
        this.status = status;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getHomeTeamName() { return homeTeamName; }
    public void setHomeTeamName(String homeTeamName) { this.homeTeamName = homeTeamName; }
    public String getAwayTeamName() { return awayTeamName; }
    public void setAwayTeamName(String awayTeamName) { this.awayTeamName = awayTeamName; }
    public LocalDateTime getMatchDate() { return matchDate; }
    public void setMatchDate(LocalDateTime matchDate) { this.matchDate = matchDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getHomeScore() { return homeScore; }
    public void setHomeScore(Integer homeScore) { this.homeScore = homeScore; }
    public Integer getAwayScore() { return awayScore; }
    public void setAwayScore(Integer awayScore) { this.awayScore = awayScore; }
}
