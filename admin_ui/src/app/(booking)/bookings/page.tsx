"use client";

import { useState, useEffect, Suspense } from "react";
import {
    Card,
    CardContent,
    CardHeader,
    CardTitle,
    CardDescription
} from "@/components/ui/card";
import {
    Pagination,
    PaginationContent,
    PaginationEllipsis,
    PaginationItem,
    PaginationLink,
    PaginationNext,
    PaginationPrevious
} from "@/components/ui/pagination";
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue
} from "@/components/ui/select";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { DatePickerWithRange } from "@/components/ui/date-range-picker";
import { BookingList } from "@/components/bookings/booking-list";
import { BookingDetailDialog } from "@/components/bookings/booking-detail-dialog";
import { BookingSummaryCards } from "@/components/bookings/booking-summary-cards";
import {
    CalendarDays,
    Search,
    Plus,
    Loader2
} from "lucide-react";
import { type DateRange } from "react-day-picker";
import { toast } from "sonner";
import { useAuth } from "@/context/AuthContext";

// Import API types and services
import {
    Booking,
    BookingParams
} from "@/types/booking";
import {
    APIBookingStatus,
    UIBookingStatus,
    mapApiStatusToUI,
    mapUIStatusToAPI,
    getStatusDisplayName,
    getCurrentUIStatusForFilter,
    getStatusFilterOptions
} from "@/types/booking/status";
import { bookingService, accommodationService, bookingActions } from "@/services";
import { Accommodation } from "@/types/accommodation";

// UI-specific types for compatibility with existing components
interface Guest {
    id: number;
    name: string;
    email: string;
    phone: string;
    avatar?: string;
}

interface Room {
    id: number;
    name: string;
    roomNumber?: string;
    unitType: string;
}

interface BookingDetails {
    id: number;
    bookingId: string;
    status: UIBookingStatus;
    checkIn: string;
    checkOut: string;
    guests: number;
    totalAmount: number;
    currency: string;
    createdAt: string;
    specialRequests?: string;
    guest: Guest;
    room: Room;
    isPaid: boolean;
    paymentMethod?: string;
}


interface FilterOptions {
    dateRange: DateRange | null;
    status: APIBookingStatus | null;
    accommodationId: number | null;
    search: string;
    sortBy: string;
    sortOrder: "asc" | "desc";
}

interface BookingSummary {
    total: number;
    pending: number;
    confirmed: number;
    checkedIn: number;
    today: {
        checkIn: number;
        checkOut: number;
    };
}

const DEFAULT_SUMMARY: BookingSummary = {
    total: 0,
    pending: 0,
    confirmed: 0,
    checkedIn: 0,
    today: {
        checkIn: 0,
        checkOut: 0,
    }
};


// Convert API Booking to UI BookingDetails
const transformBookingToBookingDetails = (booking: Booking): BookingDetails => {
    const guest: Guest = {
        id: booking.tourist?.id || 0,
        name: booking.tourist ? `${booking.tourist.firstName} ${booking.tourist.lastName}` : 'Unknown',
        email: booking.tourist?.email || '',
        phone: booking.tourist?.phoneNumber || '',
    };

    // Get the first unit for room info (simplified)
    const firstUnit = booking.units?.[0];
    const room: Room = {
        id: firstUnit?.unitId || 0,
        name: firstUnit?.fullName || 'Unknown Room',
        roomNumber: firstUnit?.unitId?.toString() || '',
        unitType: 'Standard' // Default type
    };

    return {
        id: booking.id,
        bookingId: `BK-${booking.id}`,
        status: mapApiStatusToUI(booking.status),
        checkIn: bookingService.formatDate(booking.stayFrom),
        checkOut: bookingService.formatDate(booking.stayTo),
        guests: booking.numberOfAdult + booking.numberOfChild,
        totalAmount: booking.finalAmount,
        currency: 'VND', // Default currency
        createdAt: bookingService.formatDate(booking.stayFrom), // Using stayFrom as proxy for createdAt
        specialRequests: booking.note || undefined,
        guest,
        room,
        // isPaid: booking.paymentId ? true : false,
        isPaid: booking.status !== 'PENDING',
        paymentMethod: booking.paymentId ? 'Unknown' : undefined
    };
};


// Helper function for pagination button
function PaginationButton({
    children,
    isActive,
    onClick
}: {
    children: React.ReactNode;
    isActive: boolean;
    onClick: () => void;
}) {
    return (
        <PaginationItem>
            <PaginationLink
                onClick={onClick}
                isActive={isActive}
                className={isActive ? "bg-primary text-primary-foreground hover:bg-primary/90" : ""}
            >
                {children}
            </PaginationLink>
        </PaginationItem>
    );
}

// Component chứa logic chính
function BookingsContent() {
    const { user } = useAuth();
    const [currentPage, setCurrentPage] = useState(1);
    const [pageSize] = useState(10);
    const [totalPages, setTotalPages] = useState(5);
    const [isLoading, setIsLoading] = useState(true);
    const [bookings, setBookings] = useState<BookingDetails[]>([]);
    const [accommodations, setAccommodations] = useState<Accommodation[]>([]);
    const [isLoadingAccommodations, setIsLoadingAccommodations] = useState(true);
    const [summary, setSummary] = useState<BookingSummary>(DEFAULT_SUMMARY);
    const [selectedBooking, setSelectedBooking] = useState<BookingDetails | null>(null);
    const [detailDialogOpen, setDetailDialogOpen] = useState(false); const [filterOptions, setFilterOptions] = useState<FilterOptions>({
        dateRange: null,
        status: null,
        accommodationId: null,
        search: "",
        sortBy: "stay_from",
        sortOrder: "desc"
    });
    // Load accommodations for current user
    useEffect(() => {
        const loadAccommodations = async () => {
            if (!user?.id) {
                setIsLoadingAccommodations(false);
                return;
            }

            try {
                setIsLoadingAccommodations(true);
                const userAccommodations = await accommodationService.getMyAccommodations(user.id);
                setAccommodations(userAccommodations);

                // Mặc định chọn accommodation đầu tiên nếu có
                if (userAccommodations.length > 0 && !filterOptions.accommodationId) {
                    setFilterOptions(prev => ({
                        ...prev,
                        accommodationId: userAccommodations[0].id
                    }));
                }
            } catch (error) {
                console.error('Error loading accommodations:', error);
                toast.error('Không thể tải danh sách chỗ nghỉ.');
                setAccommodations([]);
            } finally {
                setIsLoadingAccommodations(false);
            }
        };

        loadAccommodations();
    }, [user?.id]);

    // Load bookings with filtering
    useEffect(() => {
        const loadBookings = async () => {
            // Chỉ load bookings khi có accommodationId
            if (!filterOptions.accommodationId) {
                setBookings([]);
                setTotalPages(1);
                setIsLoading(false);
                return;
            }

            setIsLoading(true);
            try {
                // Prepare API parameters
                const params: BookingParams = {
                    page: currentPage - 1,
                    pageSize: pageSize,
                    orderBy: filterOptions.sortBy === 'checkIn' ? 'stay_from' :
                        filterOptions.sortBy === 'checkOut' ? 'stay_to' :
                            filterOptions.sortBy === 'createdAt' ? 'id' :
                                filterOptions.sortBy,
                    direct: filterOptions.sortOrder,
                };
                if (filterOptions.status) {
                    params.status = filterOptions.status;
                }

                params.accommodationId = filterOptions.accommodationId;

                if (filterOptions.dateRange?.from && filterOptions.dateRange?.to) {
                    params.startTime = Math.floor(filterOptions.dateRange.from.getTime() / 1000);
                    params.endTime = Math.floor(filterOptions.dateRange.to.getTime() / 1000);
                    params.dateType = 'stay_from';
                }

                const response = await bookingService.getAllBookings(params);

                // Filter by search if needed (API might not support text search)
                let filteredBookings = response.data || [];
                if (filterOptions.search) {
                    const search = filterOptions.search.toLowerCase();
                    filteredBookings = filteredBookings.filter(booking =>
                        booking.id.toString().includes(search) ||
                        booking.tourist?.firstName?.toLowerCase().includes(search) ||
                        booking.tourist?.lastName?.toLowerCase().includes(search) ||
                        booking.tourist?.email?.toLowerCase().includes(search)
                    );
                }
                // Transform API bookings to UI format
                const transformedBookings = filteredBookings.map(transformBookingToBookingDetails);
                setBookings(transformedBookings);
                setTotalPages(response.totalPage || 1);


            } catch (error) {
                console.error('Error loading bookings:', error);
                toast.error('Không thể tải danh sách đặt phòng. Vui lòng thử lại sau.');
                setBookings([]);
                setSummary(DEFAULT_SUMMARY);
            } finally {
                setIsLoading(false);
            }
        };

        loadBookings();
    }, [currentPage, pageSize, filterOptions]);

    useEffect(() => {
        const loadStatistics = async () => {
            if (!filterOptions.accommodationId) {
                setSummary(DEFAULT_SUMMARY);
                return;
            }

            try {
                const statisticsResponse = await bookingService.getBookingStatistics({
                    accommodationId: filterOptions.accommodationId,
                    ...(filterOptions.dateRange?.from && filterOptions.dateRange?.to && {
                        startTime: Math.floor(filterOptions.dateRange.from.getTime() / 1000),
                        endTime: Math.floor(filterOptions.dateRange.to.getTime() / 1000)
                    })
                });
                setSummary({
                    total: statisticsResponse.totalBookings || 0,
                    pending: statisticsResponse.statusCounts?.['PENDING'] || 0,
                    confirmed: statisticsResponse.statusCounts?.['CONFIRMED'] || 0,
                    checkedIn: statisticsResponse.statusCounts?.['CHECKED_IN'] || 0,
                    today: {
                        checkIn: statisticsResponse.statusCounts?.['TODAY_CHECK_IN'] || 0,
                        checkOut: statisticsResponse.statusCounts?.['TODAY_CHECK_OUT'] || 0
                    }
                });
            } catch (error) {
                console.error('Error loading booking statistics:', error);
                setSummary(DEFAULT_SUMMARY);
            }
        };

        loadStatistics();
    }, [filterOptions.accommodationId, filterOptions.dateRange]);

    // Handle booking status change
    const handleStatusChange = async (bookingId: number, newStatus: UIBookingStatus) => {
        try {
            const currentBooking = bookings.find(b => b.id === bookingId);
            const currentStatus = currentBooking?.status;

            // Use the new booking actions service
            await bookingActions.executeBookingStatusChange(bookingId, newStatus, currentStatus);

            // Update local state
            setBookings(prev => prev.map(booking =>
                booking.id === bookingId ? { ...booking, status: newStatus } : booking
            ));

            // Update selected booking if it's open
            if (selectedBooking?.id === bookingId) {
                setSelectedBooking(prev => prev ? { ...prev, status: newStatus } : null);
            }

            const actionName = bookingActions.getStatusActionName(newStatus, currentStatus);
            toast.success(`Đã ${actionName} đặt phòng thành công`);
        } catch (error) {
            console.error('Error updating booking status:', error);
            toast.error('Không thể cập nhật trạng thái. Vui lòng thử lại sau.');
        }
    };

    // View booking details
    const handleViewBooking = (booking: BookingDetails) => {
        setSelectedBooking(booking);
        setDetailDialogOpen(true);
    };

    // Helper function to format currency using bookingService
    const formatCurrency = (amount: number, currency: string = 'VND') => {
        return bookingService.formatCurrency(amount, currency);
    };

    return (
        <div className="container mx-auto py-6">
            <div className="flex flex-col gap-6">
                <div>
                    <h1 className="text-3xl font-bold tracking-tight">Quản lý đặt phòng</h1>
                    <p className="text-muted-foreground">
                        Quản lý tất cả các đặt phòng tại chỗ nghỉ của bạn
                    </p>
                </div>

                {/* Accommodation Selector */}
                <Card>
                    <CardContent className="p-4">
                        <div className="flex flex-col sm:flex-row sm:items-center gap-4">
                            <div className="flex-1">
                                <label className="text-sm font-medium mb-2 block">
                                    Chọn chỗ nghỉ để quản lý
                                </label>
                                <Select
                                    value={filterOptions.accommodationId?.toString() || ""}
                                    onValueChange={(value) => {
                                        setFilterOptions({
                                            ...filterOptions,
                                            accommodationId: parseInt(value)
                                        });
                                    }}
                                    disabled={isLoadingAccommodations}
                                >
                                    <SelectTrigger className="w-full sm:w-[300px]">
                                        <SelectValue placeholder={
                                            isLoadingAccommodations ? "Đang tải..." : "Chọn chỗ nghỉ"
                                        } />
                                    </SelectTrigger>
                                    <SelectContent>
                                        {accommodations.map((accommodation) => (
                                            <SelectItem
                                                key={accommodation.id}
                                                value={accommodation.id.toString()}
                                            >
                                                <div className="flex items-center">
                                                    <span>{accommodation.name}</span>
                                                    <span className="ml-2 text-xs text-muted-foreground">
                                                        ID #{accommodation.id}
                                                    </span>
                                                </div>
                                            </SelectItem>
                                        ))}
                                    </SelectContent>
                                </Select>
                            </div>

                        </div>
                    </CardContent>
                </Card>

                {/* Summary cards */}
                <div className="grid grid-cols-1 md:grid-cols-3 lg:grid-cols-5 gap-4">
                    <BookingSummaryCards summary={summary} />
                </div>

                {/* Main content */}
                <div className="space-y-4">
                    <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
                        <div>
                            <h2 className="text-xl font-semibold">Danh sách đặt phòng</h2>
                            <p className="text-sm text-muted-foreground">
                                Quản lý và theo dõi tất cả các đặt phòng
                            </p>
                        </div>

                        <div className="flex items-center gap-2">
                            <Button variant="outline" className="gap-2">
                                <CalendarDays className="h-4 w-4" />
                                <span className="hidden sm:inline">Lịch đặt phòng</span>
                            </Button>

                            <Button className="gap-2">
                                <Plus className="h-4 w-4" />
                                <span className="hidden sm:inline">Đặt phòng mới</span>
                            </Button>
                        </div>
                    </div>

                    {/* Filters */}
                    <Card>
                        <CardContent className="p-4">
                            <div className="grid grid-cols-1 md:grid-cols-12 gap-4">
                                <div className="md:col-span-4 lg:col-span-3">
                                    <DatePickerWithRange
                                        onDateChange={(date) => setFilterOptions({
                                            ...filterOptions,
                                            dateRange: date || null
                                        })}
                                        className="w-full"
                                    />
                                </div>

                                <div className="relative md:col-span-4 lg:col-span-3">
                                    <Search className="absolute left-2.5 top-2.5 h-4 w-4 text-muted-foreground" />
                                    <Input
                                        placeholder="Tìm kiếm mã, tên khách..."
                                        className="pl-8"
                                        value={filterOptions.search}
                                        onChange={(e) => setFilterOptions({
                                            ...filterOptions,
                                            search: e.target.value
                                        })}
                                    />
                                </div>
                                <div className="md:col-span-2 lg:col-span-2">
                                    <Select
                                        value={getCurrentUIStatusForFilter(filterOptions.status)}
                                        onValueChange={(value) => {
                                            // Convert UI status to API status for filtering
                                            let apiStatus: APIBookingStatus | null = null;
                                            if (value !== "all") {
                                                apiStatus = mapUIStatusToAPI(value as UIBookingStatus);
                                            }
                                            setFilterOptions({
                                                ...filterOptions,
                                                status: apiStatus
                                            });
                                        }}
                                    >
                                        <SelectTrigger>
                                            <SelectValue placeholder="Trạng thái" />
                                        </SelectTrigger>
                                        <SelectContent>
                                            {getStatusFilterOptions().map(option => (
                                                <SelectItem key={option.value} value={option.value}>
                                                    {option.label}
                                                </SelectItem>))}
                                        </SelectContent>
                                    </Select>
                                </div>

                                <div className="md:col-span-2 lg:col-span-3">
                                    <Select
                                        value={`${filterOptions.sortBy}-${filterOptions.sortOrder}`}
                                        onValueChange={(value) => {
                                            const [sortBy, sortOrder] = value.split('-');
                                            setFilterOptions({
                                                ...filterOptions,
                                                sortBy,
                                                sortOrder: sortOrder as 'asc' | 'desc'
                                            });
                                        }}
                                    >
                                        <SelectTrigger>
                                            <SelectValue placeholder="Sắp xếp theo" />
                                        </SelectTrigger>
                                        <SelectContent>
                                            <SelectItem value="stay_from-desc">Check-in (Mới nhất)</SelectItem>
                                            <SelectItem value="stay_from-asc">Check-in (Cũ nhất)</SelectItem>
                                            <SelectItem value="id-desc">Ngày đặt (Mới nhất)</SelectItem>
                                            <SelectItem value="id-asc">Ngày đặt (Cũ nhất)</SelectItem>
                                            <SelectItem value="final_amount-desc">Giá (Cao đến thấp)</SelectItem>
                                            <SelectItem value="final_amount-asc">Giá (Thấp đến cao)</SelectItem>
                                        </SelectContent>
                                    </Select>
                                </div>

                                <div className="col-span-1 flex justify-end">
                                    <Button
                                        variant="outline"
                                        onClick={() => setFilterOptions({
                                            dateRange: null,
                                            status: null,
                                            accommodationId: accommodations.length > 0 ? accommodations[0].id : null,
                                            search: "",
                                            sortBy: "stay_from",
                                            sortOrder: "desc"
                                        })}
                                        className="w-full md:w-auto"
                                    >
                                        Đặt lại bộ lọc
                                    </Button>
                                </div>
                            </div>
                        </CardContent>
                    </Card>

                    {/* Bookings list */}
                    <Card>
                        <CardHeader>
                            <CardTitle>Danh sách đặt phòng</CardTitle>
                            <CardDescription>
                                {isLoading ? (
                                    "Đang tải dữ liệu..."
                                ) : (
                                    `Hiển thị ${bookings.length} trên tổng số ${summary.total} đặt phòng`
                                )}
                            </CardDescription>
                        </CardHeader>
                        <CardContent>
                            {isLoading ? (
                                <div className="flex justify-center items-center py-12">
                                    <Loader2 className="h-8 w-8 animate-spin text-primary" />
                                    <span className="ml-2">Đang tải danh sách đặt phòng...</span>
                                </div>
                            ) : bookings.length === 0 ? (
                                <div className="flex flex-col items-center justify-center py-12 text-center">
                                    <div className="rounded-full bg-muted p-3 mb-3">
                                        <CalendarDays className="h-10 w-10 text-muted-foreground" />
                                    </div>
                                    <h3 className="mt-2 text-lg font-semibold">Không tìm thấy đặt phòng nào</h3>
                                    <p className="text-muted-foreground mt-1">
                                        Không có đặt phòng nào khớp với điều kiện tìm kiếm của bạn.
                                    </p>
                                    <Button
                                        variant="outline"
                                        className="mt-4" onClick={() => setFilterOptions({
                                            dateRange: null,
                                            status: null,
                                            accommodationId: accommodations.length > 0 ? accommodations[0].id : null,
                                            search: "",
                                            sortBy: "stay_from",
                                            sortOrder: "desc"
                                        })}
                                    >
                                        Xóa bộ lọc
                                    </Button>
                                </div>
                            ) : (
                                <>
                                    <BookingList
                                        bookings={bookings}
                                        onViewBooking={handleViewBooking}
                                        onStatusChange={handleStatusChange}
                                        formatCurrency={formatCurrency}
                                        getStatusDisplayName={getStatusDisplayName}
                                    />

                                    <div className="mt-6 flex justify-center">
                                        <Pagination>
                                            <PaginationContent>
                                                <PaginationPrevious
                                                    onClick={() => setCurrentPage(prev => Math.max(1, prev - 1))}
                                                    className={currentPage === 1 ? "opacity-50 cursor-not-allowed" : ""}
                                                    aria-disabled={currentPage === 1}
                                                />

                                                {Array.from({ length: totalPages }).map((_, i) => {
                                                    const page = i + 1;

                                                    if (
                                                        page === 1 ||
                                                        page === totalPages ||
                                                        (page >= currentPage - 1 && page <= currentPage + 1)
                                                    ) {
                                                        return (
                                                            <PaginationButton
                                                                key={page}
                                                                onClick={() => setCurrentPage(page)}
                                                                isActive={page === currentPage}
                                                            >
                                                                {page}
                                                            </PaginationButton>
                                                        );
                                                    } else if (
                                                        page === currentPage - 2 ||
                                                        page === currentPage + 2
                                                    ) {
                                                        return <PaginationEllipsis key={`ellipsis-${page}`} />;
                                                    }
                                                    return null;
                                                })}

                                                <PaginationNext
                                                    onClick={() => setCurrentPage(prev => Math.min(totalPages, prev + 1))}
                                                    className={currentPage === totalPages ? "opacity-50 cursor-not-allowed" : ""}
                                                    aria-disabled={currentPage === totalPages}
                                                />
                                            </PaginationContent>
                                        </Pagination>
                                    </div>
                                </>)}
                        </CardContent>
                    </Card>
                </div>
            </div>

            {/* Booking Detail Dialog */}
            {selectedBooking && (<BookingDetailDialog
                open={detailDialogOpen}
                onOpenChange={setDetailDialogOpen}
                booking={selectedBooking} onStatusChange={handleStatusChange}
                formatCurrency={formatCurrency}
                getStatusDisplayName={getStatusDisplayName}
            />
            )}
        </div>
    );
}

// Loading component for Suspense fallback
function BookingsPageSkeleton() {
    return (
        <div className="container mx-auto py-8 px-4">
            <div className="animate-pulse">
                <div className="h-8 bg-gray-200 rounded mb-4"></div>
                <div className="h-64 bg-gray-200 rounded"></div>
            </div>
        </div>
    );
}

// Main component với Suspense wrapper
export default function BookingsPage() {
    return (
        <Suspense fallback={<BookingsPageSkeleton />}>
            <BookingsContent />
        </Suspense>
    );
}