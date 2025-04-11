package huy.project.authentication_service.core.service;

import huy.project.authentication_service.core.domain.dto.response.LoginResponse;

public interface IAuthService {
    LoginResponse login(String email, String password);
}
