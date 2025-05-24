"use client";

import React from 'react';
import { format } from 'date-fns';
import { vi } from 'date-fns/locale';
import Image from 'next/image';
import Link from 'next/link';
import { useParams } from 'next/navigation';
import Header from '@/components/Header';
import {
    Calendar,
    MapPin,
    CheckCircle,
    Users,
    Clock,
    ChevronLeft,
    FileText,
    Phone,
    Mail,
    AlertCircle,
    Download,
    Printer,
    Share2,
    CreditCard
} from 'lucide-react';
import { Booking } from '@/services/booking/bookingService';

// Mock data for a single booking
const getMockBookingById = (id: string): Booking => {
    const mockBookings: Record<string, Booking> = {
        '1': {
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
                imageUrl: 'https://cf.bstatic.com/xdata/images/hotel/square600/286659200.webp?k=9206fc9239b3e4538c22d04b85213d6d5e6257015022de8a37effd956fcde4b6&o=&s=1',
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
        '2': {
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
                imageUrl: 'https://cf.bstatic.com/xdata/images/hotel/square600/211386522.webp?k=7d79d3d5ace43ddf019c036b4a9cf6aa9919823d4022e8e8f05d8bd1c88c5138&o=&s=1',
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
    };

    return mockBookings[id] || mockBookings['1']; // Default to first booking if ID not found
};

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
            icon = <AlertCircle size={16} className="mr-1 text-red-600 dark:text-red-400" />;
            text = 'Đã hủy';
            break;
        case 'PENDING':
            bgColor = 'bg-yellow-100 dark:bg-yellow-900/40';
            textColor = 'text-yellow-800 dark:text-yellow-300';
            icon = <Clock size={16} className="mr-1 text-yellow-600 dark:text-yellow-400" />;
            text = 'Chờ xác nhận';
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

// Information section component
const InfoSection = ({ title, children }: { title: string; children: React.ReactNode }) => {
    return (
        <div className="mb-6 bg-white dark:bg-gray-800 rounded-lg shadow-sm border border-gray-200 dark:border-gray-700 overflow-hidden">
            <div className="border-b border-gray-200 dark:border-gray-700 px-6 py-4">
                <h2 className="text-lg font-semibold text-gray-900 dark:text-gray-100">{title}</h2>
            </div>
            <div className="p-6">{children}</div>
        </div>
    );
};

export default function BookingDetailPage() {
    const { id } = useParams();
    const bookingId = Array.isArray(id) ? id[0] : (id || '1');
    const booking = getMockBookingById(bookingId);

    const checkInDate = new Date(booking.checkInDate);
    const checkOutDate = new Date(booking.checkOutDate);
    const createdDate = new Date(booking.createdAt);

    // Calculate number of nights
    const nights = Math.round(
        (checkOutDate.getTime() - checkInDate.getTime()) / (1000 * 60 * 60 * 24)
    ); return (
        <div className="min-h-screen bg-gray-50 dark:bg-gray-900">
            <Header />

            <div className="container max-w-6xl mx-auto px-4 py-8">
                <Link
                    href="/myaccount/trips"
                    className="inline-flex items-center text-[#0071c2] hover:text-[#00487a] dark:text-[#3b9de6] dark:hover:text-[#66b5f0] mb-6"
                >
                    <ChevronLeft size={18} className="mr-1" />
                    Quay lại tất cả chuyến đi
                </Link>

                <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
                    {/* Main content column */}
                    <div className="lg:col-span-2">
                        <div className="mb-6">
                            <div className="flex justify-between items-start">
                                <div>
                                    <h1 className="text-2xl md:text-3xl font-bold text-gray-900 dark:text-gray-50 mb-2">
                                        {booking.accommodation.name}
                                    </h1>
                                    <div className="flex items-center text-gray-500 dark:text-gray-400">
                                        <MapPin size={16} className="mr-1" />
                                        <span>{booking.accommodation.address}, {booking.accommodation.city}, {booking.accommodation.country}</span>
                                    </div>
                                </div>
                                <BookingStatusBadge status={booking.status} />
                            </div>
                        </div>

                        {/* Hotel Image */}
                        <div className="mb-6 relative h-[300px] rounded-lg overflow-hidden">
                            <Image
                                src={booking.accommodation.imageUrl}
                                alt={booking.accommodation.name}
                                fill
                                className="object-cover"
                            />
                        </div>

                        {/* Stay Details */}
                        <InfoSection title="Chi tiết đặt phòng">                            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                            <div>
                                <p className="text-sm text-gray-500 dark:text-gray-400 mb-1">Check-in</p>
                                <div className="flex items-center">
                                    <Calendar size={16} className="mr-2 text-gray-700 dark:text-gray-300" />
                                    <p className="font-medium dark:text-gray-200">
                                        {format(checkInDate, 'EEEE, dd MMMM yyyy', { locale: vi })}
                                    </p>
                                </div>
                                <p className="text-sm text-gray-500 dark:text-gray-400 mt-2">14:00 - 22:00</p>
                            </div>

                            <div>
                                <p className="text-sm text-gray-500 dark:text-gray-400 mb-1">Check-out</p>
                                <div className="flex items-center">
                                    <Calendar size={16} className="mr-2 text-gray-700 dark:text-gray-300" />
                                    <p className="font-medium dark:text-gray-200">
                                        {format(checkOutDate, 'EEEE, dd MMMM yyyy', { locale: vi })}
                                    </p>
                                </div>
                                <p className="text-sm text-gray-500 dark:text-gray-400 mt-2">Đến 11:00</p>
                            </div>
                        </div>                            <div className="border-t border-gray-100 dark:border-gray-700 my-4"></div>

                            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                                <div>
                                    <p className="text-sm text-gray-500 dark:text-gray-400 mb-1">Thời gian lưu trú</p>
                                    <p className="font-medium dark:text-gray-200">{nights} đêm</p>
                                </div>

                                <div>
                                    <p className="text-sm text-gray-500 dark:text-gray-400 mb-1">Số lượng khách</p>
                                    <div className="flex items-center">
                                        <Users size={16} className="mr-2 text-gray-700 dark:text-gray-300" />
                                        <p className="font-medium dark:text-gray-200">{booking.guestCount} người</p>
                                    </div>
                                </div>
                            </div>

                            <div className="border-t border-gray-100 dark:border-gray-700 my-4"></div>

                            <div>
                                <p className="text-sm text-gray-500 dark:text-gray-400 mb-1">Loại phòng</p>
                                <p className="font-medium dark:text-gray-200">{booking.room.name}</p>
                                <ul className="list-disc list-inside text-gray-700 dark:text-gray-300 text-sm mt-2">
                                    <li>Bao gồm bữa sáng</li>
                                    <li>WiFi miễn phí</li>
                                    <li>Chỗ đậu xe miễn phí</li>
                                    <li>Được phép hủy đến 48 giờ trước check-in</li>
                                </ul>
                            </div>                            {booking.specialRequests && (
                                <React.Fragment>
                                    <div className="border-t border-gray-100 dark:border-gray-700 my-4"></div>
                                    <div>
                                        <p className="text-sm text-gray-500 dark:text-gray-400 mb-1">Yêu cầu đặc biệt</p>
                                        <p className="text-gray-700 dark:text-gray-300">{booking.specialRequests}</p>
                                    </div>
                                </React.Fragment>
                            )}
                        </InfoSection>

                        {/* Payment Information */}
                        <InfoSection title="Thông tin thanh toán">                            <div className="flex justify-between items-center mb-3">
                            <div className="flex items-center">
                                <CreditCard size={20} className="mr-2 text-gray-700 dark:text-gray-300" />
                                <p className="font-medium dark:text-gray-200">{booking.paymentStatus === 'PAID' ? 'Đã thanh toán đầy đủ' : 'Đã thanh toán một phần'}</p>
                            </div>
                            {booking.isPaid ? (
                                <span className="bg-green-100 dark:bg-green-900/40 text-green-800 dark:text-green-300 px-3 py-1 rounded-full text-xs font-medium">
                                    Đã thanh toán
                                </span>
                            ) : (
                                <span className="bg-yellow-100 dark:bg-yellow-900/40 text-yellow-800 dark:text-yellow-300 px-3 py-1 rounded-full text-xs font-medium">
                                    Còn tiền phải trả
                                </span>
                            )}
                        </div>

                            <div className="bg-gray-50 dark:bg-gray-700/50 rounded-lg p-4 my-3">
                                <div className="flex justify-between items-center mb-2">
                                    <span className="text-gray-700 dark:text-gray-300">{booking.room.name} x {nights} đêm</span>
                                    <span className="dark:text-gray-200">{formatCurrency(booking.totalAmount * 0.9, booking.currency)}</span>
                                </div>
                                <div className="flex justify-between items-center mb-2">
                                    <span className="text-gray-700 dark:text-gray-300">Thuế và phí</span>
                                    <span className="dark:text-gray-200">{formatCurrency(booking.totalAmount * 0.1, booking.currency)}</span>
                                </div>
                                <div className="border-t border-gray-200 dark:border-gray-600 my-2"></div>
                                <div className="flex justify-between items-center font-bold">
                                    <span className="dark:text-gray-100">Tổng cộng</span>
                                    <span className="dark:text-gray-100">{formatCurrency(booking.totalAmount, booking.currency)}</span>
                                </div>
                            </div>                            {!booking.isPaid && (
                                <div className="mt-4">
                                    <button className="w-full bg-[#0071c2] dark:bg-[#0053d3] text-white px-4 py-2.5 rounded-md hover:bg-[#00487a] dark:hover:bg-[#0061f7] transition-colors">
                                        Thanh toán ngay
                                    </button>
                                </div>
                            )}
                        </InfoSection>
                    </div>

                    {/* Sidebar column */}
                    <div>
                        {/* Booking Summary */}                        <InfoSection title="Tóm tắt đặt phòng">
                            <p className="flex justify-between mb-2">
                                <span className="text-gray-600 dark:text-gray-400">Mã đặt phòng:</span>
                                <span className="font-semibold dark:text-gray-200">{booking.bookingNumber}</span>
                            </p>
                            <p className="flex justify-between mb-2">
                                <span className="text-gray-600 dark:text-gray-400">Ngày đặt:</span>
                                <span className="dark:text-gray-200">{format(createdDate, 'dd/MM/yyyy', { locale: vi })}</span>
                            </p>
                            {booking.status === 'CONFIRMED' && booking.isCancellable && (
                                <div className="mt-4">
                                    <button className="w-full border border-red-500 dark:border-red-400 text-red-500 dark:text-red-400 px-4 py-2 rounded-md hover:bg-red-50 dark:hover:bg-red-900/20 transition-colors font-medium">
                                        Hủy đặt phòng
                                    </button>
                                </div>
                            )}
                        </InfoSection>

                        {/* Contact Hotel */}                        <InfoSection title="Liên hệ chỗ nghỉ">
                            <div className="space-y-3">
                                <div className="flex items-center">
                                    <Phone size={18} className="mr-3 text-gray-700 dark:text-gray-300" />
                                    <span className="dark:text-gray-200">+84 258 1234 567</span>
                                </div>
                                <div className="flex items-center">
                                    <Mail size={18} className="mr-3 text-gray-700 dark:text-gray-300" />
                                    <span className="dark:text-gray-200">info@vinpearlnhatrang.com</span>
                                </div>
                            </div>
                        </InfoSection>

                        {/* Actions */}                        <InfoSection title="Các tùy chọn">
                            <div className="space-y-3">
                                <button className="w-full flex items-center justify-center text-[#0071c2] hover:text-[#00487a] dark:text-[#3b9de6] dark:hover:text-[#66b5f0] font-medium p-2 border border-gray-200 dark:border-gray-700 rounded-md hover:bg-gray-50 dark:hover:bg-gray-700/50">
                                    <Download size={18} className="mr-2" />
                                    Tải xuống hóa đơn
                                </button>
                                <button className="w-full flex items-center justify-center text-[#0071c2] hover:text-[#00487a] dark:text-[#3b9de6] dark:hover:text-[#66b5f0] font-medium p-2 border border-gray-200 dark:border-gray-700 rounded-md hover:bg-gray-50 dark:hover:bg-gray-700/50">
                                    <Printer size={18} className="mr-2" />
                                    In xác nhận đặt phòng
                                </button>
                                <button className="w-full flex items-center justify-center text-[#0071c2] hover:text-[#00487a] dark:text-[#3b9de6] dark:hover:text-[#66b5f0] font-medium p-2 border border-gray-200 dark:border-gray-700 rounded-md hover:bg-gray-50 dark:hover:bg-gray-700/50">
                                    <Share2 size={18} className="mr-2" />
                                    Chia sẻ chi tiết đặt phòng
                                </button>
                                <button className="w-full flex items-center justify-center text-[#0071c2] hover:text-[#00487a] dark:text-[#3b9de6] dark:hover:text-[#66b5f0] font-medium p-2 border border-gray-200 dark:border-gray-700 rounded-md hover:bg-gray-50 dark:hover:bg-gray-700/50">
                                    <FileText size={18} className="mr-2" />
                                    Liên hệ hỗ trợ
                                </button>
                            </div>
                        </InfoSection>
                    </div>
                </div>
            </div>
        </div>
    );
}
