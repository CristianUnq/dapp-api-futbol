package com.dapp.api_futbol.metrics;

import com.dapp.api_futbol.model.Match;
import com.dapp.api_futbol.model.Team;
import com.dapp.api_futbol.repository.MatchRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ComparisonCalculationTest {

    @Mock
    MatchRepository matchRepository;

    @Test
    void computeHeadToHead_counts() {
        ComparisonCalculation calc = new ComparisonCalculation(matchRepository);

        Team a = new Team();
        a.setName("Team A");
        Team b = new Team();
        b.setName("Team B");

        Match m1 = new Match();
        m1.setHomeTeamName("Team A");
        m1.setAwayTeamName("Team B");
        m1.setHomeScore(2);
        m1.setAwayScore(1);
        m1.setMatchDate(LocalDateTime.now().minusDays(10));

        Match m2 = new Match();
        m2.setHomeTeamName("Team B");
        m2.setAwayTeamName("Team A");
        m2.setHomeScore(0);
        m2.setAwayScore(0);
        m2.setMatchDate(LocalDateTime.now().minusDays(5));

        when(matchRepository.findHeadToHead("Team A", "Team B")).thenReturn(Arrays.asList(m1, m2));

        String summary = calc.computeHeadToHead(a, b);
        assertNotNull(summary);
        assertTrue(summary.contains("wins=1"));
        assertTrue(summary.contains("wins=0"));
        assertTrue(summary.contains("draws=1"));
    }

    @Test
    void computeWinProbabilities_withHistory_and_form() {
        ComparisonCalculation calc = new ComparisonCalculation(matchRepository);

        Team a = new Team();
        a.setName("Team A");
        a.setPuntos(30);
        Team b = new Team();
        b.setName("Team B");
        b.setPuntos(10);

        // head-to-head: 2 wins A, 1 win B, 1 draw
        Match m1 = new Match(); m1.setHomeTeamName("Team A"); m1.setAwayTeamName("Team B"); m1.setHomeScore(2); m1.setAwayScore(1);
        Match m2 = new Match(); m2.setHomeTeamName("Team B"); m2.setAwayTeamName("Team A"); m2.setHomeScore(0); m2.setAwayScore(1);
        Match m3 = new Match(); m3.setHomeTeamName("Team A"); m3.setAwayTeamName("Team B"); m3.setHomeScore(1); m3.setAwayScore(0);
        Match m4 = new Match(); m4.setHomeTeamName("Team B"); m4.setAwayTeamName("Team A"); m4.setHomeScore(0); m4.setAwayScore(0);

        when(matchRepository.findHeadToHead("Team A", "Team B")).thenReturn(Arrays.asList(m1, m2, m3, m4));

        double[] probs = calc.computeWinProbabilities(a, b);
        assertEquals(3, probs.length);
        double sum = probs[0] + probs[1] + probs[2];
        assertEquals(1.0, sum, 1e-6);
        // Expect pA > pB because A has better history and more points
        assertTrue(probs[0] > probs[1]);
    }

    @Test
    void computeExpectedGoalDifference_averageGoals() {
        ComparisonCalculation calc = new ComparisonCalculation(matchRepository);

        Team a = new Team(); a.setName("Team A");
        Team b = new Team(); b.setName("Team B");

        Match ma1 = new Match(); ma1.setHomeTeamName("Team A"); ma1.setAwayTeamName("Other"); ma1.setHomeScore(2); ma1.setAwayScore(0);
        Match ma2 = new Match(); ma2.setHomeTeamName("Other"); ma2.setAwayTeamName("Team A"); ma2.setHomeScore(1); ma2.setAwayScore(3);

        Match mb1 = new Match(); mb1.setHomeTeamName("Team B"); mb1.setAwayTeamName("Other"); mb1.setHomeScore(1); mb1.setAwayScore(0);
        Match mb2 = new Match(); mb2.setHomeTeamName("Other"); mb2.setAwayTeamName("Team B"); mb2.setHomeScore(2); mb2.setAwayScore(0);

        when(matchRepository.findMatchesByTeamName("Team A")).thenReturn(Arrays.asList(ma1, ma2));
        when(matchRepository.findMatchesByTeamName("Team B")).thenReturn(Arrays.asList(mb1, mb2));

        double diff = calc.computeExpectedGoalDifference(a, b);
        // Team A goals per match: (2 + 3) / 2 = 2.5; Team B: (1 + 0) / 2 = 0.5 -> diff = 2.0
        assertEquals(2.0, diff, 1e-6);
    }

    @Test
    void computeHeadToHead_noMatches_returnsMessage() {
        ComparisonCalculation calc = new ComparisonCalculation(matchRepository);
        Team a = new Team(); a.setName("NoTeamA");
        Team b = new Team(); b.setName("NoTeamB");
        when(matchRepository.findHeadToHead("NoTeamA", "NoTeamB")).thenReturn(Collections.emptyList());
        String summary = calc.computeHeadToHead(a, b);
        assertNotNull(summary);
        assertTrue(summary.toLowerCase().contains("no historical"));
    }

    @Test
    void computeWinProbabilities_noHistory_usesPointsOnly() {
        ComparisonCalculation calc = new ComparisonCalculation(matchRepository);
        Team a = new Team(); a.setName("A"); a.setPuntos(40);
        Team b = new Team(); b.setName("B"); b.setPuntos(20);
        when(matchRepository.findHeadToHead("A", "B")).thenReturn(Collections.emptyList());
        double[] probs = calc.computeWinProbabilities(a, b);
        assertEquals(1.0, probs[0] + probs[1] + probs[2], 1e-6);
        // pA should be roughly double pB
        assertTrue(probs[0] > probs[1]);
    }

    @Test
    void computeWinProbabilities_zeroPoints_fallbackEqual() {
        ComparisonCalculation calc = new ComparisonCalculation(matchRepository);
        Team a = new Team(); a.setName("A"); a.setPuntos(0);
        Team b = new Team(); b.setName("B"); b.setPuntos(0);
        when(matchRepository.findHeadToHead("A", "B")).thenReturn(Collections.emptyList());
        double[] probs = calc.computeWinProbabilities(a, b);
        assertEquals(1.0, probs[0] + probs[1] + probs[2], 1e-6);
        // Should be approximately equal distribution
        assertEquals(probs[0], probs[1], 0.05);
    }

    @Test
    void computeExpectedGoalDifference_noMatches_returnsZero() {
        ComparisonCalculation calc = new ComparisonCalculation(matchRepository);
        Team a = new Team(); a.setName("X");
        Team b = new Team(); b.setName("Y");
        when(matchRepository.findMatchesByTeamName("X")).thenReturn(Collections.emptyList());
        when(matchRepository.findMatchesByTeamName("Y")).thenReturn(Collections.emptyList());
        double diff = calc.computeExpectedGoalDifference(a, b);
        assertEquals(0.0, diff, 1e-6);
    }

    @Test
    void computeHeadToHead_ignoresNullScores() {
        ComparisonCalculation calc = new ComparisonCalculation(matchRepository);
        Team a = new Team(); a.setName("Team A");
        Team b = new Team(); b.setName("Team B");

        Match m1 = new Match(); m1.setHomeTeamName("Team A"); m1.setAwayTeamName("Team B"); m1.setHomeScore(null); m1.setAwayScore(null);
        Match m2 = new Match(); m2.setHomeTeamName("Team A"); m2.setAwayTeamName("Team B"); m2.setHomeScore(1); m2.setAwayScore(0);
        when(matchRepository.findHeadToHead("Team A", "Team B")).thenReturn(Arrays.asList(m1, m2));
        String summary = calc.computeHeadToHead(a, b);
        // Only one decisive match -> winsA=1, winsB=0, draws=0
        assertTrue(summary.contains("wins=1"));
        assertTrue(summary.contains("draws=0"));
    }
}
