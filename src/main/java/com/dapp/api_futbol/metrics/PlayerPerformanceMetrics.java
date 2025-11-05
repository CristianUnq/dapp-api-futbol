package com.dapp.api_futbol.metrics;

public class PlayerPerformanceMetrics {

    private final int matchesPlayed;
    private final double goalsPerMatch;
    private final double assistsPerMatch;
    private final double goalContributionsPerMatch;
    private final double normalizedGoalContrib;
    private final double performanceIndex;
    private final double attackImpact;

    public PlayerPerformanceMetrics(int matchesPlayed,
                                    double goalsPerMatch,
                                    double assistsPerMatch,
                                    double goalContributionsPerMatch,
                                    double normalizedGoalContrib,
                                    double performanceIndex,
                                    double attackImpact) {
        this.matchesPlayed = matchesPlayed;
        this.goalsPerMatch = goalsPerMatch;
        this.assistsPerMatch = assistsPerMatch;
        this.goalContributionsPerMatch = goalContributionsPerMatch;
        this.normalizedGoalContrib = normalizedGoalContrib;
        this.performanceIndex = performanceIndex;
        this.attackImpact = attackImpact;
    }

    public int getMatchesPlayed() { return matchesPlayed; }
    public double getGoalsPerMatch() { return goalsPerMatch; }
    public double getAssistsPerMatch() { return assistsPerMatch; }
    public double getGoalContributionsPerMatch() { return goalContributionsPerMatch; }
    public double getNormalizedGoalContrib() { return normalizedGoalContrib; }
    public double getPerformanceIndex() { return performanceIndex; }
    public double getAttackImpact() { return attackImpact; }

    @Override
    public String toString() {
        return "PlayerPerformanceMetrics{" +
                "matchesPlayed=" + matchesPlayed +
                ", goalsPerMatch=" + goalsPerMatch +
                ", assistsPerMatch=" + assistsPerMatch +
                ", goalContributionsPerMatch=" + goalContributionsPerMatch +
                ", normalizedGoalContrib=" + normalizedGoalContrib +
                ", performanceIndex=" + performanceIndex +
                ", attackImpact=" + attackImpact +
                '}';
    }
}