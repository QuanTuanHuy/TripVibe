package huy.project.profile_service.core.port;

import huy.project.profile_service.core.domain.entity.UserSettingEntity;

public interface IUserSettingPort {
    UserSettingEntity save(UserSettingEntity userSetting);
    UserSettingEntity getUserSettingById(Long id);
    void deleteUserSetting(Long id);
}
