package huy.project.authentication_service.core.init;

import huy.project.authentication_service.core.domain.constant.RoleType;
import huy.project.authentication_service.core.domain.dto.request.CreateRoleRequestDto;
import huy.project.authentication_service.core.domain.entity.PrivilegeEntity;
import huy.project.authentication_service.core.service.IPrivilegeService;
import huy.project.authentication_service.core.service.IRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class RoleInitializer {
    private final IRoleService roleService;
    private final IPrivilegeService privilegeService;

    public void run() {
        log.info("Initializing role data...");
        try {
            createRoles();
            log.info("Roles initialization completed successfully.");
        } catch (Exception e) {
            log.error("Failed to initialize roles: {}", e.getMessage(), e);
        }
    }

    private void createRoles() {
        List<PrivilegeEntity> allPrivileges = privilegeService.getAllPrivileges();
        Map<String, Long> privilegeNameToIdMap = allPrivileges.stream()
                .collect(Collectors.toMap(PrivilegeEntity::getName, PrivilegeEntity::getId));

        List<CreateRoleRequestDto> roles = Arrays.stream(RoleType.values())
                .map(roleType -> CreateRoleRequestDto.builder()
                        .name(roleType.getRoleName())
                        .description(roleType.getDescription())
                        .privilegeIds(getPrivilegeIds(privilegeNameToIdMap, roleType.getPrivilegeNames()))
                        .build())
                .toList();

        log.info("Creating {} roles from RoleType enum", roles.size());
        roleService.createIfNotExists(roles);
    }

    private List<Long> getPrivilegeIds(Map<String, Long> privilegeNameToIdMap, List<String> privilegeNames) {
        return privilegeNames.stream()
                .map(privilegeNameToIdMap::get)
                .filter(Objects::nonNull)
                .toList();
    }
}
