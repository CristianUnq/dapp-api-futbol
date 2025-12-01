package com.dapp.api_futbol.dto;

public class MatchPredictionDTO {

    private String localTeamName;
    private String visitorTeamName;
    private double localWinProbability;
    private double visitorWinProbability;
    private double drawProbability;
    private String predictionSummary;

    // Getters and Setters

    public String getLocalTeamName() {
        return localTeamName;
    }

    public void setLocalTeamName(String localTeamName) {
        this.localTeamName = localTeamName;
    }

    public String getVisitorTeamName() {
        return visitorTeamName;
    }

    public void setVisitorTeamName(String visitorTeamName) {
        this.visitorTeamName = visitorTeamName;
    }

    public double getLocalWinProbability() {
        return localWinProbability;
    }

    public void setLocalWinProbability(double localWinProbability) {
        this.localWinProbability = localWinProbability;
    }

    public double getVisitorWinProbability() {
        return visitorWinProbability;
    }

    public void setVisitorWinProbability(double visitorWinProbability) {
        this.visitorWinProbability = visitorWinProbability;
    }

    public double getDrawProbability() {
        return drawProbability;
    }

    public void setDrawProbability(double drawProbability) {
        this.drawProbability = drawProbability;
    }

    public String getPredictionSummary() {
        return predictionSummary;
    }

    public void setPredictionSummary(String predictionSummary) {
        this.predictionSummary = predictionSummary;
    }
}
