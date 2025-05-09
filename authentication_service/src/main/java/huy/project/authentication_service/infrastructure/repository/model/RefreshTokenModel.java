package huy.project.authentication_service.infrastructure.repository.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "refresh_tokens")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RefreshTokenModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "user_id")
    Long userId;

    @Column(name = "token")
    String token;

    @Column(name = "expiry_date")
    Instant expiryDate;

    @Column(name = "device_info")
    String deviceInfo;

    @Column(name = "ip_address")
    String ipAddress;
}
