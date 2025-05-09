package huy.project.authentication_service.core.domain.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RefreshTokenEntity {
    String id;
    Long userId;
    String token;
    Instant expiryDate;
    String deviceInfo;
    String ipAddress;
    
    public boolean isExpired() {
        return Instant.now().isAfter(expiryDate);
    }
}