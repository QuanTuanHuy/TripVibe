import { CreateBookingRequest, BookingResponse, Tourist, BookingUnit, BookingPromotion } from '@/types/booking';
import { dateToEpochSeconds } from './dateTransform';

// Frontend booking state interface for the booking flow
export interface BookingFlowData {
    accommodationId: number;
    checkInDate: Date;
    checkOutDate: Date;
    numberOfAdult: number;
    numberOfChild: number;
    selectedUnits: Array<{
        unitId: number;
        quantity: number;
        unitName: string;
        pricePerNight: number;
        totalPrice: number;
    }>;
    guestInfo: {
        firstName: string;
        lastName: string;
        email: string;
        phoneNumber: string;
        address: string;
        dateOfBirth: string;
        nationality: string;
        touristID: string;
    };
    subTotal: number;
    finalTotal: number;
    specialRequests?: string;
    appliedPromotions?: Array<{
        promotionId: number;
        code: string;
        discountAmount: number;
    }>;
}

/**
 * Transform frontend booking flow data to backend CreateBookingRequest format
 */
export function transformToCreateBookingRequest(bookingData: BookingFlowData): CreateBookingRequest {
    // Convert touristID from string to number for backend compatibility
    const touristID = parseInt(bookingData.guestInfo.touristID);
    if (isNaN(touristID)) {
        throw new Error('Invalid tourist ID format');
    }

    const tourist: Tourist = {
        touristID: touristID, // Backend expects int64
        firstName: bookingData.guestInfo.firstName,
        lastName: bookingData.guestInfo.lastName,
        email: bookingData.guestInfo.email,
        // Optional fields - backend will store additional info in user profile
        phoneNumber: bookingData.guestInfo.phoneNumber,
        address: bookingData.guestInfo.address,
        dateOfBirth: bookingData.guestInfo.dateOfBirth,
        nationality: bookingData.guestInfo.nationality,
    };

    // Each booking unit has guest info per unit (like booking.com)
    const units: BookingUnit[] = bookingData.selectedUnits.map(unit => ({
        unitId: unit.unitId,
        quantity: unit.quantity,
        fullName: `${bookingData.guestInfo.firstName} ${bookingData.guestInfo.lastName}`,
        email: bookingData.guestInfo.email,
        amount: unit.totalPrice,
    }));

    const promotions: BookingPromotion[] | undefined = bookingData.appliedPromotions?.map(promo => ({
        promotionId: promo.promotionId,
        promotionName: promo.code,
        discountPercentage: Math.round((promo.discountAmount / bookingData.selectedUnits.reduce((total, unit) => total + unit.totalPrice, 0)) * 100)
    }));

    return {
        accommodationId: bookingData.accommodationId,
        numberOfAdult: bookingData.numberOfAdult,
        numberOfChild: bookingData.numberOfChild,
        note: bookingData.specialRequests,
        stayFrom: dateToEpochSeconds(bookingData.checkInDate),
        stayTo: dateToEpochSeconds(bookingData.checkOutDate),
        invoiceAmount: bookingData.subTotal,
        finalInvoiceAmount: bookingData.finalTotal,
        tourist,
        units,
        promotions,
    };
}

/**
 * Transform backend BookingResponse to frontend display format
 */
export function transformBookingResponseForDisplay(booking: BookingResponse) {
    return {
        id: booking.id,
        status: booking.status,
        accommodationId: booking.accommodationId,
        numberOfAdult: booking.numberOfAdult,
        numberOfChild: booking.numberOfChild,
        checkInDate: new Date(booking.stayFrom * 1000),
        checkOutDate: new Date(booking.stayTo * 1000),
        totalAmount: booking.finalAmount || booking.invoiceAmount,
        currencyId: booking.currencyId,
        note: booking.note,
        tourist: booking.tourist,
        units: booking.units,
        promotions: booking.promotions,
        touristId: booking.touristId,
        paymentId: booking.paymentId,
    };
}


export function calculateTotalAmount(selectedUnits: BookingFlowData['selectedUnits']): number {
    return selectedUnits.reduce((total, unit) => total + unit.totalPrice, 0);
}

/**
 * Validate booking data before submission
 */
export function validateBookingData(bookingData: BookingFlowData): { isValid: boolean; errors: string[] } {
    const errors: string[] = [];

    // Check required fields
    if (!bookingData.accommodationId) {
        errors.push('Accommodation ID is required');
    }

    if (!bookingData.checkInDate || !bookingData.checkOutDate) {
        errors.push('Check-in and check-out dates are required');
    }

    if (bookingData.checkInDate >= bookingData.checkOutDate) {
        errors.push('Check-out date must be after check-in date');
    }

    if (bookingData.numberOfAdult < 1) {
        errors.push('At least one adult is required');
    }

    if (bookingData.selectedUnits.length === 0) {
        errors.push('At least one room/unit must be selected');
    }

    // Validate guest information
    const { guestInfo } = bookingData;
    if (!guestInfo.firstName?.trim()) {
        errors.push('First name is required');
    }

    if (!guestInfo.lastName?.trim()) {
        errors.push('Last name is required');
    }

    if (!guestInfo.email?.trim()) {
        errors.push('Email is required');
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(guestInfo.email)) {
        errors.push('Valid email is required');
    }

    if (!guestInfo.phoneNumber?.trim()) {
        errors.push('Phone number is required');
    }

    if (!guestInfo.address?.trim()) {
        errors.push('Address is required');
    }

    if (!guestInfo.dateOfBirth?.trim()) {
        errors.push('Date of birth is required');
    }

    if (!guestInfo.nationality?.trim()) {
        errors.push('Nationality is required');
    }    if (!guestInfo.touristID?.trim()) {
        errors.push('Tourist ID is required');
    } else {
        // Validate that touristID can be converted to a number (for backend compatibility)
        const touristIDNumber = parseInt(guestInfo.touristID);
        if (isNaN(touristIDNumber) || touristIDNumber <= 0) {
            errors.push('Tourist ID must be a valid positive number');
        }
    }

    return {
        isValid: errors.length === 0,
        errors,
    };
}
