package huy.project.authentication_service.core.service;

import huy.project.authentication_service.core.domain.dto.request.CreateUserRequestDto;
import huy.project.authentication_service.core.domain.entity.UserEntity;

public interface IUserService {
    UserEntity createUser(CreateUserRequestDto request);
    UserEntity getDetailUser(Long id);
}
