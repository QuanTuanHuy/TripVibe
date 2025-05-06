//package huy.project.authentication_service.core.usecase;
//
//import huy.project.authentication_service.core.domain.constant.ErrorCode;
//import huy.project.authentication_service.core.domain.entity.RefreshTokenEntity;
//import huy.project.authentication_service.core.exception.AppException;
//import huy.project.authentication_service.core.port.ICachePort;
//import huy.project.authentication_service.kernel.utils.CacheUtils;
//import huy.project.authentication_service.kernel.utils.IPUtils;
//import jakarta.servlet.http.HttpServletRequest;
//import lombok.AccessLevel;
//import lombok.RequiredArgsConstructor;
//import lombok.experimental.FieldDefaults;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.context.request.ServletRequestAttributes;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
//public class SecurityVerificationUseCase {
//    ICachePort cachePort;
//    VerifyRefreshTokenUseCase verifyRefreshTokenUseCase;
//    LogoutUseCase logoutUseCase;
//
//    private static final int MAX_FAILED_ATTEMPTS = 5;
//    private static final long BLOCKED_DURATION_MS = 3600000; // 1 hour
//
//    /**
//     * Verifies if the refresh token is being used from the same device or a suspicious location
//     * @param token The refresh token
//     * @return True if the token passes security checks
//     */
//    public boolean verifyDeviceSecurity(String token) {
//        try {
//            // Get the refresh token entity
//            RefreshTokenEntity refreshToken = verifyRefreshTokenUseCase.verifyRefreshToken(token);
//
//            // Get the current request details
//            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
//            String currentUserAgent = request.getHeader("User-Agent");
//            String currentIp = IPUtils.getClientIp(request);
//
//            // If the token doesn't have device info (older tokens), consider it passed
//            if (refreshToken.getDeviceInfo() == null || refreshToken.getIpAddress() == null) {
//                return true;
//            }
//
//            // Compare device information
//            boolean sameDevice = refreshToken.getDeviceInfo().equals(currentUserAgent);
//            boolean sameNetwork = refreshToken.getIpAddress().equals(currentIp);
//
//            // If everything matches, it's secure
//            if (sameDevice && sameNetwork) {
//                return true;
//            }
//
//            // If device changed but IP is the same, it's probably OK
//            if (!sameDevice && sameNetwork) {
//                return true;
//            }
//
//            // If device is the same but IP changed, could be mobile network change or VPN - allow it
//            if (sameDevice && !sameNetwork) {
//                return true;
//            }
//
//            // Both device and IP changed - this is suspicious
//            // Increment failed attempts counter
//            String cacheKey = CacheUtils.buildCacheKeyFailedTokenAttempts(refreshToken.getUserId().toString());
//            Integer failedAttempts = cachePort.getFromCache(cacheKey, Integer.class);
//
//            if (failedAttempts == null) {
//                failedAttempts = 1;
//            } else {
//                failedAttempts++;
//            }
//
//            cachePort.setToCache(cacheKey, failedAttempts, BLOCKED_DURATION_MS);
//
//            // If too many failed attempts, block the account temporarily
//            if (failedAttempts >= MAX_FAILED_ATTEMPTS) {
//                // Add to blocked list
//                String blockedKey = CacheUtils.buildCacheKeyBlockedUser(refreshToken.getUserId().toString());
//                Map<String, Object> blockInfo = new HashMap<>();
//                blockInfo.put("blockedUntil", System.currentTimeMillis() + BLOCKED_DURATION_MS);
//                blockInfo.put("reason", "Suspicious login activity detected");
//
//                cachePort.setToCache(blockedKey, blockInfo, BLOCKED_DURATION_MS);
//
//                // Revoke all tokens for this user as a security measure
//                logoutUseCase.logoutAll(refreshToken.getUserId());
//
//                throw new AppException(ErrorCode.ACCOUNT_TEMPORARILY_BLOCKED);
//            }
//
//            return false;
//        } catch (Exception e) {
//            log.error("Error during device security verification", e);
//            return false;
//        }
//    }
//}