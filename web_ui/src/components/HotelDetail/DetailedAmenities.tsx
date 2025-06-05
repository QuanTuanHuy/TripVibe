"use client";

import { forwardRef } from 'react';
import { Check, X } from 'lucide-react';
import { AccommodationAmenity } from '@/types/accommodation';
import { UnitAmenity } from '@/types/accommodation/accommodation/unit.types';
import {
    combineAmenitiesData,
    extractUnitAmenities
} from '@/utils/amenityUtils';

interface AmenityItem {
    name: string;
    available: boolean;
    hasCharge?: boolean;
    description?: string;
    availableTime?: string;
    icon?: React.ReactNode;
}

interface AmenityCategory {
    title: string;
    icon: React.ReactNode;
    items: AmenityItem[];
}
interface PopularAmenity {
    name: string;
    icon: React.ReactNode;
    hasCharge?: boolean;
    availableTime?: string;
}

interface DetailedAmenitiesProps {
    hotelName?: string;
    rating?: number;
    popularAmenities?: PopularAmenity[];
    amenityCategories?: AmenityCategory[];
    accommodationAmenities?: AccommodationAmenity[];
    unitAmenities?: UnitAmenity[];
    units?: any[]; // For extracting unit amenities
}

const DetailedAmenities = forwardRef<HTMLDivElement, DetailedAmenitiesProps>(
    ({
        hotelName = "",
        rating = 9.2,
        popularAmenities,
        amenityCategories,
        accommodationAmenities,
        unitAmenities,
        units
    }, ref) => {
        // Extract unit amenities if units are provided
        const extractedUnitAmenities = units ? extractUnitAmenities(units) : unitAmenities || [];

        // Combine amenities data while preserving original structure:
        // - Popular amenities from accommodation level (property amenities)
        // - Amenity categories from unit level (room amenities, deduplicated)
        const combinedData = combineAmenitiesData(
            accommodationAmenities || [],
            extractedUnitAmenities
        );

        // Use combined data if available, otherwise fallback to passed props
        const finalPopularAmenities = (accommodationAmenities && accommodationAmenities.length > 0)
            ? combinedData.popularAmenities
            : popularAmenities || [];

        const finalAmenityCategories = extractedUnitAmenities.length > 0
            ? combinedData.amenityCategories
            : amenityCategories || [];

        return (
            <div ref={ref} className="mt-12">
                <h2 className="text-2xl font-bold mb-6">Các tiện nghi của {hotelName}</h2>
                <p className="text-base mb-6">Tiện nghi tuyệt với! Điểm đánh giá: {rating}</p>

                {finalPopularAmenities.length > 0 && (
                    <>
                        <h3 className="text-lg font-medium mb-4">Các tiện nghi được ưa chuộng nhất</h3>
                        <div className="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-5 gap-4 mb-8">
                            {finalPopularAmenities.map((amenity, index) => (
                                <div key={index} className="flex items-center gap-2 p-3 rounded-lg hover:border-gray-300 transition-colors">
                                    {amenity.icon}
                                    <div className="flex-1">
                                        <span className="text-sm font-medium">{amenity.name}</span>
                                        {/* {amenity.hasCharge && (
                                            <span className="text-xs text-gray-500 block">(Phụ phí)</span>
                                        )}
                                        {amenity.availableTime && amenity.availableTime !== '24/7' && (
                                            <span className="text-xs text-gray-500 block">
                                                {formatAvailableTime(amenity.availableTime)}
                                            </span>
                                        )} */}
                                    </div>
                                </div>
                            ))}
                        </div>
                    </>
                )}

                {finalAmenityCategories.length > 0 && (
                    <div className="columns-1 md:columns-2 lg:columns-3 gap-8 space-y-0">
                        {finalAmenityCategories.map((category, categoryIndex) => (
                            <div key={categoryIndex} className="break-inside-avoid mb-8">
                                <h3 className="font-medium text-lg flex items-center mb-4">
                                    <span className="p-2 mr-3 rounded-full bg-gray-50">
                                        {category.icon}
                                    </span>
                                    {category.title}
                                </h3>                                <ul className="space-y-3">
                                    {category.items.map((item: AmenityItem, itemIndex: number) => (
                                        <li key={itemIndex} className="flex items-start gap-3">
                                            <div className="flex-shrink-0 mt-0.5">
                                                {item.available ? (
                                                    <Check size={16} className="text-green-600" />
                                                ) : (
                                                    <X size={16} className="text-red-500" />
                                                )}
                                            </div>
                                            <div className="flex-1">
                                                <div className="flex items-center gap-2 mb-1">
                                                    {item.icon}
                                                    <span className="text-sm font-medium leading-5">
                                                        {item.name}
                                                    </span>
                                                    {item.hasCharge && (
                                                        <span className="text-gray-500 text-xs bg-gray-100 px-2 py-0.5 rounded">
                                                            Phụ phí
                                                        </span>
                                                    )}
                                                </div>
                                                {/* {item.description && (
                                                    <p className="text-xs text-gray-600 leading-4">
                                                        {item.description}
                                                    </p>
                                                )} */}
                                                {/* {item.availableTime && item.availableTime !== '24/7' && (
                                                    <p className="text-xs text-gray-500 leading-4">
                                                        Thời gian: {formatAvailableTime(item.availableTime)}
                                                    </p>
                                                )} */}
                                            </div>
                                        </li>
                                    ))}
                                </ul>
                            </div>
                        ))}
                    </div>
                )}

                {finalPopularAmenities.length === 0 && finalAmenityCategories.length === 0 && (
                    <div className="text-center py-12 text-gray-500">
                        <p>Thông tin tiện nghi đang được cập nhật...</p>
                    </div>
                )}
            </div>
        );
    }
);

DetailedAmenities.displayName = 'DetailedAmenities';

export default DetailedAmenities;
