package com.booking.inventory.infrastructure.config;

import com.booking.inventory.domain.service.RoomAvailabilityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class SchedulingConfig {
    
    private final RoomAvailabilityService roomAvailabilityService;
    
    /**
     * Scheduled task to release expired locks
     * Runs every 5 minutes
     */
    @Scheduled(fixedRate = 300000) // 5 minutes in milliseconds
    public void releaseExpiredLocks() {
        log.info("Running scheduled task to release expired locks");
        roomAvailabilityService.releaseExpiredLocks();
    }
}
