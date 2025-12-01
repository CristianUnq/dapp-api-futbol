package com.dapp.api_futbol.dto;

public class ComparisonResultDTO {
    private String teamAName;
    private String teamBName;
    private TeamMetricDTO teamA;
    private TeamMetricDTO teamB;
    private String headToHeadSummary;
    private double probabilityTeamAWin;
    private double probabilityTeamBWin;
    private double probabilityDraw;
    private double expectedGoalDifference;

    public String getTeamAName() { return teamAName; }
    public void setTeamAName(String teamAName) { this.teamAName = teamAName; }

    public String getTeamBName() { return teamBName; }
    public void setTeamBName(String teamBName) { this.teamBName = teamBName; }

    public TeamMetricDTO getTeamA() { return teamA; }
    public void setTeamA(TeamMetricDTO teamA) { this.teamA = teamA; }

    public TeamMetricDTO getTeamB() { return teamB; }
    public void setTeamB(TeamMetricDTO teamB) { this.teamB = teamB; }

    public String getHeadToHeadSummary() { return headToHeadSummary; }
    public void setHeadToHeadSummary(String headToHeadSummary) { this.headToHeadSummary = headToHeadSummary; }

    public double getProbabilityTeamAWin() { return probabilityTeamAWin; }
    public void setProbabilityTeamAWin(double probabilityTeamAWin) { this.probabilityTeamAWin = probabilityTeamAWin; }

    public double getProbabilityTeamBWin() { return probabilityTeamBWin; }
    public void setProbabilityTeamBWin(double probabilityTeamBWin) { this.probabilityTeamBWin = probabilityTeamBWin; }

    public double getProbabilityDraw() { return probabilityDraw; }
    public void setProbabilityDraw(double probabilityDraw) { this.probabilityDraw = probabilityDraw; }

    public double getExpectedGoalDifference() { return expectedGoalDifference; }
    public void setExpectedGoalDifference(double expectedGoalDifference) { this.expectedGoalDifference = expectedGoalDifference; }
}
