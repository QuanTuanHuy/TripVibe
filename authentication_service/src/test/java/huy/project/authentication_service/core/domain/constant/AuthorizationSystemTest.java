package huy.project.authentication_service.core.domain.constant;

import huy.project.authentication_service.kernel.utils.AuthorizationUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Set;

@DisplayName("Authorization System Tests")
class AuthorizationSystemTest {

    @Test
    @DisplayName("Should have correct number of privileges and roles")
    void testPrivilegeAndRoleCount() {
        // Verify we have all expected privileges (100+)
        PrivilegeType[] privileges = PrivilegeType.values();
        assertTrue(privileges.length >= 100, "Should have at least 100 privileges");

        // Verify we have all expected roles (11)
        RoleType[] roles = RoleType.values();
        assertEquals(11, roles.length, "Should have exactly 11 roles");
    }

    @Test
    @DisplayName("Should correctly map privilege names and categories")
    void testPrivilegeTypeMapping() {
        // Test privilege name mapping
        assertEquals("VIEW_USERS", PrivilegeType.VIEW_USERS.getPrivilegeName());
        assertEquals("User Management", PrivilegeType.VIEW_USERS.getCategory());

        // Test privilege lookup
        PrivilegeType foundPrivilege = PrivilegeType.fromPrivilegeName("VIEW_USERS");
        assertNotNull(foundPrivilege);
        assertEquals(PrivilegeType.VIEW_USERS, foundPrivilege);

        // Test non-existent privilege
        PrivilegeType notFound = PrivilegeType.fromPrivilegeName("NON_EXISTENT");
        assertNull(notFound);
    }

    @Test
    @DisplayName("Should group privileges by category correctly")
    void testPrivilegeCategories() {
        // Test getting privileges by category
        PrivilegeType[] userMgmtPrivileges = PrivilegeType.getPrivilegesByCategory("User Management");
        assertTrue(userMgmtPrivileges.length > 0, "Should have user management privileges");

        // Verify all returned privileges belong to the category
        for (PrivilegeType privilege : userMgmtPrivileges) {
            assertEquals("User Management", privilege.getCategory());
        }

        // Test getting all categories
        Set<String> allCategories = PrivilegeType.getAllCategories();
        assertTrue(allCategories.size() > 15, "Should have multiple categories");
        assertTrue(allCategories.contains("User Management"));
        assertTrue(allCategories.contains("Booking Management"));
        assertTrue(allCategories.contains("Payment Management"));
    }

    @Test
    @DisplayName("Should correctly map role names and descriptions")
    void testRoleTypeMapping() {
        // Test role mapping
        assertEquals("CUSTOMER", RoleType.CUSTOMER.getRoleName());
        assertEquals("Registered Customer", RoleType.CUSTOMER.getDescription());

        // Test role lookup
        RoleType foundRole = RoleType.fromRoleName("CUSTOMER");
        assertNotNull(foundRole);
        assertEquals(RoleType.CUSTOMER, foundRole);

        // Test non-existent role
        RoleType notFound = RoleType.fromRoleName("NON_EXISTENT");
        assertNull(notFound);
    }

    @Test
    @DisplayName("Should correctly assign privileges to roles")
    void testRolePrivilegeAssignment() {
        // Test Guest role (should have basic view privileges only)
        assertTrue(RoleType.GUEST.hasPrivilege(PrivilegeType.VIEW_ACCOMMODATIONS));
        assertTrue(RoleType.GUEST.hasPrivilege(PrivilegeType.VIEW_ROOMS));
        assertFalse(RoleType.GUEST.hasPrivilege(PrivilegeType.CREATE_BOOKING));

        // Test Customer role (should have Guest privileges + booking)
        assertTrue(RoleType.CUSTOMER.hasPrivilege(PrivilegeType.VIEW_ACCOMMODATIONS));
        assertTrue(RoleType.CUSTOMER.hasPrivilege(PrivilegeType.CREATE_BOOKING));
        assertTrue(RoleType.CUSTOMER.hasPrivilege(PrivilegeType.VIEW_BOOKINGS));
        assertFalse(RoleType.CUSTOMER.hasPrivilege(PrivilegeType.DELETE_USER));

        // Test Host role (should have customer privileges + accommodation management)
        assertTrue(RoleType.HOST.hasPrivilege(PrivilegeType.CREATE_BOOKING));
        assertTrue(RoleType.HOST.hasPrivilege(PrivilegeType.CREATE_ACCOMMODATION));
        assertTrue(RoleType.HOST.hasPrivilege(PrivilegeType.UPDATE_ACCOMMODATION));
        assertFalse(RoleType.HOST.hasPrivilege(PrivilegeType.DELETE_USER));

        // Test Admin role (should have most privileges)
        assertTrue(RoleType.ADMIN.hasPrivilege(PrivilegeType.VIEW_USERS));
        assertTrue(RoleType.ADMIN.hasPrivilege(PrivilegeType.CREATE_USER));
        assertTrue(RoleType.ADMIN.hasPrivilege(PrivilegeType.UPDATE_USER));
        assertTrue(RoleType.ADMIN.hasPrivilege(PrivilegeType.MANAGE_SYSTEM_SETTINGS));

        // Test Super Admin role (should have ALL privileges)
        assertTrue(RoleType.SUPER_ADMIN.hasPrivilege(PrivilegeType.VIEW_USERS));
        assertTrue(RoleType.SUPER_ADMIN.hasPrivilege(PrivilegeType.DELETE_ROLE));
        assertTrue(RoleType.SUPER_ADMIN.hasPrivilege(PrivilegeType.ACCESS_SENSITIVE_DATA));
    }

    @Test
    @DisplayName("Should validate role hierarchy correctly")
    void testRoleHierarchy() {
        // Guest should have least privileges
        int guestPrivilegeCount = RoleType.GUEST.getPrivileges().size();

        // Customer should have more privileges than Guest
        int customerPrivilegeCount = RoleType.CUSTOMER.getPrivileges().size();
        assertTrue(customerPrivilegeCount > guestPrivilegeCount);

        // Host should have more privileges than Customer
        int hostPrivilegeCount = RoleType.HOST.getPrivileges().size();
        assertTrue(hostPrivilegeCount > customerPrivilegeCount);

        // Admin should have more privileges than Host
        int adminPrivilegeCount = RoleType.ADMIN.getPrivileges().size();
        assertTrue(adminPrivilegeCount > hostPrivilegeCount);

        // Super Admin should have the most privileges
        int superAdminPrivilegeCount = RoleType.SUPER_ADMIN.getPrivileges().size();
        assertTrue(superAdminPrivilegeCount > adminPrivilegeCount);

        // Super Admin should have ALL privileges
        assertEquals(PrivilegeType.values().length, superAdminPrivilegeCount);
    }

    @Test
    @DisplayName("Should work with AuthorizationUtils correctly")
    void testAuthorizationUtils() {
        // Test privilege checking
        assertTrue(AuthorizationUtils.hasPrivilege("CUSTOMER", "CREATE_BOOKING"));
        assertFalse(AuthorizationUtils.hasPrivilege("GUEST", "CREATE_BOOKING"));

        // Test getting privileges for role
        List<String> customerPrivileges = AuthorizationUtils.getPrivilegesForRole("CUSTOMER");
        assertTrue(customerPrivileges.contains("CREATE_BOOKING"));
        assertTrue(customerPrivileges.contains("VIEW_ACCOMMODATIONS"));

        // Test validation methods
        assertTrue(AuthorizationUtils.isValidRole("CUSTOMER"));
        assertFalse(AuthorizationUtils.isValidRole("INVALID_ROLE"));
        assertTrue(AuthorizationUtils.isValidPrivilege("CREATE_BOOKING"));
        assertFalse(AuthorizationUtils.isValidPrivilege("INVALID_PRIVILEGE"));

        // Test admin role checking
        assertTrue(AuthorizationUtils.isAdminRole("ADMIN"));
        assertTrue(AuthorizationUtils.isAdminRole("SUPER_ADMIN"));
        assertFalse(AuthorizationUtils.isAdminRole("CUSTOMER"));

        // Test capability checking
        assertTrue(AuthorizationUtils.canManageUsers("ADMIN"));
        assertFalse(AuthorizationUtils.canManageUsers("CUSTOMER"));
        assertTrue(AuthorizationUtils.canManageBookings("HOST"));
        assertTrue(AuthorizationUtils.canManageAccommodations("HOST"));
    }

    @Test
    @DisplayName("Should handle multiple roles correctly")
    void testMultipleRoles() {
        List<String> userRoles = List.of("CUSTOMER", "HOST");

        // Should have privilege if any role has it
        assertTrue(AuthorizationUtils.hasPrivilegeInAnyRole(userRoles, "CREATE_BOOKING")); // Both have
        assertTrue(AuthorizationUtils.hasPrivilegeInAnyRole(userRoles, "CREATE_ACCOMMODATION")); // Only HOST has
        assertFalse(AuthorizationUtils.hasPrivilegeInAnyRole(userRoles, "DELETE_USER")); // Neither has

        // Should get unique privileges from all roles
        Set<String> allPrivileges = AuthorizationUtils.getPrivilegesFromRoles(userRoles);
        assertTrue(allPrivileges.contains("CREATE_BOOKING"));
        assertTrue(allPrivileges.contains("CREATE_ACCOMMODATION"));
        assertTrue(allPrivileges.contains("VIEW_ACCOMMODATIONS"));

        // Should not have duplicates
        List<String> customerPrivileges = AuthorizationUtils.getPrivilegesForRole("CUSTOMER");
        List<String> hostPrivileges = AuthorizationUtils.getPrivilegesForRole("HOST");
        int expectedUniqueCount = Set.of(customerPrivileges, hostPrivileges)
                .stream()
                .flatMap(List::stream)
                .collect(java.util.stream.Collectors.toSet())
                .size();
        assertEquals(expectedUniqueCount, allPrivileges.size());
    }

    @Test
    @DisplayName("Should get correct role and privilege metadata")
    void testMetadata() {
        // Test role description
        assertEquals("Registered Customer", AuthorizationUtils.getRoleDescription("CUSTOMER"));
        assertNull(AuthorizationUtils.getRoleDescription("INVALID_ROLE"));

        // Test privilege category
        assertEquals("User Management", AuthorizationUtils.getPrivilegeCategory("VIEW_USERS"));
        assertEquals("Booking Management", AuthorizationUtils.getPrivilegeCategory("CREATE_BOOKING"));
        assertNull(AuthorizationUtils.getPrivilegeCategory("INVALID_PRIVILEGE"));

        // Test privileges by category
        List<String> userMgmtPrivileges = AuthorizationUtils.getPrivilegesByCategory("User Management");
        assertTrue(userMgmtPrivileges.contains("VIEW_USERS"));
        assertTrue(userMgmtPrivileges.contains("CREATE_USER"));
        assertTrue(userMgmtPrivileges.contains("UPDATE_USER"));

        // Test getting all role and privilege names
        Set<String> allRoleNames = AuthorizationUtils.getAllRoleNames();
        assertTrue(allRoleNames.contains("GUEST"));
        assertTrue(allRoleNames.contains("CUSTOMER"));
        assertTrue(allRoleNames.contains("SUPER_ADMIN"));
        assertEquals(11, allRoleNames.size());

        Set<String> allPrivilegeNames = AuthorizationUtils.getAllPrivilegeNames();
        assertTrue(allPrivilegeNames.contains("VIEW_USERS"));
        assertTrue(allPrivilegeNames.contains("CREATE_BOOKING"));
        assertTrue(allPrivilegeNames.size() >= 100);
    }
}
