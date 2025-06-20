"use client";

import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogHeader,
    DialogTitle,
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import {
    Calendar,
    ChevronDown,
    Clock,
    Phone,
    Mail,
    User,
    CreditCard,
    Building,
    MessageSquare,
    CheckCircle2,
    XCircle,
    LogIn,
    LogOut,
    AlertCircle,
    Users,
} from "lucide-react";
import { format, parseISO, differenceInDays, parse } from "date-fns";
import {
    UIBookingStatus,
    getStatusIconName,
    getStatusBadgeVariant,
    getAvailableStatusTransitions,
    getStatusBadgeStyle
} from "@/types/booking/status";

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
    status: UIBookingStatus; // Use UIBookingStatus from status module
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

interface BookingDetailDialogProps {
    open: boolean;
    onOpenChange: (open: boolean) => void;
    booking: BookingDetails;
    onStatusChange: (bookingId: number, newStatus: UIBookingStatus) => void;
    formatCurrency: (amount: number, currency: string) => string;
    getStatusDisplayName: (status: UIBookingStatus) => string;
}

export function BookingDetailDialog({
    open,
    onOpenChange,
    booking,
    onStatusChange,
    formatCurrency,
    getStatusDisplayName,
}: BookingDetailDialogProps) {
    const formatDate = (dateString: string) => {
        try {
            return format(parseISO(dateString), "dd/MM/yyyy");
        } catch (error) {
            return dateString;
        }
    };

    const getStatusIcon = (status: UIBookingStatus) => {
        const iconName = getStatusIconName(status);
        switch (iconName) {
            case 'Clock':
                return <Clock className="w-4 h-4 text-amber-500" />;
            case 'CheckCircle2':
                return <CheckCircle2 className="w-4 h-4 text-green-500" />;
            case 'UserCheck':
                return <CheckCircle2 className="w-4 h-4 text-blue-500" />;
            case 'LogIn':
                return <LogIn className="w-4 h-4 text-blue-500" />;
            case 'LogOut':
                return <LogOut className="w-4 h-4 text-gray-500" />;
            case 'XCircle':
                return <XCircle className="w-4 h-4 text-red-500" />;
            case 'AlertCircle':
                return <AlertCircle className="w-4 h-4 text-orange-500" />;
            default:
                return <Clock className="w-4 h-4 text-gray-500" />;
        }
    };

    const calculateNights = () => {
        try {
            const checkInDate = parse(booking.checkIn, 'dd/MM/yyyy', new Date());
            const checkOutDate = parse(booking.checkOut, 'dd/MM/yyyy', new Date());
            return differenceInDays(checkOutDate, checkInDate);
        } catch (error) {
            console.error("Date parsing error:", error);
            return 0;
        }
    };

    const nights = calculateNights();
    return (
        <Dialog open={open} onOpenChange={onOpenChange}>
            <DialogContent
                className="max-h-[80vh] overflow-y-auto p-0 gap-0"
                style={{
                    maxWidth: '80vw',
                    minWidth: '800px'
                }}
            >
                {/* Header - simple white background */}
                <div className="bg-white border-b p-6">
                    <DialogHeader className="space-y-0">
                        <div className="flex flex-col lg:flex-row lg:items-center lg:justify-between gap-4">
                            <div className="space-y-2">
                                <DialogTitle className="text-xl font-semibold">Chi tiết đặt phòng</DialogTitle>
                                <DialogDescription className="text-muted-foreground">
                                    Mã đặt phòng: <span className="font-medium text-foreground">{booking.bookingId}</span>
                                </DialogDescription>
                            </div>
                            <div className="flex items-center gap-3">
                                <Badge
                                    variant={getStatusBadgeVariant(booking.status)}
                                    className="flex items-center gap-2 px-3 py-1 text-sm font-medium"
                                    style={getStatusBadgeStyle(booking.status)}
                                >
                                    {getStatusIcon(booking.status)}
                                    {getStatusDisplayName(booking.status)}
                                </Badge>

                                {getAvailableStatusTransitions(booking.status).length > 0 && (
                                    <DropdownMenu>
                                        <DropdownMenuTrigger asChild>
                                            <Button variant="outline" size="sm" className="gap-2">
                                                Đổi trạng thái
                                                <ChevronDown className="h-4 w-4" />
                                            </Button>
                                        </DropdownMenuTrigger>
                                        <DropdownMenuContent align="end" className="w-48">
                                            {getAvailableStatusTransitions(booking.status).map((status: UIBookingStatus) => (
                                                <DropdownMenuItem
                                                    key={status}
                                                    onClick={() => onStatusChange(booking.id, status)}
                                                    className="gap-2 py-2 text-sm"
                                                >
                                                    {getStatusIcon(status)}
                                                    {getStatusDisplayName(status)}
                                                </DropdownMenuItem>
                                            ))}
                                        </DropdownMenuContent>
                                    </DropdownMenu>
                                )}
                            </div>
                        </div>
                    </DialogHeader>
                </div>
                {/* Main content with reduced padding */}
                <div className="p-6">
                    <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
                        {/* Left column - Booking Details */}
                        <div className="lg:col-span-2 space-y-6">
                            {/* Booking Information Card */}
                            <div className="bg-white border border-gray-200 rounded-lg shadow-sm overflow-hidden">
                                <div className="bg-gray-50 px-4 py-3 border-b border-gray-200">
                                    <h3 className="text-lg font-medium text-gray-900 flex items-center gap-2">
                                        <Calendar className="h-4 w-4 text-indigo-600" />
                                        Thông tin đặt phòng
                                    </h3>
                                </div>
                                <div className="p-4 space-y-4">
                                    <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                                        <div className="flex items-start gap-3">
                                            <div className="p-2 bg-indigo-50 rounded-md">
                                                <Calendar className="h-4 w-4 text-indigo-600" />
                                            </div>
                                            <div className="flex-1 min-w-0">
                                                <p className="text-xs font-medium text-gray-500 mb-1">Ngày đặt phòng</p>
                                                <p className="text-sm font-semibold text-gray-900">{formatDate(booking.createdAt)}</p>
                                            </div>
                                        </div>

                                        <div className="flex items-start gap-3">
                                            <div className="p-2 bg-green-50 rounded-md">
                                                <LogIn className="h-4 w-4 text-green-600" />
                                            </div>                                            <div className="flex-1 min-w-0">
                                                <p className="text-xs font-medium text-gray-500 mb-1">Check-in</p>
                                                <p className="text-sm font-semibold text-gray-900">{formatDate(booking.checkIn)}</p>
                                            </div>
                                        </div>

                                        <div className="flex items-start gap-3">
                                            <div className="p-2 bg-blue-50 rounded-md">
                                                <LogOut className="h-4 w-4 text-blue-600" />
                                            </div>
                                            <div className="flex-1 min-w-0">
                                                <p className="text-xs font-medium text-gray-500 mb-1">Check-out</p>
                                                <p className="text-sm font-semibold text-gray-900">{formatDate(booking.checkOut)}</p>
                                            </div>
                                        </div>

                                        <div className="flex items-start gap-3">
                                            <div className="p-2 bg-purple-50 rounded-md">
                                                <Clock className="h-4 w-4 text-purple-600" />
                                            </div>
                                            <div className="flex-1 min-w-0">
                                                <p className="text-xs font-medium text-gray-500 mb-1">Thời gian lưu trú</p>
                                                <p className="text-sm font-semibold text-gray-900">{nights} đêm</p>
                                            </div>
                                        </div>
                                    </div>

                                    <div className="flex items-start gap-3">
                                        <div className="p-2 bg-amber-50 rounded-md">
                                            <Users className="h-4 w-4 text-amber-600" />
                                        </div>                                        <div className="flex-1 min-w-0">
                                            <p className="text-xs font-medium text-gray-500 mb-1">Số khách</p>
                                            <p className="text-sm font-semibold text-gray-900">{booking.guests} người</p>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            {/* Room Details Card */}
                            <div className="bg-white border border-gray-200 rounded-lg shadow-sm overflow-hidden">
                                <div className="bg-gray-50 px-4 py-3 border-b border-gray-200">
                                    <h3 className="text-lg font-medium text-gray-900 flex items-center gap-2">
                                        <Building className="h-4 w-4 text-indigo-600" />
                                        Chi tiết phòng
                                    </h3>
                                </div>
                                <div className="p-4">
                                    <div className="flex items-start gap-3">
                                        <div className="p-2 bg-indigo-50 rounded-md">
                                            <Building className="h-4 w-4 text-indigo-600" />
                                        </div>
                                        <div className="flex-1 min-w-0">
                                            <h4 className="text-base font-medium text-gray-900 mb-1">{booking.room.name}</h4>
                                            <div className="space-y-1">
                                                {booking.room.roomNumber && (
                                                    <p className="text-xs text-gray-600">
                                                        Phòng số: <span className="font-medium">{booking.room.roomNumber}</span>
                                                    </p>
                                                )}
                                                <p className="text-xs text-gray-600">
                                                    Loại phòng: <span className="font-medium">{booking.room.unitType}</span>
                                                </p>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            {/* Special Requests Card */}
                            {booking.specialRequests && (
                                <div className="bg-white border border-gray-200 rounded-lg shadow-sm overflow-hidden">
                                    <div className="bg-gray-50 px-4 py-3 border-b border-gray-200">
                                        <h3 className="text-lg font-medium text-gray-900 flex items-center gap-2">
                                            <MessageSquare className="h-4 w-4 text-indigo-600" />
                                            Yêu cầu đặc biệt
                                        </h3>
                                    </div>
                                    <div className="p-4">
                                        <div className="flex items-start gap-3">
                                            <div className="p-2 bg-blue-50 rounded-md">
                                                <MessageSquare className="h-4 w-4 text-blue-600" />
                                            </div>
                                            <div className="flex-1 min-w-0">
                                                <p className="text-sm text-gray-700 leading-relaxed">{booking.specialRequests}</p>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            )}
                        </div>

                        {/* Right column - Guest & Payment Info */}
                        <div className="space-y-6">
                            {/* Guest Information Card */}
                            <div className="bg-white border border-gray-200 rounded-lg shadow-sm overflow-hidden">
                                <div className="bg-gray-50 px-4 py-3 border-b border-gray-200">
                                    <h3 className="text-lg font-medium text-gray-900 flex items-center gap-2">
                                        <User className="h-4 w-4 text-indigo-600" />
                                        Thông tin khách hàng
                                    </h3>
                                </div>
                                <div className="p-4 space-y-4">
                                    <div className="flex items-start gap-3">
                                        <div className="p-2 bg-gray-50 rounded-md">
                                            <User className="h-4 w-4 text-gray-600" />
                                        </div>
                                        <div className="flex-1 min-w-0">
                                            <p className="text-xs font-medium text-gray-500 mb-1">Họ tên</p>
                                            <p className="text-sm font-semibold text-gray-900">{booking.guest.name}</p>
                                        </div>
                                    </div>

                                    <div className="flex items-start gap-3">
                                        <div className="p-2 bg-blue-50 rounded-md">
                                            <Mail className="h-4 w-4 text-blue-600" />
                                        </div>
                                        <div className="flex-1 min-w-0">
                                            <p className="text-xs font-medium text-gray-500 mb-1">Email</p>
                                            <p className="text-sm font-medium text-gray-900 break-all">{booking.guest.email}</p>
                                        </div>
                                    </div>

                                    <div className="flex items-start gap-3">
                                        <div className="p-2 bg-green-50 rounded-md">
                                            <Phone className="h-4 w-4 text-green-600" />
                                        </div>
                                        <div className="flex-1 min-w-0">
                                            <p className="text-xs font-medium text-gray-500 mb-1">Số điện thoại</p>
                                            <p className="text-sm font-medium text-gray-900">{booking.guest.phone}</p>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            {/* Payment Information Card */}
                            <div className="bg-white border border-gray-200 rounded-lg shadow-sm overflow-hidden">
                                <div className="bg-gray-50 px-4 py-3 border-b border-gray-200">
                                    <h3 className="text-lg font-medium text-gray-900 flex items-center gap-2">
                                        <CreditCard className="h-4 w-4 text-indigo-600" />
                                        Chi tiết thanh toán
                                    </h3>
                                </div>
                                <div className="p-4">
                                    <div className="space-y-3">
                                        <div className="flex justify-between items-center py-2 border-b border-gray-100">
                                            <p className="text-sm text-gray-600">Giá phòng × {nights} đêm</p>
                                            <p className="text-sm font-semibold text-gray-900">{formatCurrency(booking.totalAmount, booking.currency)}</p>
                                        </div>

                                        <div className="flex justify-between items-center py-2 border-b border-gray-200">
                                            <p className="text-base font-semibold text-gray-900">Tổng cộng</p>
                                            <p className="text-base font-bold text-indigo-600">{formatCurrency(booking.totalAmount, booking.currency)}</p>
                                        </div>

                                        <div className="mt-4 p-3 bg-gray-50 rounded-md">
                                            <div className="flex items-start gap-3">
                                                <div className={`p-2 rounded-md ${booking.isPaid ? 'bg-green-50' : 'bg-amber-50'}`}>
                                                    <CreditCard className={`h-4 w-4 ${booking.isPaid ? 'text-green-600' : 'text-amber-600'}`} />
                                                </div>
                                                <div className="flex-1 min-w-0">
                                                    <p className="text-xs font-medium text-gray-500 mb-1">Trạng thái thanh toán</p>
                                                    <p className={`text-sm font-semibold ${booking.isPaid ? 'text-green-600' : 'text-amber-600'}`}>
                                                        {booking.isPaid ? 'Đã thanh toán' : 'Chưa thanh toán'}
                                                    </p>
                                                    {booking.paymentMethod && (
                                                        <p className="text-xs text-gray-600 mt-1">
                                                            Phương thức: <span className="font-medium">{booking.paymentMethod}</span>
                                                        </p>
                                                    )}
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </DialogContent>
        </Dialog>
    );
}