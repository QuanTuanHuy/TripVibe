package huy.project.authentication_service.ui.controller;

import huy.project.authentication_service.core.domain.dto.request.CreateUserRequestDto;
import huy.project.authentication_service.core.domain.dto.request.UpdateUserRequestDto;
import huy.project.authentication_service.core.domain.entity.UserEntity;
import huy.project.authentication_service.core.service.IUserService;
import huy.project.authentication_service.ui.resource.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/public/v1/users")
public class UserController {
    private final IUserService userService;

    @PostMapping
    public ResponseEntity<Resource<UserEntity>> register(@RequestBody CreateUserRequestDto request) {
        return ResponseEntity.ok(new Resource<>(userService.createUser(request)));
    }

    @PostMapping("/otp/verify")
    public ResponseEntity<Resource<?>> verifyOtpForRegister(@RequestParam String email, @RequestParam String otp) {
        userService.verifyOtpForRegister(email, otp);
        return ResponseEntity.ok(new Resource<>(null));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Resource<UserEntity>> getDetailUser(@PathVariable Long userId) {
        return ResponseEntity.ok(new Resource<>(userService.getDetailUser(userId)));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<Resource<UserEntity>> updateUser(
            @PathVariable Long userId,
            @RequestBody UpdateUserRequestDto req
    ) {
        return ResponseEntity.ok(new Resource<>(userService.updateUser(userId, req)));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Resource<?>> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok(new Resource<>(null));
    }
}
