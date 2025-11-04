package com.dapp.api_futbol.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@EnableScheduling
public class SchedulerConfig {

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        // allow a small pool so scheduled tasks can overlap if one run is slow
        scheduler.setPoolSize(4);
        scheduler.setThreadNamePrefix("scraper-scheduler-");
        scheduler.initialize();
        return scheduler;
    }
}
