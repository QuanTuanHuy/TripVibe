package huy.project.authentication_service.kernel.utils;

import huy.project.authentication_service.core.domain.constant.PrivilegeType;
import huy.project.authentication_service.core.domain.constant.RoleType;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Helper utility class for working with Privilege and Role enums.
 * Provides convenient methods for privilege and role management.
 */
public final class AuthorizationUtils {
    
    private AuthorizationUtils() {
        // Utility class
    }
    
    /**
     * Check if a role has a specific privilege
     * @param roleName the role name
     * @param privilegeName the privilege name
     * @return true if role has the privilege
     */
    public static boolean hasPrivilege(String roleName, String privilegeName) {
        RoleType roleType = RoleType.fromRoleName(roleName);
        return roleType != null && roleType.hasPrivilege(privilegeName);
    }
    
    /**
     * Get all privilege names for a role
     * @param roleName the role name
     * @return list of privilege names, empty if role not found
     */
    public static List<String> getPrivilegesForRole(String roleName) {
        RoleType roleType = RoleType.fromRoleName(roleName);
        return roleType != null ? roleType.getPrivilegeNames() : List.of();
    }
    
    /**
     * Get all available role names
     * @return set of role names
     */
    public static Set<String> getAllRoleNames() {
        return java.util.Arrays.stream(RoleType.values())
                .map(RoleType::getRoleName)
                .collect(Collectors.toSet());
    }
    
    /**
     * Get all available privilege names
     * @return set of privilege names
     */
    public static Set<String> getAllPrivilegeNames() {
        return java.util.Arrays.stream(PrivilegeType.values())
                .map(PrivilegeType::getPrivilegeName)
                .collect(Collectors.toSet());
    }
    
    /**
     * Check if a role name is valid
     * @param roleName the role name to check
     * @return true if valid
     */
    public static boolean isValidRole(String roleName) {
        return RoleType.fromRoleName(roleName) != null;
    }
    
    /**
     * Check if a privilege name is valid
     * @param privilegeName the privilege name to check
     * @return true if valid
     */
    public static boolean isValidPrivilege(String privilegeName) {
        return PrivilegeType.fromPrivilegeName(privilegeName) != null;
    }
    
    /**
     * Get role description by role name
     * @param roleName the role name
     * @return role description or null if not found
     */
    public static String getRoleDescription(String roleName) {
        RoleType roleType = RoleType.fromRoleName(roleName);
        return roleType != null ? roleType.getDescription() : null;
    }
    
    /**
     * Get privilege category by privilege name
     * @param privilegeName the privilege name
     * @return privilege category or null if not found
     */
    public static String getPrivilegeCategory(String privilegeName) {
        PrivilegeType privilegeType = PrivilegeType.fromPrivilegeName(privilegeName);
        return privilegeType != null ? privilegeType.getCategory() : null;
    }
    
    /**
     * Get all privileges for a specific category
     * @param category the category name
     * @return list of privilege names in the category
     */
    public static List<String> getPrivilegesByCategory(String category) {
        return java.util.Arrays.stream(PrivilegeType.values())
                .filter(privilege -> privilege.getCategory().equals(category))
                .map(PrivilegeType::getPrivilegeName)
                .collect(Collectors.toList());
    }
    
    /**
     * Check if user with multiple roles has a specific privilege
     * @param roleNames list of role names
     * @param privilegeName the privilege to check
     * @return true if any role has the privilege
     */
    public static boolean hasPrivilegeInAnyRole(List<String> roleNames, String privilegeName) {
        return roleNames.stream()
                .anyMatch(roleName -> hasPrivilege(roleName, privilegeName));
    }
    
    /**
     * Get all unique privileges from multiple roles
     * @param roleNames list of role names
     * @return set of unique privilege names
     */
    public static Set<String> getPrivilegesFromRoles(List<String> roleNames) {
        return roleNames.stream()
                .map(AuthorizationUtils::getPrivilegesForRole)
                .flatMap(List::stream)
                .collect(Collectors.toSet());
    }
    
    /**
     * Check if a role is administrative (ADMIN or SUPER_ADMIN)
     * @param roleName the role name
     * @return true if it's an admin role
     */
    public static boolean isAdminRole(String roleName) {
        return RoleType.ADMIN.getRoleName().equals(roleName) || 
               RoleType.SUPER_ADMIN.getRoleName().equals(roleName);
    }
    
    /**
     * Check if a role has user management privileges
     * @param roleName the role name
     * @return true if role can manage users
     */
    public static boolean canManageUsers(String roleName) {
        return hasPrivilege(roleName, PrivilegeType.VIEW_USERS.getPrivilegeName()) &&
               hasPrivilege(roleName, PrivilegeType.UPDATE_USER.getPrivilegeName());
    }
    
    /**
     * Check if a role has booking management privileges
     * @param roleName the role name
     * @return true if role can manage bookings
     */
    public static boolean canManageBookings(String roleName) {
        return hasPrivilege(roleName, PrivilegeType.VIEW_BOOKINGS.getPrivilegeName()) &&
               hasPrivilege(roleName, PrivilegeType.UPDATE_BOOKING.getPrivilegeName());
    }
    
    /**
     * Check if a role has accommodation management privileges
     * @param roleName the role name
     * @return true if role can manage accommodations
     */
    public static boolean canManageAccommodations(String roleName) {
        return hasPrivilege(roleName, PrivilegeType.VIEW_ACCOMMODATIONS.getPrivilegeName()) &&
               hasPrivilege(roleName, PrivilegeType.UPDATE_ACCOMMODATION.getPrivilegeName());
    }
}
