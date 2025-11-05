package com.dapp.api_futbol.metrics;

import com.dapp.api_futbol.dto.PlayerPerformanceDTO;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

class PlayerPerformanceCalculatorTest {

    @Test
    void computeMetrics_ShouldReturnCorrectValues_ForProvidedPlayerData() {
        PlayerPerformanceDTO mockDto = new PlayerPerformanceDTO();
        mockDto.setPlayerId(1L);
        mockDto.setPlayerName("Lionel Messi");
        mockDto.setTeamName("Inter Miami");
        mockDto.setAverageRating(8.5);
        mockDto.setTotalGoals(15);
        mockDto.setTotalAssists(10);
        mockDto.setLastMatches(new ArrayList<>());

        PlayerPerformanceMetrics metrics = PlayerPerformanceCalculator.computeMetrics(mockDto, null);

        assertNotNull(metrics);
        assertEquals(20, metrics.getMatchesPlayed());
        assertEquals(0.75, metrics.getGoalsPerMatch(), 0.001);
        assertEquals(0.50, metrics.getAssistsPerMatch(), 0.001);
        assertEquals(1.25, metrics.getGoalContributionsPerMatch(), 0.001);
        assertEquals(0.833, metrics.getNormalizedGoalContrib(), 0.01);
        assertEquals(8.433, metrics.getPerformanceIndex(), 0.01);
        assertEquals(4.5, metrics.getAttackImpact(), 0.01);
    }
}