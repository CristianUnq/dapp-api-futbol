package com.dapp.api_futbol.metrics;

import com.dapp.api_futbol.model.Match;
import com.dapp.api_futbol.model.Team;
import com.dapp.api_futbol.repository.MatchRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ComparisonCalculation {

    private final MatchRepository matchRepository;

    public ComparisonCalculation(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    // Compute a human-readable head-to-head summary between teams
    public String computeHeadToHead(Team a, Team b) {
        if (a == null || b == null) return "insufficient data";
        List<Match> headToHead = matchRepository.findHeadToHead(a.getName(), b.getName());
        if (headToHead == null || headToHead.isEmpty()) return "No historical head-to-head data available";
        int winsA = 0, winsB = 0, draws = 0;
        for (Match m : headToHead) {
            Integer hs = m.getHomeScore();
            Integer as = m.getAwayScore();
            if (hs == null || as == null) continue;
            boolean aIsHome = m.getHomeTeamName().equalsIgnoreCase(a.getName());
            int scoreA = aIsHome ? hs : as;
            int scoreB = aIsHome ? as : hs;
            if (scoreA > scoreB) winsA++;
            else if (scoreB > scoreA) winsB++;
            else draws++;
        }
        return String.format("Head-to-head (%d matches): %s wins=%d, %s wins=%d, draws=%d",
                winsA + winsB + draws, a.getName(), winsA, b.getName(), winsB, draws);
    }

    // Compute win/draw probabilities using head-to-head and overall form
    public double[] computeWinProbabilities(Team a, Team b) {
        double[] headProb = computeHeadProbFromHistory(a, b);
        double[] formProb = computeProbFromPoints(a, b);
        // combine: 70% head-to-head if available, otherwise rely on form
        boolean headAvailable = headProb[0] + headProb[1] + headProb[2] > 0.0;
        double weightHead = headAvailable ? 0.7 : 0.0;
        double weightForm = 1.0 - weightHead;
        double pA = headProb[0] * weightHead + formProb[0] * weightForm;
        double pB = headProb[1] * weightHead + formProb[1] * weightForm;
        double pD = headProb[2] * weightHead + formProb[2] * weightForm;
        // normalize
        double sum = pA + pB + pD;
        if (sum <= 0.0) return new double[]{0.33, 0.33, 0.34};
        return new double[]{pA / sum, pB / sum, pD / sum};
    }

    // Compute expected goal difference (A - B) using average goals for each team
    public double computeExpectedGoalDifference(Team a, Team b) {
        if (a == null || b == null) return 0.0;
        double avgA = averageGoalsFor(a.getName());
        double avgB = averageGoalsFor(b.getName());
        return avgA - avgB;
    }

    // --- helpers ---------------------------------------------------------

    private double[] computeHeadProbFromHistory(Team a, Team b) {
        List<Match> hh = matchRepository.findHeadToHead(a.getName(), b.getName());
        if (hh == null || hh.isEmpty()) return new double[]{0.0, 0.0, 0.0};
        int winsA = 0, winsB = 0, draws = 0, total = 0;
        for (Match m : hh) {
            Integer hs = m.getHomeScore();
            Integer as = m.getAwayScore();
            if (hs == null || as == null) continue;
            total++;
            boolean aIsHome = m.getHomeTeamName().equalsIgnoreCase(a.getName());
            int scoreA = aIsHome ? hs : as;
            int scoreB = aIsHome ? as : hs;
            if (scoreA > scoreB) winsA++;
            else if (scoreB > scoreA) winsB++;
            else draws++;
        }
        if (total == 0) return new double[]{0.0, 0.0, 0.0};
        return new double[]{(double) winsA / total, (double) winsB / total, (double) draws / total};
    }

    private double[] computeProbFromPoints(Team a, Team b) {
        Double pa = a.getPuntos() != null ? a.getPuntos().doubleValue() : 0.0;
        Double pb = b.getPuntos() != null ? b.getPuntos().doubleValue() : 0.0;
        double sum = pa + pb;
        if (sum <= 0.0) return new double[]{0.33, 0.33, 0.34};
        double pA = pa / sum;
        double pB = pb / sum;
        double pD = 1.0 - (pA + pB);
        return new double[]{pA, pB, pD};
    }

    private double averageGoalsFor(String teamName) {
        List<Match> matches = matchRepository.findMatchesByTeamName(teamName);
        if (matches == null || matches.isEmpty()) return 0.0;
        double totalGoals = 0.0;
        int count = 0;
        for (Match m : matches) {
            Integer hs = m.getHomeScore();
            Integer as = m.getAwayScore();
            if (hs == null || as == null) continue;
            if (m.getHomeTeamName().equalsIgnoreCase(teamName)) {
                totalGoals += hs;
            } else {
                totalGoals += as;
            }
            count++;
        }
        return count == 0 ? 0.0 : totalGoals / count;
    }

}
