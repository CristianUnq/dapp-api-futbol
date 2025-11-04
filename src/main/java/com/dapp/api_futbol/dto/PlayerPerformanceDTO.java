package com.dapp.api_futbol.dto;

import lombok.Data;
import java.util.List;

@Data
public class PlayerPerformanceDTO {
    private Long playerId;
    private String playerName;
    private String teamName;
    private Double averageRating;
    private Integer totalGoals;
    private Integer totalAssists;
    private List<MatchPerformanceDTO> lastMatches;

    @Data
    public static class MatchPerformanceDTO {
        private Long matchId;
        private String opponent;
        private String date;
        private Integer goals;
        private Integer assists;
        private Double rating;
    }
}