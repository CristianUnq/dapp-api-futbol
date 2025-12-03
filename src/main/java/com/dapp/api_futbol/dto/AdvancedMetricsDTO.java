package com.dapp.api_futbol.dto;

import java.util.List;

public class AdvancedMetricsDTO {
    // Advanced metrics no longer expose numeric summary stats (count/mean/median/stddev/min/max).
    // Instead the service returns per-team categorical ratings (S..F) inside TeamMetricDTO.
    private List<TeamMetricDTO> teams;

    // No league-level numerical stats getters/setters remain.

    public List<TeamMetricDTO> getTeams() { return teams; }
    public void setTeams(List<TeamMetricDTO> teams) { this.teams = teams; }
}
