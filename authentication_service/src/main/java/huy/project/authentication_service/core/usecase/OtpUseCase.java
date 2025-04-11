package huy.project.authentication_service.core.usecase;

import huy.project.authentication_service.core.domain.constant.OtpType;
import huy.project.authentication_service.core.domain.dto.OtpDto;
import huy.project.authentication_service.core.port.ICachePort;
import huy.project.authentication_service.kernel.utils.CacheUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OtpUseCase {
    ICachePort cachePort;

    public OtpDto generateOtpForRegister(String email) {
        String otp = generateRandomOtp(6);
        log.info("Generate OTP for {} is {}", email, otp);
        Long expiryTime = Instant.now().plusSeconds(300).toEpochMilli(); // 5 minutes expiry
        OtpDto otpDto = OtpDto.builder()
                .otp(otp)
                .type(OtpType.REGISTER)
                .expiredAt(expiryTime)
                .build();

        // set to cache
        cachePort.setToCache(CacheUtils.buildCacheKeyOtpRegister(email), otpDto, expiryTime);

        return otpDto;
    }

    public boolean verifyOtpForRegister(String email, String otp) {
        OtpDto cachedOtp = cachePort.getFromCache(CacheUtils.buildCacheKeyOtpRegister(email), OtpDto.class);
        if (cachedOtp == null || !cachedOtp.getOtp().equals(otp)) {
            log.info("OTP verification failed for {}", email);
            return false;
        }

        // Invalidate the OTP after successful verification
        cachePort.deleteFromCache(CacheUtils.buildCacheKeyOtpRegister(email));

        return true;
    }

    private String generateRandomOtp(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < length; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }
}
