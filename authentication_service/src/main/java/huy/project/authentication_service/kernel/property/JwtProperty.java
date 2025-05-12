package huy.project.authentication_service.kernel.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.security.jwt")
@Getter
@Setter
public class JwtProperty {
    private Long expiration;
    private Long accessTokenExpiration;
    private Long refreshTokenExpiration;
    private String secret;
    private String header;
    private String prefix;
    
    public Long getAccessTokenExpiration() {
        return accessTokenExpiration != null ? accessTokenExpiration : expiration;
    }
    
    public Long getRefreshTokenExpiration() {
        return refreshTokenExpiration != null ? refreshTokenExpiration : (expiration * 7); // Default to 7x regular expiration
    }
}
