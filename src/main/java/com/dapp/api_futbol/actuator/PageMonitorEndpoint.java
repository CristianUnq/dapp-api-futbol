package com.dapp.api_futbol.actuator;

import com.dapp.api_futbol.dto.PageMonitorDTO;
import com.dapp.api_futbol.service.PageMonitoringService;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

@Endpoint(id = "page-monitor")
@Component
public class PageMonitorEndpoint {

    private final PageMonitoringService monitoringService;

    public PageMonitorEndpoint(PageMonitoringService monitoringService) {
        this.monitoringService = monitoringService;
    }

    @ReadOperation
    public PageMonitorDTO monitor() {
        return monitoringService.getPageMonitor();
    }
}
