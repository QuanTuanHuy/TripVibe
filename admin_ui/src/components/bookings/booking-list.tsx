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

interface BookingListProps {
    bookings: BookingDetails[];
    onViewBooking: (booking: BookingDetails) => void;
    onStatusChange: (bookingId: number, newStatus: BookingStatus) => void;
    formatCurrency: (amount: number, currency: string) => string;
    getStatusDisplayName: (status: BookingStatus) => string;
}

export function BookingList({
    bookings,
    onViewBooking,
    onStatusChange,
    formatCurrency,
    getStatusDisplayName
}: BookingListProps) {

    const getStatusBadgeVariant = (status: BookingStatus): "default" | "destructive" | "outline" | "secondary" => {
        switch (status) {
            case 'pending':
                return "outline";
            case 'confirmed':
                return "default"; // Changed from "success"
            case 'checked_in':
                return "default";
            case 'checked_out':
                return "secondary";
            case 'cancelled':
                return "destructive";
            case 'no_show':
                return "outline"; // Changed from "warning"
            default:
                return "outline";
        }
    };

    const getAvailableStatuses = (currentStatus: BookingStatus): BookingStatus[] => {
        switch (currentStatus) {
            case 'pending':
                return ['confirmed', 'cancelled'];
            case 'confirmed':
                return ['checked_in', 'cancelled', 'no_show'];
            case 'checked_in':
                return ['checked_out'];
            case 'checked_out':
                return [];
            case 'cancelled':
                return ['pending'];
            case 'no_show':
                return ['pending', 'confirmed'];
            default:
                return [];
        }
    };

    const formatDate = (dateString: string) => {
        try {
            return format(parseISO(dateString), "dd/MM/yyyy");
        } catch (error) {
            return dateString;
        }
    };

    const getStatusIcon = (status: BookingStatus) => {
        switch (status) {
            case 'pending':
                return <Clock className="w-4 h-4 text-amber-500" />;
            case 'confirmed':
                return <CheckCircle2 className="w-4 h-4 text-green-500" />;
            case 'checked_in':
                return <LogIn className="w-4 h-4 text-blue-500" />;
            case 'checked_out':
                return <LogOut className="w-4 h-4 text-gray-500" />;
            case 'cancelled':
                return <XCircle className="w-4 h-4 text-red-500" />;
            case 'no_show':
                return <AlertCircle className="w-4 h-4 text-orange-500" />;
            default:
                return null;
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
                                    <Badge variant={getStatusBadgeVariant(booking.status)} className="flex gap-1">
                                        {getStatusIcon(booking.status)}
                                        {getStatusDisplayName(booking.status)}
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

                                        {getAvailableStatuses(booking.status).length > 0 && (
                                            <>
                                                <DropdownMenuSeparator />
                                                <DropdownMenuLabel>Đổi trạng thái</DropdownMenuLabel>
                                                {getAvailableStatuses(booking.status).map((status) => (
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