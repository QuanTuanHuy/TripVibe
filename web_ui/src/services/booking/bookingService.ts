import { apiClient } from '../api.client';

// Define booking types
export interface Booking {
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
    private basePath = '/booking_service/api/v1';

    // Get all bookings for the current user with filters
    async getBookings(params?: GetBookingsParams): Promise<{ content: Booking[], totalElements: number, totalPages: number }> {
        const response = await apiClient.get<{ content: Booking[], totalElements: number, totalPages: number }>(`${this.basePath}/bookings`, { params });
        return response;
    }

    // Get upcoming bookings (check-in date is in the future)
    async getUpcomingBookings(params?: Omit<GetBookingsParams, 'status'>): Promise<{ content: Booking[], totalElements: number, totalPages: number }> {
        return this.getBookings({ ...params, status: 'UPCOMING' });
    }

    // Get past bookings (check-out date is in the past)
    async getPastBookings(params?: Omit<GetBookingsParams, 'status'>): Promise<{ content: Booking[], totalElements: number, totalPages: number }> {
        return this.getBookings({ ...params, status: 'COMPLETED' });
    }

    // Get booking details
    async getBookingDetails(bookingId: string): Promise<Booking> {
        const response = await apiClient.get<Booking>(`${this.basePath}/bookings/${bookingId}`);
        return response;
    }

    // Cancel booking
    async cancelBooking(bookingId: string): Promise<Booking> {
        const response = await apiClient.post<Booking>(`${this.basePath}/bookings/${bookingId}/cancel`, {});
        return response;
    }
}

// Create and export a singleton instance
export const bookingService = new BookingService();
