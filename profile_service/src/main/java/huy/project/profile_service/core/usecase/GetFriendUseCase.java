package huy.project.profile_service.core.usecase;

import huy.project.profile_service.core.domain.entity.FriendEntity;
import huy.project.profile_service.core.port.IFriendPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetFriendUseCase {
    private final IFriendPort friendPort;

    public List<FriendEntity> getFriendsByTouristId(Long touristId) {
        return friendPort.getFriendsByTouristId(touristId);
    }
}
