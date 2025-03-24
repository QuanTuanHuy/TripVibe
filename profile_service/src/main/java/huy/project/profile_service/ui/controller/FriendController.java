package huy.project.profile_service.ui.controller;

import huy.project.profile_service.core.domain.dto.request.CreateFriendDto;
import huy.project.profile_service.core.domain.entity.FriendEntity;
import huy.project.profile_service.core.service.IFriendService;
import huy.project.profile_service.kernel.utils.AuthenUtils;
import huy.project.profile_service.ui.resource.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/v1/friends")
@RequiredArgsConstructor
public class FriendController {
    private final IFriendService friendService;

    @PostMapping
    public ResponseEntity<Resource<FriendEntity>> createFriend(
            @RequestBody CreateFriendDto req
    ) {
        Long touristId = AuthenUtils.getCurrentUserId();
        return ResponseEntity.ok(new Resource<>(friendService.createFriend(touristId, req)));
    }

    @GetMapping
    public ResponseEntity<Resource<List<FriendEntity>>> getFriendsByTouristId() {
        Long touristId = AuthenUtils.getCurrentUserId();
        return ResponseEntity.ok(new Resource<>(friendService.getFriendsByTouristId(touristId)));
    }

    @DeleteMapping("/{friendId}")
    public ResponseEntity<Resource<?>> deleteFriend(@PathVariable Long friendId) {
        Long touristId = AuthenUtils.getCurrentUserId();
        friendService.deleteFriend(touristId, friendId);
        return ResponseEntity.ok(new Resource<>(null));
    }
}
