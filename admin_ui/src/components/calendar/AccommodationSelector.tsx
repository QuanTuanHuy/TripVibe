"use client";

import { useEffect, useState } from 'react';
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from '@/components/ui/select';
import { accommodationService } from '@/services';
import { Accommodation, Unit } from '@/types/accommodation';
import { useAuth } from '@/context/AuthContext';

interface AccommodationSelectorProps {
    onUnitSelected: (unitId: number) => void;
}

export const AccommodationSelector = ({ onUnitSelected }: AccommodationSelectorProps) => {
    const [accommodations, setAccommodations] = useState<Accommodation[]>([]);
    const [units, setUnits] = useState<Unit[]>([]);
    const [selectedAccommodationId, setSelectedAccommodationId] = useState<number | null>(null);
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string | null>(null);
    const { user } = useAuth();

    // Fetch accommodations on mount
    useEffect(() => {
        const fetchAccommodations = async () => {
            setLoading(true);
            try {
                const accommodations = await accommodationService.getMyAccommodations(user?.id || 0);
                setAccommodations(accommodations);
                setLoading(false);
            } catch (err) {
                console.error('Failed to fetch accommodations:', err);
                setError('Failed to load accommodations. Please try again.');
                setLoading(false);
            }
        };

        fetchAccommodations();
    }, [user]);

    // Fetch units when accommodation is selected
    useEffect(() => {
        const fetchUnits = async () => {
            if (!selectedAccommodationId) {
                setUnits([]);
                return;
            }

            setLoading(true);
            try {
                const units = await accommodationService.getUnitsByAccommodationId(selectedAccommodationId);
                setUnits(units || []);
                setLoading(false);
            } catch (err) {
                console.error('Failed to fetch units:', err);
                setError('Failed to load units. Please try again.');
                setLoading(false);
            }
        };

        fetchUnits();
    }, [selectedAccommodationId]);

    const handleAccommodationChange = (value: string) => {
        const accommodationId = parseInt(value, 10);
        setSelectedAccommodationId(accommodationId);
    };

    const handleUnitChange = (value: string) => {
        const unitId = parseInt(value, 10);
        onUnitSelected(unitId);
    };

    if (error) {
        return <div className="text-red-500">{error}</div>;
    }

    return (
        <div className="space-y-6">
            <div className="space-y-2">
                <label
                    htmlFor="accommodation-select"
                    className="text-sm font-medium text-gray-700"
                >
                    Chọn Khách Sạn/Chỗ Nghỉ
                </label>
                <Select
                    disabled={loading || accommodations.length === 0}
                    onValueChange={handleAccommodationChange}
                >
                    <SelectTrigger id="accommodation-select" className="w-full">
                        <SelectValue placeholder="Chọn khách sạn/chỗ nghỉ" />
                    </SelectTrigger>
                    <SelectContent>
                        {accommodations.map((accommodation) => (
                            <SelectItem key={accommodation.id} value={accommodation.id.toString()}>
                                {accommodation.name}
                            </SelectItem>
                        ))}
                    </SelectContent>
                </Select>
            </div>

            <div className="space-y-2">
                <label
                    htmlFor="unit-select"
                    className="text-sm font-medium text-gray-700"
                >
                    Chọn Phòng/Căn Hộ
                </label>
                <Select
                    disabled={loading || units.length === 0}
                    onValueChange={handleUnitChange}
                >
                    <SelectTrigger id="unit-select" className="w-full">
                        <SelectValue placeholder="Chọn phòng/căn hộ" />
                    </SelectTrigger>
                    <SelectContent>
                        {units.map((unit) => (
                            <SelectItem key={unit.id} value={unit.id.toString()}>
                                {unit.unitName?.name} - ${unit.pricePerNight}/đêm
                            </SelectItem>
                        ))}
                    </SelectContent>
                </Select>
            </div>
        </div>
    );
};
