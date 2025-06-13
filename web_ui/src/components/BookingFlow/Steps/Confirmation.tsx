"use client";

import React, { useEffect, useState } from 'react';
import { useBooking } from '@/context/BookingContext';
import { CheckCircleIcon, CalendarIcon, MapPinIcon, UsersIcon, PrinterIcon, ShareIcon } from '@heroicons/react/24/solid';
import { EnvelopeIcon, PhoneIcon } from '@heroicons/react/24/outline';

export default function Confirmation() {
    const { state, resetBooking } = useBooking();
    const [bookingReference, setBookingReference] = useState('');

    useEffect(() => {
        if (state.bookingResponse?.id) {
            setBookingReference(`BK${state.bookingResponse.id.toString().padStart(6, '0')}`);
        } else if (state.bookingId) {
            setBookingReference(`BK${state.bookingId.padStart(6, '0')}`);
        } else {
            const ref = `HB${Date.now().toString().slice(-6)}`;
            setBookingReference(ref);
        }
    }, [state.bookingResponse, state.bookingId]);

    const formatDate = (date: Date) => {
        return new Intl.DateTimeFormat('en-US', {
            weekday: 'long',
            year: 'numeric',
            month: 'long',
            day: 'numeric'
        }).format(date);
    };

    const formatPrice = (amount: number) => {
        return new Intl.NumberFormat('en-US', {
            style: 'currency',
            currency: 'USD'
        }).format(amount);
    };

    const handlePrint = () => {
        if (typeof window !== 'undefined') {
            window.print();
        }
    };

    const handleShare = async () => {
        if (typeof window === 'undefined') return;

        const shareData = {
            title: 'Hotel Booking Confirmation',
            text: `Booking confirmed at ${state.hotelName}! Reference: ${bookingReference}`,
            url: window.location.href
        };

        if (navigator.share && navigator.canShare && navigator.canShare(shareData)) {
            try {
                await navigator.share(shareData);
                // eslint-disable-next-line @typescript-eslint/no-unused-vars
            } catch (err) {
                // Fallback to clipboard
                if (navigator.clipboard) {
                    navigator.clipboard.writeText(`Booking confirmed at ${state.hotelName}! Reference: ${bookingReference} - ${window.location.href}`);
                    alert('Booking details copied to clipboard!');
                }
            }
        } else {
            // Fallback: copy to clipboard
            if (navigator.clipboard) {
                try {
                    await navigator.clipboard.writeText(`Booking confirmed at ${state.hotelName}! Reference: ${bookingReference} - ${window.location.href}`);
                    alert('Booking details copied to clipboard!');
                } catch (err) {
                    console.error('Failed to copy to clipboard:', err);
                }
            }
        }
    };
    const handleNewBooking = () => {
        resetBooking();
        if (typeof window !== 'undefined') {
            window.location.href = '/';
        }
    };

    if (!state.bookingDates || !state.guestInfo || state.selectedRooms.length === 0) {
        return (
            <div className="text-center py-12">
                <h2 className="text-2xl font-bold text-gray-900 mb-4">Invalid Booking</h2>
                <p className="text-gray-600 mb-6">No booking information found.</p>
                <button
                    onClick={handleNewBooking}
                    className="bg-blue-600 text-white px-6 py-2 rounded-lg hover:bg-blue-700"
                >
                    Start New Booking
                </button>
            </div>
        );
    }

    return (
        <div className="max-w-4xl mx-auto">
            {/* Success Header */}
            <div className="text-center mb-8">
                <div className="inline-flex items-center justify-center w-16 h-16 bg-green-100 rounded-full mb-4">
                    <CheckCircleIcon className="w-8 h-8 text-green-600" />
                </div>
                <h1 className="text-3xl font-bold text-gray-900 mb-2">Booking Confirmed!</h1>
                <p className="text-lg text-gray-600">
                    Your reservation has been successfully confirmed.
                </p>
            </div>

            {/* Booking Reference */}
            <div className="bg-green-50 border border-green-200 rounded-lg p-6 mb-8 text-center">
                <h2 className="text-xl font-semibold text-green-800 mb-2">Booking Reference</h2>
                <div className="text-2xl font-bold text-green-900 tracking-wider">
                    {bookingReference}
                </div>
                <p className="text-sm text-green-700 mt-2">
                    Please save this reference number for your records
                </p>
            </div>

            {/* Booking Details Grid */}
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-8 mb-8">
                {/* Hotel & Dates */}
                <div className="bg-white border border-gray-200 rounded-lg p-6">
                    <h3 className="text-lg font-semibold text-gray-900 mb-4">Hotel & Dates</h3>

                    <div className="space-y-4">
                        <div className="flex items-start space-x-3">
                            <MapPinIcon className="w-5 h-5 text-gray-400 mt-1" />
                            <div>
                                <p className="font-medium text-gray-900">{state.hotelName}</p>
                                <p className="text-sm text-gray-500">Hotel ID: {state.hotelId}</p>
                            </div>
                        </div>

                        <div className="flex items-start space-x-3">
                            <CalendarIcon className="w-5 h-5 text-gray-400 mt-1" />
                            <div>
                                <p className="font-medium text-gray-900">Check-in</p>
                                <p className="text-sm text-gray-500">
                                    {formatDate(state.bookingDates.checkIn)}
                                </p>
                                <p className="text-xs text-gray-400">From 3:00 PM</p>
                            </div>
                        </div>

                        <div className="flex items-start space-x-3">
                            <CalendarIcon className="w-5 h-5 text-gray-400 mt-1" />
                            <div>
                                <p className="font-medium text-gray-900">Check-out</p>
                                <p className="text-sm text-gray-500">
                                    {formatDate(state.bookingDates.checkOut)}
                                </p>
                                <p className="text-xs text-gray-400">Until 11:00 AM</p>
                            </div>
                        </div>

                        <div className="flex items-start space-x-3">
                            <UsersIcon className="w-5 h-5 text-gray-400 mt-1" />
                            <div>
                                <p className="font-medium text-gray-900">Duration</p>
                                <p className="text-sm text-gray-500">
                                    {state.bookingDates.nights} night{state.bookingDates.nights > 1 ? 's' : ''}
                                </p>
                            </div>
                        </div>
                    </div>
                </div>

                {/* Guest Information */}
                <div className="bg-white border border-gray-200 rounded-lg p-6">
                    <h3 className="text-lg font-semibold text-gray-900 mb-4">Guest Information</h3>

                    <div className="space-y-4">
                        <div>
                            <p className="font-medium text-gray-900">
                                {state.guestInfo.firstName} {state.guestInfo.lastName}
                            </p>
                            <p className="text-sm text-gray-500">Primary Guest</p>
                        </div>

                        <div className="flex items-center space-x-3">
                            <EnvelopeIcon className="w-5 h-5 text-gray-400" />
                            <p className="text-sm text-gray-600">{state.guestInfo.email}</p>
                        </div>                        <div className="flex items-center space-x-3">
                            <PhoneIcon className="w-5 h-5 text-gray-400" />
                            <p className="text-sm text-gray-600">{state.guestInfo.phoneNumber}</p>
                        </div>

                        <div>
                            <p className="text-sm text-gray-500">Tourist ID</p>
                            <p className="text-sm text-gray-900">{state.guestInfo.touristID}</p>
                        </div>

                        {state.guestInfo.specialRequests && (
                            <div>
                                <p className="text-sm text-gray-500">Special Requests</p>
                                <p className="text-sm text-gray-900">{state.guestInfo.specialRequests}</p>
                            </div>
                        )}
                    </div>
                </div>
            </div>

            {/* Room Details */}
            <div className="bg-white border border-gray-200 rounded-lg p-6 mb-8">
                <h3 className="text-lg font-semibold text-gray-900 mb-4">Room Details</h3>

                <div className="space-y-4">
                    {state.selectedRooms.map((selectedRoom) => (
                        <div key={selectedRoom.roomId} className="border border-gray-100 rounded-lg p-4">
                            <div className="flex justify-between items-start">
                                <div>
                                    <h4 className="font-medium text-gray-900">{selectedRoom.roomName}</h4>
                                    <p className="text-sm text-gray-500">Quantity: {selectedRoom.quantity} room{selectedRoom.quantity > 1 ? 's' : ''}</p>
                                    <p className="text-sm text-gray-500">Size: {selectedRoom.room.size}</p>

                                    <div className="flex flex-wrap gap-2 mt-2">
                                        {selectedRoom.room.amenities.slice(0, 4).map((amenity, index) => (
                                            <span
                                                key={index}
                                                className="inline-flex items-center px-2 py-1 rounded-full text-xs bg-gray-100 text-gray-600"
                                            >
                                                {amenity}
                                            </span>
                                        ))}
                                    </div>
                                </div>

                                <div className="text-right">
                                    <p className="text-sm text-gray-500">Per room per night</p>
                                    <p className="font-medium text-gray-900">{formatPrice(selectedRoom.price)}</p>
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
            </div>

            {/* Payment Summary */}
            <div className="bg-white border border-gray-200 rounded-lg p-6 mb-8">
                <h3 className="text-lg font-semibold text-gray-900 mb-4">Payment Summary</h3>

                <div className="space-y-3">
                    <div className="flex justify-between text-sm">
                        <span className="text-gray-600">Subtotal ({state.bookingDates.nights} nights)</span>
                        <span className="text-gray-900">{formatPrice(state.subtotal)}</span>
                    </div>
                    <div className="flex justify-between text-sm">
                        <span className="text-gray-600">Taxes & fees</span>
                        <span className="text-gray-900">{formatPrice(state.taxes + state.fees)}</span>
                    </div>
                    <div className="border-t border-gray-200 pt-3 flex justify-between font-semibold">
                        <span className="text-gray-900">Total paid</span>
                        <span className="text-gray-900">{formatPrice(state.total)}</span>
                    </div>
                </div>

                {state.paymentInfo && (
                    <div className="mt-4 pt-4 border-t border-gray-100">
                        <p className="text-sm text-gray-500">Payment method</p>
                        <p className="text-sm text-gray-900 capitalize">
                            {state.paymentInfo.method.replace('-', ' ')}
                            {state.paymentInfo.cardNumber && ` ending in ****${state.paymentInfo.cardNumber.slice(-4)}`}
                        </p>
                    </div>
                )}
            </div>

            {/* Important Information */}
            <div className="bg-blue-50 border border-blue-200 rounded-lg p-6 mb-8">
                <h3 className="text-lg font-semibold text-blue-900 mb-4">Important Information</h3>

                <ul className="space-y-2 text-sm text-blue-800">
                    <li>• A confirmation email has been sent to {state.guestInfo.email}</li>
                    <li>• Please bring a valid ID and this confirmation for check-in</li>
                    <li>• Check-in time is from 3:00 PM, check-out time is until 11:00 AM</li>
                    <li>• Free cancellation is available until 24 hours before check-in</li>
                    <li>• For any changes or inquiries, contact the hotel directly with your booking reference</li>
                </ul>
            </div>

            {/* Action Buttons */}
            <div className="flex flex-col sm:flex-row gap-4 justify-center mb-8">
                <button
                    onClick={handlePrint}
                    className="inline-flex items-center justify-center px-6 py-3 border border-gray-300 rounded-lg text-gray-700 bg-white hover:bg-gray-50 transition-colors"
                >
                    <PrinterIcon className="w-5 h-5 mr-2" />
                    Print Confirmation
                </button>

                <button
                    onClick={handleShare}
                    className="inline-flex items-center justify-center px-6 py-3 border border-gray-300 rounded-lg text-gray-700 bg-white hover:bg-gray-50 transition-colors"
                >
                    <ShareIcon className="w-5 h-5 mr-2" />
                    Share Booking
                </button>

                <button
                    onClick={handleNewBooking}
                    className="inline-flex items-center justify-center px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
                >
                    Make Another Booking
                </button>
            </div>

            {/* Contact Information */}
            <div className="text-center text-sm text-gray-500">
                <p>Need help? Contact our support team at support@hotelbooking.com or call +1-800-HOTELS</p>
            </div>
        </div>
    );
}
