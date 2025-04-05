package huy.project.profile_service.ui.controller;

import huy.project.profile_service.core.domain.dto.request.UserProfileDto;
import huy.project.profile_service.core.service.IUserProfileService;
import huy.project.profile_service.ui.resource.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/v1/profiles")
@RequiredArgsConstructor
@Slf4j
public class UserProfileController {
    private final IUserProfileService userProfileService;

    @GetMapping("/{id}")
    public ResponseEntity<Resource<UserProfileDto>> getUserProfile(@PathVariable Long id) {
        return ResponseEntity.ok(new Resource<>(userProfileService.getUserProfile(id)));
    }
}
