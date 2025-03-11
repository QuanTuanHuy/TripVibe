package huy.project.search_service.kernel.utils;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import huy.project.search_service.kernel.property.JwtProperty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Date;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtUtils {

    private final JwtProperty jwtProperty;

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
}
