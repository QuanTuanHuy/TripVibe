package huy.project.profile_service.infrastructure.repository;

import huy.project.profile_service.infrastructure.repository.model.FriendModel;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IFriendRepository extends IBaseRepository<FriendModel> {
    List<FriendModel> findByTouristId(Long touristId);
}
