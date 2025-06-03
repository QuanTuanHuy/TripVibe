package huy.project.authentication_service.ui.controller;

import huy.project.authentication_service.core.domain.constant.RoleType;
import huy.project.authentication_service.core.domain.dto.request.UpdateUserRequestDto;
import huy.project.authentication_service.core.domain.entity.UserEntity;
import huy.project.authentication_service.core.service.IUserService;
import huy.project.authentication_service.kernel.utils.AuthenUtils;
import huy.project.authentication_service.ui.resource.Resource;
import huy.project.authentication_service.ui.resource.request.CreateUserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/public/v1/users")
public class UserController {
    private final IUserService userService;

    @PostMapping("/customer")
    public ResponseEntity<Resource<UserEntity>> registerCustomer(@RequestBody CreateUserRequest request) {
        return ResponseEntity.ok(new Resource<>(
                userService.createUser(
                        request.getEmail(),
                        request.getPassword(),
                        List.of(RoleType.CUSTOMER.name()))));
    }

    @PostMapping("/host")
    public ResponseEntity<Resource<UserEntity>> registerHost(@RequestBody CreateUserRequest request) {
        return ResponseEntity.ok(new Resource<>(
                userService.createUser(
                        request.getEmail(),
                        request.getPassword(),
                        List.of(RoleType.HOST.name()))));
    }

    @PostMapping("/admin")
    public ResponseEntity<Resource<UserEntity>> registerAdmin(@RequestBody CreateUserRequest request) {
        return ResponseEntity.ok(new Resource<>(
                userService.createUser(
                        request.getEmail(),
                        request.getPassword(),
                        List.of(RoleType.ADMIN.name()))));
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

    @GetMapping("/me")
    public ResponseEntity<Resource<UserEntity>> getMe() {
        Long currentUserId = AuthenUtils.getCurrentUserId();
        return ResponseEntity.ok(new Resource<>(userService.getDetailUser(currentUserId)));
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
