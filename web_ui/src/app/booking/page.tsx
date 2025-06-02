"use client";

import React, { useEffect, Suspense } from 'react';
import { useSearchParams } from 'next/navigation';
import { BookingProvider, useBooking } from '@/context/BookingContext';
import BookingWizard from '@/components/BookingFlow/BookingWizard';
import BookingSummary from '@/components/BookingFlow/Components/BookingSummary';
import RoomSelection from '@/components/BookingFlow/Steps/RoomSelection';
import GuestInformation from '@/components/BookingFlow/Steps/GuestInformation';
import BookingReview from '@/components/BookingFlow/Steps/BookingReview';
import Payment from '@/components/BookingFlow/Steps/Payment';
import Confirmation from '@/components/BookingFlow/Steps/Confirmation';
import Header from '@/components/Header';

// This component contains the booking flow logic
function BookingFlowContent() {
    const { state, setHotelInfo, setBookingDates } = useBooking();
    const searchParams = useSearchParams();

    useEffect(() => {
        // Initialize booking with URL parameters
        const hotelId = searchParams?.get('hotelId') || '1';
        const hotelName = searchParams?.get('hotelName') || 'La Passion Classic Hotel';
        const checkIn = searchParams?.get('checkIn');
        const checkOut = searchParams?.get('checkOut');

        setHotelInfo(hotelId, hotelName);

        if (checkIn && checkOut) {
            setBookingDates(new Date(checkIn), new Date(checkOut));
        } else {
            // Default dates (today + 1 day for 1 night)
            const tomorrow = new Date();
            tomorrow.setDate(tomorrow.getDate() + 1);
            const dayAfter = new Date(tomorrow);
            dayAfter.setDate(dayAfter.getDate() + 1);

            setBookingDates(tomorrow, dayAfter);
        }
    }, [searchParams, setHotelInfo, setBookingDates]);

    const renderCurrentStep = () => {
        switch (state.currentStep) {
            case 'room-selection':
                return <RoomSelection />;
            case 'guest-info':
                return <GuestInformation />;
            case 'review':
                return <BookingReview />;
            case 'payment':
                return <Payment />;
            case 'confirmation':
                return <Confirmation />;
            default:
                return <RoomSelection />;
        }
    };

    return (
        <BookingWizard>
            <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
                {/* Main Content */}
                <div className="lg:col-span-2">
                    {renderCurrentStep()}
                </div>

                {/* Sidebar with Booking Summary */}
                <div className="lg:col-span-1">
                    <BookingSummary />
                </div>
            </div>
        </BookingWizard>
    );
}

export default function BookingPage() {
    return (
        <BookingProvider>
            <Header />
            <Suspense fallback={
                <div className="min-h-screen flex items-center justify-center">
                    <div className="text-center">
                        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto mb-4"></div>
                        <p className="text-gray-600">Loading booking page...</p>
                    </div>
                </div>
            }>
                <BookingFlowContent />
            </Suspense>
        </BookingProvider>
    );
}
