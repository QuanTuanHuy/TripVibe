"use client";

import { useState } from 'react';
import { format } from 'date-fns';
import { vi } from 'date-fns/locale';
import { toast } from 'sonner';
import { priceService } from '@/services';
import {
    Card,
    CardContent,
    CardDescription,
    CardFooter,
    CardHeader,
    CardTitle,
} from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Calendar } from '@/components/ui/calendar';
import {
    Popover,
    PopoverContent,
    PopoverTrigger,
} from '@/components/ui/popover';

interface PriceUpdateFormProps {
    unitId: number | null;
    selectedDates: Date[];
    onSuccess: () => void;
}

export const PriceUpdateForm = ({ unitId, selectedDates, onSuccess }: PriceUpdateFormProps) => {
    const [basePrice, setBasePrice] = useState<string>('');
    const [loading, setLoading] = useState<boolean>(false);
    const [activeTab, setActiveTab] = useState<string>("selected");
    const [customStartDate, setCustomStartDate] = useState<Date | undefined>(undefined);
    const [customEndDate, setCustomEndDate] = useState<Date | undefined>(undefined);
    const [customDateError, setCustomDateError] = useState<string | null>(null);

    if (!unitId) {
        return null;
    }

    // For selected dates from calendar
    const hasSelectedDates = selectedDates.length > 0;
    const sortedDates = hasSelectedDates
        ? [...selectedDates].sort((a, b) => a.getTime() - b.getTime())
        : [];
    const startDate = hasSelectedDates ? sortedDates[0] : undefined;
    const endDate = hasSelectedDates ? sortedDates[sortedDates.length - 1] : undefined;
    // Validate custom date range
    const validateCustomDates = (): boolean => {
        if (!customStartDate || !customEndDate) {
            setCustomDateError('Vui lòng chọn ngày bắt đầu và ngày kết thúc');
            return false;
        }

        if (customEndDate < customStartDate) {
            setCustomDateError('Ngày kết thúc phải sau ngày bắt đầu');
            return false;
        }

        setCustomDateError(null);
        return true;
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        // Determine which dates to use based on active tab
        let startDateToUse: Date;
        let endDateToUse: Date;

        if (activeTab === "selected") {
            if (!hasSelectedDates) {
                toast.error('Vui lòng chọn ít nhất một ngày từ lịch');
                return;
            }

            startDateToUse = startDate as Date;
            endDateToUse = endDate as Date;
        } else {
            // Custom date range
            if (!validateCustomDates()) {
                return;
            }

            startDateToUse = customStartDate as Date;
            endDateToUse = customEndDate as Date;
        }

        if (!unitId || !basePrice) {
            toast.error('Vui lòng chọn phòng và nhập giá cơ bản');
            return;
        }

        const price = parseFloat(basePrice);
        if (isNaN(price) || price <= 0) {
            toast.error('Vui lòng nhập giá hợp lệ');
            return;
        }

    setLoading(true);
    
    try {
      await priceService.updateBasePrice(
        unitId,
        format(startDateToUse, 'yyyy-MM-dd'),
        format(endDateToUse, 'yyyy-MM-dd'),
        price
      );

            toast.success('Cập nhật giá thành công');
            setBasePrice('');

            // Reset custom dates if using that method
            if (activeTab === "custom") {
                setCustomStartDate(undefined);
                setCustomEndDate(undefined);
            }

            onSuccess();
        } catch (error) {
            console.error('Error updating price:', error);
            toast.error('Cập nhật giá thất bại. Vui lòng thử lại.');
        } finally {
            setLoading(false);
        }
    };
    const renderDateDescription = () => {
        if (activeTab === "selected" && hasSelectedDates) {
            return (
                <CardDescription>
                    Cập nhật giá cơ bản cho {selectedDates.length} ngày từ{' '}
                    {format(startDate as Date, 'd MMMM yyyy', { locale: vi })} đến{' '}
                    {format(endDate as Date, 'd MMMM yyyy', { locale: vi })}
                </CardDescription>
            );
        } else if (activeTab === "custom" && customStartDate && customEndDate) {
            return (
                <CardDescription>
                    Cập nhật giá cơ bản từ{' '}
                    {format(customStartDate, 'd MMMM yyyy', { locale: vi })} đến{' '}
                    {format(customEndDate, 'd MMMM yyyy', { locale: vi })}
                </CardDescription>
            );
        }

        return <CardDescription>Chọn khoảng thời gian để cập nhật giá</CardDescription>;
    };

    const formatDateDisplay = (date: Date | undefined) => {
        return date ? format(date, 'dd/MM/yyyy') : 'Chọn ngày';
    };

    return (
        <Card>
            <CardHeader>
                <CardTitle>Cập Nhật Giá Cơ Bản</CardTitle>
                {renderDateDescription()}
            </CardHeader>
            <CardContent>
                <form onSubmit={handleSubmit} className="space-y-6">
                    <Tabs defaultValue="selected" value={activeTab} onValueChange={setActiveTab} className="w-full">
                        <TabsList className="grid w-full grid-cols-2">
                            <TabsTrigger value="selected">Chọn từ lịch</TabsTrigger>
                            <TabsTrigger value="custom">Chọn khoảng ngày</TabsTrigger>
                        </TabsList>

                        <TabsContent value="selected" className="py-2">
                            <div className="text-sm text-muted-foreground">
                                {hasSelectedDates ? (
                                    <p className="text-green-600">
                                        Đã chọn {selectedDates.length} ngày từ lịch
                                    </p>
                                ) : (
                                    <p className="text-amber-600">
                                        Chưa có ngày nào được chọn từ lịch. Hãy nhấn vào các ngày trên lịch để chọn.
                                    </p>
                                )}
                            </div>
                        </TabsContent>

                        <TabsContent value="custom" className="space-y-4 py-2">
                            <div className="grid gap-2">
                                <Label htmlFor="from">Từ ngày</Label>
                                <Popover>
                                    <PopoverTrigger asChild>
                                        <Button
                                            id="from"
                                            variant="outline"
                                            className="w-full justify-start text-left font-normal"
                                        >
                                            {formatDateDisplay(customStartDate)}
                                        </Button>
                                    </PopoverTrigger>
                                    <PopoverContent className="w-auto p-0" align="start">
                                        <Calendar
                                            mode="single"
                                            selected={customStartDate}
                                            onSelect={setCustomStartDate}
                                            initialFocus
                                        />
                                    </PopoverContent>
                                </Popover>
                            </div>

                            <div className="grid gap-2">
                                <Label htmlFor="to">Đến ngày</Label>
                                <Popover>
                                    <PopoverTrigger asChild>
                                        <Button
                                            id="to"
                                            variant="outline"
                                            className="w-full justify-start text-left font-normal"
                                        >
                                            {formatDateDisplay(customEndDate)}
                                        </Button>
                                    </PopoverTrigger>
                                    <PopoverContent className="w-auto p-0" align="start">
                                        <Calendar
                                            mode="single"
                                            selected={customEndDate}
                                            onSelect={setCustomEndDate}
                                            initialFocus
                                        />
                                    </PopoverContent>
                                </Popover>
                            </div>

                            {customDateError && (
                                <div className="text-sm text-red-500">{customDateError}</div>
                            )}
                        </TabsContent>
                    </Tabs>

                    <div className="space-y-2">
                        <Label htmlFor="base-price">Giá Cơ Bản (VND)</Label>
                        <Input
                            id="base-price"
                            type="number"
                            placeholder="Nhập giá cơ bản"
                            value={basePrice}
                            onChange={(e) => setBasePrice(e.target.value)}
                            required
                            min="0"
                            step="1000"
                            className="w-full"
                        />
                        <p className="text-sm text-gray-500">
                            Nhập giá cơ bản cho mỗi đêm trong khoảng thời gian đã chọn
                        </p>
                    </div>

                    <Button type="submit" disabled={loading} className="w-full">
                        {loading ? 'Đang Cập Nhật...' : 'Cập Nhật Giá'}
                    </Button>
                </form>
            </CardContent>
            <CardFooter className="text-sm text-gray-500">
                <p>
                    Lưu ý: Giá này là giá cơ bản. Các quy tắc về giá cuối tuần,
                    giá theo mùa, giảm giá theo số ngày lưu trú, v.v., sẽ được áp dụng sau.
                </p>
            </CardFooter>
        </Card>
    );
};
