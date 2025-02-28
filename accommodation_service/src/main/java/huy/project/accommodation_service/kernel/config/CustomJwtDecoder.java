package huy.project.accommodation_service.kernel.config;

import huy.project.accommodation_service.kernel.property.JwtProperty;
import huy.project.accommodation_service.kernel.utils.JwtUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomJwtDecoder implements JwtDecoder {

    private final JwtProperty jwtProperty;
    private final JwtUtils jwtUtils;

    private NimbusJwtDecoder nimbusJwtDecoder;

    @PostConstruct
    public void init() {
        SecretKeySpec spec = new SecretKeySpec(jwtProperty.getSecret().getBytes(), "HS512");
        nimbusJwtDecoder = NimbusJwtDecoder.withSecretKey(spec)
                .macAlgorithm(MacAlgorithm.HS512)
                .build();
    }

    @Override
    public Jwt decode(String token) throws JwtException {
        if (!validateToken(token)) {
            throw new JwtException("Token is invalid");
        }
        return nimbusJwtDecoder.decode(token);
    }

    public Boolean validateToken(String token) {
        try {
            Date expirationTime = jwtUtils.extractExpiration(token);
            if (expirationTime.before(new Date())) {
                return false;
            }
        } catch (Exception e) {
            log.error("Error when validate token, {}", e.getMessage());
            return false;
        }
        return true;
    }
}