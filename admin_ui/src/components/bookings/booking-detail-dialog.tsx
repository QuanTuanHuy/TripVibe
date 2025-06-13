"use client";

import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Separator } from "@/components/ui/separator";
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
    MapPin,
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
import { format, parseISO, differenceInDays } from "date-fns";

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

interface BookingDetailDialogProps {
    open: boolean;
    onOpenChange: (open: boolean) => void;
    booking: BookingDetails;
    onStatusChange: (bookingId: number, newStatus: BookingStatus) => void;
    formatCurrency: (amount: number, currency: string) => string;
    getStatusDisplayName: (status: BookingStatus) => string;
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

    const getStatusBadgeVariant = (status: BookingStatus): "default" | "destructive" | "outline" | "secondary" => {
        switch (status) {
            case "pending":
                return "outline";
            case "confirmed":
                return "default";
            case "checked_in":
                return "default";
            case "checked_out":
                return "secondary";
            case "cancelled":
                return "destructive";
            case "no_show":
                return "outline";
            default:
                return "outline";
        }
    };

    const getAvailableStatuses = (currentStatus: BookingStatus): BookingStatus[] => {
        switch (currentStatus) {
            case "pending":
                return ["confirmed", "cancelled"];
            case "confirmed":
                return ["checked_in", "cancelled", "no_show"];
            case "checked_in":
                return ["checked_out"];
            case "checked_out":
                return [];
            case "cancelled":
                return ["pending"];
            case "no_show":
                return ["pending", "confirmed"];
            default:
                return [];
        }
    };

    const getStatusIcon = (status: BookingStatus) => {
        switch (status) {
            case "pending":
                return <Clock className="w-4 h-4 text-amber-500" />;
            case "confirmed":
                return <CheckCircle2 className="w-4 h-4 text-green-500" />;
            case "checked_in":
                return <LogIn className="w-4 h-4 text-blue-500" />;
            case "checked_out":
                return <LogOut className="w-4 h-4 text-gray-500" />;
            case "cancelled":
                return <XCircle className="w-4 h-4 text-red-500" />;
            case "no_show":
                return <AlertCircle className="w-4 h-4 text-orange-500" />;
            default:
                return null;
        }
    };

    // Calculate the number of nights
    const calculateNights = () => {
        try {
            const checkInDate = parseISO(booking.checkIn);
            const checkOutDate = parseISO(booking.checkOut);
            return differenceInDays(checkOutDate, checkInDate);
        } catch (error) {
            return 0;
        }
    };

    const nights = calculateNights();

    return (
        <Dialog open={open} onOpenChange={onOpenChange}>
            <DialogContent className="max-w-3xl max-h-[90vh] overflow-y-auto">
                <DialogHeader>
                    <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-2">
                        <div>
                            <DialogTitle className="text-2xl">Chi tiết đặt phòng</DialogTitle>
                            <DialogDescription className="text-muted-foreground">
                                Mã đặt phòng: {booking.bookingId}
                            </DialogDescription>
                        </div>

                        <div className="flex items-center gap-2">
                            <Badge
                                variant={getStatusBadgeVariant(booking.status)}
                                className="flex items-center gap-1"
                            >
                                {getStatusIcon(booking.status)}
                                {getStatusDisplayName(booking.status)}
                            </Badge>

                            {getAvailableStatuses(booking.status).length > 0 && (
                                <DropdownMenu>
                                    <DropdownMenuTrigger asChild>
                                        <Button variant="outline" size="sm" className="gap-1">
                                            Đổi trạng thái
                                            <ChevronDown className="h-4 w-4" />
                                        </Button>
                                    </DropdownMenuTrigger>
                                    <DropdownMenuContent align="end">
                                        {getAvailableStatuses(booking.status).map((status) => (
                                            <DropdownMenuItem
                                                key={status}
                                                onClick={() => onStatusChange(booking.id, status)}
                                                className="gap-2"
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

                {/* Main content */}
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mt-4">
                    {/* Left column */}
                    <div className="space-y-6">
                        <div>
                            <h3 className="text-lg font-semibold mb-3">Chi tiết đặt phòng</h3>
                            <div className="space-y-4">
                                <div className="flex items-center gap-3">
                                    <div className="p-2 bg-primary/10 rounded-full">
                                        <Calendar className="h-5 w-5 text-primary" />
                                    </div>
                                    <div>
                                        <p className="text-sm text-muted-foreground">Ngày đặt phòng</p>
                                        <p className="font-medium">{formatDate(booking.createdAt)}</p>
                                    </div>
                                </div>

                                <div className="flex items-center gap-3">
                                    <div className="p-2 bg-green-50 rounded-full">
                                        <LogIn className="h-5 w-5 text-green-600" />
                                    </div>
                                    <div>
                                        <p className="text-sm text-muted-foreground">Check-in</p>
                                        <p className="font-medium">{formatDate(booking.checkIn)}</p>
                                    </div>
                                </div>

                                <div className="flex items-center gap-3">
                                    <div className="p-2 bg-blue-50 rounded-full">
                                        <LogOut className="h-5 w-5 text-blue-600" />
                                    </div>
                                    <div>
                                        <p className="text-sm text-muted-foreground">Check-out</p>
                                        <p className="font-medium">{formatDate(booking.checkOut)}</p>
                                    </div>
                                </div>

                                <div className="flex items-center gap-3">
                                    <div className="p-2 bg-purple-50 rounded-full">
                                        <Clock className="h-5 w-5 text-purple-600" />
                                    </div>
                                    <div>
                                        <p className="text-sm text-muted-foreground">Thời gian lưu trú</p>
                                        <p className="font-medium">{nights} đêm</p>
                                    </div>
                                </div>

                                <div className="flex items-center gap-3">
                                    <div className="p-2 bg-amber-50 rounded-full">
                                        <Users className="h-5 w-5 text-amber-600" />
                                    </div>
                                    <div>
                                        <p className="text-sm text-muted-foreground">Số khách</p>
                                        <p className="font-medium">{booking.guests} người</p>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <Separator />

                        <div>
                            <h3 className="text-lg font-semibold mb-3">Chi tiết phòng</h3>
                            <div className="p-4 border rounded-md">
                                <div className="flex items-center gap-3 mb-3">
                                    <div className="p-2 bg-indigo-50 rounded-full">
                                        <Building className="h-5 w-5 text-indigo-600" />
                                    </div>
                                    <div>
                                        <p className="font-medium">{booking.room.name}</p>
                                        <p className="text-sm text-muted-foreground">
                                            {booking.room.roomNumber && `Phòng ${booking.room.roomNumber}`}
                                        </p>
                                    </div>
                                </div>
                                <p className="text-sm text-muted-foreground ml-10">
                                    Loại phòng: {booking.room.unitType}
                                </p>
                            </div>
                        </div>

                        {booking.specialRequests && (
                            <>
                                <Separator />

                                <div>
                                    <h3 className="text-lg font-semibold mb-3">Yêu cầu đặc biệt</h3>
                                    <div className="p-4 border rounded-md">
                                        <div className="flex items-start gap-3">
                                            <MessageSquare className="h-5 w-5 text-primary mt-0.5" />
                                            <p className="text-sm">{booking.specialRequests}</p>
                                        </div>
                                    </div>
                                </div>
                            </>
                        )}
                    </div>

                    {/* Right column */}
                    <div className="space-y-6">
                        <div>
                            <h3 className="text-lg font-semibold mb-3">Thông tin khách hàng</h3>
                            <div className="p-4 border rounded-md space-y-3">
                                <div className="flex items-center gap-3">
                                    <div className="p-2 bg-gray-100 rounded-full">
                                        <User className="h-5 w-5 text-gray-600" />
                                    </div>
                                    <div>
                                        <p className="text-sm text-muted-foreground">Họ tên</p>
                                        <p className="font-medium">{booking.guest.name}</p>
                                    </div>
                                </div>

                                <div className="flex items-center gap-3">
                                    <div className="p-2 bg-gray-100 rounded-full">
                                        <Mail className="h-5 w-5 text-gray-600" />
                                    </div>
                                    <div>
                                        <p className="text-sm text-muted-foreground">Email</p>
                                        <p className="font-medium">{booking.guest.email}</p>
                                    </div>
                                </div>

                                <div className="flex items-center gap-3">
                                    <div className="p-2 bg-gray-100 rounded-full">
                                        <Phone className="h-5 w-5 text-gray-600" />
                                    </div>
                                    <div>
                                        <p className="text-sm text-muted-foreground">Số điện thoại</p>
                                        <p className="font-medium">{booking.guest.phone}</p>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <Separator />

                        <div>
                            <h3 className="text-lg font-semibold mb-3">Chi tiết thanh toán</h3>
                            <div className="p-4 border rounded-md">
                                <div className="flex justify-between items-center mb-2">
                                    <p className="text-sm text-muted-foreground">Giá phòng x {nights} đêm</p>
                                    <p className="font-medium">{formatCurrency(booking.totalAmount, booking.currency)}</p>
                                </div>

                                <Separator className="my-3" />

                                <div className="flex justify-between items-center">
                                    <p className="font-medium">Tổng cộng</p>
                                    <p className="font-semibold">{formatCurrency(booking.totalAmount, booking.currency)}</p>
                                </div>

                                <div className="flex items-center gap-2 mt-3">
                                    <div className="p-1.5 rounded-full">
                                        <CreditCard className="h-4 w-4" />
                                    </div>
                                    <div className="flex-1">
                                        <p className="text-sm font-medium">
                                            {booking.isPaid ? (
                                                <span className="text-green-600">Đã thanh toán</span>
                                            ) : (
                                                <span className="text-amber-600">Chưa thanh toán</span>
                                            )}
                                        </p>
                                        {booking.paymentMethod && (
                                            <p className="text-xs text-muted-foreground">
                                                Phương thức: {booking.paymentMethod}
                                            </p>
                                        )}
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <DialogFooter className="mt-6">
                    <Button variant="outline" onClick={() => onOpenChange(false)}>
                        Đóng
                    </Button>
                </DialogFooter>
            </DialogContent>
        </Dialog>
    );
}