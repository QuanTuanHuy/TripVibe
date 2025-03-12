package huy.project.profile_service.infrastructure.repository.adapter;

import huy.project.profile_service.core.domain.entity.FriendEntity;
import huy.project.profile_service.core.port.IFriendPort;
import huy.project.profile_service.infrastructure.repository.IFriendRepository;
import huy.project.profile_service.infrastructure.repository.mapper.FriendMapper;
import huy.project.profile_service.infrastructure.repository.model.FriendModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendAdapter implements IFriendPort {
    private final IFriendRepository friendRepository;

    @Override
    public FriendEntity save(FriendEntity friend) {
        FriendModel friendModel = FriendMapper.INSTANCE.toModel(friend);
        return FriendMapper.INSTANCE.toEntity(friendRepository.save(friendModel));
    }

    @Override
    public List<FriendEntity> getFriendsByTouristId(Long touristId) {
        return FriendMapper.INSTANCE.toListEntity(friendRepository.findByTouristId(touristId));
    }

    @Override
    public FriendEntity getFriendById(Long id) {
        return friendRepository.findById(id)
                .map(FriendMapper.INSTANCE::toEntity)
                .orElse(null);
    }

    @Override
    public void deleteFriendById(Long id) {
        friendRepository.deleteById(id);
    }
}
