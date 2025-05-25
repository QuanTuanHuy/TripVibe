'use client';

import { useState } from 'react';
import { AmenityList } from './AmenityList';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import amenityService from '@/services/accommodation/amenityService';

interface UnitAmenityManagerProps {
    unitId: number;
    accommodationId: number;
    onClose?: () => void;
    variant?: 'card' | 'plain';
}

export function UnitAmenityManager({
    unitId,
    accommodationId,
    onClose,
    variant = 'plain'
}: UnitAmenityManagerProps) {
    const [selectedAmenityIds, setSelectedAmenityIds] = useState<number[]>([]);
    const [isLoading, setIsLoading] = useState(false);
    const [isSaved, setIsSaved] = useState(false); const handleSave = async () => {
        try {
            setIsLoading(true);
            await amenityService.updateUnitAmenities(accommodationId, unitId, selectedAmenityIds);
            setIsSaved(true);
            // Show success notification (you can replace this with your preferred notification system)
            alert("Unit amenities updated successfully");

            if (onClose) {
                setTimeout(() => {
                    onClose();
                }, 1500);
            }
        } catch (error) {
            console.error('Error updating unit amenities:', error);
            // Show error notification
            alert("Failed to update amenities. Please try again.");
        } finally {
            setIsLoading(false);
        }
    };

    const content = (
        <>
            <AmenityList
                unitId={unitId}
                accommodationId={accommodationId}
                onSelectionChange={setSelectedAmenityIds}
            />

            <div className="flex justify-end space-x-4 pt-4">
                {onClose && (
                    <Button variant="outline" onClick={onClose}>
                        Cancel
                    </Button>
                )}
                <Button
                    onClick={handleSave}
                    disabled={isLoading || isSaved}
                >                    {isLoading ? "Saving..." : isSaved ? "Saved!" : "Save Amenities"}
                </Button>
            </div>
        </>
    );

    if (variant === 'card') {
        return (
            <Card>
                <CardHeader>
                    <CardTitle>Unit Amenities</CardTitle>
                    <CardDescription>Select the amenities available in this accommodation unit</CardDescription>
                </CardHeader>
                <CardContent className="space-y-6">
                    {content}
                </CardContent>
            </Card>
        );
    }

    return (
        <div className="space-y-6">
            <div className="flex items-center justify-between">
                <h2 className="text-xl font-semibold">Manage Unit Amenities</h2>
                {onClose && (
                    <Button variant="outline" onClick={onClose}>
                        Close
                    </Button>
                )}
            </div>

            {content}
        </div>
    );
}
