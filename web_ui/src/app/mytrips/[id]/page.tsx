"use client";

import React, { useState, useEffect } from 'react';
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
    XCircle,
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
    CreditCard,
    Loader2
} from 'lucide-react';
import { bookingService } from '@/services/booking/bookingService';
import accommodationService from '@/services/accommodation/accommodationService';
import { BookingResponse, BookingUnit } from '@/types/booking/booking.types';
import { AccommodationThumbnail, Accommodation, Unit } from '@/types/accommodation';

// Enhanced booking unit with accommodation details
interface EnhancedBookingUnit extends BookingUnit {
    unitDetails?: Unit;
    unitName: string;
    pricePerNight: number;
    maxAdults: number;
    maxChildren: number;
    description: string;
}

// Enhanced booking interface with accommodation details
interface EnhancedBooking extends BookingResponse {
    accommodation?: AccommodationThumbnail;
    accommodationDetails?: Accommodation;
}

// Helper function to format currency
const formatCurrency = (amount: number, currency: string = 'VND') => {
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
        case 'APPROVED':
            bgColor = 'bg-green-100 dark:bg-green-900/40';
            textColor = 'text-green-800 dark:text-green-300';
            icon = <CheckCircle size={16} className="mr-1 text-green-600 dark:text-green-400" />;
            text = 'Đã xác nhận';
            break;
        case 'CANCELLED':
        case 'REJECTED':
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
        case 'CHECKED_OUT':
            bgColor = 'bg-blue-100 dark:bg-blue-900/40';
            textColor = 'text-blue-800 dark:text-blue-300';
            icon = <CheckCircle size={16} className="mr-1 text-blue-600 dark:text-blue-400" />;
            text = 'Đã hoàn thành';
            break;
        case 'CHECKED_IN':
            bgColor = 'bg-purple-100 dark:bg-purple-900/40';
            textColor = 'text-purple-800 dark:text-purple-300';
            icon = <CheckCircle size={16} className="mr-1 text-purple-600 dark:text-purple-400" />;
            text = 'Đã nhận phòng';
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
    const bookingId = Array.isArray(id) ? parseInt(id[0]) : parseInt(id || '1');

    const [booking, setBooking] = useState<EnhancedBooking | null>(null);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    // Fetch booking details and accommodation info
    useEffect(() => {
        const fetchBookingDetails = async () => {
            try {
                setIsLoading(true);
                setError(null);

                const bookingResponse = await bookingService.getBookingDetails(bookingId);

                let enhancedBooking: EnhancedBooking = bookingResponse;

                try {
                    const accommodationThumbnails = await accommodationService.getAccommodationThumbnails([bookingResponse.accommodationId]);

                    const accommodationDetails = await accommodationService.getAccommodationById(bookingResponse.accommodationId);

                    enhancedBooking = {
                        ...bookingResponse,
                        accommodation: accommodationThumbnails.length > 0 ? accommodationThumbnails[0] : undefined,
                        accommodationDetails: accommodationDetails,
                    };
                } catch (accommodationError) {
                    console.error('Error fetching accommodation details:', accommodationError);
                    // Continue with booking data only if accommodation fetch fails
                }

                setBooking(enhancedBooking);
            } catch (error) {
                console.error('Error fetching booking details:', error);
                setError('Không thể tải thông tin đặt phòng. Vui lòng thử lại sau.');
            } finally {
                setIsLoading(false);
            }
        };

        fetchBookingDetails();
    }, [bookingId]);

    // Loading state
    if (isLoading) {
        return (
            <div className="min-h-screen bg-gray-50 dark:bg-gray-900">
                <Header />
                <div className="container max-w-6xl mx-auto px-4 py-8">
                    <div className="flex items-center justify-center py-16">
                        <Loader2 size={32} className="animate-spin text-[#0071c2] mr-3" />
                        <span className="text-lg text-gray-600 dark:text-gray-400">Đang tải thông tin đặt phòng...</span>
                    </div>
                </div>
            </div>
        );
    }

    // Error state
    if (error || !booking) {
        return (
            <div className="min-h-screen bg-gray-50 dark:bg-gray-900">
                <Header />
                <div className="container max-w-6xl mx-auto px-4 py-8">
                    <Link
                        href="/mytrips"
                        className="inline-flex items-center text-[#0071c2] hover:text-[#00487a] dark:text-[#3b9de6] dark:hover:text-[#66b5f0] mb-6"
                    >
                        <ChevronLeft size={18} className="mr-1" />
                        Quay lại tất cả chuyến đi
                    </Link>

                    <div className="flex flex-col items-center justify-center py-16 border-2 border-dashed border-gray-200 dark:border-gray-700 rounded-lg bg-gray-50 dark:bg-gray-800/50">
                        <AlertCircle size={48} className="text-red-500 mb-4" />
                        <h3 className="text-lg font-medium text-gray-900 dark:text-gray-100 mb-2">
                            Không thể tải thông tin đặt phòng
                        </h3>
                        <p className="text-sm text-gray-500 dark:text-gray-400 text-center max-w-sm mb-6">
                            {error || 'Đặt phòng không tồn tại hoặc bạn không có quyền truy cập.'}
                        </p>
                        <Link
                            href="/mytrips"
                            className="bg-[#0071c2] dark:bg-[#0053d3] text-white px-4 py-2 rounded-md hover:bg-[#00487a] dark:hover:bg-[#0061f7] transition-colors"
                        >
                            Quay lại danh sách đặt phòng
                        </Link>
                    </div>
                </div>
            </div>
        );
    }

    const checkInDate = new Date(booking.stayFrom * 1000);
    const checkOutDate = new Date(booking.stayTo * 1000);

    // Calculate number of nights
    const nights = Math.round(
        (checkOutDate.getTime() - checkInDate.getTime()) / (1000 * 60 * 60 * 24)
    );

    // Get detailed unit information by matching booking units with accommodation units
    const getBookedUnitsWithDetails = (): EnhancedBookingUnit[] => {
        if (!booking.units || !booking.accommodationDetails?.units) {
            return booking.units?.map(unit => ({
                ...unit,
                unitName: 'Room',
                pricePerNight: 0,
                maxAdults: 0,
                maxChildren: 0,
                description: '',
            })) || [];
        }

        return booking.units.map(bookingUnit => {
            // Find the corresponding accommodation unit
            const accommodationUnit = booking.accommodationDetails?.units?.find(
                accUnit => accUnit.id === bookingUnit.unitId
            );

            return {
                ...bookingUnit,
                unitDetails: accommodationUnit,
                unitName: accommodationUnit?.unitName?.name || bookingUnit.fullName || 'Room',
                pricePerNight: accommodationUnit?.pricePerNight || 0,
                maxAdults: accommodationUnit?.maxAdults || 0,
                maxChildren: accommodationUnit?.maxChildren || 0,
                description: accommodationUnit?.description || '',
            };
        });
    };

    const bookedUnitsWithDetails = getBookedUnitsWithDetails();
    const totalGuests = booking.numberOfAdult + booking.numberOfChild;

    // Check if booking is cancellable (only confirmed/approved bookings can be cancelled)
    const isCancellable = ['CONFIRMED', 'APPROVED'].includes(booking.status);

    // Determine payment status
    const isPaid = booking.finalAmount > 0; // Simplified logic - you might need to check actual payment status

    return (
        <div className="min-h-screen bg-gray-50 dark:bg-gray-900">
            <Header />

            <div className="container max-w-6xl mx-auto px-4 py-8">
                <Link
                    href="/mytrips"
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
                                        {booking.accommodation?.name || 'Accommodation'}
                                    </h1>
                                    <div className="flex items-center text-gray-500 dark:text-gray-400">
                                        <MapPin size={16} className="mr-1" />
                                        <span>
                                            {booking.accommodation?.location?.address || 'Address not available'}
                                        </span>
                                    </div>
                                </div>
                                <BookingStatusBadge status={booking.status} />
                            </div>
                        </div>

                        {/* Hotel Image */}
                        <div className="mb-6 relative h-[300px] rounded-lg overflow-hidden">
                            <Image
                                src={booking.accommodation?.thumbnailUrl || '/placeholder-hotel.jpg'}
                                alt={booking.accommodation?.name || 'Accommodation'}
                                fill
                                className="object-cover"
                            />
                        </div>

                        {/* Stay Details */}
                        <InfoSection title="Chi tiết đặt phòng">
                            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                                <div>
                                    <p className="text-sm text-gray-500 dark:text-gray-400 mb-1">Check-in</p>
                                    <div className="flex items-center">
                                        <Calendar size={16} className="mr-2 text-gray-700 dark:text-gray-300" />
                                        <p className="font-medium dark:text-gray-200">
                                            {format(checkInDate, 'EEEE, dd MMMM yyyy', { locale: vi })}
                                        </p>
                                    </div>
                                    <p className="text-sm text-gray-500 dark:text-gray-400 mt-2">{booking.accommodationDetails?.checkInTimeFrom}:00 - {booking.accommodationDetails?.checkInTimeTo}:00</p>
                                </div>

                                <div>
                                    <p className="text-sm text-gray-500 dark:text-gray-400 mb-1">Check-out</p>
                                    <div className="flex items-center">
                                        <Calendar size={16} className="mr-2 text-gray-700 dark:text-gray-300" />
                                        <p className="font-medium dark:text-gray-200">
                                            {format(checkOutDate, 'EEEE, dd MMMM yyyy', { locale: vi })}
                                        </p>
                                    </div>
                                    <p className="text-sm text-gray-500 dark:text-gray-400 mt-2">{booking.accommodationDetails?.checkOutTimeFrom}:00 - {booking.accommodationDetails?.checkOutTimeTo}:00</p>
                                </div>
                            </div>
                            <div className="border-t border-gray-100 dark:border-gray-700 my-4"></div>

                            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                                <div>
                                    <p className="text-sm text-gray-500 dark:text-gray-400 mb-1">Thời gian lưu trú</p>
                                    <p className="font-medium dark:text-gray-200">{nights} đêm</p>
                                </div>

                                <div>
                                    <p className="text-sm text-gray-500 dark:text-gray-400 mb-1">Số lượng khách</p>
                                    <div className="flex items-center">
                                        <Users size={16} className="mr-2 text-gray-700 dark:text-gray-300" />
                                        <p className="font-medium dark:text-gray-200">{totalGuests} người</p>
                                    </div>
                                </div>
                            </div>                            <div className="border-t border-gray-100 dark:border-gray-700 my-4"></div>

                            <div>
                                <p className="text-sm text-gray-500 dark:text-gray-400 mb-3">Phòng đã đặt</p>
                                <div className="space-y-4">
                                    {bookedUnitsWithDetails.map((unit, index) => (
                                        <div key={index} className="bg-gray-50 dark:bg-gray-700/30 rounded-lg p-4">
                                            <div className="flex justify-between items-start mb-2">
                                                <div className="flex-1">
                                                    <h4 className="font-medium dark:text-gray-200 text-base">
                                                        {unit.unitName}
                                                    </h4>
                                                    {unit.description && (
                                                        <p className="text-sm text-gray-600 dark:text-gray-400 mt-1">
                                                            {unit.description}
                                                        </p>
                                                    )}
                                                </div>
                                                <div className="text-right ml-4">
                                                    <p className="font-semibold dark:text-gray-200">
                                                        {formatCurrency(unit.amount)}
                                                    </p>
                                                    <p className="text-xs text-gray-500 dark:text-gray-400">
                                                        Tổng {nights} đêm
                                                    </p>
                                                </div>
                                            </div>

                                            <div className="grid grid-cols-2 gap-4 mt-3">
                                                <div>
                                                    <p className="text-xs text-gray-500 dark:text-gray-400">Số lượng phòng</p>
                                                    <p className="text-sm font-medium dark:text-gray-200">{unit.quantity} phòng</p>
                                                </div>
                                                {unit.pricePerNight > 0 && (
                                                    <div>
                                                        <p className="text-xs text-gray-500 dark:text-gray-400">Giá mỗi đêm</p>
                                                        <p className="text-sm font-medium dark:text-gray-200">
                                                            {formatCurrency(unit.pricePerNight)}
                                                        </p>
                                                    </div>
                                                )}
                                                {unit.maxAdults > 0 && (
                                                    <div>
                                                        <p className="text-xs text-gray-500 dark:text-gray-400">Tối đa người lớn</p>
                                                        <p className="text-sm font-medium dark:text-gray-200">{unit.maxAdults} người</p>
                                                    </div>
                                                )}
                                                {unit.maxChildren > 0 && (
                                                    <div>
                                                        <p className="text-xs text-gray-500 dark:text-gray-400">Tối đa trẻ em</p>
                                                        <p className="text-sm font-medium dark:text-gray-200">{unit.maxChildren} trẻ</p>
                                                    </div>
                                                )}
                                            </div>

                                            <div className="mt-3 pt-3 border-t border-gray-200 dark:border-gray-600">
                                                <div className="flex justify-between items-center">
                                                    <div>
                                                        <p className="text-xs text-gray-500 dark:text-gray-400">Khách hàng</p>
                                                        <p className="text-sm font-medium dark:text-gray-200">
                                                            {unit.fullName}
                                                        </p>
                                                        <p className="text-xs text-gray-500 dark:text-gray-400">
                                                            {unit.email}
                                                        </p>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    ))}
                                </div>

                                <div className="mt-4 pt-4 border-t border-gray-200 dark:border-gray-700">
                                    <div className="grid grid-cols-2 gap-4">
                                        <div>
                                            <p className="text-sm text-gray-500 dark:text-gray-400">Tổng số phòng</p>
                                            <p className="font-semibold dark:text-gray-200 text-lg">
                                                {bookedUnitsWithDetails.reduce((total, unit) => total + unit.quantity, 0)} phòng
                                            </p>
                                        </div>
                                        <div>
                                            <p className="text-sm text-gray-500 dark:text-gray-400">Loại phòng</p>
                                            <p className="font-semibold dark:text-gray-200 text-lg">
                                                {bookedUnitsWithDetails.length} loại
                                            </p>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            {booking.note && (
                                <React.Fragment>
                                    <div className="border-t border-gray-100 dark:border-gray-700 my-4"></div>
                                    <div>
                                        <p className="text-sm text-gray-500 dark:text-gray-400 mb-1">Ghi chú</p>
                                        <p className="text-gray-700 dark:text-gray-300">{booking.note}</p>
                                    </div>
                                </React.Fragment>
                            )}
                        </InfoSection>

                        {/* Payment Information */}
                        <InfoSection title="Thông tin thanh toán">
                            <div className="flex justify-between items-center mb-3">
                                <div className="flex items-center">
                                    <CreditCard size={20} className="mr-2 text-gray-700 dark:text-gray-300" />
                                    <p className="font-medium dark:text-gray-200">
                                        {isPaid ? 'Đã thanh toán đầy đủ' : 'Chưa thanh toán'}
                                    </p>
                                </div>
                                {isPaid ? (
                                    <span className="bg-green-100 dark:bg-green-900/40 text-green-800 dark:text-green-300 px-3 py-1 rounded-full text-xs font-medium">
                                        Đã thanh toán
                                    </span>
                                ) : (
                                    <span className="bg-yellow-100 dark:bg-yellow-900/40 text-yellow-800 dark:text-yellow-300 px-3 py-1 rounded-full text-xs font-medium">
                                        Chưa thanh toán
                                    </span>
                                )}
                            </div>

                            <div className="bg-gray-50 dark:bg-gray-700/50 rounded-lg p-4 my-3">
                                {booking.units && booking.units.map((unit, index) => (
                                    <div key={index} className="flex justify-between items-center mb-2">
                                        <span className="text-gray-700 dark:text-gray-300">
                                            {unit.fullName} x {unit.quantity}
                                        </span>
                                        <span className="dark:text-gray-200">{formatCurrency(unit.amount)}</span>
                                    </div>
                                ))}
                                {booking.promotions && booking.promotions.length > 0 && (
                                    <div className="border-t border-gray-200 dark:border-gray-600 my-2 pt-2">
                                        {booking.promotions.map((promotion, index) => (
                                            <div key={index} className="flex justify-between items-center mb-2 text-green-600 dark:text-green-400">
                                                <span className="text-sm">
                                                    {promotion.promotionName} ({promotion.discountPercentage}% giảm)
                                                </span>
                                                <span className="text-sm">
                                                    -{formatCurrency((booking.invoiceAmount - booking.finalAmount))}
                                                </span>
                                            </div>
                                        ))}
                                    </div>
                                )}
                                <div className="border-t border-gray-200 dark:border-gray-600 my-2"></div>
                                <div className="flex justify-between items-center font-bold">
                                    <span className="dark:text-gray-100">Tổng cộng</span>
                                    <span className="dark:text-gray-100">{formatCurrency(booking.finalAmount)}</span>
                                </div>
                            </div>
                            {!isPaid && ['CONFIRMED', 'APPROVED'].includes(booking.status) && (
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
                        {/* Booking Summary */}
                        <InfoSection title="Tóm tắt đặt phòng">
                            <p className="flex justify-between mb-2">
                                <span className="text-gray-600 dark:text-gray-400">Mã đặt phòng:</span>
                                <span className="font-semibold dark:text-gray-200">BK{booking.id.toString().padStart(6, '0')}</span>
                            </p>
                            <p className="flex justify-between mb-2">
                                <span className="text-gray-600 dark:text-gray-400">ID khách hàng:</span>
                                <span className="dark:text-gray-200">{booking.touristId}</span>
                            </p>
                            {booking.tourist && (
                                <div className="mt-4 pt-4 border-t border-gray-200 dark:border-gray-700">
                                    <p className="text-sm text-gray-500 dark:text-gray-400 mb-2">Thông tin khách hàng:</p>
                                    <p className="dark:text-gray-200 font-medium">
                                        {booking.tourist.firstName} {booking.tourist.lastName}
                                    </p>
                                    <p className="text-sm text-gray-600 dark:text-gray-400">{booking.tourist.email}</p>
                                    {booking.tourist.phoneNumber && (
                                        <p className="text-sm text-gray-600 dark:text-gray-400">{booking.tourist.phoneNumber}</p>
                                    )}
                                </div>
                            )}
                            {isCancellable && (
                                <div className="mt-4">
                                    <button className="w-full border border-red-500 dark:border-red-400 text-red-500 dark:text-red-400 px-4 py-2 rounded-md hover:bg-red-50 dark:hover:bg-red-900/20 transition-colors font-medium">
                                        Hủy đặt phòng
                                    </button>
                                </div>
                            )}
                        </InfoSection>
                        {/* Contact Hotel */}
                        <InfoSection title="Liên hệ chỗ nghỉ">
                            <div className="space-y-3">
                                <div className="flex items-center">
                                    <Phone size={18} className="mr-3 text-gray-700 dark:text-gray-300" />
                                    <span className="dark:text-gray-200">+84 258 1234 567</span>
                                </div>
                                <div className="flex items-center">
                                    <Mail size={18} className="mr-3 text-gray-700 dark:text-gray-300" />
                                    <span className="dark:text-gray-200">info@accommodation.com</span>
                                </div>
                            </div>
                        </InfoSection>

                        {/* Actions */}
                        <InfoSection title="Các tùy chọn">
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
