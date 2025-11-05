package com.dapp.api_futbol.metrics;

import com.dapp.api_futbol.dto.PlayerPerformanceDTO;

public class PlayerPerformanceCalculator {

    private static final int DEFAULT_MATCHES = 20;
    private static final double EXPECTED_MAX_GOAL_CONTRIB = 1.5;
    private static final double WEIGHT_RATING = 0.6;
    private static final double WEIGHT_GOAL_CONTRIB = 0.4;
    private static final double GOAL_WEIGHT = 4.0;
    private static final double ASSIST_WEIGHT = 3.0;

    public static PlayerPerformanceMetrics computeMetrics(PlayerPerformanceDTO dto) {
        int matches = (dto.getLastMatches() != null && !dto.getLastMatches().isEmpty())
                ? dto.getLastMatches().size()
                : DEFAULT_MATCHES;

        double goals = safe(dto.getTotalGoals());
        double assists = safe(dto.getTotalAssists());
        double rating = safe(dto.getAverageRating());

        double goalsPM = goals / matches;
        double assistsPM = assists / matches;
        double contribPM = goalsPM + assistsPM;

        double normContrib = clamp(contribPM / EXPECTED_MAX_GOAL_CONTRIB, 0, 1);

        double weightedScore = WEIGHT_RATING * (rating / 10.0)
                             + WEIGHT_GOAL_CONTRIB * normContrib;

        double performanceIndex = weightedScore * 10.0;

        double attackImpact = (goals * GOAL_WEIGHT + assists * ASSIST_WEIGHT) / matches;

        return new PlayerPerformanceMetrics(
                matches,
                round(goalsPM, 3),
                round(assistsPM, 3),
                round(contribPM, 3),
                round(normContrib, 3),
                round(performanceIndex, 3),
                round(attackImpact, 3)
        );
    }

    // overload para compatibilidad con el test
    public static PlayerPerformanceMetrics computeMetrics(PlayerPerformanceDTO dto, Object matchDetails) {
        return computeMetrics(dto);
    }

    private static double safe(Number n) {
        return n == null ? 0.0 : n.doubleValue();
    }

    private static double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(v, max));
    }

    private static double round(double v, int d) {
        double s = Math.pow(10, d);
        return Math.round(v * s) / s;
    }
}