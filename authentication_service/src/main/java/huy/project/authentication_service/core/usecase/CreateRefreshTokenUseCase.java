package huy.project.authentication_service.core.usecase;

import huy.project.authentication_service.core.domain.entity.RefreshTokenEntity;
import huy.project.authentication_service.core.domain.entity.UserEntity;
import huy.project.authentication_service.core.port.IRefreshTokenPort;
import huy.project.authentication_service.kernel.property.JwtProperty;
import huy.project.authentication_service.kernel.utils.IPUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.keygen.BytesKeyGenerator;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CreateRefreshTokenUseCase {
    IRefreshTokenPort refreshTokenPort;
    JwtProperty jwtProperty;

    // Use a cryptographically strong key generator
    private static final BytesKeyGenerator DEFAULT_TOKEN_GENERATOR = KeyGenerators.secureRandom(32);
    private static final Base64.Encoder BASE64_ENCODER = Base64.getUrlEncoder().withoutPadding();

    @Transactional(rollbackFor = Exception.class)
    public RefreshTokenEntity createRefreshToken(UserEntity user) {
        String tokenValue = generateTokenValue(user);

        RefreshTokenEntity refreshToken = RefreshTokenEntity.builder()
                .userId(user.getId())
                .token(tokenValue)
                .expiryDate(Instant.now().plusMillis(jwtProperty.getRefreshTokenExpiration()))
                .build();

        // Capture device info and IP
        try {
            HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(
                    RequestContextHolder.getRequestAttributes())).getRequest();
            refreshToken.setDeviceInfo(request.getHeader("User-Agent"));
            refreshToken.setIpAddress(IPUtils.getClientIp(request));
        } catch (Exception e) {
            log.warn("Could not capture device info", e);
        }

        return refreshTokenPort.save(refreshToken);
    }

    /**
     * Generates a cryptographically secure token value that includes user-specific information
     * and is hashed for additional security
     *
     * @param user The user for whom the token is generated
     * @return A secure token string
     */
    private String generateTokenValue(UserEntity user) {
        try {
            // Generate a secure random byte array
            byte[] randomBytes = DEFAULT_TOKEN_GENERATOR.generateKey();

            // Add user-specific information to make the token tied to the user
            // This helps prevent token reuse across users, even if somehow leaked
            String userSpecificData = user.getId() + ":" + user.getEmail() + ":" + System.currentTimeMillis();

            // Combine random bytes with user data
            String combinedData = BASE64_ENCODER.encodeToString(randomBytes) + userSpecificData;

            // Hash the combined data for additional security
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest(combinedData.getBytes(StandardCharsets.UTF_8));

            // Convert to URL-safe Base64 string
            return BASE64_ENCODER.encodeToString(hashedBytes);

        } catch (NoSuchAlgorithmException e) {
            log.error("Error generating secure token", e);
            // Fallback to UUID if SHA-256 is somehow not available
            return java.util.UUID.randomUUID().toString();
        }
    }
}
