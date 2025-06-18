'use client';

import { useState, useEffect } from 'react';
import { AmenityGroup } from '@/types/accommodation';
import { Checkbox } from '@/components/ui/checkbox';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import amenityService from '@/services/accommodation/amenityService';

interface AmenityListProps {
    unitId: number;
    accommodationId: number;
    selectedAmenityIds?: number[];
    onSelectionChange?: (amenityIds: number[]) => void;
    readOnly?: boolean;
}

export function AmenityList({
    unitId,
    accommodationId,
    selectedAmenityIds = [],
    onSelectionChange,
    readOnly = false
}: AmenityListProps) {
    const [amenityGroups, setAmenityGroups] = useState<AmenityGroup[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [selectedAmenities, setSelectedAmenities] = useState<number[]>(selectedAmenityIds);

    useEffect(() => {
        setSelectedAmenities(selectedAmenityIds);
    }, [selectedAmenityIds]);

    useEffect(() => {
        const fetchAmenityGroups = async () => {
            try {
                setLoading(true);
                // Get unit-specific amenity groups
                const response = await amenityService.getAmenityGroupsByType("");
                console.log("response from amenityService:", response);
                setAmenityGroups(response.data || []);
                console.log('Fetched amenity groups:', response.data);
                // If no selected amenities were passed, fetch the current unit amenities
                if (selectedAmenityIds.length === 0) {
                    const unitAmenities = await amenityService.getUnitAmenities(accommodationId, unitId);
                    console.log('Fetched unit amenities:', unitAmenities);
                    setSelectedAmenities(unitAmenities);
                }

                setError(null);
            } catch (err) {
                console.error('Error fetching amenity groups:', err);
                setError('Có lỗi xảy ra khi tải danh sách tiện nghi. Vui lòng thử lại.');
            } finally {
                setLoading(false);
            }
        };
        fetchAmenityGroups();
    }, [unitId, accommodationId]);

    const handleAmenityToggle = (amenityId: number, checked: boolean) => {
        const updatedSelection = checked
            ? [...selectedAmenities, amenityId]
            : selectedAmenities.filter(id => id !== amenityId);

        setSelectedAmenities(updatedSelection);

        if (onSelectionChange) {
            onSelectionChange(updatedSelection);
        }
    };

    if (loading) {
        return (
            <div className="flex justify-center p-8">
                <div className="text-gray-500">Đang tải danh sách tiện nghi...</div>
            </div>
        );
    }

    if (error) {
        return (
            <div className="text-red-500 p-4 bg-red-50 rounded-lg border border-red-200">
                <div className="font-medium">Có lỗi xảy ra</div>
                <div className="text-sm mt-1">{error}</div>
            </div>
        );
    }

    return (
        <div className="space-y-8">
            <div className="flex justify-between items-center">
                <div>
                    <h3 className="text-xl font-semibold">Tiện nghi có sẵn</h3>
                    <p className="text-sm text-gray-600 mt-1">
                        Chọn tất cả tiện nghi có trong phòng này
                    </p>
                </div>
                <div className="text-sm text-gray-500">
                    Đã chọn: <span className="font-medium text-gray-900">{selectedAmenities.length}</span>
                </div>
            </div>

            {amenityGroups.length === 0 ? (
                <p>No amenities available.</p>
            ) : (amenityGroups.map((group) => (
                <Card key={group.id} className="shadow-sm border-l-4 border-l-blue-500">
                    <CardHeader className="pb-4">
                        <CardTitle className="text-lg flex items-center gap-3">
                            {group.icon && (
                                <span className="text-2xl p-2 bg-blue-50 rounded-lg">{group.icon}</span>
                            )}
                            <div>
                                <div>{group.name}</div>
                                <div className="text-sm font-normal text-gray-500 mt-1">
                                    {group.amenities?.length || 0} tiện nghi có sẵn
                                </div>
                            </div>
                        </CardTitle>
                        {group.description && (
                            <CardDescription className="text-base">{group.description}</CardDescription>
                        )}
                    </CardHeader>
                    <CardContent className="pt-0"><div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-3">
                        {group.amenities?.map((amenity) => (
                            <div
                                key={amenity.id}
                                className="flex items-center space-x-3 p-3 rounded-lg border hover:bg-gray-50 transition-colors duration-200"
                            >
                                <Checkbox
                                    id={`amenity-${amenity.id}`}
                                    checked={selectedAmenities.includes(amenity.id)}
                                    disabled={readOnly}
                                    onCheckedChange={(checked) => handleAmenityToggle(amenity.id, !!checked)}
                                />
                                <label
                                    htmlFor={`amenity-${amenity.id}`}
                                    className="text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70 cursor-pointer flex items-center gap-2 flex-1"
                                >
                                    {amenity.icon && <span className="text-lg">{amenity.icon}</span>}
                                    <div className="flex flex-col">
                                        <span>{amenity.name}</span>
                                        {(amenity.isPaid || amenity.needToReserve) && (
                                            <div className="flex gap-2 text-xs text-gray-500 mt-1">
                                                {amenity.isPaid && <span className="bg-amber-100 text-amber-700 px-2 py-0.5 rounded-full">Có phí</span>}
                                                {amenity.needToReserve && <span className="bg-blue-100 text-blue-700 px-2 py-0.5 rounded-full">Đặt trước</span>}
                                            </div>
                                        )}
                                    </div>
                                </label>
                            </div>
                        ))}
                    </div>
                    </CardContent>
                </Card>
            ))
            )}
        </div>
    );
}
