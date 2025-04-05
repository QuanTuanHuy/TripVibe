package huy.project.profile_service.infrastructure.repository.adapter;

import huy.project.profile_service.core.domain.entity.PassportEntity;
import huy.project.profile_service.core.port.IPassportPort;
import huy.project.profile_service.infrastructure.repository.IPassportRepository;
import huy.project.profile_service.infrastructure.repository.mapper.PassportMapper;
import huy.project.profile_service.infrastructure.repository.model.PassportModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PassportAdapter implements IPassportPort {
    private final IPassportRepository passportRepository;

    @Override
    public PassportEntity save(PassportEntity passport) {
        PassportModel passportModel = PassportMapper.INSTANCE.toModel(passport);
        return PassportMapper.INSTANCE.toEntity(passportRepository.save(passportModel));
    }

    @Override
    public void deletePassportById(Long id) {
        passportRepository.deleteById(id);
    }

    @Override
    public PassportEntity getPassportById(Long id) {
        return PassportMapper.INSTANCE.toEntity(passportRepository.findById(id).orElse(null));
    }
}
