package huy.project.authentication_service.core.init;

import huy.project.authentication_service.core.domain.constant.RoleType;
import huy.project.authentication_service.core.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserInitializer {
    private final IUserService userService;

    public void run() {
        log.info("Initializing default users...");

        if (userService.countAll() > 0) {
            log.info("Default users already exist, skipping initialization.");
            return;
        }

        createDefaultUsers();
        createTestUsers();

        log.info("User initialization completed.");
    }

    /**
     * Create default system users with administrative privileges
     */
    private void createDefaultUsers() {
        log.info("Creating default system users...");

        // Super Admin
        userService.createIfNotExists(
                "quan.tuan.huy@gmail.com",
                "SuperAdmin123!",
                List.of(RoleType.SUPER_ADMIN.getRoleName())
        );

        // System Admin
        userService.createIfNotExists(
                "admin@tripvibe.com",
                "Admin123!",
                List.of(RoleType.ADMIN.getRoleName())
        );

        // Content Moderator
        userService.createIfNotExists(
                "moderator@tripvibe.com",
                "Moderator123!",
                List.of(RoleType.CONTENT_MODERATOR.getRoleName())
        );

        // Customer Support
        userService.createIfNotExists(
                "support@tripvibe.com",
                "Support123!",
                List.of(RoleType.CUSTOMER_SUPPORT.getRoleName())
        );

        // Finance Manager
        userService.createIfNotExists(
                "finance@tripvibe.com",
                "Finance123!",
                List.of(RoleType.FINANCE_MANAGER.getRoleName())
        );

        log.info("Default system users created successfully.");
    }

    /**
     * Create test users for development and testing purposes
     */
    private void createTestUsers() {
        log.info("Creating test users for development...");

        // Test Customer
        userService.createIfNotExists(
                "customer@example.com",
                "Customer123!",
                List.of(RoleType.CUSTOMER.getRoleName())
        );

        // Test Host (Individual)
        userService.createIfNotExists(
                "host@example.com",
                "Host123!",
                Arrays.asList(
                        RoleType.CUSTOMER.getRoleName(),
                        RoleType.HOST.getRoleName()
                )
        );

        // Test Property Manager (Business Host)
        userService.createIfNotExists(
                "propertymanager@example.com",
                "PropertyManager123!",
                Arrays.asList(
                        RoleType.CUSTOMER.getRoleName(),
                        RoleType.HOST.getRoleName(),
                        RoleType.PROPERTY_MANAGER.getRoleName()
                )
        );

        // Test Multi-role User (Customer + Support)
        userService.createIfNotExists(
                "support.customer@example.com",
                "SupportCustomer123!",
                Arrays.asList(
                        RoleType.CUSTOMER.getRoleName(),
                        RoleType.CUSTOMER_SUPPORT.getRoleName()
                )
        );

        // VIP Customer (Customer with additional privileges)
        userService.createIfNotExists(
                "vip.customer@example.com",
                "VipCustomer123!",
                List.of(RoleType.CUSTOMER.getRoleName())
        );

        // Business User (Finance + Support)
        userService.createIfNotExists(
                "business@example.com",
                "Business123!",
                Arrays.asList(
                        RoleType.CUSTOMER_SUPPORT.getRoleName(),
                        RoleType.FINANCE_MANAGER.getRoleName()
                )
        );

        // Hotel Chain Manager (All business roles)
        userService.createIfNotExists(
                "hotelchain@example.com",
                "HotelChain123!",
                Arrays.asList(
                        RoleType.HOST.getRoleName(),
                        RoleType.PROPERTY_MANAGER.getRoleName(),
                        RoleType.FINANCE_MANAGER.getRoleName()
                )
        );

        log.info("Test users created successfully.");
    }
}
