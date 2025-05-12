package huy.project.authentication_service.ui.controller;

import huy.project.authentication_service.core.domain.constant.ErrorCode;
import huy.project.authentication_service.core.domain.dto.request.LoginRequest;
import huy.project.authentication_service.core.domain.dto.request.TokenRefreshRequest;
import huy.project.authentication_service.core.domain.dto.response.LoginResponse;
import huy.project.authentication_service.core.exception.AppException;
import huy.project.authentication_service.core.service.IAuthService;
import huy.project.authentication_service.kernel.utils.AuthenUtils;
import huy.project.authentication_service.ui.resource.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/public/v1/auth")
public class AuthController {
    private final IAuthService authService;

    @PostMapping("/login")
    public ResponseEntity<Resource<LoginResponse>> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(new Resource<>(authService.login(request.getEmail(), request.getPassword())));
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<Resource<LoginResponse>> loginWithRefreshToken(@RequestBody TokenRefreshRequest request) {
        return ResponseEntity.ok(new Resource<>(authService.loginWithRefreshToken(request)));
    }
    
    @PostMapping("/logout")
    public ResponseEntity<Resource<?>> logout(@RequestBody TokenRefreshRequest request) {
        Long userId = AuthenUtils.getCurrentUserId();
        if (userId == null) {
            throw new AppException(ErrorCode.USER_NOT_LOGGED_IN);
        }
        authService.logout(userId, request.getRefreshToken());
        return ResponseEntity.ok(new Resource<>(null));
    }
    
    @PostMapping("/logout/all")
    public ResponseEntity<Resource<?>> logoutAll() {
        Long userId = AuthenUtils.getCurrentUserId();
        if (userId == null) {
            throw new AppException(ErrorCode.USER_NOT_LOGGED_IN);
        }
        authService.logoutAll(userId);
        return ResponseEntity.ok(new Resource<>(null));
    }
}
