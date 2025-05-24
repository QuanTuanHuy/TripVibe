"use client";

import { useState, useEffect } from 'react';
import { format, addMonths, subMonths, startOfMonth, endOfMonth, eachDayOfInterval, isSameDay, isSameMonth } from 'date-fns';
import { vi } from 'date-fns/locale';
import { ChevronLeft, ChevronRight } from 'lucide-react';
import { UnitPriceCalendar } from '@/types/accommodation/price';
import { priceService } from '@/services';
import {
    Card,
    CardContent,
    CardHeader,
    CardTitle,
} from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { cn } from '@/lib/utils';

interface PriceCalendarProps {
    unitId: number | null;
    onDateSelect?: (dates: Date[]) => void;
}

export const PriceCalendar = ({ unitId, onDateSelect }: PriceCalendarProps) => {
    const [currentMonth, setCurrentMonth] = useState<Date>(new Date());
    const [selectedDates, setSelectedDates] = useState<Date[]>([]);
    const [priceData, setPriceData] = useState<UnitPriceCalendar[]>([]);
    const [loading, setLoading] = useState<boolean>(false);

    // Fetch price data when unit changes or month changes
    useEffect(() => {
        const fetchPriceData = async () => {
            if (!unitId) return;

            setLoading(true);

            try {
                const startDate = format(startOfMonth(currentMonth), 'yyyy-MM-dd');
                const endDate = format(endOfMonth(currentMonth), 'yyyy-MM-dd');

                const data = await priceService.getUnitPriceCalendar(
                    unitId,
                    startDate,
                    endDate
                );

                setPriceData(data);
            } catch (error) {
                console.error('Error fetching price data:', error);
            } finally {
                setLoading(false);
            }
        };

        fetchPriceData();
    }, [unitId, currentMonth]);

    const nextMonth = () => setCurrentMonth(addMonths(currentMonth, 1));
    const prevMonth = () => setCurrentMonth(subMonths(currentMonth, 1));
    const handleDateClick = (date: Date) => {
        const isCurrentMonth = isSameMonth(date, currentMonth);
        if (!isCurrentMonth) return;

        setSelectedDates(prev => {
            const isSelected = prev.some(d => isSameDay(d, date));

            const newSelection = isSelected
                ? prev.filter(d => !isSameDay(d, date)) // Deselect if already selected
                : [...prev, date]; // Add to selection

            // Call the onDateSelect callback if provided
            if (onDateSelect) {
                onDateSelect(newSelection);
            }

            return newSelection;
        });
    };

    // Generate calendar days
    const monthStart = startOfMonth(currentMonth);
    const monthEnd = endOfMonth(currentMonth);
    const monthDays = eachDayOfInterval({ start: monthStart, end: monthEnd });

    // Find price for a specific date
    const getPriceForDate = (date: Date) => {
        const dateString = format(date, 'yyyy-MM-dd');
        const priceEntry = priceData.find(p => p.date === dateString);
        return priceEntry?.basePrice || 0;
    };

    // Determine if date is selected
    const isDateSelected = (date: Date) => {
        return selectedDates.some(d => isSameDay(d, date));
    };

    if (!unitId) {
        return (
            <div className="flex justify-center items-center h-64">
                <p className="text-gray-500">Vui lòng chọn khách sạn và phòng để xem bảng giá</p>
            </div>
        );
    }

    return (
        <Card className="w-full">
            <CardHeader className="flex flex-row items-center justify-between pb-2">
                <CardTitle className="text-xl">
                    Bảng Giá Tháng {format(currentMonth, 'MMMM yyyy', { locale: vi })}
                </CardTitle>
                <div className="flex items-center space-x-2">
                    <Button variant="outline" size="icon" onClick={prevMonth}>
                        <ChevronLeft className="h-4 w-4" />
                    </Button>
                    <Button variant="outline" size="icon" onClick={nextMonth}>
                        <ChevronRight className="h-4 w-4" />
                    </Button>
                </div>
            </CardHeader>
            <CardContent>
                {loading ? (
                    <div className="flex justify-center items-center h-64">
                        <p>Đang tải dữ liệu...</p>
                    </div>
                ) : (
                    <div className="grid grid-cols-7 gap-1">
                        {/* Day labels */}
                        {['CN', 'T2', 'T3', 'T4', 'T5', 'T6', 'T7'].map((day, i) => (
                            <div
                                key={day}
                                className="h-10 flex items-center justify-center font-medium text-sm"
                            >
                                {day}
                            </div>
                        ))}

                        {/* Empty cells before start of month */}
                        {Array.from({ length: monthStart.getDay() }).map((_, i) => (
                            <div key={`empty-start-${i}`} className="h-24 bg-gray-50"></div>
                        ))}

                        {/* Days of the month */}
                        {monthDays.map((day) => {
                            const price = getPriceForDate(day);
                            const isSelected = isDateSelected(day);
                            const isWeekend = [0, 6].includes(day.getDay()); // 0 is Sunday, 6 is Saturday

                            return (
                                <div
                                    key={day.toString()}
                                    onClick={() => handleDateClick(day)}
                                    className={cn(
                                        "h-24 p-1 border rounded-md relative cursor-pointer transition-colors",
                                        isSelected ? "border-blue-500 bg-blue-50" : "hover:bg-gray-50",
                                        isWeekend ? "bg-gray-50" : ""
                                    )}
                                >
                                    <div className="text-right text-sm p-1">{format(day, 'd')}</div>
                                    <div className={cn(
                                        "text-center font-semibold mt-4",
                                        price > 0 ? "text-green-700" : "text-gray-400"
                                    )}>
                                        {price > 0 ? new Intl.NumberFormat('vi-VN', {
                                            style: 'currency',
                                            currency: 'VND'
                                        }).format(price) : "---"}
                                    </div>
                                </div>
                            );
                        })}

                        {/* Empty cells after end of month */}
                        {Array.from({ length: 6 - monthEnd.getDay() }).map((_, i) => (
                            <div key={`empty-end-${i}`} className="h-24 bg-gray-50"></div>
                        ))}
                    </div>
                )}
            </CardContent>
        </Card>
    );
};
