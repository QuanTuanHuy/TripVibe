package huy.project.payment_service.kernel.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

@Slf4j
public class AuthenUtils {
    public static Long getCurrentUserId() {
        try {
            Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getCredentials();
            return Long.parseLong(jwt.getSubject());
        } catch (Exception e) {
            log.info("Error when get userId from token: {}", e.getMessage());
            return null;
        }
    }
}
