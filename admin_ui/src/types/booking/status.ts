/**
 * Booking Status Management
 * Centralized place for all booking status-related logic
 */

// API booking statuses (from backend)
export type APIBookingStatus =
    | 'PENDING'
    | 'CONFIRMED'
    | 'APPROVED'
    | 'REJECTED'
    | 'CANCELLED'
    | 'CHECKED_IN'
    | 'CHECKED_OUT'
    | 'NO_SHOW'
    | 'COMPLETED';

// UI booking statuses (for frontend display and processing)
export type UIBookingStatus =
    | 'pending'
    | 'confirmed'
    | 'approved'
    | 'checked_in'
    | 'checked_out'
    | 'cancelled'
    | 'no_show';

/**
 * BOOKING STATUS WORKFLOW:
 * 1. PENDING: Booking mới tạo, chờ khách thanh toán
 * 2. CONFIRMED: Khách đã thanh toán xong (có paymentId)
 * 3. APPROVED: Chủ chỗ nghỉ đã duyệt booking (sau khi CONFIRMED)
 * 4. REJECTED: Chủ chỗ nghỉ từ chối booking (có thể từ CONFIRMED hoặc APPROVED)
 * 5. CANCELLED: Khách hủy booking hoặc hệ thống hủy
 * 6. CHECKED_IN: Khách đã nhận phòng
 * 7. CHECKED_OUT: Khách đã trả phòng
 * 8. NO_SHOW: Khách không đến
 * 9. COMPLETED: Booking hoàn thành
 * 
 * Flow chính: PENDING → CONFIRMED (payment) → APPROVED/REJECTED (host decision) → CHECKED_IN → CHECKED_OUT → COMPLETED
 */

// Status mapping from API to UI
const API_TO_UI_STATUS_MAP: Record<APIBookingStatus, UIBookingStatus> = {
    'PENDING': 'pending',
    'CONFIRMED': 'confirmed',
    'APPROVED': 'approved',
    'REJECTED': 'cancelled', // Map REJECTED to cancelled for UI display
    'CANCELLED': 'cancelled',
    'CHECKED_IN': 'checked_in',
    'CHECKED_OUT': 'checked_out',
    'NO_SHOW': 'no_show',
    'COMPLETED': 'checked_out' // Map COMPLETED to checked_out for UI
};

// Status mapping from UI to API
const UI_TO_API_STATUS_MAP: Record<UIBookingStatus, APIBookingStatus> = {
    'pending': 'PENDING',
    'confirmed': 'CONFIRMED',
    'approved': 'APPROVED',
    'checked_in': 'CHECKED_IN',
    'checked_out': 'CHECKED_OUT',
    'cancelled': 'CANCELLED', // Default to CANCELLED (can be overridden in specific cases)
    'no_show': 'NO_SHOW'
};

// Status display names in Vietnamese
const STATUS_DISPLAY_NAMES: Record<UIBookingStatus, string> = {
    'pending': 'Chờ thanh toán',
    'confirmed': 'Đã thanh toán',
    'approved': 'Đã duyệt',
    'checked_in': 'Đã nhận phòng',
    'checked_out': 'Đã trả phòng',
    'cancelled': 'Đã hủy/Từ chối',
    'no_show': 'Không đến'
};

// Status colors for UI
const STATUS_COLORS: Record<UIBookingStatus, string> = {
    'pending': 'amber-500',
    'confirmed': 'blue-600',
    'approved': 'green-600',
    'checked_in': 'indigo-500',
    'checked_out': 'slate-500',
    'cancelled': 'rose-600',
    'no_show': 'red-700'
};

// Status priorities for sorting
const STATUS_PRIORITIES: Record<UIBookingStatus, number> = {
    'pending': 1,
    'confirmed': 2,
    'approved': 3,
    'checked_in': 4,
    'checked_out': 5,
    'cancelled': 6,
    'no_show': 7
};

/**
 * Convert API status to UI status
 */
export const mapApiStatusToUI = (apiStatus: APIBookingStatus): UIBookingStatus => {
    return API_TO_UI_STATUS_MAP[apiStatus] || 'pending';
};

/**
 * Convert UI status to API status
 */
export const mapUIStatusToAPI = (uiStatus: UIBookingStatus): APIBookingStatus => {
    return UI_TO_API_STATUS_MAP[uiStatus] || 'PENDING';
};

/**
 * Get display name for UI status
 */
export const getStatusDisplayName = (status: UIBookingStatus): string => {
    return STATUS_DISPLAY_NAMES[status] || status;
};

/**
 * Get color for UI status
 */
export const getStatusColor = (status: UIBookingStatus): string => {
    return STATUS_COLORS[status] || 'gray';
};

/**
 * Get priority for UI status (for sorting)
 */
export const getStatusPriority = (status: UIBookingStatus): number => {
    return STATUS_PRIORITIES[status] || 999;
};

/**
 * Helper function to get current UI status for display in filters
 */
export const getCurrentUIStatusForFilter = (apiStatus: APIBookingStatus | null): string => {
    if (!apiStatus) return "all";
    return mapApiStatusToUI(apiStatus);
};

/**
 * Check if a status transition is valid
 */
export const isValidStatusTransition = (from: UIBookingStatus, to: UIBookingStatus): boolean => {
    const validTransitions: Record<UIBookingStatus, UIBookingStatus[]> = {
        'pending': ['confirmed', 'cancelled'],
        'confirmed': ['approved', 'cancelled'],
        'approved': ['checked_in', 'cancelled'],
        'checked_in': ['checked_out', 'no_show'],
        'checked_out': [], // Terminal state
        'cancelled': [], // Terminal state
        'no_show': [] // Terminal state
    };

    return validTransitions[from]?.includes(to) || false;
};

/**
 * Get available status transitions for a given status
 */
export const getAvailableStatusTransitions = (currentStatus: UIBookingStatus): UIBookingStatus[] => {
    const transitions: Record<UIBookingStatus, UIBookingStatus[]> = {
        'pending': ['confirmed', 'cancelled'],
        'confirmed': ['approved', 'cancelled'],
        'approved': ['checked_in', 'cancelled', 'no_show'],
        'checked_in': ['checked_out'],
        'checked_out': [],
        'cancelled': [],
        'no_show': []
    };

    return transitions[currentStatus] || [];
};

/**
 * Check if status represents a paid booking
 */
export const isPaidStatus = (status: UIBookingStatus): boolean => {
    return ['confirmed', 'approved', 'checked_in', 'checked_out'].includes(status);
};

/**
 * Check if status represents an active booking
 */
export const isActiveStatus = (status: UIBookingStatus): boolean => {
    return ['pending', 'confirmed', 'approved', 'checked_in'].includes(status);
};

/**
 * Check if status represents a completed booking
 */
export const isCompletedStatus = (status: UIBookingStatus): boolean => {
    return ['checked_out'].includes(status);
};

/**
 * Check if status represents a cancelled/failed booking
 */
export const isCancelledStatus = (status: UIBookingStatus): boolean => {
    return ['cancelled', 'no_show'].includes(status);
};

/**
 * Get all possible UI statuses for filters
 */
export const getAllUIStatuses = (): UIBookingStatus[] => {
    return Object.keys(STATUS_DISPLAY_NAMES) as UIBookingStatus[];
};

/**
 * Filter options for status dropdown
 */
export const getStatusFilterOptions = () => {
    return [
        { value: 'all', label: 'Tất cả trạng thái' },
        ...getAllUIStatuses().map(status => ({
            value: status, label: getStatusDisplayName(status)
        }))
    ];
};

/**
 * Get icon properties for UI status
 */
export const getStatusIconName = (status: UIBookingStatus): string => {
    const iconNames: Record<UIBookingStatus, string> = {
        'pending': 'Clock',
        'confirmed': 'CheckCircle2',
        'approved': 'UserCheck',
        'checked_in': 'LogIn',
        'checked_out': 'LogOut',
        'cancelled': 'XCircle',
        'no_show': 'AlertCircle'
    };

    return iconNames[status] || 'Clock';
};

/**
 * Get badge variant for UI status
 */
export const getStatusBadgeVariant = (status: UIBookingStatus): "default" | "destructive" | "outline" | "secondary" => {
    const variants: Record<UIBookingStatus, "default" | "destructive" | "outline" | "secondary"> = {
        'pending': 'outline',
        'confirmed': 'default',
        'approved': 'default',
        'checked_in': 'default',
        'checked_out': 'secondary',
        'cancelled': 'destructive',
        'no_show': 'outline'
    };

    return variants[status] || 'outline';
};

/**
 * Get actual color values for inline styles (to avoid Tailwind dynamic class issues)
 */
export const getStatusColorValue = (status: UIBookingStatus): string => {
    const colorValues: Record<UIBookingStatus, string> = {
        'pending': '#f59e0b',      // amber-500
        'confirmed': '#2563eb',    // blue-600
        'approved': '#16a34a',     // green-600
        'checked_in': '#6366f1',   // indigo-500
        'checked_out': '#64748b',  // slate-500
        'cancelled': '#e11d48',    // rose-600
        'no_show': '#b91c1c'       // red-700
    };

    return colorValues[status] || '#6b7280'; // gray-500 as default
};

/**
 * Get status badge style with proper colors
 */
export const getStatusBadgeStyle = (status: UIBookingStatus): React.CSSProperties => {
    const color = getStatusColorValue(status);
    const variant = getStatusBadgeVariant(status);

    // For destructive variant, use different styling
    if (variant === 'destructive') {
        return {
            backgroundColor: color,
            color: 'white',
            borderColor: color
        };
    }

    // For outline variant, use border and text color
    if (variant === 'outline') {
        return {
            backgroundColor: 'transparent',
            color: color,
            borderColor: color
        };
    }

    // For default/secondary, use light background
    return {
        backgroundColor: color + '20', // Add transparency
        color: color,
        borderColor: color + '40'
    };
};

/**
 * Get CSS class name for status badge (alternative to inline styles)
 */
export const getStatusBadgeClassName = (status: UIBookingStatus): string => {
    const classNames: Record<UIBookingStatus, string> = {
        'pending': 'badge-pending',
        'confirmed': 'badge-confirmed',
        'approved': 'badge-approved',
        'checked_in': 'badge-checked-in',
        'checked_out': 'badge-checked-out',
        'cancelled': 'badge-cancelled',
        'no_show': 'badge-no-show'
    };

    return classNames[status] || 'badge-pending';
};

/**
 * Get Tailwind classes for status (safelist approach)
 */
export const getStatusTailwindClasses = (status: UIBookingStatus): string => {
    const classes: Record<UIBookingStatus, string> = {
        'pending': 'bg-amber-50 text-amber-700 border-amber-200',
        'confirmed': 'bg-blue-50 text-blue-700 border-blue-200',
        'approved': 'bg-green-50 text-green-700 border-green-200',
        'checked_in': 'bg-indigo-50 text-indigo-700 border-indigo-200',
        'checked_out': 'bg-slate-50 text-slate-700 border-slate-200',
        'cancelled': 'bg-rose-50 text-rose-700 border-rose-200',
        'no_show': 'bg-red-50 text-red-700 border-red-200'
    };

    return classes[status] || 'bg-gray-50 text-gray-700 border-gray-200';
};
