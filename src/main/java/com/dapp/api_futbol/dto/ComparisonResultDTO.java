package com.dapp.api_futbol.dto;

public class ComparisonResultDTO {
    private String teamAName;
    private String teamBName;
    private TeamStatsDTO teamA;
    private TeamStatsDTO teamB;

    public String getTeamAName() { return teamAName; }
    public void setTeamAName(String teamAName) { this.teamAName = teamAName; }

    public String getTeamBName() { return teamBName; }
    public void setTeamBName(String teamBName) { this.teamBName = teamBName; }

    public TeamStatsDTO getTeamA() { return teamA; }
    public void setTeamA(TeamStatsDTO teamA) { this.teamA = teamA; }

    public TeamStatsDTO getTeamB() { return teamB; }
    public void setTeamB(TeamStatsDTO teamB) { this.teamB = teamB; }


}
