package com.booking.inventory.infrastructure.redis;

import com.booking.inventory.domain.model.RoomLock;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoomLockRepository extends CrudRepository<RoomLock, String> {
    
    List<RoomLock> findByRoomId(Long roomId);
    
    List<RoomLock> findBySessionId(String sessionId);
    
    Optional<RoomLock> findByRoomIdAndDate(Long roomId, LocalDate date);
    
    void deleteBySessionId(String sessionId);
}
