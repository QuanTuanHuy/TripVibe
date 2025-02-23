package huy.project.authentication_service.core.service.impl;

import huy.project.authentication_service.core.domain.dto.request.CreateUserRequestDto;
import huy.project.authentication_service.core.domain.entity.UserEntity;
import huy.project.authentication_service.core.service.IUserService;
import huy.project.authentication_service.core.usecase.CreateUserUseCase;
import huy.project.authentication_service.core.usecase.GetUserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final CreateUserUseCase createUserUseCase;
    private final GetUserUseCase getUserUseCase;

    @Override
    public UserEntity createUser(CreateUserRequestDto request) {
        return createUserUseCase.createUser(request);
    }

    @Override
    public UserEntity getDetailUser(Long id) {
        return getUserUseCase.getUserById(id);
    }
}
