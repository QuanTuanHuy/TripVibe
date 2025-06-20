// Base interfaces từ backend
export interface BookingUser {
    id: number;
    lastName: string;
    firstName: string;
    email: string;
    phoneNumber: string;
}

export interface BookingUnit {
    id: number;
    unitId: number;
    quantity: number;
    fullName: string;
    email: string;
    amount: number;
}

export interface BookingPromotion {
    id: number;
    promotionId: number;
    discountAmount: number;
    promotionCode: string;
    promotionName: string;
}

// Re-export from status module for backward compatibility
export type {
    APIBookingStatus as BookingStatus,
    UIBookingStatus
} from './status';

export {
    mapApiStatusToUI,
    mapUIStatusToAPI
} from './status';

// Import for internal use
import type { APIBookingStatus } from './status';

export interface Booking {
    id: number;
    touristId: number;
    accommodationId: number;
    numberOfAdult: number;
    numberOfChild: number;
    paymentId?: number;
    currencyId: number;
    status: APIBookingStatus;
    note: string;
    stayFrom: number; // timestamp
    stayTo: number; // timestamp
    invoiceAmount: number;
    finalAmount: number;
    tourist?: BookingUser;
    units: BookingUnit[];
    promotions?: BookingPromotion[];
}

// Request parameters
export interface BookingParams {
    page?: number;
    pageSize?: number;
    orderBy?: string;
    direct?: 'asc' | 'desc';
    userId?: number;
    status?: string;
    unitId?: number;
    accommodationId?: number;
    startTime?: number;
    endTime?: number;
    dateType?: string;
}

export interface BookingStatisticsRequest {
    accommodationId: number;
    startTime?: number;
    endTime?: number;
}

export interface BookingStatisticsResponse {
    totalBookings: number;
    totalRevenue: number;
    statusCounts: Record<string, number>;
}

export interface CheckInBookingRequest {
    bookingId: number;
    checkInTime?: number;
    notes?: string;
}

export interface CheckOutBookingRequest {
    bookingId: number;
    checkOutTime?: number;
    notes?: string;
    damages?: Array<{
        description: string;
        amount: number;
    }>;
}

// Note: BookingFilters and BookingSummary interfaces have been moved to component level
// as they are UI-specific and not shared across the application

export interface CheckInBookingRequest {
    bookingId: number;
    checkInTime?: number;
    notes?: string;
}

export interface CheckOutBookingRequest {
    bookingId: number;
    checkOutTime?: number;
    notes?: string;
    damages?: Array<{
        description: string;
        amount: number;
    }>;
}

// Note: Status-related functions have been moved to './status'
// Import getStatusDisplayName and getStatusColor from '@/types/booking/status'