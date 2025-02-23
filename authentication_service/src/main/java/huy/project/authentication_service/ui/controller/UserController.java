package huy.project.authentication_service.ui.controller;

import huy.project.authentication_service.core.domain.dto.request.CreateUserRequestDto;
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

    @GetMapping("/{userId}")
    public ResponseEntity<Resource<UserEntity>> getDetailUser(@PathVariable Long userId) {
        return ResponseEntity.ok(new Resource<>(userService.getDetailUser(userId)));
    }
}
