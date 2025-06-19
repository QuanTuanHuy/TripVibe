'use client';

import { useState, useEffect } from 'react';
import { AmenityList } from './AmenityList';
import { Button } from '@/components/ui/button';
import accommodationService from '@/services/accommodation/accommodationService';
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
    const [initialAmenityIds, setInitialAmenityIds] = useState<number[]>([]);
    const [isLoading, setIsLoading] = useState(false);
    const [isSaved, setIsSaved] = useState(false);
    const [initialLoading, setInitialLoading] = useState(true);

    // Load current amenities when component mounts
    useEffect(() => {
        const loadCurrentAmenities = async () => {
            try {
                setInitialLoading(true);
                const currentAmenities = await amenityService.getUnitAmenities(accommodationId, unitId);
                setSelectedAmenityIds(currentAmenities);
                setInitialAmenityIds(currentAmenities); // Store initial state for comparison
                console.log('Loaded current amenities:', currentAmenities);
            } catch (error) {
                console.error('Error loading current amenities:', error);
            } finally {
                setInitialLoading(false);
            }
        };

        loadCurrentAmenities();
    }, [accommodationId, unitId]);

    const handleSave = async () => {
        try {
            setIsLoading(true);

            // Use the new updateUnitAmenities API method with current and new amenities
            await accommodationService.updateUnitAmenities(
                accommodationId,
                unitId,
                selectedAmenityIds,
                initialAmenityIds
            );
            setIsSaved(true);

            console.log('Amenities updated successfully:', {
                initial: initialAmenityIds,
                selected: selectedAmenityIds
            });

            // Update initial state to reflect saved state
            setInitialAmenityIds(selectedAmenityIds);

            // Call onClose callback to refresh data in parent component
            if (onClose) {
                // For card variant, call callback immediately to refresh data
                if (variant === 'card') {
                    await onClose();
                    setTimeout(() => {
                        setIsSaved(false); // Reset saved state after showing success
                    }, 2000);
                } else {
                    // For dialog/modal variant, delay close
                    setTimeout(() => {
                        onClose();
                    }, 1500);
                }
            }
        } catch (error) {
            console.error('Error updating unit amenities:', error);
            const errorMessage = error instanceof Error ? error.message : "Unknown error occurred";
            alert(`Có lỗi xảy ra khi cập nhật tiện nghi: ${errorMessage}. Vui lòng thử lại.`);
        } finally {
            setIsLoading(false);
        }
    };

    const content = (
        <>
            <AmenityList
                unitId={unitId}
                accommodationId={accommodationId}
                selectedAmenityIds={selectedAmenityIds}
                onSelectionChange={setSelectedAmenityIds}
            />

            <div className={`flex ${variant === 'card' ? 'justify-between' : 'justify-end'} space-x-4 pt-6 border-t`}>
                {isSaved && variant === 'card' && (
                    <div className="flex items-center text-green-600 text-sm">
                        <div className="w-2 h-2 bg-green-500 rounded-full mr-2 animate-pulse"></div>
                        Đã lưu thành công!
                    </div>
                )}

                <div className="flex space-x-3">
                    {onClose && variant !== 'card' && (
                        <Button variant="outline" onClick={onClose}>
                            Hủy
                        </Button>
                    )}
                    <Button
                        onClick={handleSave}
                        disabled={isLoading || isSaved || initialLoading}
                        className={`${variant === 'card' ? 'px-8' : ''}`}
                    >
                        {initialLoading ? "Đang tải..." : isLoading ? "Đang lưu..." : isSaved ? "Đã lưu!" : "Lưu thay đổi"}
                    </Button>
                </div>
            </div>
        </>
    );

    if (variant === 'card') {
        return (
            <div className="space-y-6">
                {initialLoading ? (
                    <div className="flex justify-center p-8">
                        <div className="text-gray-500">Đang tải tiện nghi hiện tại...</div>
                    </div>
                ) : (
                    content
                )}
            </div>
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

            {initialLoading ? (
                <div className="flex justify-center p-8">
                    <div className="text-gray-500">Đang tải tiện nghi hiện tại...</div>
                </div>
            ) : (
                content
            )}
        </div>
    );
}
