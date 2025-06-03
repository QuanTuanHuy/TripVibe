package huy.project.authentication_service.core.service;

import huy.project.authentication_service.core.domain.dto.request.UpdateUserRequestDto;
import huy.project.authentication_service.core.domain.entity.UserEntity;

import java.util.List;

public interface IUserService {
    void verifyOtpForRegister(String email, String otp);
    UserEntity getDetailUser(Long id);
    UserEntity updateUser(Long userId, UpdateUserRequestDto req);
    void deleteUser(Long userId);
    void createIfNotExists(String email, String password, List<String> roleNames);
    UserEntity createUser(String email, String password, List<String> roleNames);
    long countAll();
}
