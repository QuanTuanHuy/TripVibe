package huy.project.profile_service.core.port;

import huy.project.profile_service.core.domain.entity.PassportEntity;

public interface IPassportPort {
    PassportEntity save(PassportEntity passport);
    void deletePassportById(Long id);
    PassportEntity getPassportById(Long id);
}
