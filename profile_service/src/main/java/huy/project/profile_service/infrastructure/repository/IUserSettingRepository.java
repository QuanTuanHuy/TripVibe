package huy.project.profile_service.infrastructure.repository;

import huy.project.profile_service.infrastructure.repository.model.UserSettingModel;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserSettingRepository extends IBaseRepository<UserSettingModel> {
}
