package huy.project.authentication_service.core.service.impl;

import huy.project.authentication_service.core.domain.dto.response.LoginResponse;
import huy.project.authentication_service.core.service.IAuthService;
import huy.project.authentication_service.core.usecase.LoginUserUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService implements IAuthService {
    private final LoginUserUseCase loginUserUseCase;

    @Override
    public LoginResponse login(String email, String password) {
        return loginUserUseCase.login(email, password);
    }
}
