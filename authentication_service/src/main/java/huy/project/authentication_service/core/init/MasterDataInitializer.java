package huy.project.authentication_service.core.init;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MasterDataInitializer implements CommandLineRunner {
    private final PrivilegeInitializer privilegeInitializer;
    private final RoleInitializer roleInitializer;
    private final UserInitializer userInitializer;


    @Override
    public void run(String... args) {
        log.info("Initializing master data...");

        privilegeInitializer.run();
        roleInitializer.run();
        userInitializer.run();
    }
}
