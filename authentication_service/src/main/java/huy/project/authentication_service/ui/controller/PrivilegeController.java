package huy.project.authentication_service.ui.controller;

import huy.project.authentication_service.core.domain.dto.request.CreatePrivilegeRequestDto;
import huy.project.authentication_service.core.domain.dto.request.UpdatePrivilegeRequestDto;
import huy.project.authentication_service.core.domain.entity.PrivilegeEntity;
import huy.project.authentication_service.core.service.IPrivilegeService;
import huy.project.authentication_service.ui.resource.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/public/v1/privileges")
public class PrivilegeController {
    private final IPrivilegeService privilegeService;

    @PostMapping
    public ResponseEntity<Resource<PrivilegeEntity>> createPrivilege(@RequestBody CreatePrivilegeRequestDto req) {
        return ResponseEntity.ok(new Resource<>(privilegeService.createPrivilege(req)));
    }

    @GetMapping
    public ResponseEntity<Resource<List<PrivilegeEntity>>> getAll() {
        return ResponseEntity.ok(new Resource<>(privilegeService.getAllPrivileges()));
    }

    @PutMapping("/{privilegeId}")
    public ResponseEntity<Resource<PrivilegeEntity>> updatePrivilege(
            @PathVariable Long privilegeId,
            @RequestBody UpdatePrivilegeRequestDto req
    ) {
        return ResponseEntity.ok(new Resource<>(privilegeService.updatePrivilege(privilegeId, req)));
    }
}
