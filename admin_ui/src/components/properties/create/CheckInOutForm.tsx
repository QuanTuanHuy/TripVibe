"use client";

import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from "@/components/ui/select";
import { Label } from "@/components/ui/label";
import { CreateAccommodationDto } from "@/types/accommodation";
import { Alert, AlertDescription } from "@/components/ui/alert";
import { AlertCircle } from "lucide-react";

interface CheckInOutFormProps {
    formData: CreateAccommodationDto;
    updateFormData: (data: Partial<CreateAccommodationDto>) => void;
}

export default function CheckInOutForm({
    formData,
    updateFormData,
}: CheckInOutFormProps) {
    // Generate hours for select options (0-24)
    const hours = Array.from({ length: 25 }, (_, i) => i);

    const formatHour = (hour: number) => {
        if (hour === 0 || hour === 24) return "00:00";
        return `${String(hour).padStart(2, "0")}:00`;
    };

    const handleCheckInFromChange = (value: string) => {
        const hour = parseInt(value, 10);
        updateFormData({ checkInTimeFrom: hour });

        // If checkInTimeTo is earlier than checkInTimeFrom, update it
        if (hour >= formData.checkInTimeTo) {
            updateFormData({ checkInTimeTo: Math.min(hour + 8, 24) });
        }
    };

    const handleCheckInToChange = (value: string) => {
        updateFormData({ checkInTimeTo: parseInt(value, 10) });
    };

    const handleCheckOutFromChange = (value: string) => {
        const hour = parseInt(value, 10);
        updateFormData({ checkOutTimeFrom: hour });

        // If checkOutTimeTo is earlier than checkOutTimeFrom, update it
        if (hour >= formData.checkOutTimeTo) {
            updateFormData({ checkOutTimeTo: Math.min(hour + 4, 24) });
        }
    };

    const handleCheckOutToChange = (value: string) => {
        updateFormData({ checkOutTimeTo: parseInt(value, 10) });
    };

    const isCheckInValid =
        formData.checkInTimeFrom < formData.checkInTimeTo &&
        formData.checkInTimeFrom >= 0 &&
        formData.checkInTimeTo <= 24;

    const isCheckOutValid =
        formData.checkOutTimeFrom < formData.checkOutTimeTo &&
        formData.checkOutTimeFrom >= 0 &&
        formData.checkOutTimeTo <= 24;

    return (
        <div className="space-y-6">
            <h2 className="text-xl font-semibold">Thời gian check-in và check-out</h2>
            <p className="text-gray-600 mb-4">
                Cài đặt giờ nhận phòng và trả phòng cho chỗ nghỉ của bạn. Điều này sẽ
                giúp khách hàng biết khi nào họ có thể đến và khi nào họ cần rời đi.
            </p>

            <div className="space-y-6">
                <div className="space-y-4">
                    <h3 className="font-medium">Thời gian check-in</h3>
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                        <div className="space-y-2">
                            <Label htmlFor="checkInTimeFrom">Từ</Label>
                            <Select
                                value={formData.checkInTimeFrom.toString()}
                                onValueChange={handleCheckInFromChange}
                            >
                                <SelectTrigger>
                                    <SelectValue placeholder="Giờ check-in từ" />
                                </SelectTrigger>
                                <SelectContent>
                                    {hours.slice(0, 24).map((hour) => (
                                        <SelectItem key={`checkin-from-${hour}`} value={hour.toString()}>
                                            {formatHour(hour)}
                                        </SelectItem>
                                    ))}
                                </SelectContent>
                            </Select>
                        </div>

                        <div className="space-y-2">
                            <Label htmlFor="checkInTimeTo">Đến</Label>
                            <Select
                                value={formData.checkInTimeTo.toString()}
                                onValueChange={handleCheckInToChange}
                            >
                                <SelectTrigger>
                                    <SelectValue placeholder="Giờ check-in đến" />
                                </SelectTrigger>
                                <SelectContent>
                                    {hours.slice(formData.checkInTimeFrom + 1).map((hour) => (
                                        <SelectItem key={`checkin-to-${hour}`} value={hour.toString()}>
                                            {formatHour(hour)}
                                        </SelectItem>
                                    ))}
                                </SelectContent>
                            </Select>
                        </div>
                    </div>
                    <p className="text-sm text-gray-500">
                        Khách hàng có thể check-in (nhận phòng) từ {formatHour(formData.checkInTimeFrom)} đến{" "}
                        {formatHour(formData.checkInTimeTo)}.
                    </p>

                    {!isCheckInValid && (
                        <Alert variant="destructive">
                            <AlertCircle className="h-4 w-4" />
                            <AlertDescription>Thời gian check-in không hợp lệ.</AlertDescription>
                        </Alert>
                    )}
                </div>

                <div className="space-y-4">
                    <h3 className="font-medium">Thời gian check-out</h3>
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                        <div className="space-y-2">
                            <Label htmlFor="checkOutTimeFrom">Từ</Label>
                            <Select
                                value={formData.checkOutTimeFrom.toString()}
                                onValueChange={handleCheckOutFromChange}
                            >
                                <SelectTrigger>
                                    <SelectValue placeholder="Giờ check-out từ" />
                                </SelectTrigger>
                                <SelectContent>
                                    {hours.slice(0, 24).map((hour) => (
                                        <SelectItem key={`checkout-from-${hour}`} value={hour.toString()}>
                                            {formatHour(hour)}
                                        </SelectItem>
                                    ))}
                                </SelectContent>
                            </Select>
                        </div>

                        <div className="space-y-2">
                            <Label htmlFor="checkOutTimeTo">Đến</Label>
                            <Select
                                value={formData.checkOutTimeTo.toString()}
                                onValueChange={handleCheckOutToChange}
                            >
                                <SelectTrigger>
                                    <SelectValue placeholder="Giờ check-out đến" />
                                </SelectTrigger>
                                <SelectContent>
                                    {hours.slice(formData.checkOutTimeFrom + 1).map((hour) => (
                                        <SelectItem key={`checkout-to-${hour}`} value={hour.toString()}>
                                            {formatHour(hour)}
                                        </SelectItem>
                                    ))}
                                </SelectContent>
                            </Select>
                        </div>
                    </div>
                    <p className="text-sm text-gray-500">
                        Khách hàng cần check-out (trả phòng) trong khoảng từ {formatHour(formData.checkOutTimeFrom)} đến{" "}
                        {formatHour(formData.checkOutTimeTo)}.
                    </p>

                    {!isCheckOutValid && (
                        <Alert variant="destructive">
                            <AlertCircle className="h-4 w-4" />
                            <AlertDescription>Thời gian check-out không hợp lệ.</AlertDescription>
                        </Alert>
                    )}
                </div>
            </div>

            <div className="p-4 bg-blue-50 rounded-md border border-blue-200 mt-6">
                <p className="text-sm text-blue-700">
                    <strong>Lời khuyên:</strong> Hầu hết các chỗ nghỉ cho phép check-in từ 14:00 đến 22:00 và check-out từ 07:00 đến 12:00.
                    Nếu bạn có thể linh hoạt về giờ giấc, điều này sẽ được khách hàng đánh giá cao.
                </p>
            </div>
        </div>
    );
}