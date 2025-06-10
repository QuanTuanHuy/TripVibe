"use client";

import { useState, useEffect } from 'react';
import { Filter, SlidersHorizontal } from 'lucide-react';
import SearchResultItem from '../SearchResultItem';
import { AccommodationThumbnail } from '@/types/accommodation/accommodation';

interface SearchResultsListProps {
    onFilterClick?: () => void;
    accommodations?: AccommodationThumbnail[];
    loading?: boolean;
    totalResults?: number;
    onSortChange?: (sortBy: string) => void;
    onPageChange?: (page: number) => void;
    currentPage?: number;
}

const SearchResultsList: React.FC<SearchResultsListProps> = ({
    onFilterClick,
    accommodations = [],
    loading = false,
    totalResults = 0,
    onSortChange,
    onPageChange,
    currentPage = 1
}) => {
    const [sortBy, setSortBy] = useState<string>('recommended');

    const handleSortChange = (value: string) => {
        setSortBy(value);
        if (onSortChange) {
            onSortChange(value);
        }
    };

    // Function to transform AccommodationThumbnail to SearchResultItem props
    const transformAccommodation = (accommodation: AccommodationThumbnail) => {
        // Use the first unit's description, or accommodation description as fallback
        const description = accommodation.units && accommodation.units.length > 0
            ? accommodation.units[0].description
            : accommodation.description;

        // Use the first unit's name as type, or a default value
        const type = accommodation.units && accommodation.units.length > 0
            ? accommodation.units[0].name
            : 'Phòng nghỉ';

        return {
            id: accommodation.id.toString(),
            name: accommodation.name,
            imageUrl: accommodation.thumbnailUrl || '',
            rating: accommodation.ratingSummary?.rating || 0,
            reviewCount: accommodation.ratingSummary?.numberOfRatings || 0,
            location: accommodation.location?.address || 'Không xác định',
            distance: '0km', // This would need to be calculated based on user location
            description: description, price: accommodation.priceInfo?.currentPrice || 0,
            discountPrice: accommodation.priceInfo?.initialPrice || undefined,
            amenities: [
                { id: 'free-cancel', name: 'Miễn phí hủy' },
                { id: 'wifi', name: 'WiFi miễn phí' }
            ], // Default amenities - these would come from API
            type: type,
            rooms: 1, // Default value - this would need to be calculated
            beds: undefined, // Optional property
            hasPromotion: accommodation.priceInfo ? accommodation.priceInfo.initialPrice > accommodation.priceInfo.currentPrice : false,
            geniusLevel: 1 // Default value
        };
    };

    return (
        <div className="flex-1 px-0 md:px-6 py-4">
            <div className="flex flex-wrap items-center justify-between mb-8">
                <h1 className="text-2xl font-bold mb-2 md:mb-0 text-gray-800">
                    Hà Nội: tìm thấy {totalResults} chỗ nghỉ
                </h1>

                <div className="w-full md:w-auto flex items-center space-x-4">
                    <button
                        onClick={onFilterClick}
                        className="md:hidden flex items-center justify-center space-x-2 border border-gray-300 rounded-md px-4 py-2"
                    >
                        <Filter size={16} />
                        <span>Bộ lọc</span>
                    </button>

                    <div className="relative flex-1 md:flex-none">
                        <div className="flex items-center border border-gray-300 rounded-md px-4 py-2">
                            <SlidersHorizontal size={16} className="mr-2 text-gray-600" />
                            <select
                                className="bg-transparent outline-none appearance-none w-full text-gray-700"
                                value={sortBy}
                                onChange={(e) => handleSortChange(e.target.value)}
                            >
                                <option value="recommended">Lựa chọn hàng đầu của chúng tôi</option>
                                <option value="price-asc">Giá (thấp đến cao)</option>
                                <option value="price-desc">Giá (cao đến thấp)</option>
                                <option value="rating">Xếp hạng cao nhất</option>
                                <option value="distance">Gần trung tâm nhất</option>
                            </select>
                        </div>
                    </div>
                </div>
            </div>

            {/* Toggle views */}
            <div className="flex justify-end mb-6">
                <div className="flex space-x-2">
                    <button className="px-3 py-1 border border-gray-200 rounded-l-md bg-white text-gray-700">Xem ngang</button>
                    <button className="px-3 py-1 border border-gray-200 rounded-r-md bg-blue-50 text-blue-700 font-medium">Xem dọc</button>
                </div>
            </div>

            {/* Loading indicator */}
            {loading && (
                <div className="flex justify-center my-8">
                    <div className="animate-spin rounded-full h-10 w-10 border-t-2 border-b-2 border-blue-500"></div>
                </div>
            )}

            {/* Results list */}
            {!loading && (
                <div className="space-y-8">
                    {accommodations.map((accommodation) => {
                        const transformedData = transformAccommodation(accommodation);
                        return (
                            <SearchResultItem
                                key={transformedData.id}
                                id={transformedData.id}
                                name={transformedData.name}
                                imageUrl={transformedData.imageUrl}
                                rating={transformedData.rating}
                                reviewCount={transformedData.reviewCount}
                                location={transformedData.location}
                                distance={transformedData.distance}
                                description={transformedData.description}
                                price={transformedData.price}
                                discountPrice={transformedData.discountPrice}
                                amenities={transformedData.amenities}
                                type={transformedData.type}
                                beds={transformedData.beds}
                                rooms={transformedData.rooms}
                                hasPromotion={transformedData.hasPromotion}
                                geniusLevel={transformedData.geniusLevel}
                            />
                        );
                    })}
                </div>
            )}

            {/* No results */}
            {!loading && accommodations.length === 0 && (
                <div className="text-center py-8">
                    <p className="text-gray-700 text-lg">Không tìm thấy kết quả phù hợp.</p>
                    <p className="text-gray-600">Vui lòng thử lại với tiêu chí tìm kiếm khác.</p>
                </div>
            )}

            {/* Pagination - Simple example */}
            {!loading && accommodations.length > 0 && onPageChange && (
                <div className="flex justify-center mt-8">
                    <div className="flex space-x-2">
                        <button
                            onClick={() => onPageChange(Math.max(1, currentPage - 1))}
                            disabled={currentPage <= 1}
                            className="px-4 py-2 border border-gray-300 rounded-md disabled:opacity-50 disabled:cursor-not-allowed"
                        >
                            Trước
                        </button>
                        <span className="px-4 py-2 text-gray-700">
                            Trang {currentPage}
                        </span>
                        <button
                            onClick={() => onPageChange(currentPage + 1)}
                            className="px-4 py-2 border border-gray-300 rounded-md"
                        >
                            Sau
                        </button>
                    </div>
                </div>
            )}
        </div>
    );
};

export default SearchResultsList;