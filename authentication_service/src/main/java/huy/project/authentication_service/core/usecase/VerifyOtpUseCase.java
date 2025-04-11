package huy.project.authentication_service.core.usecase;

import huy.project.authentication_service.core.domain.constant.ErrorCode;
import huy.project.authentication_service.core.domain.constant.TopicConstant;
import huy.project.authentication_service.core.domain.dto.OtpDto;
import huy.project.authentication_service.core.domain.dto.kafka.CreateTouristMessage;
import huy.project.authentication_service.core.domain.entity.UserEntity;
import huy.project.authentication_service.core.exception.AppException;
import huy.project.authentication_service.core.port.ICachePort;
import huy.project.authentication_service.core.port.IPublisherPort;
import huy.project.authentication_service.core.port.IUserPort;
import huy.project.authentication_service.kernel.utils.CacheUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VerifyOtpUseCase {
    IUserPort userPort;
    IPublisherPort publisherPort;
    ICachePort cachePort;

    @Transactional(rollbackFor = Exception.class)
    public void verifyOtpForRegister(String email, String otp) {
        UserEntity user = userPort.getUserByEmail(email);
        if (user == null) {
            log.error("user with email {} not found", email);
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        if (user.getEnabled()) {
            log.error("user with email {} is already verified", email);
            throw new AppException(ErrorCode.USER_ALREADY_ENABLED);
        }

        OtpDto cachedOtp = cachePort.getFromCache(CacheUtils.buildCacheKeyOtpRegister(email), OtpDto.class);
        if (cachedOtp == null || !cachedOtp.getOtp().equals(otp)) {
            log.info("OTP verification failed for {}", email);
            throw new AppException(ErrorCode.OTP_NOT_VALID);
        }

        // Invalidate the OTP after successful verification
        cachePort.deleteFromCache(CacheUtils.buildCacheKeyOtpRegister(email));

        user.setEnabled(true);
        userPort.save(user);

        handleAfterVerifyUser(user);
    }

    public void handleAfterVerifyUser(UserEntity user) {
        // send message to create tourist
        CreateTouristMessage message = CreateTouristMessage.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .build();
        var kafkaBaseDto = message.toKafkaBaseDto();
        publisherPort.pushAsync(kafkaBaseDto, TopicConstant.TouristCommand.TOPIC, null);
    }
}