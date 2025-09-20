package com.dapp.api_futbol.dto;
import com.dapp.api_futbol.model.Team;

public static class PlayerDto {
    private Long id;
    private String name;
    private Team team;
    private Integer matchesPlayed;
    private Integer goals;
    private Integer assists;
    private Doble rating;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Team getTeam() { return team; }
    public void setTeam(Team team) { this.team = team; }
    public Integer getMatchesPlayed() { return matchesPlayed; }
    public void setMatchesPlayed(Integer matchesPlayed) { this.matchesPlayed = matchesPlayed; }
    public Integer getGoals() { return goals; }
    public void setGoals(Integer goals) { this.goals = goals; }
    public Integer getAssists() { return assists; }
    public void setAssists(Integer assists) { this.assists = assists; }
    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }
}
