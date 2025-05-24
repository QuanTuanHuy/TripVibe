"use client";

import React, { useState } from 'react';
import { format } from 'date-fns';
import { vi } from 'date-fns/locale';
import Image from 'next/image';
import Link from 'next/link';
import Header from '@/components/Header';
import { Calendar, MapPin, CheckCircle, XCircle, Clock, ChevronRight, Star, AlertCircle } from 'lucide-react';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Booking } from '@/services/booking/bookingService';

// Mock data for upcoming bookings
const mockUpcomingBookings: Booking[] = [
    {
        id: '1',
        bookingNumber: 'BK-123456',
        status: 'CONFIRMED',
        checkInDate: '2025-06-15T14:00:00Z',
        checkOutDate: '2025-06-18T11:00:00Z',
        totalAmount: 1250000,
        currency: 'VND',
        createdAt: '2025-05-01T08:32:45Z',
        updatedAt: '2025-05-01T08:32:45Z',
        guestCount: 2,
        accommodation: {
            id: 'h1',
            name: 'Vinpearl Resort & Spa Nha Trang',
            type: 'Resort',
            address: 'Vinpearl Resort, Hòn Tre',
            city: 'Nha Trang',
            country: 'Việt Nam',
            imageUrl: 'https://cf.bstatic.com/xdata/images/hotel/square600/303038392.webp?k=76ff82cbb302474f4d65f7486d2b683c811d033d1415335e95d690172a763149&o=',
            rating: 4.8,
        },
        room: {
            id: 'r1',
            name: 'Deluxe Ocean View',
            type: 'Deluxe',
        },
        paymentStatus: 'PAID',
        isPaid: true,
        isCancellable: true,
        specialRequests: 'Room on high floor with ocean view if possible',
    },
    {
        id: '2',
        bookingNumber: 'BK-789012',
        status: 'CONFIRMED',
        checkInDate: '2025-09-20T15:00:00Z',
        checkOutDate: '2025-09-25T12:00:00Z',
        totalAmount: 3450000,
        currency: 'VND',
        createdAt: '2025-05-10T14:45:30Z',
        updatedAt: '2025-05-10T14:45:30Z',
        guestCount: 4,
        accommodation: {
            id: 'h2',
            name: 'JW Marriott Phú Quốc Emerald Bay Resort & Spa',
            type: 'Resort',
            address: 'Bãi Khem, An Thới',
            city: 'Phú Quốc',
            country: 'Việt Nam',
            imageUrl: 'https://cf.bstatic.com/xdata/images/hotel/square600/303038392.webp?k=76ff82cbb302474f4d65f7486d2b683c811d033d1415335e95d690172a763149&o=',
            rating: 4.9,
        },
        room: {
            id: 'r2',
            name: 'Family Suite Garden View',
            type: 'Suite',
        },
        paymentStatus: 'PARTIALLY_PAID',
        isPaid: false,
        isCancellable: true,
    },
];

// Mock data for past bookings
const mockPastBookings: Booking[] = [
    {
        id: '3',
        bookingNumber: 'BK-345678',
        status: 'COMPLETED',
        checkInDate: '2025-03-10T14:00:00Z',
        checkOutDate: '2025-03-15T11:00:00Z',
        totalAmount: 2100000,
        currency: 'VND',
        createdAt: '2025-02-15T10:23:18Z',
        updatedAt: '2025-03-15T11:30:00Z',
        guestCount: 2,
        accommodation: {
            id: 'h3',
            name: 'InterContinental Danang Sun Peninsula Resort',
            type: 'Resort',
            address: 'Bai Bac, Son Tra Peninsula',
            city: 'Đà Nẵng',
            country: 'Việt Nam',
            imageUrl: 'https://cf.bstatic.com/xdata/images/hotel/max1024x768/634661490.jpg?k=64335594c8f4913551dc9f611b2758d7174782f2ea954a672ab4f474e585ffad&o=',
            rating: 4.7,
        },
        room: {
            id: 'r3',
            name: 'Classic Room Sea View',
            type: 'Classic',
        },
        paymentStatus: 'PAID',
        isPaid: true,
        isCancellable: false,
    },
    {
        id: '4',
        bookingNumber: 'BK-567890',
        status: 'CANCELLED',
        checkInDate: '2025-04-05T15:00:00Z',
        checkOutDate: '2025-04-07T12:00:00Z',
        totalAmount: 1800000,
        currency: 'VND',
        createdAt: '2025-03-20T16:42:51Z',
        updatedAt: '2025-03-25T09:15:30Z',
        guestCount: 3,
        accommodation: {
            id: 'h4',
            name: 'Sofitel Legend Metropole Hanoi',
            type: 'Hotel',
            address: '15 Ngo Quyen Street',
            city: 'Hà Nội',
            country: 'Việt Nam',
            imageUrl: 'https://cf.bstatic.com/xdata/images/hotel/max1024x768/634661490.jpg?k=64335594c8f4913551dc9f611b2758d7174782f2ea954a672ab4f474e585ffad&o=',
            rating: 4.8,
        },
        room: {
            id: 'r4',
            name: 'Premium Room',
            type: 'Premium',
        },
        paymentStatus: 'REFUNDED',
        isPaid: true,
        isCancellable: false,
    },
];

// Helper function to format currency
const formatCurrency = (amount: number, currency: string) => {
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: currency,
        minimumFractionDigits: 0,
        maximumFractionDigits: 0,
    }).format(amount);
};

// BookingStatusBadge component
const BookingStatusBadge = ({ status }: { status: string }) => {
    let bgColor = '';
    let textColor = '';
    let icon = null;
    let text = '';

    switch (status) {
        case 'CONFIRMED':
            bgColor = 'bg-green-100 dark:bg-green-900/40';
            textColor = 'text-green-800 dark:text-green-300';
            icon = <CheckCircle size={16} className="mr-1 text-green-600 dark:text-green-400" />;
            text = 'Đã xác nhận';
            break;
        case 'CANCELLED':
            bgColor = 'bg-red-100 dark:bg-red-900/40';
            textColor = 'text-red-800 dark:text-red-300';
            icon = <XCircle size={16} className="mr-1 text-red-600 dark:text-red-400" />;
            text = 'Đã hủy';
            break;
        case 'PENDING':
            bgColor = 'bg-yellow-100 dark:bg-yellow-900/40';
            textColor = 'text-yellow-800 dark:text-yellow-300';
            icon = <Clock size={16} className="mr-1 text-yellow-600 dark:text-yellow-400" />;
            text = 'Chờ xác nhận';
            break;
        case 'COMPLETED':
            bgColor = 'bg-blue-100 dark:bg-blue-900/40';
            textColor = 'text-blue-800 dark:text-blue-300';
            icon = <CheckCircle size={16} className="mr-1 text-blue-600 dark:text-blue-400" />;
            text = 'Đã hoàn thành';
            break;
        default:
            bgColor = 'bg-gray-100 dark:bg-gray-800/60';
            textColor = 'text-gray-800 dark:text-gray-300';
            icon = <AlertCircle size={16} className="mr-1 text-gray-600 dark:text-gray-400" />;
            text = status;
    }

    return (
        <span className={`flex items-center px-2.5 py-1 rounded-full text-xs font-medium ${bgColor} ${textColor}`}>
            {icon}
            {text}
        </span>
    );
};

// Single booking card component
const BookingCard = ({ booking }: { booking: Booking }) => {
    const checkInDate = new Date(booking.checkInDate);
    const checkOutDate = new Date(booking.checkOutDate);

    // Calculate number of nights
    const nights = Math.round(
        (checkOutDate.getTime() - checkInDate.getTime()) / (1000 * 60 * 60 * 24)
    );

    return (<div className="mb-6 bg-white dark:bg-gray-900 rounded-lg shadow-sm border border-gray-200 dark:border-gray-800 overflow-hidden">
        <div className="flex flex-col md:flex-row">
            {/* Hotel Image */}
            <div className="md:w-1/3 h-48 md:h-auto relative">
                <Image
                    src={booking.accommodation.imageUrl}
                    alt={booking.accommodation.name}
                    fill
                    className="object-cover"
                />
            </div>

            {/* Booking details */}
            <div className="p-4 md:p-6 flex-1">
                <div className="flex justify-between items-start">
                    <div>
                        <h3 className="text-lg md:text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">
                            {booking.accommodation.name}
                        </h3>
                        <div className="flex items-center text-gray-500 dark:text-gray-400 mb-3">
                            <MapPin size={16} className="mr-1" />
                            <span className="text-sm">{booking.accommodation.city}, {booking.accommodation.country}</span>
                            <div className="ml-3 flex items-center">
                                <Star size={16} className="text-yellow-500 fill-yellow-500 mr-1" />
                                <span className="text-sm font-medium">{booking.accommodation.rating}</span>
                            </div>
                        </div>
                    </div>
                    <BookingStatusBadge status={booking.status} />
                </div>                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-4">
                    <div>
                        <p className="text-sm text-gray-500 dark:text-gray-400 mb-1">Check-in</p>
                        <div className="flex items-center">
                            <Calendar size={16} className="mr-2 text-gray-700 dark:text-gray-300" />
                            <p className="font-medium dark:text-gray-200">
                                {format(checkInDate, 'EEE, dd MMM yyyy', { locale: vi })}
                            </p>
                        </div>
                    </div>

                    <div>
                        <p className="text-sm text-gray-500 dark:text-gray-400 mb-1">Check-out</p>
                        <div className="flex items-center">
                            <Calendar size={16} className="mr-2 text-gray-700 dark:text-gray-300" />
                            <p className="font-medium dark:text-gray-200">
                                {format(checkOutDate, 'EEE, dd MMM yyyy', { locale: vi })}
                            </p>
                        </div>
                    </div>
                </div>

                <div className="border-t border-gray-100 dark:border-gray-800 pt-3">
                    <div className="flex flex-wrap justify-between items-center">
                        <div>
                            <p className="text-sm text-gray-500 dark:text-gray-400 mb-1">Mã đặt phòng: {booking.bookingNumber}</p>
                            <p className="flex items-center">
                                <span className="text-gray-500 dark:text-gray-400 text-sm mr-2">{booking.room.name} • {nights} đêm</span>
                                <span className="text-gray-500 dark:text-gray-400 text-sm mr-2">•</span>
                                <span className="text-gray-500 dark:text-gray-400 text-sm">{booking.guestCount} khách</span>
                            </p>
                        </div>

                        <div className="text-right mt-2 md:mt-0">
                            <p className="text-lg font-bold text-gray-900 dark:text-gray-100">
                                {formatCurrency(booking.totalAmount, booking.currency)}
                            </p>
                            <Link
                                href={`/mytrips/${booking.id}`}
                                className="mt-2 inline-flex items-center text-[#0071c2] hover:text-[#00487a] dark:text-[#3b9de6] dark:hover:text-[#66b5f0] font-medium text-sm"
                            >
                                Chi tiết
                                <ChevronRight size={16} />
                            </Link>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    );
};

// EmptyState component for when there are no bookings
const EmptyState = ({ type }: { type: 'upcoming' | 'past' }) => {
    return (
        <div className="flex flex-col items-center justify-center py-16 border-2 border-dashed border-gray-200 dark:border-gray-700 rounded-lg bg-gray-50 dark:bg-gray-800/50">
            <div className="w-16 h-16 bg-gray-100 dark:bg-gray-700 rounded-full flex items-center justify-center mb-4">
                {type === 'upcoming' ? (
                    <Calendar size={24} className="text-gray-400 dark:text-gray-300" />
                ) : (
                    <Clock size={24} className="text-gray-400 dark:text-gray-300" />
                )}
            </div>
            <h3 className="text-lg font-medium text-gray-900 dark:text-gray-100 mb-2">
                {type === 'upcoming'
                    ? 'Bạn chưa có chuyến đi nào sắp tới'
                    : 'Bạn chưa có chuyến đi nào trước đây'}
            </h3>
            <p className="text-sm text-gray-500 dark:text-gray-400 text-center max-w-sm mb-6">
                {type === 'upcoming'
                    ? 'Hãy đặt phòng cho kỳ nghỉ tiếp theo và quản lý đơn đặt phòng của bạn tại đây.'
                    : 'Khi bạn hoàn thành chuyến đi, chúng sẽ hiển thị ở đây.'}
            </p>
            <Link
                href="/"
                className="bg-[#0071c2] dark:bg-[#0053d3] text-white px-4 py-2 rounded-md hover:bg-[#00487a] dark:hover:bg-[#0061f7] transition-colors"
            >
                Tìm chỗ nghỉ
            </Link>
        </div>
    );
};

export default function TripsPage() {
    const [, setActiveTab] = useState("upcoming");

    return (
        <div className="min-h-screen bg-gray-50 dark:bg-gray-900">
            <Header />

            <div className="container max-w-5xl mx-auto px-4 py-8">
                <div className="mb-8">
                    <h1 className="text-2xl md:text-3xl font-bold text-gray-900 dark:text-gray-50 mb-2">Chuyến đi của tôi</h1>
                    <p className="text-gray-600 dark:text-gray-400">Quản lý tất cả đơn đặt phòng sắp tới và đã qua của bạn.</p>
                </div>

                <Tabs defaultValue="upcoming" className="w-full" onValueChange={setActiveTab}>
                    <TabsList className="mb-6 bg-white dark:bg-gray-800 border border-gray-200 dark:border-gray-700 rounded-lg p-1 w-full md:w-auto">
                        <TabsTrigger value="upcoming" className="flex-1 md:flex-none">
                            Sắp tới
                        </TabsTrigger>
                        <TabsTrigger value="past" className="flex-1 md:flex-none">
                            Đã qua
                        </TabsTrigger>
                    </TabsList>

                    <TabsContent value="upcoming" className="mt-0">
                        {mockUpcomingBookings.length > 0 ? (
                            <div>
                                {mockUpcomingBookings.map((booking) => (
                                    <BookingCard key={booking.id} booking={booking} />
                                ))}
                            </div>
                        ) : (
                            <EmptyState type="upcoming" />
                        )}
                    </TabsContent>

                    <TabsContent value="past" className="mt-0">
                        {mockPastBookings.length > 0 ? (
                            <div>
                                {mockPastBookings.map((booking) => (
                                    <BookingCard key={booking.id} booking={booking} />
                                ))}
                            </div>
                        ) : (
                            <EmptyState type="past" />
                        )}
                    </TabsContent>
                </Tabs>
            </div>
        </div>
    );
}
