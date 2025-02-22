package huy.project.authentication_service.ui.controller;

import huy.project.authentication_service.core.domain.dto.request.CreateRoleRequestDto;
import huy.project.authentication_service.core.domain.dto.request.UpdateRoleRequestDto;
import huy.project.authentication_service.core.domain.entity.RoleEntity;
import huy.project.authentication_service.core.service.IRoleService;
import huy.project.authentication_service.ui.resource.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/public/v1/roles")
public class RoleController {
    private final IRoleService roleService;

    @PostMapping
    public ResponseEntity<Resource<RoleEntity>> createRole(@RequestBody CreateRoleRequestDto req) {
        return ResponseEntity.ok(new Resource<>(roleService.createRole(req)));
    }

    @GetMapping
    public ResponseEntity<Resource<List<RoleEntity>>> getAll() {
        return ResponseEntity.ok(new Resource<>(roleService.getAllRoles()));
    }

    @PutMapping("/{roleId}")
    public ResponseEntity<Resource<RoleEntity>> updateRole(
            @PathVariable Long roleId,
            @RequestBody UpdateRoleRequestDto req
    ) {
        return ResponseEntity.ok(new Resource<>(roleService.updateRole(roleId, req)));
    }
}
