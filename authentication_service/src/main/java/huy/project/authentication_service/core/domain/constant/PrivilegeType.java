package huy.project.authentication_service.core.domain.constant;

import lombok.Getter;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Enum defining all privilege types in the booking system.
 * This enum provides a centralized way to manage all privileges and reduces the risk of typos.
 */
@Getter
public enum PrivilegeType {

    // User Management
    VIEW_USERS("VIEW_USERS", "User Management"),
    CREATE_USER("CREATE_USER", "User Management"),
    UPDATE_USER("UPDATE_USER", "User Management"),
    DELETE_USER("DELETE_USER", "User Management"),
    SUSPEND_USER("SUSPEND_USER", "User Management"),
    ACTIVATE_USER("ACTIVATE_USER", "User Management"),

    // Accommodation Management
    VIEW_ACCOMMODATIONS("VIEW_ACCOMMODATIONS", "Accommodation Management"),
    CREATE_ACCOMMODATION("CREATE_ACCOMMODATION", "Accommodation Management"),
    UPDATE_ACCOMMODATION("UPDATE_ACCOMMODATION", "Accommodation Management"),
    DELETE_ACCOMMODATION("DELETE_ACCOMMODATION", "Accommodation Management"),
    APPROVE_ACCOMMODATION("APPROVE_ACCOMMODATION", "Accommodation Management"),
    REJECT_ACCOMMODATION("REJECT_ACCOMMODATION", "Accommodation Management"),
    PUBLISH_ACCOMMODATION("PUBLISH_ACCOMMODATION", "Accommodation Management"),

    // Room/Unit Management
    VIEW_ROOMS("VIEW_ROOMS", "Room/Unit Management"),
    CREATE_ROOM("CREATE_ROOM", "Room/Unit Management"),
    UPDATE_ROOM("UPDATE_ROOM", "Room/Unit Management"),
    DELETE_ROOM("DELETE_ROOM", "Room/Unit Management"),
    MANAGE_ROOM_AVAILABILITY("MANAGE_ROOM_AVAILABILITY", "Room/Unit Management"),
    BULK_UPDATE_ROOMS("BULK_UPDATE_ROOMS", "Room/Unit Management"),

    // Booking Management
    VIEW_BOOKINGS("VIEW_BOOKINGS", "Booking Management"),
    VIEW_ALL_BOOKINGS("VIEW_ALL_BOOKINGS", "Booking Management"),
    CREATE_BOOKING("CREATE_BOOKING", "Booking Management"),
    UPDATE_BOOKING("UPDATE_BOOKING", "Booking Management"),
    CANCEL_BOOKING("CANCEL_BOOKING", "Booking Management"),
    CONFIRM_BOOKING("CONFIRM_BOOKING", "Booking Management"),
    MODIFY_BOOKING("MODIFY_BOOKING", "Booking Management"),
    VIEW_BOOKING_HISTORY("VIEW_BOOKING_HISTORY", "Booking Management"),
    EXPORT_BOOKING_DATA("EXPORT_BOOKING_DATA", "Booking Management"),

    // Payment Management
    VIEW_PAYMENTS("VIEW_PAYMENTS", "Payment Management"),
    PROCESS_PAYMENT("PROCESS_PAYMENT", "Payment Management"),
    REFUND_PAYMENT("REFUND_PAYMENT", "Payment Management"),
    VIEW_PAYMENT_REPORTS("VIEW_PAYMENT_REPORTS", "Payment Management"),
    MANAGE_PAYMENT_METHODS("MANAGE_PAYMENT_METHODS", "Payment Management"),

    // Pricing Management
    VIEW_PRICING("VIEW_PRICING", "Pricing Management"),
    UPDATE_PRICING("UPDATE_PRICING", "Pricing Management"),
    CREATE_PRICING_RULES("CREATE_PRICING_RULES", "Pricing Management"),
    MANAGE_SEASONAL_PRICING("MANAGE_SEASONAL_PRICING", "Pricing Management"),
    BULK_PRICING_UPDATE("BULK_PRICING_UPDATE", "Pricing Management"),
    VIEW_PRICING_ANALYTICS("VIEW_PRICING_ANALYTICS", "Pricing Management"),

    // Promotion Management
    VIEW_PROMOTIONS("VIEW_PROMOTIONS", "Promotion Management"),
    CREATE_PROMOTION("CREATE_PROMOTION", "Promotion Management"),
    UPDATE_PROMOTION("UPDATE_PROMOTION", "Promotion Management"),
    DELETE_PROMOTION("DELETE_PROMOTION", "Promotion Management"),
    ACTIVATE_PROMOTION("ACTIVATE_PROMOTION", "Promotion Management"),
    DEACTIVATE_PROMOTION("DEACTIVATE_PROMOTION", "Promotion Management"),

    // Review & Rating Management
    VIEW_REVIEWS("VIEW_REVIEWS", "Review & Rating Management"),
    MODERATE_REVIEWS("MODERATE_REVIEWS", "Review & Rating Management"),
    DELETE_REVIEW("DELETE_REVIEW", "Review & Rating Management"),
    RESPOND_TO_REVIEW("RESPOND_TO_REVIEW", "Review & Rating Management"),
    FLAG_INAPPROPRIATE_REVIEW("FLAG_INAPPROPRIATE_REVIEW", "Review & Rating Management"),
    VIEW_REVIEW_ANALYTICS("VIEW_REVIEW_ANALYTICS", "Review & Rating Management"),

    // Notification Management
    SEND_NOTIFICATIONS("SEND_NOTIFICATIONS", "Notification Management"),
    VIEW_NOTIFICATION_LOGS("VIEW_NOTIFICATION_LOGS", "Notification Management"),
    MANAGE_NOTIFICATION_TEMPLATES("MANAGE_NOTIFICATION_TEMPLATES", "Notification Management"),
    CONFIGURE_NOTIFICATION_SETTINGS("CONFIGURE_NOTIFICATION_SETTINGS", "Notification Management"),

    // Inventory Management
    VIEW_INVENTORY("VIEW_INVENTORY", "Inventory Management"),
    UPDATE_INVENTORY("UPDATE_INVENTORY", "Inventory Management"),
    BULK_INVENTORY_UPDATE("BULK_INVENTORY_UPDATE", "Inventory Management"),
    GENERATE_INVENTORY_REPORTS("GENERATE_INVENTORY_REPORTS", "Inventory Management"),

    // Location Management
    VIEW_LOCATIONS("VIEW_LOCATIONS", "Location Management"),
    CREATE_LOCATION("CREATE_LOCATION", "Location Management"),
    UPDATE_LOCATION("UPDATE_LOCATION", "Location Management"),
    DELETE_LOCATION("DELETE_LOCATION", "Location Management"),

    // Search Management
    CONFIGURE_SEARCH_PARAMETERS("CONFIGURE_SEARCH_PARAMETERS", "Search Management"),
    VIEW_SEARCH_ANALYTICS("VIEW_SEARCH_ANALYTICS", "Search Management"),
    MANAGE_SEARCH_FILTERS("MANAGE_SEARCH_FILTERS", "Search Management"),

    // Profile & Host Management
    VIEW_HOST_PROFILES("VIEW_HOST_PROFILES", "Profile & Host Management"),
    VERIFY_HOST("VERIFY_HOST", "Profile & Host Management"),
    SUSPEND_HOST("SUSPEND_HOST", "Profile & Host Management"),
    MANAGE_HOST_DOCUMENTS("MANAGE_HOST_DOCUMENTS", "Profile & Host Management"),
    VIEW_HOST_PERFORMANCE("VIEW_HOST_PERFORMANCE", "Profile & Host Management"),

    // Communication
    SEND_MESSAGES("SEND_MESSAGES", "Communication"),
    VIEW_MESSAGE_HISTORY("VIEW_MESSAGE_HISTORY", "Communication"),
    MODERATE_COMMUNICATIONS("MODERATE_COMMUNICATIONS", "Communication"),

    // File Management
    UPLOAD_FILES("UPLOAD_FILES", "File Management"),
    DELETE_FILES("DELETE_FILES", "File Management"),
    MANAGE_FILE_STORAGE("MANAGE_FILE_STORAGE", "File Management"),

    // Reporting & Analytics
    VIEW_FINANCIAL_REPORTS("VIEW_FINANCIAL_REPORTS", "Reporting & Analytics"),
    VIEW_BOOKING_REPORTS("VIEW_BOOKING_REPORTS", "Reporting & Analytics"),
    VIEW_USER_ANALYTICS("VIEW_USER_ANALYTICS", "Reporting & Analytics"),
    EXPORT_REPORTS("EXPORT_REPORTS", "Reporting & Analytics"),
    VIEW_PERFORMANCE_METRICS("VIEW_PERFORMANCE_METRICS", "Reporting & Analytics"),

    // System Administration
    MANAGE_SYSTEM_SETTINGS("MANAGE_SYSTEM_SETTINGS", "System Administration"),
    VIEW_SYSTEM_LOGS("VIEW_SYSTEM_LOGS", "System Administration"),
    BACKUP_SYSTEM("BACKUP_SYSTEM", "System Administration"),
    MAINTAIN_SYSTEM("MAINTAIN_SYSTEM", "System Administration"),
    CONFIGURE_INTEGRATIONS("CONFIGURE_INTEGRATIONS", "System Administration"),

    // Role & Permission Management
    VIEW_ROLES("VIEW_ROLES", "Role & Permission Management"),
    CREATE_ROLE("CREATE_ROLE", "Role & Permission Management"),
    UPDATE_ROLE("UPDATE_ROLE", "Role & Permission Management"),
    DELETE_ROLE("DELETE_ROLE", "Role & Permission Management"),
    ASSIGN_PERMISSIONS("ASSIGN_PERMISSIONS", "Role & Permission Management"),
    MANAGE_USER_ROLES("MANAGE_USER_ROLES", "Role & Permission Management"),

    // Content Management
    CREATE_CONTENT("CREATE_CONTENT", "Content Management"),
    UPDATE_CONTENT("UPDATE_CONTENT", "Content Management"),
    DELETE_CONTENT("DELETE_CONTENT", "Content Management"),
    PUBLISH_CONTENT("PUBLISH_CONTENT", "Content Management"),
    MODERATE_CONTENT("MODERATE_CONTENT", "Content Management"),
    MANAGE_CONTENT_CATEGORIES("MANAGE_CONTENT_CATEGORIES", "Content Management"),

    // Audit & Security
    VIEW_AUDIT_LOGS("VIEW_AUDIT_LOGS", "Audit & Security"),
    MANAGE_SECURITY_SETTINGS("MANAGE_SECURITY_SETTINGS", "Audit & Security"),
    ACCESS_SENSITIVE_DATA("ACCESS_SENSITIVE_DATA", "Audit & Security"),

    // Customer Service
    ACCESS_CUSTOMER_SUPPORT("ACCESS_CUSTOMER_SUPPORT", "Customer Service"),
    RESOLVE_DISPUTES("RESOLVE_DISPUTES", "Customer Service"),
    ESCALATE_ISSUES("ESCALATE_ISSUES", "Customer Service"),
    VIEW_CUSTOMER_HISTORY("VIEW_CUSTOMER_HISTORY", "Customer Service");

    private final String privilegeName;
    private final String category;

    PrivilegeType(String privilegeName, String category) {
        this.privilegeName = privilegeName;
        this.category = category;
    }

    /**
     * Get PrivilegeType by privilege name
     *
     * @param privilegeName the privilege name
     * @return PrivilegeType or null if not found
     */
    public static PrivilegeType fromPrivilegeName(String privilegeName) {
        for (PrivilegeType type : PrivilegeType.values()) {
            if (type.getPrivilegeName().equals(privilegeName)) {
                return type;
            }
        }
        return null;
    }

    /**
     * Get all privileges for a specific category
     *
     * @param category the category name
     * @return array of PrivilegeType for the category
     */
    public static PrivilegeType[] getPrivilegesByCategory(String category) {
        return Arrays.stream(PrivilegeType.values())
                .filter(privilege -> privilege.getCategory().equals(category))
                .toArray(PrivilegeType[]::new);
    }

    /**
     * Get all available categories
     *
     * @return set of unique category names
     */
    public static Set<String> getAllCategories() {
        return Arrays.stream(PrivilegeType.values())
                .map(PrivilegeType::getCategory)
                .collect(Collectors.toSet());
    }
}
