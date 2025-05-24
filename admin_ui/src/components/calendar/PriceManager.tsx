"use client";

import { useState } from 'react';
import { AccommodationSelector } from './AccommodationSelector';
import { PriceCalendar } from './PriceCalendar';
import { PriceUpdateForm } from './PriceUpdateForm';
import { PriceStatistics } from './PriceStatistics';
import { formatDateForApi } from '@/lib/priceUtils';

export const PriceManager = () => {
    const [selectedUnitId, setSelectedUnitId] = useState<number | null>(null);
    const [selectedDates, setSelectedDates] = useState<Date[]>([]);
    const [currentMonth, setCurrentMonth] = useState<Date>(new Date());

    // Calculate first and last day of month for statistics
    const firstDayOfMonth = new Date(currentMonth.getFullYear(), currentMonth.getMonth(), 1);
    const lastDayOfMonth = new Date(currentMonth.getFullYear(), currentMonth.getMonth() + 1, 0);

    const handleUnitSelected = (unitId: number) => {
        setSelectedUnitId(unitId);
        setSelectedDates([]); // Reset selected dates when unit changes
    };

    const handleDatesSelected = (dates: Date[]) => {
        setSelectedDates(dates);
    };

    const handleUpdateSuccess = () => {
        setSelectedDates([]); // Clear selected dates after successful update
    };

    return (
        <div className="container mx-auto py-6 space-y-8">
            <div>
                <h1 className="text-3xl font-bold mb-3">Quản Lý Giá Phòng</h1>
                <p className="text-gray-600 mb-4">
                    Chọn khách sạn/chỗ nghỉ và phòng, sau đó nhấn vào lịch để chọn ngày và cập nhật giá cơ bản.
                </p>
            </div>

            <div className="bg-white p-6 rounded-lg border shadow-sm">
                <h2 className="text-xl font-semibold mb-4">Chọn Khách Sạn & Phòng</h2>
                <AccommodationSelector onUnitSelected={handleUnitSelected} />
            </div>

            {selectedUnitId && (
                <>
                    <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
                        <div className="lg:col-span-2">
                            <PriceCalendar
                                unitId={selectedUnitId}
                                onDateSelect={handleDatesSelected}
                            />
                        </div>
                        <div className="space-y-6">
                            <PriceUpdateForm
                                unitId={selectedUnitId}
                                selectedDates={selectedDates}
                                onSuccess={handleUpdateSuccess}
                            />

                            <PriceStatistics
                                unitId={selectedUnitId}
                                startDate={formatDateForApi(firstDayOfMonth)}
                                endDate={formatDateForApi(lastDayOfMonth)}
                            />
                        </div>
                    </div>

                    <div className="bg-blue-50 p-6 rounded-lg border border-blue-100 shadow-sm">
                        <h3 className="text-lg font-semibold mb-2 text-blue-800">Hướng Dẫn Sử Dụng</h3>
                        <ul className="list-disc pl-5 space-y-1 text-blue-700">
                            <li>Nhấn vào các ngày trong lịch để chọn những ngày cần cập nhật giá</li>
                            <li>Chọn nhiều ngày bằng cách nhấn liên tiếp vào các ngày khác nhau</li>
                            <li>Điền giá cơ bản và nhấn nút cập nhật để áp dụng cho tất cả các ngày đã chọn</li>
                            <li>Để xóa lựa chọn một ngày, nhấn lại vào ngày đó</li>
                        </ul>
                    </div>
                </>
            )}
        </div>
    );
};
