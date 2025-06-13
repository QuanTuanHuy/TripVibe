"use client";

import React, { createContext, useContext, useReducer, ReactNode, useCallback, useMemo } from 'react';
import { bookingService } from '@/services/booking/bookingService';
import { transformToCreateBookingRequest, validateBookingData, BookingFlowData } from '@/utils/booking';
import type { BookingResponse } from '@/types/booking';

// Types
interface Bed {
    type: string;
    count: number;
}

interface Occupancy {
    adults: number;
    children: number;
}

interface Room {
    id: number;
    name: string;
    size: string;
    beds: Bed[];
    occupancy: Occupancy;
    amenities: string[];
    breakfast: boolean;
    freeCancellation: boolean;
    prepayment: boolean;
    price: number;
    remainingRooms?: number;
    images: string[];
    specialOffers?: string[];
}

interface SelectedRoom {
    roomId: number;
    roomName: string;
    quantity: number;
    price: number;
    totalPrice: number;
    room: Room;
}

interface GuestInfo {
    touristID: string; // Keep as string in UI for form handling, will convert to number in transform
    firstName: string;
    lastName: string;
    email: string;
    phoneNumber: string;
    address: string;
    dateOfBirth: string;
    nationality: string;
    specialRequests?: string;
}

interface PaymentInfo {
    method: 'credit-card' | 'internet-banking' | 'e-wallet';
    cardNumber?: string;
    cardHolder?: string;
    expiryDate?: string;
    cvv?: string;
    bankName?: string;
    accountNumber?: string;
    walletProvider?: string;
    walletAccount?: string;
    billingAddress?: {
        street: string;
        city: string;
        state: string;
        zipCode: string;
        country: string;
    };
}

interface BookingDates {
    checkIn: Date;
    checkOut: Date;
    nights: number;
}

interface BookingState {
    // Step management
    currentStep: 'room-selection' | 'guest-info' | 'review' | 'payment' | 'confirmation';

    // Hotel info
    hotelId: string;
    hotelName: string;

    // Booking dates
    bookingDates: BookingDates | null;

    // Room selection
    selectedRooms: SelectedRoom[];
    // Guest information
    guestInfo: GuestInfo | null;

    // Payment information
    paymentInfo: PaymentInfo | null;    // Booking details
    bookingId: string | null;
    bookingResponse: BookingResponse | null;

    // Payment
    paymentStatus: 'idle' | 'processing' | 'success' | 'failed';
    paymentMethod: string | null;

    // Loading states
    isLoading: boolean;
    error: string | null;

    // Pricing
    subtotal: number;
    taxes: number;
    fees: number;
    total: number;
}

type BookingAction =
    | { type: 'SET_STEP'; payload: BookingState['currentStep'] }
    | { type: 'SET_HOTEL_INFO'; payload: { hotelId: string; hotelName: string } }
    | { type: 'SET_BOOKING_DATES'; payload: BookingDates }
    | { type: 'SET_SELECTED_ROOMS'; payload: SelectedRoom[] }
    | { type: 'ADD_ROOM'; payload: SelectedRoom }
    | { type: 'REMOVE_ROOM'; payload: number }
    | { type: 'UPDATE_ROOM_QUANTITY'; payload: { roomId: number; quantity: number } }
    | { type: 'SET_GUEST_INFO'; payload: GuestInfo }
    | { type: 'SET_PAYMENT_INFO'; payload: PaymentInfo }
    | { type: 'SET_BOOKING_ID'; payload: string }
    | { type: 'SET_BOOKING_RESPONSE'; payload: BookingResponse }
    | { type: 'SET_PAYMENT_STATUS'; payload: BookingState['paymentStatus'] }
    | { type: 'SET_PAYMENT_METHOD'; payload: string }
    | { type: 'SET_LOADING'; payload: boolean }
    | { type: 'SET_ERROR'; payload: string | null }
    | { type: 'CALCULATE_PRICING' }
    | { type: 'RESET_BOOKING' };

const initialState: BookingState = {
    currentStep: 'room-selection',
    hotelId: '',
    hotelName: '',
    bookingDates: null,
    selectedRooms: [],
    guestInfo: null,
    paymentInfo: null,
    bookingId: null,
    bookingResponse: null,
    paymentStatus: 'idle',
    paymentMethod: null,
    isLoading: false,
    error: null,
    subtotal: 0,
    taxes: 0,
    fees: 0,
    total: 0,
};

function bookingReducer(state: BookingState, action: BookingAction): BookingState {
    switch (action.type) {
        case 'SET_STEP':
            return { ...state, currentStep: action.payload };

        case 'SET_HOTEL_INFO':
            return {
                ...state,
                hotelId: action.payload.hotelId,
                hotelName: action.payload.hotelName
            };

        case 'SET_BOOKING_DATES':
            return { ...state, bookingDates: action.payload };

        case 'SET_SELECTED_ROOMS':
            const newState = { ...state, selectedRooms: action.payload };
            return calculatePricing(newState);

        case 'ADD_ROOM':
            const existingRoomIndex = state.selectedRooms.findIndex(r => r.roomId === action.payload.roomId);
            if (existingRoomIndex >= 0) {
                const updatedRooms = [...state.selectedRooms];
                updatedRooms[existingRoomIndex] = {
                    ...updatedRooms[existingRoomIndex],
                    quantity: updatedRooms[existingRoomIndex].quantity + action.payload.quantity,
                    totalPrice: (updatedRooms[existingRoomIndex].quantity + action.payload.quantity) * action.payload.price
                };
                return calculatePricing({ ...state, selectedRooms: updatedRooms });
            } else {
                return calculatePricing({
                    ...state,
                    selectedRooms: [...state.selectedRooms, action.payload]
                });
            }

        case 'REMOVE_ROOM':
            return calculatePricing({
                ...state,
                selectedRooms: state.selectedRooms.filter(r => r.roomId !== action.payload)
            });

        case 'UPDATE_ROOM_QUANTITY':
            const updatedRooms = state.selectedRooms.map(room => {
                if (room.roomId === action.payload.roomId) {
                    return {
                        ...room,
                        quantity: action.payload.quantity,
                        totalPrice: action.payload.quantity * room.price
                    };
                }
                return room;
            }).filter(room => room.quantity > 0);
            return calculatePricing({ ...state, selectedRooms: updatedRooms });

        case 'SET_GUEST_INFO':
            return { ...state, guestInfo: action.payload };

        case 'SET_PAYMENT_INFO':
            return { ...state, paymentInfo: action.payload };

        case 'SET_BOOKING_ID':
            return { ...state, bookingId: action.payload };

        case 'SET_BOOKING_RESPONSE':
            return {
                ...state,
                bookingResponse: action.payload,
                bookingId: action.payload.id.toString()
            };

        case 'SET_PAYMENT_STATUS':
            return { ...state, paymentStatus: action.payload };

        case 'SET_PAYMENT_METHOD':
            return { ...state, paymentMethod: action.payload };

        case 'SET_LOADING':
            return { ...state, isLoading: action.payload };

        case 'SET_ERROR':
            return { ...state, error: action.payload };

        case 'CALCULATE_PRICING':
            return calculatePricing(state);

        case 'RESET_BOOKING':
            return { ...initialState };

        default:
            return state;
    }
}

function calculatePricing(state: BookingState): BookingState {
    const subtotal = state.selectedRooms.reduce((total, room) => total + room.totalPrice, 0);
    const nights = state.bookingDates?.nights || 1;
    const totalSubtotal = subtotal * nights;

    // Calculate taxes (10% VAT)
    const taxes = Math.round(totalSubtotal * 0.1);

    // Calculate fees (service fee 5%)
    const fees = Math.round(totalSubtotal * 0.05);

    const total = totalSubtotal + taxes + fees;

    return {
        ...state,
        subtotal: totalSubtotal,
        taxes,
        fees,
        total
    };
}

interface BookingContextType {
    state: BookingState;
    dispatch: React.Dispatch<BookingAction>;

    // Helper functions
    goToStep: (step: BookingState['currentStep']) => void;
    addRoom: (room: Room, quantity: number) => void;
    removeRoom: (roomId: number) => void;
    updateRoomQuantity: (roomId: number, quantity: number) => void;
    getTotalRooms: () => number;
    hasSelectedRooms: () => boolean;
    setHotelInfo: (hotelId: string, hotelName: string) => void;
    setBookingDates: (checkIn: Date, checkOut: Date) => void;
    setGuestInfo: (guestInfo: GuestInfo) => void;
    setPaymentInfo: (paymentInfo: PaymentInfo) => void;
    resetBooking: () => void;
    createBooking: () => Promise<void>;
    confirmBooking: () => Promise<void>;
}

const BookingContext = createContext<BookingContextType | undefined>(undefined);

export function BookingProvider({ children }: { children: ReactNode }) {
    const [state, dispatch] = useReducer(bookingReducer, initialState);

    const goToStep = useCallback((step: BookingState['currentStep']) => {
        dispatch({ type: 'SET_STEP', payload: step });
    }, []);

    const addRoom = useCallback((room: Room, quantity: number) => {
        const selectedRoom: SelectedRoom = {
            roomId: room.id,
            roomName: room.name,
            quantity,
            price: room.price,
            totalPrice: room.price * quantity,
            room
        };
        dispatch({ type: 'ADD_ROOM', payload: selectedRoom });
    }, []);

    const removeRoom = useCallback((roomId: number) => {
        dispatch({ type: 'REMOVE_ROOM', payload: roomId });
    }, []);

    const updateRoomQuantity = useCallback((roomId: number, quantity: number) => {
        dispatch({ type: 'UPDATE_ROOM_QUANTITY', payload: { roomId, quantity } });
    }, []);

    const getTotalRooms = useCallback(() => {
        return state.selectedRooms.reduce((total, room) => total + room.quantity, 0);
    }, [state.selectedRooms]);

    const hasSelectedRooms = useCallback(() => {
        return state.selectedRooms.length > 0;
    }, [state.selectedRooms]);

    const setHotelInfo = useCallback((hotelId: string, hotelName: string) => {
        dispatch({ type: 'SET_HOTEL_INFO', payload: { hotelId, hotelName } });
    }, []);

    const setBookingDates = useCallback((checkIn: Date, checkOut: Date) => {
        const nights = Math.ceil((checkOut.getTime() - checkIn.getTime()) / (1000 * 60 * 60 * 24));
        dispatch({
            type: 'SET_BOOKING_DATES',
            payload: { checkIn, checkOut, nights }
        });
    }, []);

    const setGuestInfo = useCallback((guestInfo: GuestInfo) => {
        dispatch({ type: 'SET_GUEST_INFO', payload: guestInfo });
    }, []);

    const setPaymentInfo = useCallback((paymentInfo: PaymentInfo) => {
        dispatch({ type: 'SET_PAYMENT_INFO', payload: paymentInfo });
    }, []);

    const resetBooking = useCallback(() => {
        dispatch({ type: 'RESET_BOOKING' });
    }, []);

    const createBooking = useCallback(async () => {
        try {
            dispatch({ type: 'SET_LOADING', payload: true });

            // Check required data
            if (!state.guestInfo || !state.bookingDates || state.selectedRooms.length === 0) {
                dispatch({ type: 'SET_ERROR', payload: 'Missing required booking information' });
                return;
            }

            const bookingData: BookingFlowData = {
                accommodationId: parseInt(state.hotelId),
                checkInDate: state.bookingDates.checkIn,
                checkOutDate: state.bookingDates.checkOut,
                numberOfAdult: state.selectedRooms.reduce((total, room) => total + room.room.occupancy.adults * room.quantity, 0),
                numberOfChild: state.selectedRooms.reduce((total, room) => total + room.room.occupancy.children * room.quantity, 0),
                selectedUnits: state.selectedRooms.map(room => ({
                    unitId: room.roomId,
                    quantity: room.quantity,
                    unitName: room.roomName,
                    pricePerNight: room.price,
                    totalPrice: room.totalPrice,
                })),
                subTotal: state.subtotal,
                finalTotal: state.total,
                guestInfo: state.guestInfo,
                specialRequests: state.guestInfo.specialRequests,
            };

            // Validate booking data
            const validation = validateBookingData(bookingData);
            if (!validation.isValid) {
                dispatch({ type: 'SET_ERROR', payload: validation.errors.join(', ') });
                return;
            }

            // Create booking
            const response: BookingResponse = await bookingService.createBooking(transformToCreateBookingRequest(bookingData));
            dispatch({ type: 'SET_BOOKING_RESPONSE', payload: response });
            dispatch({ type: 'SET_STEP', payload: 'payment' });

        } catch (error: any) {
            dispatch({ type: 'SET_ERROR', payload: error.message });
        } finally {
            dispatch({ type: 'SET_LOADING', payload: false });
        }
    }, [state.guestInfo, state.bookingDates, state.selectedRooms, state.hotelId]);

    const confirmBooking = useCallback(async () => {
        try {
            if (!state.bookingId) {
                dispatch({ type: 'SET_ERROR', payload: 'No booking ID available' });
                return;
            }

            dispatch({ type: 'SET_LOADING', payload: true });
            dispatch({ type: 'SET_PAYMENT_STATUS', payload: 'processing' });

            // Confirm booking
            await bookingService.confirmBooking(parseInt(state.bookingId));
            dispatch({ type: 'SET_PAYMENT_STATUS', payload: 'success' });
            dispatch({ type: 'SET_STEP', payload: 'confirmation' });
        } catch (error: any) {
            dispatch({ type: 'SET_ERROR', payload: error.message });
            dispatch({ type: 'SET_PAYMENT_STATUS', payload: 'failed' });
        } finally {
            dispatch({ type: 'SET_LOADING', payload: false });
        }
    }, [state.bookingId]);

    const value: BookingContextType = useMemo(() => ({
        state,
        dispatch,
        goToStep,
        addRoom,
        removeRoom,
        updateRoomQuantity,
        getTotalRooms,
        hasSelectedRooms,
        setHotelInfo,
        setBookingDates,
        setGuestInfo,
        setPaymentInfo,
        resetBooking,
        createBooking,
        confirmBooking,
    }), [
        state,
        goToStep,
        addRoom,
        removeRoom,
        updateRoomQuantity,
        getTotalRooms,
        hasSelectedRooms,
        setHotelInfo,
        setBookingDates,
        setGuestInfo,
        setPaymentInfo,
        resetBooking,
        createBooking,
        confirmBooking
    ]);

    return (
        <BookingContext.Provider value={value}>
            {children}
        </BookingContext.Provider>
    );
}

export function useBooking() {
    const context = useContext(BookingContext);
    if (context === undefined) {
        throw new Error('useBooking must be used within a BookingProvider');
    }
    return context;
}

export type { BookingState, SelectedRoom, GuestInfo, PaymentInfo, Room, BookingDates };
