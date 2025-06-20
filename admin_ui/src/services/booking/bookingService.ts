import {ListDataResponseV2} from '@/types/common';
import apiClient from '../apiClient';
import {
    Booking,
    BookingParams,
    BookingStatisticsRequest,
    BookingStatisticsResponse,
    CheckInBookingRequest,
    CheckOutBookingRequest
} from '@/types/booking';

const BOOKING_PATH = '/booking_service/api/public/v1';

const bookingService = {
    /**
     * Lấy danh sách tất cả bookings với params
     */
    getAllBookings: async (params?: BookingParams): Promise<ListDataResponseV2<Booking>> => {
        const queryParams = new URLSearchParams();

        if (params?.page !== undefined) queryParams.append('page', params.page.toString());
        if (params?.pageSize !== undefined) queryParams.append('pageSize', params.pageSize.toString());
        if (params?.orderBy) queryParams.append('orderBy', params.orderBy);
        if (params?.direct) queryParams.append('direct', params.direct);
        if (params?.userId) queryParams.append('userId', params.userId.toString());
        if (params?.status) queryParams.append('status', params.status);
        if (params?.unitId) queryParams.append('unitId', params.unitId.toString());
        if (params?.accommodationId) queryParams.append('accommodationId', params.accommodationId.toString());
        if (params?.startTime) queryParams.append('startTime', params.startTime.toString());
        if (params?.endTime) queryParams.append('endTime', params.endTime.toString());
        if (params?.dateType) queryParams.append('dateType', params.dateType);

        const queryString = queryParams.toString() ? `?${queryParams.toString()}` : '';

        return apiClient.get<ListDataResponseV2<Booking>>(`${BOOKING_PATH}/bookings${queryString}`);
    },

    /**
     * Lấy chi tiết một booking
     */
    getBookingDetail: async (bookingId: number): Promise<Booking> => {
        return apiClient.get<Booking>(`${BOOKING_PATH}/bookings/${bookingId}`);
    },

    /**
     * Xác nhận booking (confirm)
     */
    confirmBooking: async (bookingId: number): Promise<boolean> => {
        return apiClient.put<boolean>(`${BOOKING_PATH}/bookings/${bookingId}/confirm`, {});
    },

    /**
     * Duyệt booking (approve) - chỉ dành cho host/admin
     */
    approveBooking: async (bookingId: number): Promise<boolean> => {
        return apiClient.put<boolean>(`${BOOKING_PATH}/bookings/${bookingId}/approve`, {});
    },

    /**
     * Từ chối booking (reject) - chỉ dành cho host/admin
     */
    rejectBooking: async (bookingId: number): Promise<boolean> => {
        return apiClient.put<boolean>(`${BOOKING_PATH}/bookings/${bookingId}/reject`, {});
    },

    /**
     * Hủy booking (cancel)
     */
    cancelBooking: async (bookingId: number): Promise<boolean> => {
        return apiClient.put<boolean>(`${BOOKING_PATH}/bookings/${bookingId}/cancel`, {});
    },

    /**
     * Check-in booking
     */
    checkInBooking: async (bookingId: number, checkInData: Omit<CheckInBookingRequest, 'bookingId'>): Promise<any> => {
        const requestData = {
            ...checkInData,
            bookingId
        };
        return apiClient.put<any>(`${BOOKING_PATH}/bookings/${bookingId}/checkin`, requestData);
    },

    /**
     * Check-out booking
     */
    checkOutBooking: async (bookingId: number, checkOutData: Omit<CheckOutBookingRequest, 'bookingId'>): Promise<any> => {
        const requestData = {
            ...checkOutData,
            bookingId
        };
        return apiClient.put<any>(`${BOOKING_PATH}/bookings/${bookingId}/checkout`, requestData);
    },

    /**
     * Lấy thống kê booking cho host
     */
    getBookingStatistics: async (params: BookingStatisticsRequest): Promise<BookingStatisticsResponse> => {
        const queryParams = new URLSearchParams();
        queryParams.append('accommodationId', params.accommodationId.toString());

        if (params.startTime) queryParams.append('startTime', params.startTime.toString());
        if (params.endTime) queryParams.append('endTime', params.endTime.toString());

        const queryString = queryParams.toString() ? `?${queryParams.toString()}` : '';

        return apiClient.get<BookingStatisticsResponse>(`${BOOKING_PATH}/bookings/statistics${queryString}`);
    },

    /**
     * Helper functions để format dữ liệu
     */
    formatCurrency: (amount: number, currency: string = 'VND'): string => {
        return new Intl.NumberFormat('vi-VN', {
            style: 'currency',
            currency: currency
        }).format(amount);
    },

    /**
     * Convert timestamp to date string
     */
    formatDate: (timestamp: number): string => {
        return new Date(timestamp * 1000).toLocaleDateString('vi-VN');
    },

    /**
     * Convert timestamp to datetime string
     */
    formatDateTime: (timestamp: number): string => {
        return new Date(timestamp * 1000).toLocaleString('vi-VN');
    },

    /**
     * Calculate total nights
     */
    calculateNights: (stayFrom: number, stayTo: number): number => {
        const diffTime = (stayTo - stayFrom) * 1000; // convert to milliseconds
        return Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    },

    /**
     * Get booking duration in human readable format
     */
    getBookingDuration: (stayFrom: number, stayTo: number): string => {
        const nights = bookingService.calculateNights(stayFrom, stayTo);
        return `${nights} đêm`;
    },
};

export default bookingService;
