"use client";

import { useState, useEffect } from 'react';
import { format } from 'date-fns';
import { vi } from 'date-fns/locale';
import { UnitPriceCalendar } from '@/types/accommodation/price';
import { priceService } from '@/services';
import {
    Card,
    CardContent,
    CardDescription,
    CardHeader,
    CardTitle,
} from '@/components/ui/card';
import { formatCurrency } from '@/lib/priceUtils';
import { Unit } from '@/types/accommodation';

interface PriceStatisticsProps {
    unitId: number | null;
    startDate: string;
    endDate: string;
}

export const PriceStatistics = ({ unitId, startDate, endDate }: PriceStatisticsProps) => {
    const [unit, setUnit] = useState<Unit | null>(null);
    const [priceData, setPriceData] = useState<UnitPriceCalendar[]>([]);
    const [loading, setLoading] = useState<boolean>(false);
    const [stats, setStats] = useState({
        avgPrice: 0,
        minPrice: 0,
        maxPrice: 0,
        weekdayAvg: 0,
        weekendAvg: 0,
    });

    useEffect(() => {
        const fetchData = async () => {
            if (!unitId) return;

            setLoading(true);

            try {
                // Get price calendar data
                const data = await priceService.getUnitPriceCalendar(
                    unitId,
                    startDate,
                    endDate
                );

                setPriceData(data);

                // Calculate statistics
                if (data.length > 0) {
                    const prices = data.map(item => item.basePrice);
                    const weekdayPrices = data
                        .filter(item => {
                            const date = new Date(item.date);
                            return ![0, 6].includes(date.getDay()); // 0 is Sunday, 6 is Saturday
                        })
                        .map(item => item.basePrice);

                    const weekendPrices = data
                        .filter(item => {
                            const date = new Date(item.date);
                            return [0, 6].includes(date.getDay());
                        })
                        .map(item => item.basePrice);

                    setStats({
                        avgPrice: prices.reduce((sum, price) => sum + price, 0) / prices.length,
                        minPrice: Math.min(...prices),
                        maxPrice: Math.max(...prices),
                        weekdayAvg: weekdayPrices.length > 0
                            ? weekdayPrices.reduce((sum, price) => sum + price, 0) / weekdayPrices.length
                            : 0,
                        weekendAvg: weekendPrices.length > 0
                            ? weekendPrices.reduce((sum, price) => sum + price, 0) / weekendPrices.length
                            : 0,
                    });
                }
            } catch (error) {
                console.error('Error fetching data:', error);
            } finally {
                setLoading(false);
            }
        };

        fetchData();
    }, [unitId, startDate, endDate]);

    if (!unitId) {
        return null;
    }

    return (
        <Card>
            <CardHeader>
                <CardTitle>Thống Kê Giá</CardTitle>
                <CardDescription>
                    Từ {format(new Date(startDate), 'd MMMM yyyy', { locale: vi })} đến{' '}
                    {format(new Date(endDate), 'd MMMM yyyy', { locale: vi })}
                </CardDescription>
            </CardHeader>
            <CardContent>
                {loading ? (
                    <p>Đang tải...</p>
                ) : (
                    <div className="space-y-4">
                        <div className="grid grid-cols-2 gap-4">
                            <div className="bg-blue-50 p-3 rounded-md">
                                <div className="text-sm text-gray-600">Giá Trung Bình</div>
                                <div className="text-xl font-semibold">{formatCurrency(stats.avgPrice)}</div>
                            </div>
                            <div className="bg-green-50 p-3 rounded-md">
                                <div className="text-sm text-gray-600">Giá Cuối Tuần</div>
                                <div className="text-xl font-semibold">{formatCurrency(stats.weekendAvg)}</div>
                            </div>
                            <div className="bg-yellow-50 p-3 rounded-md">
                                <div className="text-sm text-gray-600">Giá Thấp Nhất</div>
                                <div className="text-xl font-semibold">{formatCurrency(stats.minPrice)}</div>
                            </div>
                            <div className="bg-red-50 p-3 rounded-md">
                                <div className="text-sm text-gray-600">Giá Cao Nhất</div>
                                <div className="text-xl font-semibold">{formatCurrency(stats.maxPrice)}</div>
                            </div>
                        </div>

                        <div className="mt-4 text-sm text-gray-600">
                            <p className="mb-1">Số ngày có dữ liệu giá: {priceData.length}</p>
                            <p>Chú ý: Thống kê chỉ bao gồm giá cơ bản, không tính các bội số và giảm giá.</p>
                        </div>
                    </div>
                )}
            </CardContent>
        </Card>
    );
};
