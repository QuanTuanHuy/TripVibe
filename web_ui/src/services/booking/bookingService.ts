import { ListDataResponseV2 } from '@/types/common';
import { 
    CreateBookingRequest, 
    BookingResponse, 
    ConfirmBookingResponse, 
    BookingParams,
    BookingStatus as BookingStatusType
} from '@/types/booking';
import { apiClient } from '../api.client';

// Legacy booking interface for backward compatibility
export interface LegacyBooking {
    id: string;
    bookingNumber: string;
    status: string;
    checkInDate: string;
    checkOutDate: string;
    totalAmount: number;
    currency: string;
    createdAt: string;
    updatedAt: string;
    guestCount: number;
    accommodation: {
        id: string;
        name: string;
        type: string;
        address: string;
        city: string;
        country: string;
        imageUrl: string;
        rating: number;
    };
    room: {
        id: string;
        name: string;
        type: string;
    };
    paymentStatus: string;
    isPaid: boolean;
    isCancellable: boolean;
    specialRequests?: string;
}

export type BookingStatus = 'UPCOMING' | 'COMPLETED' | 'CANCELLED';

export interface GetBookingsParams {
    status?: BookingStatus;
    page?: number;
    size?: number;
    sort?: string;
}

class BookingService {
    private basePath = '/booking_service/api/public/v1';

    // Create a new booking
    async createBooking(bookingData: CreateBookingRequest): Promise<BookingResponse> {
        console.log("create bookingData:", bookingData);
        return await apiClient.post<BookingResponse>(`${this.basePath}/bookings`, bookingData);
    }

    // Confirm booking after payment
    async confirmBooking(bookingId: number): Promise<ConfirmBookingResponse> {
        return await apiClient.put<ConfirmBookingResponse>(`${this.basePath}/bookings/${bookingId}/confirm`, {});
    }

    // Get all bookings for the current user with filters
    async getBookings(params?: GetBookingsParams): Promise<ListDataResponseV2<BookingResponse>> {
        return await apiClient.get<ListDataResponseV2<BookingResponse>>(`${this.basePath}/bookings`, { params });
    }

    // Get upcoming bookings (check-in date is in the future)
    async getUpcomingBookings(params?: Omit<GetBookingsParams, 'status'>): Promise<ListDataResponseV2<BookingResponse>> {
        return this.getBookings({ ...params, status: 'UPCOMING' });
    }

    // Get past bookings (check-out date is in the past)
    async getPastBookings(params?: Omit<GetBookingsParams, 'status'>): Promise<ListDataResponseV2<BookingResponse>> {
        return this.getBookings({ ...params, status: 'COMPLETED' });
    }

    // Get booking details
    async getBookingDetails(bookingId: number): Promise<BookingResponse> {
        return await apiClient.get<BookingResponse>(`${this.basePath}/bookings/${bookingId}`);
    }

    // Cancel booking
    async cancelBooking(bookingId: number): Promise<BookingResponse> {
        return await apiClient.post<BookingResponse>(`${this.basePath}/bookings/${bookingId}/cancel`, {});
    }

    // Approve booking (admin/host only)
    async approveBooking(bookingId: number): Promise<ConfirmBookingResponse> {
        return await apiClient.put<ConfirmBookingResponse>(`${this.basePath}/bookings/${bookingId}/approve`, {});
    }

    // Reject booking (admin/host only)
    async rejectBooking(bookingId: number): Promise<ConfirmBookingResponse> {
        return await apiClient.put<ConfirmBookingResponse>(`${this.basePath}/bookings/${bookingId}/reject`, {});
    }

    // Check-in booking
    async checkInBooking(bookingId: number): Promise<ConfirmBookingResponse> {
        return await apiClient.put<ConfirmBookingResponse>(`${this.basePath}/bookings/${bookingId}/check-in`, {});
    }

    // Check-out booking
    async checkOutBooking(bookingId: number): Promise<ConfirmBookingResponse> {
        return await apiClient.put<ConfirmBookingResponse>(`${this.basePath}/bookings/${bookingId}/check-out`, {});
    }
}

// Create and export a singleton instance
export const bookingService = new BookingService();
