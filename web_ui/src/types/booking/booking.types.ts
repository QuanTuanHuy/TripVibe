export interface BookingUnit {
    id?: number;
    unitId: number;
    quantity: number;
    fullName: string;
    email: string;
    amount: number;
}

export interface BookingPromotion {
    id?: number;
    promotionId: number;
    promotionName?: string;
    discountPercentage?: number;
}

export interface Tourist {
    touristID: number;
    firstName: string;
    lastName: string;
    email: string;
    phoneNumber?: string;
    address?: string;
    dateOfBirth?: string;
    nationality?: string;
}

export interface CreateBookingRequest {
    accommodationId: number;
    currencyId?: number;
    numberOfAdult: number;
    numberOfChild: number;
    note?: string;
    stayFrom: number; // Unix timestamp (epoch seconds)
    stayTo: number;
    invoiceAmount?: number;
    finalInvoiceAmount?: number;
    tourist: Tourist;
    units: BookingUnit[];
    promotions?: BookingPromotion[];
}

export interface BookingResponse {
    id: number;
    status: string;
    touristId: number;
    accommodationId: number;
    numberOfAdult: number;
    numberOfChild: number;
    paymentId?: number;
    currencyId: number;
    note?: string;
    stayFrom: number;
    stayTo: number;
    invoiceAmount: number;
    finalAmount: number;
    tourist?: Tourist;
    units: BookingUnit[];
    promotions?: BookingPromotion[];
}

export interface ConfirmBookingResponse {
    success: boolean;
    message: string;
    bookingId: number;
}

export type BookingStatus = 'PENDING' | 'CONFIRMED' | 'APPROVED' | 'CHECKED_IN' | 'CHECKED_OUT' | 'CANCELLED' | 'REJECTED';

export interface BookingParams {
    page?: number;
    pageSize?: number;
    status?: BookingStatus;
    accommodationId?: number;
    touristId?: number;
}
