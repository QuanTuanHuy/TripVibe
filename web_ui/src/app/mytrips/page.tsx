"use client";

import React, { useState, useEffect } from 'react';
import { format } from 'date-fns';
import { vi } from 'date-fns/locale';
import Image from 'next/image';
import Link from 'next/link';
import Header from '@/components/Header';
import { Calendar, MapPin, CheckCircle, XCircle, Clock, ChevronRight, Star, AlertCircle, ChevronLeft } from 'lucide-react';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Button } from '@/components/ui/button';
import { bookingService } from '@/services/booking/bookingService';
import accommodationService from '@/services/accommodation/accommodationService';
import { BookingResponse } from '@/types/booking/booking.types';
import { AccommodationThumbnail } from '@/types/accommodation';

// Enhanced booking interface with accommodation details
interface EnhancedBooking extends BookingResponse {
    accommodation?: AccommodationThumbnail;
}

// Pagination state interface
interface PaginationState {
    currentPage: number;
    pageSize: number;
    totalPages: number;
    totalItems: number;
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
    let text = ''; switch (status) {
        case 'CONFIRMED':
            bgColor = 'bg-green-100 dark:bg-green-900/40';
            textColor = 'text-green-800 dark:text-green-300';
            icon = <CheckCircle size={16} className="mr-1 text-green-600 dark:text-green-400" />;
            text = 'Đã xác nhận';
            break;
        case 'APPROVED':
            bgColor = 'bg-blue-100 dark:bg-blue-900/40';
            textColor = 'text-blue-800 dark:text-blue-300';
            icon = <CheckCircle size={16} className="mr-1 text-blue-600 dark:text-blue-400" />;
            text = 'Đã phê duyệt';
            break;
        case 'CANCELLED':
            bgColor = 'bg-red-100 dark:bg-red-900/40';
            textColor = 'text-red-800 dark:text-red-300';
            icon = <XCircle size={16} className="mr-1 text-red-600 dark:text-red-400" />;
            text = 'Đã hủy';
            break;
        case 'REJECTED':
            bgColor = 'bg-red-100 dark:bg-red-900/40';
            textColor = 'text-red-800 dark:text-red-300';
            icon = <XCircle size={16} className="mr-1 text-red-600 dark:text-red-400" />;
            text = 'Bị từ chối';
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
const BookingCard = ({ booking }: { booking: EnhancedBooking }) => {
    const checkInDate = new Date(booking.stayFrom * 1000); // Convert from Unix timestamp
    const checkOutDate = new Date(booking.stayTo * 1000);

    // Calculate number of nights
    const nights = Math.round(
        (checkOutDate.getTime() - checkInDate.getTime()) / (1000 * 60 * 60 * 24)
    );

    // Get unit information for display
    const getUnitDisplayInfo = () => {
        if (!booking.units || booking.units.length === 0) {
            return { unitNames: 'Room', totalRooms: 1 };
        }

        // Group units by quantity to show proper room count
        const totalRooms = booking.units.reduce((total, unit) => total + unit.quantity, 0);

        // For display, we'll show unit count since we don't have detailed unit names from thumbnail
        if (booking.units.length === 1) {
            const unit = booking.units[0];
            return {
                unitNames: unit.quantity > 1 ? `${unit.quantity} phòng` : '1 phòng',
                totalRooms: unit.quantity
            };
        } else {
            return {
                unitNames: `${booking.units.length} loại phòng`,
                totalRooms: totalRooms
            };
        }
    };

    const { unitNames, totalRooms } = getUnitDisplayInfo();
    const totalGuests = booking.numberOfAdult + booking.numberOfChild;

    return (
        <div className="mb-6 bg-white dark:bg-gray-900 rounded-lg shadow-sm border border-gray-200 dark:border-gray-800 overflow-hidden">
            <div className="flex flex-col md:flex-row">
                {/* Hotel Image */}
                <div className="md:w-1/3 h-48 md:h-auto relative">
                    <Image
                        src={booking.accommodation?.thumbnailUrl || '/placeholder-hotel.jpg'}
                        alt={booking.accommodation?.name || 'Accommodation'}
                        fill
                        className="object-cover"
                    />
                </div>

                {/* Booking details */}
                <div className="p-4 md:p-6 flex-1">
                    <div className="flex justify-between items-start">
                        <div>
                            <h3 className="text-lg md:text-xl font-bold text-gray-900 dark:text-gray-100 mb-1">
                                {booking.accommodation?.name || 'Accommodation'}
                            </h3>
                            <div className="flex items-center text-gray-500 dark:text-gray-400 mb-3">
                                <MapPin size={16} className="mr-1" />
                                <span className="text-sm">
                                    {booking.accommodation?.location?.address || 'Address not available'}
                                </span>
                                {booking.accommodation?.ratingSummary?.rating && (
                                    <div className="ml-3 flex items-center">
                                        <Star size={16} className="text-yellow-500 fill-yellow-500 mr-1" />
                                        <span className="text-sm font-medium">
                                            {booking.accommodation.ratingSummary.rating.toFixed(1)}
                                        </span>
                                    </div>
                                )}
                            </div>
                        </div>
                        <BookingStatusBadge status={booking.status} />
                    </div>

                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-4">
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
                                <p className="text-sm text-gray-500 dark:text-gray-400 mb-1">
                                    Mã đặt phòng: BK{booking.id.toString().padStart(6, '0')}
                                </p>                                <p className="flex items-center">
                                    <span className="text-gray-500 dark:text-gray-400 text-sm mr-2">
                                        {unitNames} • {nights} đêm
                                    </span>
                                    <span className="text-gray-500 dark:text-gray-400 text-sm mr-2">•</span>
                                    <span className="text-gray-500 dark:text-gray-400 text-sm">
                                        {totalGuests} khách
                                    </span>
                                </p>
                            </div>

                            <div className="text-right mt-2 md:mt-0">
                                <p className="text-lg font-bold text-gray-900 dark:text-gray-100">
                                    {formatCurrency(booking.finalAmount || booking.invoiceAmount)}
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

// Pagination component
const Pagination = ({
    currentPage,
    totalPages,
    onPageChange
}: {
    currentPage: number;
    totalPages: number;
    onPageChange: (page: number) => void;
}) => {
    if (totalPages <= 1) return null;

    const getVisiblePages = () => {
        const delta = 2;
        const range = [];
        const rangeWithDots = [];

        for (let i = Math.max(2, currentPage - delta); i <= Math.min(totalPages - 1, currentPage + delta); i++) {
            range.push(i);
        }

        if (currentPage - delta > 2) {
            rangeWithDots.push(1, '...');
        } else {
            rangeWithDots.push(1);
        }

        rangeWithDots.push(...range);

        if (currentPage + delta < totalPages - 1) {
            rangeWithDots.push('...', totalPages);
        } else {
            rangeWithDots.push(totalPages);
        }

        return rangeWithDots;
    };

    return (
        <div className="flex items-center justify-center space-x-2 mt-8">
            <Button
                variant="outline"
                size="sm"
                onClick={() => onPageChange(currentPage - 1)}
                disabled={currentPage === 1}
                className="flex items-center"
            >
                <ChevronLeft size={16} className="mr-1" />
                Trước
            </Button>

            {getVisiblePages().map((page, index) => (
                <Button
                    key={index}
                    variant={page === currentPage ? "default" : "outline"}
                    size="sm"
                    onClick={() => typeof page === 'number' && onPageChange(page)}
                    disabled={typeof page !== 'number'}
                    className={typeof page !== 'number' ? 'cursor-default' : ''}
                >
                    {page}
                </Button>
            ))}

            <Button
                variant="outline"
                size="sm"
                onClick={() => onPageChange(currentPage + 1)}
                disabled={currentPage === totalPages}
                className="flex items-center"
            >
                Sau
                <ChevronRight size={16} className="ml-1" />
            </Button>
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
    const [activeTab, setActiveTab] = useState<'upcoming' | 'past'>('upcoming');
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [upcomingBookings, setUpcomingBookings] = useState<EnhancedBooking[]>([]);
    const [pastBookings, setPastBookings] = useState<EnhancedBooking[]>([]);

    // Pagination states
    const [upcomingPagination, setUpcomingPagination] = useState<PaginationState>({
        currentPage: 1,
        pageSize: 10,
        totalPages: 0,
        totalItems: 0,
    });

    const [pastPagination, setPastPagination] = useState<PaginationState>({
        currentPage: 1,
        pageSize: 10,
        totalPages: 0,
        totalItems: 0,
    });

    // Fetch accommodation details for bookings
    const fetchAccommodationDetails = async (bookings: BookingResponse[]): Promise<EnhancedBooking[]> => {
        try {
            const accommodationIds = [...new Set(bookings.map(booking => booking.accommodationId))];

            if (accommodationIds.length === 0) {
                return bookings as EnhancedBooking[];
            }

            const accommodationThumbnails = await accommodationService.getAccommodationThumbnails(accommodationIds);
            const accommodationMap = new Map(accommodationThumbnails.map(acc => [acc.id, acc]));

            return bookings.map(booking => ({
                ...booking,
                accommodation: accommodationMap.get(booking.accommodationId),
            }));
        } catch (error) {
            console.error('Error fetching accommodation details:', error);
            return bookings as EnhancedBooking[];
        }
    };

    // Fetch upcoming bookings
    const fetchUpcomingBookings = async (page: number = 1) => {
        try {
            setIsLoading(true);
            setError(null);

            const response = await bookingService.getBookings({
                page: page - 1,
                pageSize: upcomingPagination.pageSize,
                sort: 'stayFrom,asc', // Sort by check-in date ascending
            });

            // Filter for upcoming bookings (check-in date is in the future or booking is in progress)
            const now = Math.floor(Date.now() / 1000); // Current time in Unix timestamp
            const upcomingBookings = response.data.filter(booking => {
                // Include bookings that haven't started yet or are in progress (not completed/cancelled)
                return booking.stayFrom > now ||
                    (booking.stayFrom <= now && booking.stayTo > now &&
                        booking.status !== 'COMPLETED' && booking.status !== 'CANCELLED' && booking.status !== 'REJECTED');
            });

            const enhancedBookings = await fetchAccommodationDetails(upcomingBookings);
            setUpcomingBookings(enhancedBookings);
            setUpcomingPagination(prev => ({
                ...prev,
                currentPage: page,
                totalPages: Math.ceil(upcomingBookings.length / upcomingPagination.pageSize),
                totalItems: upcomingBookings.length,
            }));
        } catch (error) {
            console.error('Error fetching upcoming bookings:', error);
            setError('Không thể tải danh sách đặt phòng sắp tới. Vui lòng thử lại sau.');
        } finally {
            setIsLoading(false);
        }
    };

    // Fetch past bookings
    const fetchPastBookings = async (page: number = 1) => {
        try {
            setIsLoading(true);
            setError(null);

            const response = await bookingService.getBookings({
                page: page - 1,
                pageSize: pastPagination.pageSize,
                sort: 'stayTo,desc', // Sort by check-out date descending
            });

            // Filter for past bookings (check-out date is in the past or booking is completed)
            const now = Math.floor(Date.now() / 1000); // Current time in Unix timestamp
            const pastBookings = response.data.filter(booking => {
                // Include bookings that have ended or are completed/cancelled
                return booking.stayTo <= now ||
                    booking.status === 'COMPLETED' ||
                    booking.status === 'CANCELLED' ||
                    booking.status === 'REJECTED';
            });

            const enhancedBookings = await fetchAccommodationDetails(pastBookings);
            setPastBookings(enhancedBookings);
            setPastPagination(prev => ({
                ...prev,
                currentPage: page,
                totalPages: Math.ceil(pastBookings.length / pastPagination.pageSize),
                totalItems: pastBookings.length,
            }));
        } catch (error) {
            console.error('Error fetching past bookings:', error);
            setError('Không thể tải danh sách đặt phòng đã qua. Vui lòng thử lại sau.');
        } finally {
            setIsLoading(false);
        }
    };

    // Load initial data for both tabs on mount
    useEffect(() => {
        // Load data for both tabs to show correct counts
        fetchUpcomingBookings(upcomingPagination.currentPage);

        // Load past bookings data to show correct count in tab, but don't set loading state
        const loadPastBookingsData = async () => {
            try {
                const response = await bookingService.getBookings({
                    page: 0,
                    pageSize: pastPagination.pageSize,
                    sort: 'stayTo,desc',
                });

                const now = Math.floor(Date.now() / 1000);
                const pastBookings = response.data.filter(booking => {
                    return booking.stayTo <= now ||
                        booking.status === 'COMPLETED' ||
                        booking.status === 'CANCELLED' ||
                        booking.status === 'REJECTED';
                });

                const enhancedBookings = await fetchAccommodationDetails(pastBookings);
                setPastBookings(enhancedBookings);
                setPastPagination(prev => ({
                    ...prev,
                    currentPage: 1,
                    totalPages: Math.ceil(pastBookings.length / pastPagination.pageSize),
                    totalItems: pastBookings.length,
                }));
            } catch (error) {
                console.error('Error fetching past bookings for count:', error);
            }
        };

        loadPastBookingsData();
    }, []);

    // Load data when tab changes (if needed)
    useEffect(() => {
        if (activeTab === 'past' && pastBookings.length === 0) {
            // Only fetch if we don't have past bookings data yet
            fetchPastBookings(pastPagination.currentPage);
        }
    }, [activeTab]);

    // Handle pagination for upcoming bookings
    const handleUpcomingPageChange = (page: number) => {
        fetchUpcomingBookings(page);
    };

    // Handle pagination for past bookings
    const handlePastPageChange = (page: number) => {
        fetchPastBookings(page);
    };

    return (
        <div className="min-h-screen bg-gray-50 dark:bg-gray-900">
            <Header />

            <div className="container max-w-5xl mx-auto px-4 py-8">
                <div className="mb-8">
                    <h1 className="text-2xl md:text-3xl font-bold text-gray-900 dark:text-gray-50 mb-2">
                        Chuyến đi của tôi
                    </h1>
                    <p className="text-gray-600 dark:text-gray-400">
                        Quản lý tất cả đơn đặt phòng sắp tới và đã qua của bạn.
                    </p>
                </div>

                {error && (
                    <div className="mb-6 p-4 bg-red-50 dark:bg-red-900/50 border border-red-200 dark:border-red-800 rounded-lg">
                        <p className="text-red-700 dark:text-red-300">{error}</p>
                    </div>
                )}

                <Tabs defaultValue="upcoming" className="w-full" onValueChange={(value) => setActiveTab(value as 'upcoming' | 'past')}>
                    <TabsList className="mb-6 bg-white dark:bg-gray-800 border border-gray-200 dark:border-gray-700 rounded-lg p-1 w-full md:w-auto">
                        <TabsTrigger value="upcoming" className="flex-1 md:flex-none">
                            Sắp tới ({upcomingPagination.totalItems})
                        </TabsTrigger>
                        <TabsTrigger value="past" className="flex-1 md:flex-none">
                            Đã qua ({pastPagination.totalItems})
                        </TabsTrigger>
                    </TabsList>

                    <TabsContent value="upcoming" className="mt-0">
                        {isLoading ? (
                            <div className="flex items-center justify-center py-16">
                                <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-[#0071c2]"></div>
                                <span className="ml-2 text-gray-600 dark:text-gray-400">Đang tải...</span>
                            </div>
                        ) : upcomingBookings.length > 0 ? (
                            <>
                                <div>
                                    {upcomingBookings.map((booking) => (
                                        <BookingCard key={booking.id} booking={booking} />
                                    ))}
                                </div>
                                <Pagination
                                    currentPage={upcomingPagination.currentPage}
                                    totalPages={upcomingPagination.totalPages}
                                    onPageChange={handleUpcomingPageChange}
                                />
                            </>
                        ) : (
                            <EmptyState type="upcoming" />
                        )}
                    </TabsContent>

                    <TabsContent value="past" className="mt-0">
                        {isLoading ? (
                            <div className="flex items-center justify-center py-16">
                                <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-[#0071c2]"></div>
                                <span className="ml-2 text-gray-600 dark:text-gray-400">Đang tải...</span>
                            </div>
                        ) : pastBookings.length > 0 ? (
                            <>
                                <div>
                                    {pastBookings.map((booking) => (
                                        <BookingCard key={booking.id} booking={booking} />
                                    ))}
                                </div>
                                <Pagination
                                    currentPage={pastPagination.currentPage}
                                    totalPages={pastPagination.totalPages}
                                    onPageChange={handlePastPageChange}
                                />
                            </>
                        ) : (
                            <EmptyState type="past" />
                        )}
                    </TabsContent>
                </Tabs>
            </div>
        </div>
    );
}
