package com.dapp.api_futbol.metrics;

import com.dapp.api_futbol.dto.TeamMetricDTO;
import com.dapp.api_futbol.model.Team;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AdvancedMetricsCalculation {

    public List<TeamMetricDTO> computeTeamMetrics(List<Team> teams) {
        List<TeamMetricDTO> summary = new ArrayList<>();
        if (teams == null || teams.isEmpty()) return summary;

        List<Double> finishingRaw = new ArrayList<>();
        List<Double> longShotRaw = new ArrayList<>();
        List<Double> comebackRaw = new ArrayList<>();
        List<Double> chanceCreationRaw = new ArrayList<>();
        List<Double> protectRaw = new ArrayList<>();
        List<Double> controlRaw = new ArrayList<>();
        List<Double> aerialsRaw = new ArrayList<>();

        for (Team t : teams) {
            TeamMetricDTO dto = new TeamMetricDTO();
            dto.setId(Long.valueOf(t.getId()));
            dto.setName(t.getName());

            int pj = t.getMatchsPlayed() != null && t.getMatchsPlayed() > 0 ? t.getMatchsPlayed() : 1;
            double gf = t.getGoalsInFavor() != null ? t.getGoalsInFavor() : 0.0;
            double ga = t.getGoalsAgainst() != null ? t.getGoalsAgainst() : 0.0;
            double points = t.getPoints() != null ? t.getPoints() : 0.0;
            double diff = t.getGoalsDifference() != null ? t.getGoalsDifference() : 0.0;

            double golesPerMatch = gf / pj;
            double tirosPp = parseDecimal(t.getShotsPerMatch());
            double possession = parsePercent(t.getPossesion());
            double passAcc = parsePercent(t.getPassAccuracy());
            double aerials = parseDecimal(t.getAerialDuels());
            double rating = parseDecimal(t.getRating());
            double wins = t.getMatchsWon() != null ? t.getMatchsWon() : 0.0;
            double losses = t.getMatchsLost() != null ? t.getMatchsLost() : 0.0;

            double finishingScore = golesPerMatch * 0.6 + (rating > 0 ? (rating / 10.0) * 0.4 : 0.0);
            double longShotScore = tirosPp;
            double comebackScore = (gf / (ga + 1.0)) * 0.7 + Math.max(0.0, (wins - losses)) * 0.3;
            double chanceCreationScore = possession * 0.4 + passAcc * 0.3 + tirosPp * 0.3;
            double protectScore = (1.0 / (ga + 1.0)) * 0.5 + normalizeValue(diff) * 0.3 + (points / 100.0) * 0.2;
            double controlScore = possession * 0.6 + passAcc * 0.4;
            double aerialsScore = aerials;

            finishingRaw.add(finishingScore);
            longShotRaw.add(longShotScore);
            comebackRaw.add(comebackScore);
            chanceCreationRaw.add(chanceCreationScore);
            protectRaw.add(protectScore);
            controlRaw.add(controlScore);
            aerialsRaw.add(aerialsScore);

            summary.add(dto);
        }

        List<Double> finishingNorm = normalizeList(finishingRaw);
        List<Double> longShotNorm = normalizeList(longShotRaw);
        List<Double> comebackNorm = normalizeList(comebackRaw);
        List<Double> chanceNorm = normalizeList(chanceCreationRaw);
        List<Double> protectNorm = normalizeList(protectRaw);
        List<Double> controlNorm = normalizeList(controlRaw);
        List<Double> aerialsNorm = normalizeList(aerialsRaw);

        for (int i = 0; i < summary.size(); i++) {
            TeamMetricDTO dto = summary.get(i);
            dto.setFinishingOpportunities(mapScoreToGrade(finishingNorm.get(i)));
            dto.setLongRangeShotOpportunities(mapScoreToGrade(longShotNorm.get(i)));
            dto.setComebackAbility(mapScoreToGrade(comebackNorm.get(i)));
            dto.setChanceCreation(mapScoreToGrade(chanceNorm.get(i)));
            dto.setProtectLead(mapScoreToGrade(protectNorm.get(i)));
            dto.setControlOpponentsHalf(mapScoreToGrade(controlNorm.get(i)));
            dto.setAerialDuels(mapScoreToGrade(aerialsNorm.get(i)));
        }

        return summary;
    }

    private static double parsePercent(String s) {
        if (s == null || s.isEmpty()) return 0.0;
        try {
            String cleaned = s.replace("%", "").replace(',', '.').replaceAll("[^0-9.\\-]", "");
            if (cleaned.isEmpty()) return 0.0;
            return Double.parseDouble(cleaned);
        } catch (Exception e) {
            return 0.0;
        }
    }

    private static double parseDecimal(String s) {
        if (s == null || s.isEmpty()) return 0.0;
        try {
            String cleaned = s.replace(',', '.').replaceAll("[^0-9.\\-]", "");
            if (cleaned.isEmpty()) return 0.0;
            return Double.parseDouble(cleaned);
        } catch (Exception e) {
            return 0.0;
        }
    }

    private static double normalizeValue(double v) {
        if (Double.isNaN(v) || Double.isInfinite(v)) return 0.0;
        return Math.tanh(v / 10.0);
    }

    private static List<Double> normalizeList(List<Double> values) {
        double min = Double.MAX_VALUE, max = -Double.MAX_VALUE;
        for (Double d : values) {
            if (d == null) continue;
            if (d < min) min = d;
            if (d > max) max = d;
        }
        List<Double> out = new ArrayList<>();
        if (min == Double.MAX_VALUE || max == -Double.MAX_VALUE) return out;
        if (Math.abs(max - min) < 1e-9) {
            for (int i = 0; i < values.size(); i++) out.add(50.0);
            return out;
        }
        for (Double d : values) {
            double v = d == null ? 0.0 : d;
            double norm = (v - min) / (max - min) * 100.0;
            out.add(norm);
        }
        return out;
    }

    private static String mapScoreToGrade(double score) {
        if (Double.isNaN(score)) return "F";
        if (score >= 95) return "S";
        if (score >= 85) return "A";
        if (score >= 70) return "B";
        if (score >= 55) return "C";
        if (score >= 40) return "D";
        if (score >= 25) return "E";
        return "F";
    }

}
