package com.dapp.api_futbol.dto;

public class TeamStatsDTO {
    private Long id;
    private String name;
    private Integer points;
    private Integer goalsInFavor;
    private Integer goalsAgainst;
    private Integer goalsDifference;
    private Double zScorePoints;

    public TeamStatsDTO() {

    }

    public TeamStatsDTO(Long id, String name, Integer points, Integer goalsInFavor, Integer goalsAgainst, Integer goalsDifference) {
        this.id = id;
        this.name = name;
        this.points = points;
        this.goalsInFavor = goalsInFavor;
        this.goalsAgainst = goalsAgainst;
        this.goalsDifference = goalsDifference;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getPoints() { return points; }
    public void setPoints(Integer points) { this.points = points; }
    public Integer getGoalsInFavor() { return goalsInFavor; }
    public void setGoalsInFavor(Integer goalsInFavor) { this.goalsInFavor = goalsInFavor; }
    public Integer getGoalsAgainst() { return goalsAgainst; }
    public void setGoalsAgainst(Integer goalsAgainst) { this.goalsAgainst = goalsAgainst; }
    public Integer getGoalsDifference() { return goalsDifference; }
    public void setGoalsDifference(Integer goalsDifference) { this.goalsDifference = goalsDifference; }
    public Double getzScorePoints() { return zScorePoints; }
    public void setzScorePoints(Double zScorePoints) { this.zScorePoints = zScorePoints; }
}
