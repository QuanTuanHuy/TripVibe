"use client";

import { useState, useEffect, Suspense } from "react";
import { useSearchParams } from "next/navigation";
import {
    Tabs,
    TabsContent,
    TabsList,
    TabsTrigger
} from "@/components/ui/tabs";
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
import { format } from "date-fns";

// Types for booking data
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
    status: BookingStatus;
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

type BookingStatus =
    | "pending"
    | "confirmed"
    | "checked_in"
    | "checked_out"
    | "cancelled"
    | "no_show";

interface FilterOptions {
    dateRange: DateRange | null;
    status: BookingStatus | null;
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

// Mock data for demonstration
const MOCK_SUMMARY: BookingSummary = {
    total: 148,
    pending: 12,
    confirmed: 95,
    checkedIn: 34,
    today: {
        checkIn: 8,
        checkOut: 5,
    }
};

// Mock bookings data
const MOCK_BOOKINGS: BookingDetails[] = Array.from({ length: 50 }, (_, i) => {
    const id = i + 1;
    const checkIn = new Date();
    checkIn.setDate(checkIn.getDate() + Math.floor(Math.random() * 30) - 15);

    const checkOut = new Date(checkIn);
    checkOut.setDate(checkOut.getDate() + Math.floor(Math.random() * 7) + 1);

    const statuses: BookingStatus[] = ["pending", "confirmed", "checked_in", "checked_out", "cancelled", "no_show"];
    const status = statuses[Math.floor(Math.random() * (i > 45 ? 6 : 4))]; // More chances for active statuses

    const firstName = ["Nguyễn", "Trần", "Lê", "Phạm", "Hoàng", "Huỳnh", "Phan", "Vũ", "Đặng", "Bùi"][Math.floor(Math.random() * 10)];
    const lastName = ["An", "Bình", "Cường", "Dũng", "Em", "Phúc", "Giang", "Huy", "Khang", "Linh"][Math.floor(Math.random() * 10)];

    const roomTypes = ["Standard", "Deluxe", "Suite", "Family", "Executive"];
    const roomType = roomTypes[Math.floor(Math.random() * roomTypes.length)];
    const roomNumber = Math.floor(Math.random() * 500) + 100;

    return {
        id,
        bookingId: `BK-${100000 + id}`,
        status,
        checkIn: format(checkIn, "yyyy-MM-dd"),
        checkOut: format(checkOut, "yyyy-MM-dd"),
        guests: Math.floor(Math.random() * 4) + 1,
        totalAmount: Math.floor(Math.random() * 5000000) + 500000,
        currency: "VND",
        createdAt: format(new Date(Date.now() - Math.floor(Math.random() * 30 * 24 * 60 * 60 * 1000)), "yyyy-MM-dd"),
        specialRequests: Math.random() > 0.7 ? "Phòng ở tầng cao, xa thang máy" : undefined,
        guest: {
            id: id * 10,
            name: `${firstName} ${lastName}`,
            email: `guest${id}@example.com`.toLowerCase(),
            phone: `+84${Math.floor(Math.random() * 900000000) + 100000000}`,
        },
        room: {
            id: id * 5,
            name: `${roomType} Room`,
            roomNumber: `${roomNumber}`,
            unitType: roomType
        },
        isPaid: Math.random() > 0.3,
        paymentMethod: Math.random() > 0.3 ? (Math.random() > 0.5 ? "Credit Card" : "Bank Transfer") : undefined,
    };
});

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

// Component chứa logic chính với useSearchParams
function BookingsContent() {
    const searchParams = useSearchParams();
    const [activeTab, setActiveTab] = useState("all");
    const [currentPage, setCurrentPage] = useState(1);
    const [pageSize] = useState(10);
    const [totalPages, setTotalPages] = useState(5);
    const [isLoading, setIsLoading] = useState(true);
    const [bookings, setBookings] = useState<BookingDetails[]>([]);
    const [summary, setSummary] = useState<BookingSummary>(MOCK_SUMMARY);
    const [selectedBooking, setSelectedBooking] = useState<BookingDetails | null>(null);
    const [detailDialogOpen, setDetailDialogOpen] = useState(false);
    const [filterOptions, setFilterOptions] = useState<FilterOptions>({
        dateRange: null,
        status: null,
        search: "",
        sortBy: "checkIn",
        sortOrder: "desc"
    });

    // Set initial tab based on URL param if present
    useEffect(() => {
        const tab = searchParams.get('tab');
        if (tab && ['all', 'pending', 'confirmed', 'checked_in', 'checked_out', 'cancelled'].includes(tab)) {
            setActiveTab(tab);
        }
    }, [searchParams]);

    // Load bookings with filtering
    useEffect(() => {
        const loadBookings = async () => {
            setIsLoading(true);
            try {
                // In a real app, you would call your API here
                // const response = await bookingService.getBookings({
                //   page: currentPage - 1,
                //   size: pageSize,
                //   status: activeTab !== 'all' ? activeTab : undefined,
                //   ...other filters
                // });

                // Simulate API delay
                await new Promise(resolve => setTimeout(resolve, 800));

                // Filter mock data based on active tab and filter options
                let filteredBookings = [...MOCK_BOOKINGS];

                // Filter by tab/status
                if (activeTab !== 'all') {
                    filteredBookings = filteredBookings.filter(booking =>
                        booking.status === activeTab
                    );
                }

                // Apply additional filters
                if (filterOptions.status) {
                    filteredBookings = filteredBookings.filter(booking =>
                        booking.status === filterOptions.status
                    );
                }

                if (filterOptions.search) {
                    const search = filterOptions.search.toLowerCase();
                    filteredBookings = filteredBookings.filter(booking =>
                        booking.bookingId.toLowerCase().includes(search) ||
                        booking.guest.name.toLowerCase().includes(search) ||
                        booking.guest.email.toLowerCase().includes(search) ||
                        booking.room.name.toLowerCase().includes(search)
                    );
                }

                if (filterOptions.dateRange?.from) {
                    const fromDate = new Date(filterOptions.dateRange.from);
                    filteredBookings = filteredBookings.filter(booking =>
                        new Date(booking.checkIn) >= fromDate
                    );
                }

                if (filterOptions.dateRange?.to) {
                    const toDate = new Date(filterOptions.dateRange.to);
                    filteredBookings = filteredBookings.filter(booking =>
                        new Date(booking.checkIn) <= toDate
                    );
                }

                // Sort bookings
                filteredBookings.sort((a, b) => {
                    let valA: string | number | Date = a[filterOptions.sortBy as keyof BookingDetails] as string;
                    let valB: string | number | Date = b[filterOptions.sortBy as keyof BookingDetails] as string;

                    if (filterOptions.sortBy === 'checkIn' || filterOptions.sortBy === 'checkOut' || filterOptions.sortBy === 'createdAt') {
                        valA = new Date(valA);
                        valB = new Date(valB);
                    }

                    if (valA < valB) return filterOptions.sortOrder === 'asc' ? -1 : 1;
                    if (valA > valB) return filterOptions.sortOrder === 'asc' ? 1 : -1;
                    return 0;
                });

                // Calculate pagination
                const total = filteredBookings.length;
                setTotalPages(Math.ceil(total / pageSize));

                // Get current page of data
                const start = (currentPage - 1) * pageSize;
                const end = start + pageSize;
                const paginatedBookings = filteredBookings.slice(start, end);

                setBookings(paginatedBookings);

                // Update summary with filtered totals
                setSummary({
                    total: MOCK_BOOKINGS.length,
                    pending: MOCK_BOOKINGS.filter(b => b.status === 'pending').length,
                    confirmed: MOCK_BOOKINGS.filter(b => b.status === 'confirmed').length,
                    checkedIn: MOCK_BOOKINGS.filter(b => b.status === 'checked_in').length,
                    today: {
                        checkIn: MOCK_BOOKINGS.filter(b => b.checkIn === format(new Date(), 'yyyy-MM-dd')).length,
                        checkOut: MOCK_BOOKINGS.filter(b => b.checkOut === format(new Date(), 'yyyy-MM-dd')).length
                    }
                });

            } catch (error) {
                console.error('Error loading bookings:', error);
                toast.error('Không thể tải danh sách đặt phòng. Vui lòng thử lại sau.');
            } finally {
                setIsLoading(false);
            }
        };

        loadBookings();
    }, [activeTab, currentPage, pageSize, filterOptions]);

    // Handle booking status change
    const handleStatusChange = async (bookingId: number, newStatus: BookingStatus) => {
        try {
            // In a real app, call API to update status
            // await bookingService.updateStatus(bookingId, newStatus);

            // Update local state
            setBookings(prev => prev.map(booking =>
                booking.id === bookingId ? { ...booking, status: newStatus } : booking
            ));

            // Update selected booking if it's open
            if (selectedBooking?.id === bookingId) {
                setSelectedBooking(prev => prev ? { ...prev, status: newStatus } : null);
            }

            toast.success(`Đã cập nhật trạng thái đặt phòng thành ${getStatusDisplayName(newStatus)}`);
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

    // Update URL when tab changes
    const handleTabChange = (tab: string) => {
        setActiveTab(tab);
        setCurrentPage(1);

        // Update URL
        const url = new URL(window.location.href);
        url.searchParams.set('tab', tab);
        window.history.pushState({}, '', url.toString());
    };

    // Helper function to format currency
    const formatCurrency = (amount: number, currency: string) => {
        return new Intl.NumberFormat('vi-VN', {
            style: 'currency',
            currency: currency || 'VND'
        }).format(amount);
    };

    // Helper function to get display name for status
    const getStatusDisplayName = (status: BookingStatus) => {
        switch (status) {
            case 'pending': return 'Chờ xác nhận';
            case 'confirmed': return 'Đã xác nhận';
            case 'checked_in': return 'Đã nhận phòng';
            case 'checked_out': return 'Đã trả phòng';
            case 'cancelled': return 'Đã hủy';
            case 'no_show': return 'Không đến';
            default: return status;
        }
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

                {/* Summary cards */}
                <div className="grid grid-cols-1 md:grid-cols-3 lg:grid-cols-5 gap-4">
                    <BookingSummaryCards summary={summary} />
                </div>

                {/* Main content */}
                <div className="space-y-4">
                    <Tabs value={activeTab} onValueChange={handleTabChange}>
                        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
                            <TabsList>
                                <TabsTrigger value="all">Tất cả</TabsTrigger>
                                <TabsTrigger value="pending">Chờ xác nhận</TabsTrigger>
                                <TabsTrigger value="confirmed">Đã xác nhận</TabsTrigger>
                                <TabsTrigger value="checked_in">Đang lưu trú</TabsTrigger>
                                <TabsTrigger value="cancelled">Đã hủy</TabsTrigger>
                            </TabsList>

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
                        <Card className="mt-4">
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
                                            value={filterOptions.status || "all"}
                                            onValueChange={(value) => setFilterOptions({
                                                ...filterOptions,
                                                status: value === "all" ? null : value as BookingStatus
                                            })}
                                        >
                                            <SelectTrigger>
                                                <SelectValue placeholder="Trạng thái" />
                                            </SelectTrigger>
                                            <SelectContent>
                                                <SelectItem value="all">Tất cả trạng thái</SelectItem>
                                                <SelectItem value="pending">Chờ xác nhận</SelectItem>
                                                <SelectItem value="confirmed">Đã xác nhận</SelectItem>
                                                <SelectItem value="checked_in">Đang lưu trú</SelectItem>
                                                <SelectItem value="checked_out">Đã trả phòng</SelectItem>
                                                <SelectItem value="cancelled">Đã hủy</SelectItem>
                                                <SelectItem value="no_show">Không đến</SelectItem>
                                            </SelectContent>
                                        </Select>
                                    </div>

                                    <div className="md:col-span-2 lg:col-span-2">
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
                                                <SelectItem value="checkIn-desc">Check-in (Mới nhất)</SelectItem>
                                                <SelectItem value="checkIn-asc">Check-in (Cũ nhất)</SelectItem>
                                                <SelectItem value="createdAt-desc">Ngày đặt (Mới nhất)</SelectItem>
                                                <SelectItem value="createdAt-asc">Ngày đặt (Cũ nhất)</SelectItem>
                                                <SelectItem value="totalAmount-desc">Giá (Cao đến thấp)</SelectItem>
                                                <SelectItem value="totalAmount-asc">Giá (Thấp đến cao)</SelectItem>
                                            </SelectContent>
                                        </Select>
                                    </div>

                                    <div className="md:col-span-12 lg:col-span-2 flex justify-end">
                                        <Button
                                            variant="outline"
                                            onClick={() => setFilterOptions({
                                                dateRange: null,
                                                status: null,
                                                search: "",
                                                sortBy: "checkIn",
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

                        {/* Tab content - Bookings list */}
                        <TabsContent value={activeTab} className="mt-0">
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
                                                className="mt-4"
                                                onClick={() => setFilterOptions({
                                                    dateRange: null,
                                                    status: null,
                                                    search: "",
                                                    sortBy: "checkIn",
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
                                        </>
                                    )}
                                </CardContent>
                            </Card>
                        </TabsContent>
                    </Tabs>
                </div>
            </div>

            {/* Booking Detail Dialog */}
            {selectedBooking && (<BookingDetailDialog
                open={detailDialogOpen}
                onOpenChange={setDetailDialogOpen}
                booking={selectedBooking}
                onStatusChange={handleStatusChange}
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