package huy.project.authentication_service.core.domain.constant;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Enum defining all role types in the booking system.
 * This enum provides a centralized way to manage all roles and their associated privileges.
 */
@Getter
public enum RoleType {
    
    GUEST("GUEST", "Guest User", 
        Arrays.asList(
            PrivilegeType.VIEW_ACCOMMODATIONS,
            PrivilegeType.VIEW_ROOMS,
            PrivilegeType.VIEW_PRICING,
            PrivilegeType.VIEW_REVIEWS,
            PrivilegeType.VIEW_LOCATIONS,
            PrivilegeType.VIEW_PROMOTIONS
        )
    ),
    
    CUSTOMER("CUSTOMER", "Registered Customer",
        Arrays.asList(
            // Guest privileges
            PrivilegeType.VIEW_ACCOMMODATIONS,
            PrivilegeType.VIEW_ROOMS,
            PrivilegeType.VIEW_PRICING,
            PrivilegeType.VIEW_REVIEWS,
            PrivilegeType.VIEW_LOCATIONS,
            PrivilegeType.VIEW_PROMOTIONS,
            // Customer specific privileges
            PrivilegeType.CREATE_BOOKING,
            PrivilegeType.VIEW_BOOKINGS,
            PrivilegeType.UPDATE_BOOKING,
            PrivilegeType.CANCEL_BOOKING,
            PrivilegeType.MODIFY_BOOKING,
            PrivilegeType.VIEW_BOOKING_HISTORY,
            PrivilegeType.VIEW_PAYMENTS,
            PrivilegeType.PROCESS_PAYMENT,
            PrivilegeType.SEND_MESSAGES,
            PrivilegeType.VIEW_MESSAGE_HISTORY,
            PrivilegeType.UPLOAD_FILES
        )
    ),
    
    HOST("HOST", "Property Host",
        Arrays.asList(
            // Customer privileges
            PrivilegeType.VIEW_ACCOMMODATIONS,
            PrivilegeType.VIEW_ROOMS,
            PrivilegeType.VIEW_PRICING,
            PrivilegeType.VIEW_REVIEWS,
            PrivilegeType.VIEW_LOCATIONS,
            PrivilegeType.VIEW_PROMOTIONS,
            PrivilegeType.CREATE_BOOKING,
            PrivilegeType.VIEW_BOOKINGS,
            PrivilegeType.UPDATE_BOOKING,
            PrivilegeType.CANCEL_BOOKING,
            PrivilegeType.VIEW_PAYMENTS,
            PrivilegeType.SEND_MESSAGES,
            PrivilegeType.VIEW_MESSAGE_HISTORY,
            PrivilegeType.UPLOAD_FILES,
            // Host specific privileges
            PrivilegeType.CREATE_ACCOMMODATION,
            PrivilegeType.UPDATE_ACCOMMODATION,
            PrivilegeType.PUBLISH_ACCOMMODATION,
            PrivilegeType.CREATE_ROOM,
            PrivilegeType.UPDATE_ROOM,
            PrivilegeType.MANAGE_ROOM_AVAILABILITY,
            PrivilegeType.UPDATE_PRICING,
            PrivilegeType.CREATE_PRICING_RULES,
            PrivilegeType.MANAGE_SEASONAL_PRICING,
            PrivilegeType.CREATE_PROMOTION,
            PrivilegeType.UPDATE_PROMOTION,
            PrivilegeType.ACTIVATE_PROMOTION,
            PrivilegeType.DEACTIVATE_PROMOTION,
            PrivilegeType.CONFIRM_BOOKING,
            PrivilegeType.RESPOND_TO_REVIEW,
            PrivilegeType.VIEW_INVENTORY,
            PrivilegeType.UPDATE_INVENTORY
        )
    ),
    
    PROPERTY_MANAGER("PROPERTY_MANAGER", "Property Manager",
        Arrays.asList(
            // Host privileges plus additional management privileges
            PrivilegeType.VIEW_ACCOMMODATIONS,
            PrivilegeType.CREATE_ACCOMMODATION,
            PrivilegeType.UPDATE_ACCOMMODATION,
            PrivilegeType.DELETE_ACCOMMODATION,
            PrivilegeType.PUBLISH_ACCOMMODATION,
            PrivilegeType.VIEW_ROOMS,
            PrivilegeType.CREATE_ROOM,
            PrivilegeType.UPDATE_ROOM,
            PrivilegeType.DELETE_ROOM,
            PrivilegeType.MANAGE_ROOM_AVAILABILITY,
            PrivilegeType.BULK_UPDATE_ROOMS,
            PrivilegeType.VIEW_BOOKINGS,
            PrivilegeType.VIEW_ALL_BOOKINGS,
            PrivilegeType.CONFIRM_BOOKING,
            PrivilegeType.CANCEL_BOOKING,
            PrivilegeType.MODIFY_BOOKING,
            PrivilegeType.VIEW_BOOKING_HISTORY,
            PrivilegeType.VIEW_PAYMENTS,
            PrivilegeType.VIEW_PAYMENT_REPORTS,
            PrivilegeType.VIEW_PRICING,
            PrivilegeType.UPDATE_PRICING,
            PrivilegeType.CREATE_PRICING_RULES,
            PrivilegeType.MANAGE_SEASONAL_PRICING,
            PrivilegeType.BULK_PRICING_UPDATE,
            PrivilegeType.VIEW_PRICING_ANALYTICS,
            PrivilegeType.VIEW_PROMOTIONS,
            PrivilegeType.CREATE_PROMOTION,
            PrivilegeType.UPDATE_PROMOTION,
            PrivilegeType.ACTIVATE_PROMOTION,
            PrivilegeType.DEACTIVATE_PROMOTION,
            PrivilegeType.VIEW_REVIEWS,
            PrivilegeType.RESPOND_TO_REVIEW,
            PrivilegeType.VIEW_INVENTORY,
            PrivilegeType.UPDATE_INVENTORY,
            PrivilegeType.BULK_INVENTORY_UPDATE,
            PrivilegeType.GENERATE_INVENTORY_REPORTS,
            PrivilegeType.SEND_MESSAGES,
            PrivilegeType.VIEW_MESSAGE_HISTORY,
            PrivilegeType.UPLOAD_FILES,
            PrivilegeType.DELETE_FILES,
            PrivilegeType.VIEW_BOOKING_REPORTS,
            PrivilegeType.VIEW_PERFORMANCE_METRICS
        )
    ),
    
    CUSTOMER_SUPPORT("CUSTOMER_SUPPORT", "Customer Support Agent",
        Arrays.asList(
            PrivilegeType.VIEW_USERS,
            PrivilegeType.VIEW_ACCOMMODATIONS,
            PrivilegeType.VIEW_ROOMS,
            PrivilegeType.VIEW_BOOKINGS,
            PrivilegeType.VIEW_ALL_BOOKINGS,
            PrivilegeType.UPDATE_BOOKING,
            PrivilegeType.CANCEL_BOOKING,
            PrivilegeType.MODIFY_BOOKING,
            PrivilegeType.VIEW_BOOKING_HISTORY,
            PrivilegeType.VIEW_PAYMENTS,
            PrivilegeType.REFUND_PAYMENT,
            PrivilegeType.VIEW_REVIEWS,
            PrivilegeType.MODERATE_REVIEWS,
            PrivilegeType.FLAG_INAPPROPRIATE_REVIEW,
            PrivilegeType.SEND_NOTIFICATIONS,
            PrivilegeType.SEND_MESSAGES,
            PrivilegeType.VIEW_MESSAGE_HISTORY,
            PrivilegeType.MODERATE_COMMUNICATIONS,
            PrivilegeType.ACCESS_CUSTOMER_SUPPORT,
            PrivilegeType.RESOLVE_DISPUTES,
            PrivilegeType.ESCALATE_ISSUES,
            PrivilegeType.VIEW_CUSTOMER_HISTORY
        )
    ),
    
    FINANCE_MANAGER("FINANCE_MANAGER", "Finance Manager",
        Arrays.asList(
            PrivilegeType.VIEW_PAYMENTS,
            PrivilegeType.PROCESS_PAYMENT,
            PrivilegeType.REFUND_PAYMENT,
            PrivilegeType.VIEW_PAYMENT_REPORTS,
            PrivilegeType.MANAGE_PAYMENT_METHODS,
            PrivilegeType.VIEW_PRICING,
            PrivilegeType.UPDATE_PRICING,
            PrivilegeType.VIEW_PRICING_ANALYTICS,
            PrivilegeType.VIEW_PROMOTIONS,
            PrivilegeType.CREATE_PROMOTION,
            PrivilegeType.UPDATE_PROMOTION,
            PrivilegeType.VIEW_BOOKINGS,
            PrivilegeType.VIEW_ALL_BOOKINGS,
            PrivilegeType.EXPORT_BOOKING_DATA,
            PrivilegeType.VIEW_FINANCIAL_REPORTS,
            PrivilegeType.VIEW_BOOKING_REPORTS,
            PrivilegeType.EXPORT_REPORTS,
            PrivilegeType.VIEW_PERFORMANCE_METRICS
        )
    ),
    
    CONTENT_MODERATOR("CONTENT_MODERATOR", "Content Moderator",
        Arrays.asList(
            PrivilegeType.VIEW_ACCOMMODATIONS,
            PrivilegeType.APPROVE_ACCOMMODATION,
            PrivilegeType.REJECT_ACCOMMODATION,
            PrivilegeType.VIEW_REVIEWS,
            PrivilegeType.MODERATE_REVIEWS,
            PrivilegeType.DELETE_REVIEW,
            PrivilegeType.FLAG_INAPPROPRIATE_REVIEW,
            PrivilegeType.VIEW_HOST_PROFILES,
            PrivilegeType.VERIFY_HOST,
            PrivilegeType.MANAGE_HOST_DOCUMENTS,
            PrivilegeType.MODERATE_COMMUNICATIONS,
            PrivilegeType.CREATE_CONTENT,
            PrivilegeType.UPDATE_CONTENT,
            PrivilegeType.DELETE_CONTENT,
            PrivilegeType.PUBLISH_CONTENT,
            PrivilegeType.MODERATE_CONTENT,
            PrivilegeType.MANAGE_CONTENT_CATEGORIES
        )
    ),
    
    MARKETING_MANAGER("MARKETING_MANAGER", "Marketing Manager",
        Arrays.asList(
            PrivilegeType.VIEW_ACCOMMODATIONS,
            PrivilegeType.VIEW_PROMOTIONS,
            PrivilegeType.CREATE_PROMOTION,
            PrivilegeType.UPDATE_PROMOTION,
            PrivilegeType.DELETE_PROMOTION,
            PrivilegeType.ACTIVATE_PROMOTION,
            PrivilegeType.DEACTIVATE_PROMOTION,
            PrivilegeType.SEND_NOTIFICATIONS,
            PrivilegeType.VIEW_NOTIFICATION_LOGS,
            PrivilegeType.MANAGE_NOTIFICATION_TEMPLATES,
            PrivilegeType.CREATE_CONTENT,
            PrivilegeType.UPDATE_CONTENT,
            PrivilegeType.PUBLISH_CONTENT,
            PrivilegeType.MANAGE_CONTENT_CATEGORIES,
            PrivilegeType.VIEW_USER_ANALYTICS,
            PrivilegeType.VIEW_BOOKING_REPORTS,
            PrivilegeType.VIEW_PERFORMANCE_METRICS,
            PrivilegeType.EXPORT_REPORTS,
            PrivilegeType.CONFIGURE_SEARCH_PARAMETERS,
            PrivilegeType.VIEW_SEARCH_ANALYTICS,
            PrivilegeType.MANAGE_SEARCH_FILTERS
        )
    ),
    
    OPERATIONS_MANAGER("OPERATIONS_MANAGER", "Operations Manager",
        Arrays.asList(
            PrivilegeType.VIEW_USERS,
            PrivilegeType.VIEW_ACCOMMODATIONS,
            PrivilegeType.APPROVE_ACCOMMODATION,
            PrivilegeType.REJECT_ACCOMMODATION,
            PrivilegeType.VIEW_ROOMS,
            PrivilegeType.VIEW_BOOKINGS,
            PrivilegeType.VIEW_ALL_BOOKINGS,
            PrivilegeType.EXPORT_BOOKING_DATA,
            PrivilegeType.VIEW_PAYMENTS,
            PrivilegeType.VIEW_PAYMENT_REPORTS,
            PrivilegeType.VIEW_INVENTORY,
            PrivilegeType.GENERATE_INVENTORY_REPORTS,
            PrivilegeType.VIEW_LOCATIONS,
            PrivilegeType.CREATE_LOCATION,
            PrivilegeType.UPDATE_LOCATION,
            PrivilegeType.VIEW_HOST_PROFILES,
            PrivilegeType.VERIFY_HOST,
            PrivilegeType.VIEW_HOST_PERFORMANCE,
            PrivilegeType.SEND_NOTIFICATIONS,
            PrivilegeType.VIEW_NOTIFICATION_LOGS,
            PrivilegeType.VIEW_FINANCIAL_REPORTS,
            PrivilegeType.VIEW_BOOKING_REPORTS,
            PrivilegeType.VIEW_USER_ANALYTICS,
            PrivilegeType.VIEW_PERFORMANCE_METRICS,
            PrivilegeType.EXPORT_REPORTS,
            PrivilegeType.ACCESS_CUSTOMER_SUPPORT,
            PrivilegeType.ESCALATE_ISSUES,
            PrivilegeType.VIEW_SEARCH_ANALYTICS
        )
    ),
    
    ADMIN("ADMIN", "System Administrator",
        Arrays.asList(
            // All user and accommodation management
            PrivilegeType.VIEW_USERS,
            PrivilegeType.CREATE_USER,
            PrivilegeType.UPDATE_USER,
            PrivilegeType.SUSPEND_USER,
            PrivilegeType.ACTIVATE_USER,
            PrivilegeType.VIEW_ACCOMMODATIONS,
            PrivilegeType.CREATE_ACCOMMODATION,
            PrivilegeType.UPDATE_ACCOMMODATION,
            PrivilegeType.DELETE_ACCOMMODATION,
            PrivilegeType.APPROVE_ACCOMMODATION,
            PrivilegeType.REJECT_ACCOMMODATION,
            PrivilegeType.PUBLISH_ACCOMMODATION,
            // Room and booking management
            PrivilegeType.VIEW_ROOMS,
            PrivilegeType.CREATE_ROOM,
            PrivilegeType.UPDATE_ROOM,
            PrivilegeType.DELETE_ROOM,
            PrivilegeType.MANAGE_ROOM_AVAILABILITY,
            PrivilegeType.BULK_UPDATE_ROOMS,
            PrivilegeType.VIEW_BOOKINGS,
            PrivilegeType.VIEW_ALL_BOOKINGS,
            PrivilegeType.CREATE_BOOKING,
            PrivilegeType.UPDATE_BOOKING,
            PrivilegeType.CANCEL_BOOKING,
            PrivilegeType.CONFIRM_BOOKING,
            PrivilegeType.MODIFY_BOOKING,
            PrivilegeType.VIEW_BOOKING_HISTORY,
            PrivilegeType.EXPORT_BOOKING_DATA,
            // Payment and pricing
            PrivilegeType.VIEW_PAYMENTS,
            PrivilegeType.PROCESS_PAYMENT,
            PrivilegeType.REFUND_PAYMENT,
            PrivilegeType.VIEW_PAYMENT_REPORTS,
            PrivilegeType.MANAGE_PAYMENT_METHODS,
            PrivilegeType.VIEW_PRICING,
            PrivilegeType.UPDATE_PRICING,
            PrivilegeType.CREATE_PRICING_RULES,
            PrivilegeType.MANAGE_SEASONAL_PRICING,
            PrivilegeType.BULK_PRICING_UPDATE,
            PrivilegeType.VIEW_PRICING_ANALYTICS,
            // Promotions and reviews
            PrivilegeType.VIEW_PROMOTIONS,
            PrivilegeType.CREATE_PROMOTION,
            PrivilegeType.UPDATE_PROMOTION,
            PrivilegeType.DELETE_PROMOTION,
            PrivilegeType.ACTIVATE_PROMOTION,
            PrivilegeType.DEACTIVATE_PROMOTION,
            PrivilegeType.VIEW_REVIEWS,
            PrivilegeType.MODERATE_REVIEWS,
            PrivilegeType.DELETE_REVIEW,
            PrivilegeType.RESPOND_TO_REVIEW,
            PrivilegeType.FLAG_INAPPROPRIATE_REVIEW,
            PrivilegeType.VIEW_REVIEW_ANALYTICS,
            // Notifications and inventory
            PrivilegeType.SEND_NOTIFICATIONS,
            PrivilegeType.VIEW_NOTIFICATION_LOGS,
            PrivilegeType.MANAGE_NOTIFICATION_TEMPLATES,
            PrivilegeType.CONFIGURE_NOTIFICATION_SETTINGS,
            PrivilegeType.VIEW_INVENTORY,
            PrivilegeType.UPDATE_INVENTORY,
            PrivilegeType.BULK_INVENTORY_UPDATE,
            PrivilegeType.GENERATE_INVENTORY_REPORTS,
            // Location and search
            PrivilegeType.VIEW_LOCATIONS,
            PrivilegeType.CREATE_LOCATION,
            PrivilegeType.UPDATE_LOCATION,
            PrivilegeType.DELETE_LOCATION,
            PrivilegeType.CONFIGURE_SEARCH_PARAMETERS,
            PrivilegeType.VIEW_SEARCH_ANALYTICS,
            PrivilegeType.MANAGE_SEARCH_FILTERS,
            // Host and communication
            PrivilegeType.VIEW_HOST_PROFILES,
            PrivilegeType.VERIFY_HOST,
            PrivilegeType.SUSPEND_HOST,
            PrivilegeType.MANAGE_HOST_DOCUMENTS,
            PrivilegeType.VIEW_HOST_PERFORMANCE,
            PrivilegeType.SEND_MESSAGES,
            PrivilegeType.VIEW_MESSAGE_HISTORY,
            PrivilegeType.MODERATE_COMMUNICATIONS,
            // File and reporting
            PrivilegeType.UPLOAD_FILES,
            PrivilegeType.DELETE_FILES,
            PrivilegeType.MANAGE_FILE_STORAGE,
            PrivilegeType.VIEW_FINANCIAL_REPORTS,
            PrivilegeType.VIEW_BOOKING_REPORTS,
            PrivilegeType.VIEW_USER_ANALYTICS,
            PrivilegeType.EXPORT_REPORTS,
            PrivilegeType.VIEW_PERFORMANCE_METRICS,
            // System administration
            PrivilegeType.MANAGE_SYSTEM_SETTINGS,
            PrivilegeType.VIEW_SYSTEM_LOGS,
            PrivilegeType.BACKUP_SYSTEM,
            PrivilegeType.MAINTAIN_SYSTEM,
            PrivilegeType.CONFIGURE_INTEGRATIONS,
            // Role management
            PrivilegeType.VIEW_ROLES,
            PrivilegeType.CREATE_ROLE,
            PrivilegeType.UPDATE_ROLE,
            PrivilegeType.ASSIGN_PERMISSIONS,
            PrivilegeType.MANAGE_USER_ROLES,
            // Content management
            PrivilegeType.CREATE_CONTENT,
            PrivilegeType.UPDATE_CONTENT,
            PrivilegeType.DELETE_CONTENT,
            PrivilegeType.PUBLISH_CONTENT,
            PrivilegeType.MODERATE_CONTENT,
            PrivilegeType.MANAGE_CONTENT_CATEGORIES,
            // Audit and customer service
            PrivilegeType.VIEW_AUDIT_LOGS,
            PrivilegeType.MANAGE_SECURITY_SETTINGS,
            PrivilegeType.ACCESS_CUSTOMER_SUPPORT,
            PrivilegeType.RESOLVE_DISPUTES,
            PrivilegeType.ESCALATE_ISSUES,
            PrivilegeType.VIEW_CUSTOMER_HISTORY
        )
    ),
    
    SUPER_ADMIN("SUPER_ADMIN", "Super Administrator",
        // Super admin has ALL privileges
        Arrays.asList(PrivilegeType.values())
    );
    
    private final String roleName;
    private final String description;
    private final List<PrivilegeType> privileges;
    
    RoleType(String roleName, String description, List<PrivilegeType> privileges) {
        this.roleName = roleName;
        this.description = description;
        this.privileges = privileges;
    }

    /**
     * Get privilege names as strings for this role
     * @return list of privilege names
     */
    public List<String> getPrivilegeNames() {
        return privileges.stream()
                .map(PrivilegeType::getPrivilegeName)
                .collect(Collectors.toList());
    }
    
    /**
     * Get RoleType by role name
     * @param roleName the role name
     * @return RoleType or null if not found
     */
    public static RoleType fromRoleName(String roleName) {
        for (RoleType type : RoleType.values()) {
            if (type.getRoleName().equals(roleName)) {
                return type;
            }
        }
        return null;
    }
    
    /**
     * Check if this role has a specific privilege
     * @param privilege the privilege to check
     * @return true if role has the privilege
     */
    public boolean hasPrivilege(PrivilegeType privilege) {
        return privileges.contains(privilege);
    }
    
    /**
     * Check if this role has a specific privilege by name
     * @param privilegeName the privilege name to check
     * @return true if role has the privilege
     */
    public boolean hasPrivilege(String privilegeName) {
        return privileges.stream()
                .anyMatch(p -> p.getPrivilegeName().equals(privilegeName));
    }
}
