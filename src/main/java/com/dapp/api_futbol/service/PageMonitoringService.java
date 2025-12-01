package com.dapp.api_futbol.service;

import com.dapp.api_futbol.dto.PageMonitorDTO;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.time.Instant;

@Service
public class PageMonitoringService {

    private final MeterRegistry meterRegistry;

    public PageMonitoringService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public PageMonitorDTO getPageMonitor() {
        long uptimeMillis = ManagementFactory.getRuntimeMXBean().getUptime();
        long uptimeSeconds = uptimeMillis / 1000;

        // Total requests observed by the http.server.requests timer
        Timer totalTimer = meterRegistry.find("http.server.requests").timer();
        long totalCount = totalTimer != null ? totalTimer.count() : 0L;

        // Errors (5xx) timer if available
        Timer errorsTimer = meterRegistry.find("http.server.requests").tag("status", "500").timer();
        long errorCount = errorsTimer != null ? errorsTimer.count() : 0L;

        double errorRate = totalCount > 0 ? (double) errorCount / (double) totalCount : 0.0;

        double uptimeMinutes = uptimeSeconds > 0 ? ((double) uptimeSeconds) / 60.0 : 1.0;
        double requestsPerMinute = uptimeMinutes > 0 ? ((double) totalCount) / uptimeMinutes : 0.0;

        PageMonitorDTO dto = new PageMonitorDTO(uptimeSeconds, requestsPerMinute, errorRate, Instant.now());
        return dto;
    }
}
