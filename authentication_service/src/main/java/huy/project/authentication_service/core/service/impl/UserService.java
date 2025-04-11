package huy.project.authentication_service.core.service.impl;

import huy.project.authentication_service.core.domain.dto.request.CreateUserRequestDto;
import huy.project.authentication_service.core.domain.dto.request.UpdateUserRequestDto;
import huy.project.authentication_service.core.domain.entity.UserEntity;
import huy.project.authentication_service.core.service.IUserService;
import huy.project.authentication_service.core.usecase.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService implements IUserService {
    CreateUserUseCase createUserUseCase;
    GetUserUseCase getUserUseCase;
    UpdateUserUseCase updateUserUseCase;
    DeleteUserUseCase deleteUserUseCase;
    VerifyOtpUseCase verifyOtpUseCase;

    @Override
    public UserEntity createUser(CreateUserRequestDto request) {
        return createUserUseCase.createUser(request);
    }

    @Override
    public void verifyOtpForRegister(String email, String otp) {
        verifyOtpUseCase.verifyOtpForRegister(email, otp);
    }

    @Override
    public UserEntity getDetailUser(Long id) {
        return getUserUseCase.getUserById(id);
    }

    @Override
    public UserEntity updateUser(Long userId, UpdateUserRequestDto req) {
        return updateUserUseCase.updateUser(userId, req);
    }

    @Override
    public void deleteUser(Long userId) {
        deleteUserUseCase.deleteUser(userId);
    }
}
