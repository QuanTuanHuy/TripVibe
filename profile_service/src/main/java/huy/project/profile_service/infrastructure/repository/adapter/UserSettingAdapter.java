package huy.project.profile_service.infrastructure.repository.adapter;

import huy.project.profile_service.core.domain.entity.UserSettingEntity;
import huy.project.profile_service.core.port.IUserSettingPort;
import huy.project.profile_service.infrastructure.repository.IUserSettingRepository;
import huy.project.profile_service.infrastructure.repository.mapper.UserSettingMapper;
import huy.project.profile_service.infrastructure.repository.model.UserSettingModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserSettingAdapter implements IUserSettingPort {
    private final IUserSettingRepository userSettingRepository;

    @Override
    public UserSettingEntity save(UserSettingEntity userSetting) {
        UserSettingModel settingModel = UserSettingMapper.INSTANCE.toModel(userSetting);
        return UserSettingMapper.INSTANCE.toEntity(userSettingRepository.save(settingModel));
    }

    @Override
    public UserSettingEntity getUserSettingById(Long id) {
        return UserSettingMapper.INSTANCE.toEntity(userSettingRepository.findById(id).orElse(null));
    }

    @Override
    public void deleteUserSetting(Long id) {
        userSettingRepository.deleteById(id);
    }
}
