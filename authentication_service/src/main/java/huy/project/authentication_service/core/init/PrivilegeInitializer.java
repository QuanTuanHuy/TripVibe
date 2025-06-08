package huy.project.authentication_service.core.init;

import huy.project.authentication_service.core.domain.constant.PrivilegeType;
import huy.project.authentication_service.core.domain.dto.request.CreatePrivilegeRequestDto;
import huy.project.authentication_service.core.service.IPrivilegeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PrivilegeInitializer {
    private final IPrivilegeService privilegeService;

    public void run() {
        log.info("Initializing privilege data...");
        try {
            createPrivileges();
            log.info("Privileges initialization completed successfully.");
        } catch (Exception e) {
            log.error("Failed to initialize privileges: {}", e.getMessage(), e);
        }
    }

    private void createPrivileges() {
        List<CreatePrivilegeRequestDto> privileges = Arrays.stream(PrivilegeType.values())
                .map(privilegeType -> new CreatePrivilegeRequestDto(
                        privilegeType.getPrivilegeName(),
                        "Privilege for " + privilegeType.getCategory() + ": " + privilegeType.getPrivilegeName()
                ))
                .toList();

        log.info("Creating {} privileges from PrivilegeType enum", privileges.size());
        privilegeService.createIfNotExists(privileges);
    }
}
