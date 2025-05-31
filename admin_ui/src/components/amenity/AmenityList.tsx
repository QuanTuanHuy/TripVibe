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
        const fetchAmenityGroups = async () => {
            try {
                setLoading(true);
                // Get unit-specific amenity groups
                const response = await amenityService.getAmenityGroupsByType("");
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
                setError('Failed to load amenities. Please try again.');
            } finally {
                setLoading(false);
            }
        };

        fetchAmenityGroups();
    }, [unitId, accommodationId, selectedAmenityIds.length]);

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
        return <div className="flex justify-center p-8">Loading amenities...</div>;
    }

    if (error) {
        return <div className="text-red-500 p-4">{error}</div>;
    }

    return (
        <div className="space-y-6">
            <h3 className="text-lg font-semibold">Unit Amenities</h3>

            {amenityGroups.length === 0 ? (
                <p>No amenities available.</p>
            ) : (
                amenityGroups.map((group) => (
                    <Card key={group.id} className="shadow-sm">
                        <CardHeader className="pb-2">
                            <CardTitle className="text-md flex items-center gap-2">
                                {group.icon && (
                                    <span className="text-xl">{group.icon}</span>
                                )}
                                {group.name}
                            </CardTitle>
                            {group.description && (
                                <CardDescription>{group.description}</CardDescription>
                            )}
                        </CardHeader>
                        <CardContent>
                            <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-2">
                                {group.amenities?.map((amenity) => (
                                    <div
                                        key={amenity.id}
                                        className="flex items-center space-x-2 p-2 rounded hover:bg-gray-50"
                                    >
                                        <Checkbox
                                            id={`amenity-${amenity.id}`}
                                            checked={selectedAmenities.includes(amenity.id)}
                                            disabled={readOnly}
                                            onCheckedChange={(checked) => handleAmenityToggle(amenity.id, !!checked)}
                                        />
                                        <label
                                            htmlFor={`amenity-${amenity.id}`}
                                            className="text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70 cursor-pointer flex items-center gap-1"
                                        >
                                            {amenity.icon && <span className="text-base">{amenity.icon}</span>}
                                            {amenity.name}
                                            {amenity.isPaid && <span className="text-xs text-gray-500 ml-1">(paid)</span>}
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
