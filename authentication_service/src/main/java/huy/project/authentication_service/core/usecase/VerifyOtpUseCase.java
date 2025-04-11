package huy.project.authentication_service.core.usecase;

import huy.project.authentication_service.core.domain.constant.ErrorCode;
import huy.project.authentication_service.core.domain.constant.TopicConstant;
import huy.project.authentication_service.core.domain.dto.kafka.CreateTouristMessage;
import huy.project.authentication_service.core.domain.entity.UserEntity;
import huy.project.authentication_service.core.exception.AppException;
import huy.project.authentication_service.core.port.IPublisherPort;
import huy.project.authentication_service.core.port.IUserPort;
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
    OtpUseCase otpUseCase;
    IPublisherPort publisherPort;

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

        boolean verifyResult = otpUseCase.verifyOtpForRegister(email, otp);
        if (!verifyResult) {
            log.error("otp is not valid");
            throw new AppException(ErrorCode.OTP_NOT_VALID);
        }
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