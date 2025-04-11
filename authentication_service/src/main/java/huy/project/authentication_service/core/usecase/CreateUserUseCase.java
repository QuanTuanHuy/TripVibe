package huy.project.authentication_service.core.usecase;

import huy.project.authentication_service.core.domain.constant.ErrorCode;
import huy.project.authentication_service.core.domain.constant.TopicConstant;
import huy.project.authentication_service.core.domain.dto.kafka.CreateTouristMessage;
import huy.project.authentication_service.core.domain.dto.request.CreateUserRequestDto;
import huy.project.authentication_service.core.domain.entity.RoleEntity;
import huy.project.authentication_service.core.domain.entity.UserEntity;
import huy.project.authentication_service.core.domain.entity.UserRoleEntity;
import huy.project.authentication_service.core.domain.mapper.UserMapper;
import huy.project.authentication_service.core.exception.AppException;
import huy.project.authentication_service.core.port.IPublisherPort;
import huy.project.authentication_service.core.port.IUserPort;
import huy.project.authentication_service.core.port.IUserRolePort;
import huy.project.authentication_service.core.validation.RoleValidation;
import huy.project.authentication_service.core.validation.UserValidation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateUserUseCase {
    private final IUserPort userPort;
    private final IUserRolePort userRolePort;

    private final RoleValidation roleValidation;
    private final UserValidation userValidation;

    private final PasswordEncoder passwordEncoder;

    private final IPublisherPort publisherPort;


    @Transactional(rollbackFor = Exception.class)
    public UserEntity createUser(CreateUserRequestDto request) {
        // validate req
        if (userValidation.isEmailExist(request.getEmail())) {
            log.error("Email is already taken");
            throw new AppException(ErrorCode.USER_EMAIL_EXISTED);
        }

        Pair<Boolean, List<RoleEntity>> roleExisted = roleValidation.isRolesExist(request.getRoleIds());
        if (!roleExisted.getFirst()) {
            log.error("Some roles are not existed");
            throw new AppException(ErrorCode.ROLE_NOT_FOUND);
        }

        // create user
        UserEntity user = UserMapper.INSTANCE.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(true);
        user = userPort.save(user);
        user.setRoles(roleExisted.getSecond());
        user.setPassword(null);
        final Long userId = user.getId();

        List<UserRoleEntity> userRoles = roleExisted.getSecond().stream()
                .map(role -> UserRoleEntity.builder()
                        .userId(userId)
                        .roleId(role.getId())
                        .build())
                .toList();
        userRolePort.saveAll(userRoles);

        handleAfterCreateUser(user);

        return user;
    }

    public void handleAfterCreateUser(UserEntity user) {
        // send message to create tourist
        CreateTouristMessage message = CreateTouristMessage.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .build();
        var kafkaBaseDto = message.toKafkaBaseDto();
        publisherPort.pushAsync(kafkaBaseDto, TopicConstant.TouristCommand.TOPIC, null);
    }
}
