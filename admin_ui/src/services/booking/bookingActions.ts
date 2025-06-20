/**
 * Booking Actions Service
 * Handles all booking status change actions
 */

import { bookingService } from '@/services';
import {
    UIBookingStatus,
    mapUIStatusToAPI,
    isValidStatusTransition
} from '@/types/booking/status';

/**
 * Execute booking status change based on the new status
 */
export const executeBookingStatusChange = async (
    bookingId: number,
    newStatus: UIBookingStatus,
    currentStatus?: UIBookingStatus
): Promise<boolean> => {
    // Validate transition if current status is provided
    if (currentStatus && !isValidStatusTransition(currentStatus, newStatus)) {
        throw new Error(`Invalid status transition from ${currentStatus} to ${newStatus}`);
    }

    const apiStatus = mapUIStatusToAPI(newStatus);

    switch (apiStatus) {
        case 'CONFIRMED':
            return await bookingService.confirmBooking(bookingId);

        case 'APPROVED':
            return await bookingService.approveBooking(bookingId);

        case 'CANCELLED':
            // Determine if this is a rejection or cancellation
            if (currentStatus && ['confirmed', 'approved'].includes(currentStatus)) {
                // This is a rejection by host
                return await bookingService.rejectBooking(bookingId);
            } else {
                // This is a regular cancellation
                return await bookingService.cancelBooking(bookingId);
            }

        case 'CHECKED_IN':
            return await bookingService.checkInBooking(bookingId, {});

        case 'CHECKED_OUT':
            return await bookingService.checkOutBooking(bookingId, {});

        default:
            throw new Error(`Unsupported status change to: ${apiStatus}`);
    }
};

/**
 * Get the appropriate action name for a status change
 */
export const getStatusActionName = (
    newStatus: UIBookingStatus,
    currentStatus?: UIBookingStatus
): string => {
    switch (newStatus) {
        case 'confirmed':
            return 'xác nhận';
        case 'approved':
            return 'duyệt';
        case 'cancelled':
            if (currentStatus && ['confirmed', 'approved'].includes(currentStatus)) {
                return 'từ chối';
            }
            return 'hủy';
        case 'checked_in':
            return 'check-in';
        case 'checked_out':
            return 'check-out';
        default:
            return 'cập nhật';
    }
};

/**
 * Check if user has permission to change status
 * This can be extended with proper role-based permissions
 */
export const canChangeStatus = (
    fromStatus: UIBookingStatus,
    toStatus: UIBookingStatus,
    userRole?: string
): boolean => {
    // Basic validation - can be extended with role checks
    if (!isValidStatusTransition(fromStatus, toStatus)) {
        return false;
    }

    // Example: Only hosts can approve/reject
    if (['approved'].includes(toStatus) && userRole !== 'host' && userRole !== 'admin') {
        return false;
    }

    return true;
};

export default {
    executeBookingStatusChange,
    getStatusActionName,
    canChangeStatus
};
