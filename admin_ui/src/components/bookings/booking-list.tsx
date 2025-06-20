"use client";

import { Button } from "@/components/ui/button";
import {
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableHeader,
    TableRow
} from "@/components/ui/table";
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuLabel,
    DropdownMenuSeparator,
    DropdownMenuTrigger
} from "@/components/ui/dropdown-menu";
import { Badge } from "@/components/ui/badge";
import {
    Calendar,
    MoreHorizontal,
    User,
    Eye,
    CheckCircle2,
    XCircle,
    LogIn,
    LogOut,
    AlertCircle,
    Building,
    Clock
} from "lucide-react";
import { format, parseISO } from "date-fns";
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

interface BookingListProps {
    bookings: BookingDetails[];
    onViewBooking: (booking: BookingDetails) => void;
    onStatusChange: (bookingId: number, newStatus: UIBookingStatus) => void;
    formatCurrency: (amount: number, currency: string) => string;
    getStatusDisplayName: (status: UIBookingStatus) => string;
}

export function BookingList({
    bookings,
    onViewBooking,
    onStatusChange,
    formatCurrency,
    getStatusDisplayName
}: BookingListProps) {

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
                return <CheckCircle2 className="w-4 h-4 text-blue-500" />; // Use CheckCircle2 as fallback
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

    return (
        <div className="border rounded-md">
            <Table>
                <TableHeader>
                    <TableRow>
                        <TableHead>Mã đặt phòng</TableHead>
                        <TableHead className="hidden md:table-cell">Khách hàng</TableHead>
                        <TableHead className="hidden lg:table-cell">Phòng</TableHead>
                        <TableHead className="hidden sm:table-cell">Check-in / out</TableHead>
                        <TableHead>Trạng thái</TableHead>
                        <TableHead className="hidden lg:table-cell">Thanh toán</TableHead>
                        <TableHead className="text-right">Thao tác</TableHead>
                    </TableRow>
                </TableHeader>
                <TableBody>
                    {bookings.map((booking) => (
                        <TableRow key={booking.id}>
                            <TableCell className="font-medium">
                                <div>
                                    <div className="font-medium">{booking.bookingId}</div>
                                    <div className="text-xs text-muted-foreground flex items-center">
                                        <Calendar className="w-3 h-3 mr-1" />
                                        {formatDate(booking.createdAt)}
                                    </div>
                                </div>
                            </TableCell>
                            <TableCell className="hidden md:table-cell">
                                <div className="flex items-center">
                                    <div className="w-8 h-8 rounded-full bg-muted flex items-center justify-center mr-2">
                                        <User className="w-4 h-4" />
                                    </div>
                                    <div>
                                        <div className="font-medium">{booking.guest.name}</div>
                                        <div className="text-xs text-muted-foreground truncate max-w-[150px]">
                                            {booking.guest.email}
                                        </div>
                                    </div>
                                </div>
                            </TableCell>
                            <TableCell className="hidden lg:table-cell">
                                <div className="flex items-center">
                                    <div className="w-8 h-8 rounded-full bg-muted flex items-center justify-center mr-2">
                                        <Building className="w-4 h-4" />
                                    </div>
                                    <div>
                                        <div className="font-medium truncate max-w-[150px]">{booking.room.name}</div>
                                        <div className="text-xs text-muted-foreground">
                                            {booking.room.roomNumber && `Phòng ${booking.room.roomNumber}`}
                                        </div>
                                    </div>
                                </div>
                            </TableCell>
                            <TableCell className="hidden sm:table-cell">
                                <div>
                                    <div className="flex items-center gap-1">
                                        <LogIn className="w-3 h-3 text-green-600" />
                                        <span>{formatDate(booking.checkIn)}</span>
                                    </div>
                                    <div className="flex items-center gap-1 mt-1">
                                        <LogOut className="w-3 h-3 text-blue-600" />
                                        <span>{formatDate(booking.checkOut)}</span>
                                    </div>
                                </div>
                            </TableCell>
                            <TableCell>
                                <div className="flex items-center">
                                    <Badge
                                        variant={getStatusBadgeVariant(booking.status)}
                                        className="flex gap-1"
                                        style={getStatusBadgeStyle(booking.status)}
                                    >
                                        {getStatusIcon(booking.status)}
                                        <span className="ml-1">{getStatusDisplayName(booking.status)}</span>
                                    </Badge>
                                </div>
                            </TableCell>
                            <TableCell className="hidden lg:table-cell">
                                <div>
                                    <div className="font-medium">{formatCurrency(booking.totalAmount, booking.currency)}</div>
                                    <div className="text-xs text-muted-foreground">
                                        {booking.isPaid ? (
                                            <span className="text-green-500">Đã thanh toán</span>
                                        ) : (
                                            <span className="text-amber-500">Chưa thanh toán</span>
                                        )}
                                    </div>
                                </div>
                            </TableCell>
                            <TableCell className="text-right">
                                <DropdownMenu>
                                    <DropdownMenuTrigger asChild>
                                        <Button variant="ghost" className="h-8 w-8 p-0">
                                            <span className="sr-only">Mở menu</span>
                                            <MoreHorizontal className="h-4 w-4" />
                                        </Button>
                                    </DropdownMenuTrigger>
                                    <DropdownMenuContent align="end">
                                        <DropdownMenuLabel>Thao tác</DropdownMenuLabel>
                                        <DropdownMenuItem onClick={() => onViewBooking(booking)}>
                                            <Eye className="w-4 h-4 mr-2" />
                                            Xem chi tiết
                                        </DropdownMenuItem>

                                        {getAvailableStatusTransitions(booking.status).length > 0 && (
                                            <>
                                                <DropdownMenuSeparator />
                                                <DropdownMenuLabel>Đổi trạng thái</DropdownMenuLabel>
                                                {getAvailableStatusTransitions(booking.status).map((status: UIBookingStatus) => (
                                                    <DropdownMenuItem
                                                        key={status}
                                                        onClick={() => onStatusChange(booking.id, status)}
                                                    >
                                                        {getStatusIcon(status)}
                                                        <span className="ml-2">{getStatusDisplayName(status)}</span>
                                                    </DropdownMenuItem>
                                                ))}
                                            </>
                                        )}
                                    </DropdownMenuContent>
                                </DropdownMenu>
                            </TableCell>
                        </TableRow>
                    ))}
                </TableBody>
            </Table>
        </div>
    );
}