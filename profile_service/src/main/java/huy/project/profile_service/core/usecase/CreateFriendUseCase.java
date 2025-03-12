package huy.project.profile_service.core.usecase;

import huy.project.profile_service.core.domain.dto.request.CreateFriendDto;
import huy.project.profile_service.core.domain.entity.FriendEntity;
import huy.project.profile_service.core.domain.mapper.FriendMapper;
import huy.project.profile_service.core.port.IFriendPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateFriendUseCase {
    private final IFriendPort friendPort;
    private final GetTouristUseCase getTouristUseCase;

    @Transactional(rollbackFor = Exception.class)
    public FriendEntity createFriend(Long touristId, CreateFriendDto req) {
        // check tourist is existed
        getTouristUseCase.getTouristById(touristId);

        FriendEntity friend = FriendMapper.INSTANCE.toEntity(req);
        friend.setTouristId(touristId);
        return friendPort.save(friend);
    }


}
