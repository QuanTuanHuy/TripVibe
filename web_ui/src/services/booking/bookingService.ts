import { ListDataResponse, ListDataResponseV2 } from '@/types/common';
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

export interface BookingV2 {
    id: number;
    touristId: number;
    accommodationId: number;
    numberOfAdult: number;
    numberOfChild: number;
    paymentId: number | null;
    currencyId: number;
    status: string;
    note: string;
    stayFrom: number; // Unix timestamp
    stayTo: number; // Unix timestamp
    invoiceAmount: number;
    finalAmount: number;
    tourist: {
        id: number;
        lastName: string;
        firstName: string;
        email: string;
        phoneNumber: string;
    };
    units: Array<{
        id: number;
        unitId: number;
        quantity: number;
        fullName: string;
        email: string;
        amount: number;
    }>;
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

    // Get all bookings for the current user with filters
    async getBookings(params?: GetBookingsParams): Promise<ListDataResponseV2<BookingV2>> {
        return await apiClient.get<ListDataResponseV2<BookingV2>>(`${this.basePath}/bookings`, { params });
    }

    // Get upcoming bookings (check-in date is in the future)
    async getUpcomingBookings(params?: Omit<GetBookingsParams, 'status'>): Promise<ListDataResponseV2<BookingV2>> {
        return this.getBookings({ ...params, status: 'UPCOMING' });
    }

    // Get past bookings (check-out date is in the past)
    async getPastBookings(params?: Omit<GetBookingsParams, 'status'>): Promise<ListDataResponseV2<BookingV2>> {
        return this.getBookings({ ...params, status: 'COMPLETED' });
    }

    // Get booking details
    async getBookingDetails(bookingId: number): Promise<BookingV2> {
        return await apiClient.get<BookingV2>(`${this.basePath}/bookings/${bookingId}`);
    }

    // Cancel booking
    async cancelBooking(bookingId: string): Promise<Booking> {
        const response = await apiClient.post<Booking>(`${this.basePath}/bookings/${bookingId}/cancel`, {});
        return response;
    }
}

// Create and export a singleton instance
export const bookingService = new BookingService();
