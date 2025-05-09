package huy.project.authentication_service.kernel.utils;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import huy.project.authentication_service.core.domain.constant.ErrorCode;
import huy.project.authentication_service.core.domain.entity.PrivilegeEntity;
import huy.project.authentication_service.core.domain.entity.RoleEntity;
import huy.project.authentication_service.core.domain.entity.UserEntity;
import huy.project.authentication_service.core.exception.AppException;
import huy.project.authentication_service.kernel.property.JwtProperty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtUtils {

    private final JwtProperty jwtProperty;

    /**
     * Generate access token with optimized claims
     * @param user User entity
     * @return JWT access token
     */
    public String generateAccessToken(UserEntity user) {
        JWSHeader jwsHeader = new JWSHeader.Builder(JWSAlgorithm.HS512)
                .type(JOSEObjectType.JWT)
                .build();

        // Create claims with only essential information
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getId().toString())
                .issuer("quantuanhuy")
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now().plusMillis(jwtProperty.getAccessTokenExpiration()).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                // Optimized permissions approach
//                .claim("roles", buildRolesList(user))
//                .claim("permissions", buildEssentialPermissionsList(user))
                .claim("scope", buildScope(user))
                .claim("email", user.getEmail())
                .claim("type", "access")
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(jwsHeader, payload);

        try {
            jwsObject.sign(new MACSigner(jwtProperty.getSecret().getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Error when sign JWT", e);
            throw new AppException(ErrorCode.GENERATE_TOKEN_FAILED);
        }
    }

    public JWTClaimsSet extractAllClaims(String token) {
        try {
            JWSVerifier jwsVerifier = new MACVerifier(jwtProperty.getSecret().getBytes());

            SignedJWT signedJWT = SignedJWT.parse(token);

            var verified = signedJWT.verify(jwsVerifier);
            if (!verified) {
                throw new IllegalArgumentException("Token is invalid");
            }

            return signedJWT.getJWTClaimsSet();
        } catch (JOSEException | ParseException e) {
            log.error("Error when extract claims from token, {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public <T> T extractClaim(String token, Function<JWTClaimsSet, T> claimsResolver) {
        final JWTClaimsSet claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, JWTClaimsSet::getExpirationTime);
    }

    public Long extractUserId(String token) {
        String subject = extractClaim(token, JWTClaimsSet::getSubject);
        return Long.parseLong(subject);
    }
    
    public boolean isTokenExpired(String token) {
        Date expiration = extractExpiration(token);
        return expiration.before(new Date());
    }

    private String buildScope(UserEntity user) {
        return user.getRoles().stream()
                .map(RoleEntity::getName)
                .reduce((s1, s2) -> s1 + " " + s2)
                .orElse("");
    }
    
    private List<String> buildRolesList(UserEntity user) {
        return user.getRoles().stream()
                .map(RoleEntity::getName)
                .collect(Collectors.toList());
    }
    
    private List<String> buildEssentialPermissionsList(UserEntity user) {
        // Extract only essential permissions to minimize token size
        Set<String> permissions = new HashSet<>();
        
        for (RoleEntity role : user.getRoles()) {
            for (PrivilegeEntity privilege : role.getPrivileges()) {
                permissions.add(privilege.getName());
            }
        }
        
        return new ArrayList<>(permissions);
    }
}
