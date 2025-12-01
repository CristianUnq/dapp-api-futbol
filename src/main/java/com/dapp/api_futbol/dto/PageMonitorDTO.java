package com.dapp.api_futbol.dto;

import java.time.Instant;

public class PageMonitorDTO {

    private long uptimeSeconds;
    private double requestsPerMinute;
    private double errorRate;
    private Instant lastUpdated;

    public PageMonitorDTO() {
    }

    public PageMonitorDTO(long uptimeSeconds, double requestsPerMinute, double errorRate, Instant lastUpdated) {
        this.uptimeSeconds = uptimeSeconds;
        this.requestsPerMinute = requestsPerMinute;
        this.errorRate = errorRate;
        this.lastUpdated = lastUpdated;
    }

    public long getUptimeSeconds() {
        return uptimeSeconds;
    }

    public void setUptimeSeconds(long uptimeSeconds) {
        this.uptimeSeconds = uptimeSeconds;
    }

    public double getRequestsPerMinute() {
        return requestsPerMinute;
    }

    public void setRequestsPerMinute(double requestsPerMinute) {
        this.requestsPerMinute = requestsPerMinute;
    }

    public double getErrorRate() {
        return errorRate;
    }

    public void setErrorRate(double errorRate) {
        this.errorRate = errorRate;
    }

    public Instant getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Instant lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
