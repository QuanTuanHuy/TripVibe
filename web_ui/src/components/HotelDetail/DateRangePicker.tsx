"use client";

import { useRef, useEffect } from 'react';
import { Calendar } from 'lucide-react';
import { format } from 'date-fns';
import { vi } from 'date-fns/locale';
import { DayPicker } from 'react-day-picker';
import 'react-day-picker/dist/style.css';

interface DateRange {
    from: Date | undefined;
    to: Date | undefined;
}

interface DateRangePickerProps {
    dateRange: DateRange;
    onDateRangeChange: (range: DateRange) => void;
    showDatePicker: boolean;
    onShowDatePickerChange: (show: boolean) => void;
}

export default function DateRangePicker({
    dateRange,
    onDateRangeChange,
    showDatePicker,
    onShowDatePickerChange
}: DateRangePickerProps) {
    const datePickerRef = useRef<HTMLDivElement>(null);
    const today = new Date();

    // Handle outside click
    useEffect(() => {
        const handleClickOutside = (event: MouseEvent) => {
            if (datePickerRef.current && !datePickerRef.current.contains(event.target as Node)) {
                onShowDatePickerChange(false);
            }
        };

        document.addEventListener('mousedown', handleClickOutside);
        return () => {
            document.removeEventListener('mousedown', handleClickOutside);
        };
    }, [onShowDatePickerChange]);

    return (
        <div ref={datePickerRef} className="relative">
            <div
                className="flex items-center cursor-pointer"
                onClick={() => onShowDatePickerChange(!showDatePicker)}
            >
                <Calendar className="w-6 h-6 text-gray-500 mr-2" />
                <div className="text-gray-800">
                    {dateRange.from ? (
                        <>
                            {format(dateRange.from, 'EEE, dd/MM/yyyy', { locale: vi })}
                            {dateRange.to ? ` — ${format(dateRange.to, 'EEE, dd/MM/yyyy', { locale: vi })}` : ''}
                        </>
                    ) : (
                        "Chọn ngày"
                    )}
                </div>
            </div>

            {showDatePicker && (
                <div className="absolute left-0 top-full mt-1 bg-white shadow-lg rounded-lg border border-gray-200 p-4 z-[999]">
                    <DayPicker
                        mode="range"
                        locale={vi}
                        selected={dateRange}
                        onSelect={(range) => {
                            if (range) {
                                onDateRangeChange({
                                    from: range.from,
                                    to: range.to || undefined
                                });
                            } else {
                                onDateRangeChange({ from: undefined, to: undefined });
                            }
                        }}
                        numberOfMonths={2}
                        fromMonth={today}
                        modifiers={{
                            disabled: { before: today }
                        }}
                        styles={{
                            caption: { color: '#2563eb' },
                            day: { margin: '2px' }
                        }}
                        footer={
                            <div className="pt-4 flex justify-end">
                                <button
                                    onClick={() => onShowDatePickerChange(false)}
                                    className="px-4 py-2 bg-blue-600 text-white rounded font-medium hover:bg-blue-700"
                                >
                                    Xong
                                </button>
                            </div>
                        }
                    />
                </div>
            )}
        </div>
    );
}
