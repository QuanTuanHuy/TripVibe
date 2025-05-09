package huy.project.authentication_service.ui.controller;

import huy.project.authentication_service.core.domain.constant.ErrorCode;
import huy.project.authentication_service.core.domain.entity.RefreshTokenEntity;
import huy.project.authentication_service.core.exception.AppException;
import huy.project.authentication_service.core.service.IAuthService;
import huy.project.authentication_service.kernel.utils.AuthenUtils;
import huy.project.authentication_service.ui.resource.Resource;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/public/v1/sessions")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserSessionController {

    private final IAuthService authService;
    
    @GetMapping
    public ResponseEntity<Resource<List<RefreshTokenEntity>>> getUserSessions() {
        Long userId = AuthenUtils.getCurrentUserId();
        if (userId == null) {
            throw new AppException(ErrorCode.USER_NOT_LOGGED_IN);
        }
        List<RefreshTokenEntity> sessions = authService.getUserSessions(userId);
        return ResponseEntity.ok(new Resource<>(sessions));
    }
    
    @DeleteMapping("/{token}")
    public ResponseEntity<Resource<?>> revokeSession(@PathVariable String token) {
        Long userId = AuthenUtils.getCurrentUserId();
        if (userId == null) {
            throw new AppException(ErrorCode.USER_NOT_LOGGED_IN);
        }
        authService.logout(userId, token);
        return ResponseEntity.ok(new Resource<>(null));
    }
    
    @DeleteMapping
    public ResponseEntity<Resource<Void>> revokeAllSessions() {
        Long userId = AuthenUtils.getCurrentUserId();
        if (userId == null) {
            throw new AppException(ErrorCode.USER_NOT_LOGGED_IN);
        }
        authService.logoutAll(userId);
        return ResponseEntity.ok(new Resource<>(null));
    }
}