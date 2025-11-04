package com.dapp.api_futbol.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String homeTeamName;
    private String awayTeamName;

    private LocalDateTime matchDate;

    private Integer homeScore;
    private Integer awayScore;
    private Integer footballDataId;
    private String status;

    // Getters needed by services
    public LocalDateTime getDate() {
        return matchDate;
    }

    // Backward-compatible accessor used in other services
    public LocalDateTime getMatchTime() {
        return matchDate;
    }

    public Team getHomeTeam() {
        // We can't actually return a Team object since we only store names
        return null;
    }

    public Team getAwayTeam() {
        // We can't actually return a Team object since we only store names
        return null;
    }

    public String getName(boolean isHome) {
        return isHome ? homeTeamName : awayTeamName;
    }

}