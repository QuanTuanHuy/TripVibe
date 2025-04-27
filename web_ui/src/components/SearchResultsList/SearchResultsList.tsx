"use client";

import { useState, useEffect } from 'react';
import { Filter, SlidersHorizontal } from 'lucide-react';
import SearchResultItem from '../SearchResultItem';

const mockHotels = [
    {
        id: '1',
        name: 'La Passion Hanoi Hotel & Spa',
        imageUrl: 'https://cf.bstatic.com/xdata/images/hotel/square600/303038392.webp?k=76ff82cbb302474f4d65f7486d2b683c811d033d1415335e95d690172a763149&o=',
        rating: 9.2,
        reviewCount: 2954,
        location: 'Quận Hoàn Kiếm, Hà Nội',
        distance: '0.5km',
        description: 'Phòng Deluxe Giường Đôi - 1 giường đôi lớn. Phòng này có điều hòa không khí, minibar và TV màn hình phẳng.',
        price: 3780000,
        discountPrice: 5000000,
        amenities: [
            { id: 'free-cancel', name: 'Miễn phí hủy' },
            { id: 'breakfast', name: 'Bao gồm bữa sáng' }
        ],
        type: 'Phòng Deluxe Giường Đôi',
        rooms: 1,
        hasPromotion: true,
        geniusLevel: 1
    },
    {
        id: '2',
        name: 'Hanoi Ben\'s Apartment And Hotel',
        imageUrl: 'https://cf.bstatic.com/xdata/images/hotel/square600/303038392.webp?k=76ff82cbb302474f4d65f7486d2b683c811d033d1415335e95d690172a763149&o=',
        rating: 8.9,
        reviewCount: 192,
        location: 'Quận Hai Bà Trưng, Hà Nội',
        distance: '1.3km',
        description: 'Phòng Deluxe Giường Đôi với ban công hướng nhìn thành phố, có bể bơi ngoài trời và quán bar trên tầng thượng.',
        price: 2004750,
        discountPrice: 2450000,
        amenities: [
            { id: 'free-cancel', name: 'Miễn phí hủy' },
            { id: 'wifi', name: 'WiFi miễn phí' },
            { id: 'pool', name: 'Hồ bơi' }
        ],
        type: 'Phòng Deluxe Giường Đôi',
        rooms: 1,
        hasPromotion: true,
        geniusLevel: 1
    },
    {
        id: '3',
        name: 'Full House Times City',
        imageUrl: 'https://cf.bstatic.com/xdata/images/hotel/square600/303038392.webp?k=76ff82cbb302474f4d65f7486d2b683c811d033d1415335e95d690172a763149&o=',
        rating: 9.0,
        reviewCount: 95,
        location: 'Quận Hai Bà Trưng, Hà Nội',
        distance: '3.0km',
        description: 'Căn hộ 1 Phòng ngủ - 1 phòng ngủ • 1 phòng khách • 1 phòng tắm • 1 phòng bếp • 53m²',
        price: 3159000,
        discountPrice: 3510000,
        amenities: [
            { id: 'free-cancel', name: 'Miễn phí hủy' },
            { id: 'kitchen', name: 'Bếp đầy đủ' }
        ],
        type: 'Căn hộ 1 Phòng ngủ',
        rooms: 1,
        beds: 2,
        geniusLevel: 1
    },
    {
        id: '4',
        name: 'Eli Rina Hotel',
        imageUrl: 'https://cf.bstatic.com/xdata/images/hotel/square600/303038392.webp?k=76ff82cbb302474f4d65f7486d2b683c811d033d1415335e95d690172a763149&o=',
        rating: 9.2,
        reviewCount: 272,
        location: 'Quận Đống Đa, Hà Nội',
        distance: '2.5km',
        description: 'Phòng Deluxe Giường Đôi/2 Giường Đơn Nhìn Ra Thành Phố',
        price: 2755620,
        discountPrice: 3061800,
        amenities: [
            { id: 'free-cancel', name: 'Miễn phí hủy' },
            { id: 'breakfast', name: 'Bao gồm bữa sáng' }
        ],
        type: 'Phòng Deluxe Giường Đôi',
        hasPromotion: true,
        geniusLevel: 1
    },
    {
        id: '5',
        name: 'An Suites',
        imageUrl: 'https://cf.bstatic.com/xdata/images/hotel/square600/303038392.webp?k=76ff82cbb302474f4d65f7486d2b683c811d033d1415335e95d690172a763149&o=',
        rating: 8.7,
        reviewCount: 52,
        location: 'Quận Đống Đa, Hà Nội',
        distance: '0.8km',
        description: 'Studio Có Ban Công - 1 studio nguyên căn • 1 phòng tắm • 1 phòng bếp • 35m²',
        price: 1386000,
        discountPrice: 1560000,
        amenities: [
            { id: 'kitchen', name: 'Bếp đầy đủ' },
            { id: 'wifi', name: 'WiFi miễn phí' }
        ],
        type: 'Studio Có Ban Công',
        rooms: 1,
        hasPromotion: true,
        geniusLevel: 0
    }
];

interface SearchResultsListProps {
    onFilterClick?: () => void;
}

const SearchResultsList: React.FC<SearchResultsListProps> = ({ onFilterClick }) => {
    const [hotels, setHotels] = useState(mockHotels);
    const [sortBy, setSortBy] = useState<string>('recommended');
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        // Giả lập tải dữ liệu
        setLoading(true);
        setTimeout(() => {
            setLoading(false);
        }, 500);
    }, []);

    // Sắp xếp kết quả dựa trên lựa chọn
    useEffect(() => {
        const sortedHotels = [...mockHotels];

        switch (sortBy) {
            case 'price-asc':
                sortedHotels.sort((a, b) => a.price - b.price);
                break;
            case 'price-desc':
                sortedHotels.sort((a, b) => b.price - a.price);
                break;
            case 'rating':
                sortedHotels.sort((a, b) => (b.rating || 0) - (a.rating || 0));
                break;
            case 'distance':
                // Ví dụ này giả định rằng khoảng cách là chuỗi có dạng "0.5km"
                sortedHotels.sort((a, b) => {
                    const distanceA = a.distance ? parseFloat(a.distance.replace('km', '')) : 999;
                    const distanceB = b.distance ? parseFloat(b.distance.replace('km', '')) : 999;
                    return distanceA - distanceB;
                });
                break;
            default:
                // Mặc định giữ nguyên thứ tự ban đầu
                break;
        }

        setHotels(sortedHotels);
    }, [sortBy]);

    return (
        <div className="flex-1 px-0 md:px-6 py-4">
            <div className="flex flex-wrap items-center justify-between mb-8">
                <h1 className="text-2xl font-bold mb-2 md:mb-0 text-gray-800">
                    Hà Nội: tìm thấy {hotels.length} chỗ nghỉ
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
                                onChange={(e) => setSortBy(e.target.value)}
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
                    {hotels.map((hotel) => (
                        <SearchResultItem
                            key={hotel.id}
                            id={hotel.id}
                            name={hotel.name}
                            imageUrl={hotel.imageUrl}
                            rating={hotel.rating}
                            reviewCount={hotel.reviewCount}
                            location={hotel.location}
                            distance={hotel.distance}
                            description={hotel.description}
                            price={hotel.price}
                            discountPrice={hotel.discountPrice}
                            amenities={hotel.amenities}
                            type={hotel.type}
                            beds={hotel.beds}
                            rooms={hotel.rooms}
                            hasPromotion={hotel.hasPromotion}
                            geniusLevel={hotel.geniusLevel}
                        />
                    ))}
                </div>
            )}

            {/* No results */}
            {!loading && hotels.length === 0 && (
                <div className="text-center py-8">
                    <p className="text-gray-700 text-lg">Không tìm thấy kết quả phù hợp.</p>
                    <p className="text-gray-600">Vui lòng thử lại với tiêu chí tìm kiếm khác.</p>
                </div>
            )}
        </div>
    );
};

export default SearchResultsList;