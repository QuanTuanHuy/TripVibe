package huy.project.authentication_service.ui.controller;

import huy.project.authentication_service.core.domain.dto.request.LoginRequest;
import huy.project.authentication_service.core.domain.dto.response.LoginResponse;
import huy.project.authentication_service.core.service.IAuthService;
import huy.project.authentication_service.ui.resource.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/public/v1/auth")
public class AuthController {
    private final IAuthService authService;

    @PostMapping
    public ResponseEntity<Resource<LoginResponse>> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(new Resource<>(authService.login(request.getUsername(), request.getPassword())));
    }

}
