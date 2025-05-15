package com.booking.inventory.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * Redis entity to manage temporary room locks
 */
@RedisHash("room_lock")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomLock {
    
    @Id
    private String id;
    
    @Indexed
    private Long roomId;
    
    @Indexed
    private String sessionId;
    
    private LocalDate date;
    
    private LocalDateTime createdAt;
    
    @TimeToLive(unit = TimeUnit.MINUTES)
    private Long timeToLive; // TTL in minutes (15-30 minutes)
}
