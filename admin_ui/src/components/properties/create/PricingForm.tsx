"use client";

import { useState, useEffect } from 'react';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Button } from '@/components/ui/button';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { CreateAccommodationDto, CreatePriceTypeDto, CreatePriceGroupDto, PriceType } from '@/types/accommodation';
import { Plus, Trash2, Info, AlertCircle } from 'lucide-react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Tooltip, TooltipContent, TooltipProvider, TooltipTrigger } from '@/components/ui/tooltip';
import { Alert, AlertDescription } from '@/components/ui/alert';
import { priceTypeService } from '@/services';

interface PricingFormProps {
    formData: CreateAccommodationDto;
    updateFormData: (data: Partial<CreateAccommodationDto>) => void;
}

export default function PricingForm({ formData, updateFormData }: PricingFormProps) {
    // Danh sách các loại giá - sử dụng Standard Rate và Non-refundable Rate
    const [priceTypes, setPriceTypes] = useState<PriceType[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const fetchPriceTypes = async () => {
            try {
                setLoading(true);
                setError(null);

                const response = await priceTypeService.getPriceTypes({
                    page: 0,
                    pageSize: 2,
                    sortType: 'asc'
                });
                setPriceTypes(response.data || []);
            } catch (err) {
                console.error('Error fetching price types:', err);
                setError('Có lỗi xảy ra khi tải dữ liệu loại giá. Vui lòng tải lại trang.');
            } finally {
                setLoading(false);
            }
        };

        fetchPriceTypes();

    }, []);


    // Tự động tạo giá theo số lượng khách khi component được load
    useEffect(() => {
        // Chỉ tạo nếu chưa có giá theo số lượng khách
        if (!formData.unit.priceGroups || formData.unit.priceGroups.length === 0) {
            const maxGuests = formData.unit.maxAdults;
            if (maxGuests > 0) {
                const priceGroups: CreatePriceGroupDto[] = [];

                // Tạo giá cho số lượng khách tối đa (100%)
                priceGroups.push({
                    numberOfGuests: maxGuests,
                    percentage: 100,
                });

                // Tạo giá cho số lượng khách giảm dần (giảm 10% mỗi người)
                for (let i = 1; i < maxGuests; i++) {
                    priceGroups.push({
                        numberOfGuests: maxGuests - i,
                        percentage: 100 - (i * 10),
                    });
                }

                updateFormData({
                    unit: {
                        ...formData.unit,
                        priceGroups,
                    },
                });
            }
        }
    }, [formData.unit.maxAdults]);

    // Thêm một loại giá mới
    const addPriceType = () => {
        const priceTypes = [...(formData.unit.priceTypes || [])];

        priceTypes.push({
            priceTypeId: 1, // Default to standard rate
            percentage: 100, // 100% của giá cơ bản
        });

        updateFormData({
            unit: {
                ...formData.unit,
                priceTypes
            }
        });
    };

    // Xóa một loại giá
    const removePriceType = (index: number) => {
        const priceTypes = [...(formData.unit.priceTypes || [])];
        priceTypes.splice(index, 1);

        updateFormData({
            unit: {
                ...formData.unit,
                priceTypes
            }
        });
    };

    // Cập nhật thông tin cho một loại giá
    const updatePriceType = (index: number, field: keyof CreatePriceTypeDto, value: any) => {
        const priceTypes = [...(formData.unit.priceTypes || [])];

        priceTypes[index] = {
            ...priceTypes[index],
            [field]: value
        };

        // Tự động thiết lập phần trăm giá theo loại
        if (field === 'priceTypeId') {
            // Nếu là giá không hoàn tiền, mặc định thiết lập là 90% giá gốc
            if (value === 2) {
                priceTypes[index].percentage = 90;
            } else {
                priceTypes[index].percentage = 100;
            }
        }

        updateFormData({
            unit: {
                ...formData.unit,
                priceTypes
            }
        });
    };

    // Thêm một nhóm giá mới
    const addPriceGroup = () => {
        const priceGroups = [...(formData.unit.priceGroups || [])];
        const maxGuests = formData.unit.maxAdults;
        const currentGuestCounts = priceGroups.map(group => group.numberOfGuests);

        // Tìm số lượng khách chưa có trong danh sách
        let newGuestCount = maxGuests;
        while (currentGuestCounts.includes(newGuestCount) && newGuestCount > 0) {
            newGuestCount--;
        }

        // Tính phần trăm dựa trên số người giảm so với max
        const guestDifference = maxGuests - newGuestCount;
        const percentage = Math.max(100 - (guestDifference * 10), 50); // Giảm tối đa 50%

        if (newGuestCount > 0) {
            priceGroups.push({
                numberOfGuests: newGuestCount,
                percentage: percentage
            });

            updateFormData({
                unit: {
                    ...formData.unit,
                    priceGroups
                }
            });
        }
    };

    // Xóa một nhóm giá
    const removePriceGroup = (index: number) => {
        const priceGroups = [...(formData.unit.priceGroups || [])];
        priceGroups.splice(index, 1);

        updateFormData({
            unit: {
                ...formData.unit,
                priceGroups
            }
        });
    };

    // Cập nhật thông tin cho một nhóm giá
    const updatePriceGroup = (index: number, field: keyof CreatePriceGroupDto, value: any) => {
        const priceGroups = [...(formData.unit.priceGroups || [])];

        // Nếu đang cập nhật số lượng khách, tự động tính phần trăm theo quy tắc
        if (field === 'numberOfGuests') {
            const maxGuests = formData.unit.maxAdults;
            const guestDifference = maxGuests - value;
            const percentage = Math.max(100 - (guestDifference * 10), 50); // Giảm tối đa 50%

            priceGroups[index] = {
                ...priceGroups[index],
                numberOfGuests: value,
                percentage: percentage
            };
        } else {
            priceGroups[index] = {
                ...priceGroups[index],
                [field]: value
            };
        }

        updateFormData({
            unit: {
                ...formData.unit,
                priceGroups
            }
        });
    };

    // Get price type name
    const getPriceTypeName = (typeId: number): string => {
        return priceTypes.find(type => type.id === typeId)?.name || 'Không xác định';
    };

    // Tính giá từ phần trăm
    const calculatePriceFromPercentage = (percentage: number): number => {
        return Math.round((percentage / 100) * formData.unit.pricePerNight);
    };

    if (loading) {
        return (
            <div className="flex items-center justify-center p-8">
                <div className="w-8 h-8 border-4 border-blue-500 border-t-transparent rounded-full animate-spin"></div>
                <span className="ml-2">Đang tải dữ liệu...</span>
            </div>
        );
    }

    if (error) {
        return (
            <Alert variant="destructive">
                <AlertCircle className="h-4 w-4" />
                <AlertDescription>{error}</AlertDescription>
            </Alert>
        );
    }

    return (
        <div className="space-y-6">
            <h2 className="text-xl font-semibold">Thiết lập giá cả</h2>
            <p className="text-gray-600 mb-4">
                Thiết lập giá cho phòng/đơn vị của bạn. Bạn có thể cấu hình giá cơ bản, giá theo loại hình và giá theo số lượng khách.
            </p>

            <div className="p-4 border rounded-md bg-slate-50 mb-6">
                <div className="flex justify-between items-center">
                    <div>
                        <h3 className="font-medium">Giá cơ bản</h3>
                        <p className="text-sm text-gray-500">Giá mặc định cho phòng/đơn vị này</p>
                    </div>
                    <div className="flex items-center space-x-2">
                        <Input
                            type="number"
                            value={formData.unit.pricePerNight || ''}
                            onChange={(e) => updateFormData({
                                unit: {
                                    ...formData.unit,
                                    pricePerNight: Number(e.target.value)
                                }
                            })}
                            min="0"
                            step="10000"
                            className="w-36"
                        />
                        <span className="text-gray-500">VND/đêm</span>
                    </div>
                </div>
            </div>

            <Tabs defaultValue="rateTypes" className="w-full">
                <TabsList className="grid w-full grid-cols-2">
                    <TabsTrigger value="rateTypes">Loại giá</TabsTrigger>
                    <TabsTrigger value="occupancy">Giá theo số lượng khách</TabsTrigger>
                </TabsList>

                <TabsContent value="rateTypes" className="space-y-4">
                    <div className="flex justify-between items-center">
                        <div className="flex items-center gap-2">
                            <h3 className="font-medium">Loại giá</h3>
                            <TooltipProvider>
                                <Tooltip>
                                    <TooltipTrigger>
                                        <Info className="h-4 w-4 text-blue-500" />
                                    </TooltipTrigger>
                                    <TooltipContent>
                                        <p className="max-w-xs">
                                            Giá tiêu chuẩn: Cho phép hủy phòng<br />
                                            Giá không hoàn tiền: Giá thấp hơn nhưng không thể hủy
                                        </p>
                                    </TooltipContent>
                                </Tooltip>
                            </TooltipProvider>
                        </div>
                        <Button
                            type="button"
                            variant="outline"
                            size="sm"
                            onClick={addPriceType}
                            className="flex items-center gap-1"
                        >
                            <Plus className="h-4 w-4" />
                            <span>Thêm loại giá</span>
                        </Button>
                    </div>

                    {(!formData.unit.priceTypes || formData.unit.priceTypes.length === 0) && (
                        <div className="text-center p-8 border-2 border-dashed rounded-md">
                            <p className="text-gray-500 mb-2">Chưa có loại giá nào được thiết lập</p>
                            <Button
                                type="button"
                                variant="default"
                                size="sm"
                                onClick={addPriceType}
                                className="flex items-center gap-1 mx-auto"
                            >
                                <Plus className="h-4 w-4" />
                                <span>Thêm loại giá</span>
                            </Button>
                        </div>
                    )}

                    <div className="space-y-4">
                        {formData.unit.priceTypes?.map((priceType, index) => (
                            <Card key={index}>
                                <CardHeader className="flex flex-row items-center justify-between pb-2">
                                    <CardTitle className="text-sm font-medium">
                                        {getPriceTypeName(priceType.priceTypeId)}
                                    </CardTitle>
                                    <Button
                                        type="button"
                                        variant="ghost"
                                        size="sm"
                                        onClick={() => removePriceType(index)}
                                        className="h-8 w-8 p-0 text-red-500"
                                    >
                                        <Trash2 className="h-4 w-4" />
                                    </Button>
                                </CardHeader>
                                <CardContent className="space-y-4">
                                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                                        <div className="space-y-2">
                                            <Label>Loại giá</Label>
                                            <Select
                                                value={priceType.priceTypeId.toString()}
                                                onValueChange={(value) =>
                                                    updatePriceType(index, 'priceTypeId', parseInt(value))
                                                }
                                            >
                                                <SelectTrigger>
                                                    <SelectValue placeholder="Chọn loại giá" />
                                                </SelectTrigger>
                                                <SelectContent>
                                                    {priceTypes.map((type) => (
                                                        <SelectItem key={type.id} value={type.id.toString()}>
                                                            {type.name}
                                                        </SelectItem>
                                                    ))}
                                                </SelectContent>
                                            </Select>
                                            <p className="text-xs text-gray-500 mt-1">
                                                {priceTypes.find(type => type.id === priceType.priceTypeId)?.description}
                                            </p>
                                        </div>

                                        <div className="space-y-2">
                                            <Label>Phần trăm giá cơ bản</Label>
                                            <div className="grid grid-cols-2 gap-4">
                                                <div className="flex items-center space-x-2">
                                                    <Input
                                                        type="number"
                                                        value={priceType.percentage || ''}
                                                        onChange={(e) =>
                                                            updatePriceType(index, 'percentage', Number(e.target.value))
                                                        }
                                                        min="50"
                                                        max="200"
                                                        step="5"
                                                        className="w-full"
                                                    />
                                                    <span className="text-gray-500">%</span>
                                                </div>
                                                <div className="flex items-center space-x-2">
                                                    <span className="text-gray-500">=</span>
                                                    <span className="font-medium">
                                                        {calculatePriceFromPercentage(priceType.percentage)} VND
                                                    </span>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </CardContent>
                            </Card>
                        ))}
                    </div>
                </TabsContent>

                <TabsContent value="occupancy" className="space-y-4">
                    <div className="flex justify-between items-center">
                        <div className="flex items-center gap-2">
                            <h3 className="font-medium">Giá theo số lượng khách</h3>
                            <TooltipProvider>
                                <Tooltip>
                                    <TooltipTrigger>
                                        <Info className="h-4 w-4 text-blue-500" />
                                    </TooltipTrigger>
                                    <TooltipContent>
                                        <p className="max-w-xs">
                                            Giá sẽ được giảm 10% cho mỗi người ít hơn so với số lượng tối đa
                                        </p>
                                    </TooltipContent>
                                </Tooltip>
                            </TooltipProvider>
                        </div>
                        <Button
                            type="button"
                            variant="outline"
                            size="sm"
                            onClick={addPriceGroup}
                            className="flex items-center gap-1"
                        >
                            <Plus className="h-4 w-4" />
                            <span>Thêm giá theo khách</span>
                        </Button>
                    </div>

                    {(!formData.unit.priceGroups || formData.unit.priceGroups.length === 0) && (
                        <div className="text-center p-8 border-2 border-dashed rounded-md">
                            <p className="text-gray-500 mb-2">Chưa có giá theo số lượng khách</p>
                            <Button
                                type="button"
                                variant="default"
                                size="sm"
                                onClick={addPriceGroup}
                                className="flex items-center gap-1 mx-auto"
                            >
                                <Plus className="h-4 w-4" />
                                <span>Thêm giá theo khách</span>
                            </Button>
                        </div>
                    )}

                    <div className="space-y-4">
                        {formData.unit.priceGroups?.map((priceGroup, index) => (
                            <Card key={index}>
                                <CardHeader className="flex flex-row items-center justify-between pb-2">
                                    <CardTitle className="text-sm font-medium">
                                        {priceGroup.numberOfGuests} khách
                                    </CardTitle>
                                    <Button
                                        type="button"
                                        variant="ghost"
                                        size="sm"
                                        onClick={() => removePriceGroup(index)}
                                        className="h-8 w-8 p-0 text-red-500"
                                    >
                                        <Trash2 className="h-4 w-4" />
                                    </Button>
                                </CardHeader>
                                <CardContent className="space-y-4">
                                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                                        <div className="space-y-2">
                                            <Label>Số lượng khách</Label>
                                            <Input
                                                type="number"
                                                value={priceGroup.numberOfGuests || ''}
                                                onChange={(e) =>
                                                    updatePriceGroup(index, 'numberOfGuests', parseInt(e.target.value))
                                                }
                                                min="1"
                                                max={formData.unit.maxAdults}
                                                className="w-full"
                                            />
                                        </div>

                                        <div className="space-y-2">
                                            <Label>Phần trăm giá cơ bản</Label>
                                            <div className="grid grid-cols-2 gap-4">
                                                <div className="flex items-center space-x-2">
                                                    <Input
                                                        type="number"
                                                        value={priceGroup.percentage || ''}
                                                        onChange={(e) =>
                                                            updatePriceGroup(index, 'percentage', Number(e.target.value))
                                                        }
                                                        min="50"
                                                        max="100"
                                                        step="5"
                                                        className="w-full"
                                                    />
                                                    <span className="text-gray-500">%</span>
                                                </div>
                                                <div className="flex items-center space-x-2">
                                                    <span className="text-gray-500">=</span>
                                                    <span className="font-medium">
                                                        {calculatePriceFromPercentage(priceGroup.percentage)} VND
                                                    </span>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </CardContent>
                            </Card>
                        ))}
                    </div>
                </TabsContent>
            </Tabs>

            <div className="p-4 bg-blue-50 rounded-md border border-blue-200 mt-6">
                <p className="text-sm text-blue-700">
                    <strong>Lời khuyên:</strong> Thiết lập giá "Không hoàn tiền" thấp hơn 10-15% so với giá tiêu chuẩn
                    sẽ khuyến khích đặt phòng sớm và giảm tỷ lệ hủy. Giá theo số lượng khách hợp lý sẽ tăng tỷ lệ lấp đầy phòng.
                </p>
            </div>
        </div>
    );
}