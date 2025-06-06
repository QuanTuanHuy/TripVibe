"use client";

import React, { forwardRef, useState } from 'react';
import { MapPin, Navigation, ExternalLink } from 'lucide-react';
import HotelMap from './HotelMap';
import MapModal from './MapModal';
import { Location } from '@/types/location';

interface HotelLocationSectionProps {
    location?: Location;
    hotelName: string;
    address: string;
    nearbyAttractions?: Array<{
        name: string;
        distance: string;
        type: string;
    }>;
}

const HotelLocationSection = forwardRef<HTMLDivElement, HotelLocationSectionProps>(
    ({ location, hotelName, address, nearbyAttractions }, ref) => {
        const [isMapModalOpen, setIsMapModalOpen] = useState(false);

        const handleMapClick = () => {
            setIsMapModalOpen(true);
        };

        const defaultAttractions = [
            { name: "Chợ Đồng Xuân", distance: "300m", type: "Mua sắm" },
            { name: "Ô Quan Chưởng", distance: "600m", type: "Di tích lịch sử" },
            { name: "Đền Ngọc Sơn", distance: "1.1km", type: "Điểm tham quan" },
            { name: "Nhà hát lớn Hà Nội", distance: "1.5km", type: "Điểm tham quan" },
            { name: "Sân bay Quốc tế Nội Bài", distance: "20km", type: "Sân bay" }
        ];

        const attractions = nearbyAttractions || defaultAttractions;

        const openInGoogleMaps = () => {
            if (location) {
                const url = `https://www.google.com/maps?q=${location.latitude},${location.longitude}`;
                window.open(url, '_blank');
            }
        };

        const openDirections = () => {
            if (location) {
                const url = `https://www.google.com/maps/dir/?api=1&destination=${location.latitude},${location.longitude}`;
                window.open(url, '_blank');
            }
        };

        return (
            <div ref={ref} className="mt-16 pb-8">
                {/* Section Header */}
                <div className="flex items-center justify-between mb-8">
                    <div>
                        <h2 className="text-2xl font-bold text-gray-800 mb-2">Vị trí</h2>
                        <p className="text-gray-600">{address}</p>
                    </div>
                    <div className="flex gap-3">
                        <button
                            onClick={openDirections}
                            className="flex items-center gap-2 px-4 py-2 bg-green-600 text-white font-medium rounded-lg hover:bg-green-700 transition-colors"
                        >
                            <Navigation size={18} />
                            Chỉ đường
                        </button>
                        <button
                            onClick={openInGoogleMaps}
                            className="flex items-center gap-2 px-4 py-2 bg-blue-600 text-white font-medium rounded-lg hover:bg-blue-700 transition-colors"
                        >
                            <ExternalLink size={18} />
                            Google Maps
                        </button>
                    </div>
                </div>
                <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
                    {/* Map */}
                    <div className="lg:col-span-2 relative">
                        <HotelMap
                            location={location}
                            hotelName={hotelName}
                            className="w-full"
                            onClick={handleMapClick}
                            isModalOpen={isMapModalOpen}
                        />
                        {/* Simple click overlay */}
                        {!isMapModalOpen && (
                            <div
                                className="absolute top-4 right-4 bg-blue-600 hover:bg-blue-700 text-white px-3 py-2 rounded-lg shadow-lg cursor-pointer transition-colors z-10"
                                onClick={handleMapClick}
                            >
                                <span className="text-sm font-medium">📍 Xem bản đồ lớn</span>
                            </div>
                        )}
                    </div>

                    {/* Nearby Attractions */}
                    <div className="space-y-6">
                        {/* Location Highlights */}
                        <div className="bg-gradient-to-br from-blue-50 to-indigo-50 rounded-xl p-6 border border-blue-100">
                            <div className="flex items-center gap-3 mb-4">
                                <div className="bg-blue-600 p-2 rounded-lg">
                                    <MapPin className="text-white" size={20} />
                                </div>
                                <h3 className="font-bold text-lg text-gray-800">Điểm nổi bật vị trí</h3>
                            </div>
                            <div className="space-y-3">
                                <div className="flex items-center gap-2">
                                    <div className="w-2 h-2 bg-blue-600 rounded-full"></div>
                                    <span className="text-sm text-gray-700">Nằm ngay trung tâm thành phố</span>
                                </div>
                                <div className="flex items-center gap-2">
                                    <div className="w-2 h-2 bg-green-600 rounded-full"></div>
                                    <span className="text-sm text-gray-700">Gần nhiều điểm tham quan nổi tiếng</span>
                                </div>
                                <div className="flex items-center gap-2">
                                    <div className="w-2 h-2 bg-orange-600 rounded-full"></div>
                                    <span className="text-sm text-gray-700">Dễ dàng di chuyển bằng phương tiện công cộng</span>
                                </div>
                            </div>
                        </div>

                        {/* Nearby Attractions */}
                        <div className="bg-white rounded-xl p-6 border border-gray-200 shadow-sm">
                            <h3 className="font-bold text-lg text-gray-800 mb-4">Điểm tham quan gần đây</h3>
                            <div className="space-y-3">
                                {attractions.map((attraction, index) => (
                                    <div key={index} className="flex items-center justify-between p-3 hover:bg-gray-50 rounded-lg transition-colors">
                                        <div className="flex-1">
                                            <div className="font-medium text-gray-800">{attraction.name}</div>
                                            <div className="text-sm text-gray-500">{attraction.type}</div>
                                        </div>
                                        <div className="text-sm font-medium text-blue-600 ml-4">
                                            {attraction.distance}
                                        </div>
                                    </div>
                                ))}
                            </div>
                        </div>

                        {/* Transportation Info */}
                        {/* <div className="bg-green-50 rounded-xl p-6 border border-green-100">
                            <h3 className="font-bold text-lg text-gray-800 mb-4">Thông tin di chuyển</h3>
                            <div className="space-y-3">
                                <div className="flex items-start gap-3">
                                    <div className="bg-green-600 p-1 rounded">
                                        <div className="w-2 h-2 bg-white rounded-full"></div>
                                    </div>
                                    <div>
                                        <div className="font-medium text-gray-800">Từ sân bay</div>
                                        <div className="text-sm text-gray-600">Taxi: 45-60 phút (tùy thuộc vào giao thông)</div>
                                    </div>
                                </div>
                                <div className="flex items-start gap-3">
                                    <div className="bg-blue-600 p-1 rounded">
                                        <div className="w-2 h-2 bg-white rounded-full"></div>
                                    </div>
                                    <div>
                                        <div className="font-medium text-gray-800">Giao thông công cộng</div>
                                        <div className="text-sm text-gray-600">Dễ dàng di chuyển bằng xe buýt và taxi</div>
                                    </div>
                                </div>
                                <div className="flex items-start gap-3">
                                    <div className="bg-orange-600 p-1 rounded">
                                        <div className="w-2 h-2 bg-white rounded-full"></div>
                                    </div>
                                    <div>
                                        <div className="font-medium text-gray-800">Đi bộ</div>
                                        <div className="text-sm text-gray-600">Nhiều điểm tham quan trong bán kính đi bộ</div>
                                    </div>
                                </div>
                            </div>
                        </div> */}

                        {/* Coordinates */}
                        {/* {location && (
                            <div className="bg-gray-50 rounded-xl p-4 border border-gray-200">
                                <h4 className="font-medium text-gray-800 mb-2">Tọa độ GPS</h4>
                                <div className="text-sm text-gray-600 space-y-1">
                                    <div>Vĩ độ: {location.latitude.toFixed(6)}</div>
                                    <div>Kinh độ: {location.longitude.toFixed(6)}</div>
                                </div>
                                <button
                                    onClick={() => {
                                        const coords = `${location.latitude},${location.longitude}`;
                                        navigator.clipboard.writeText(coords);
                                        // You could add a toast notification here
                                    }}
                                    className="mt-2 text-xs text-blue-600 hover:text-blue-700 font-medium"                                >
                                    Sao chép tọa độ
                                </button>
                            </div>
                        )} */}
                    </div>
                </div>

                {/* Map Modal */}
                <MapModal
                    isOpen={isMapModalOpen}
                    onClose={() => setIsMapModalOpen(false)}
                    location={location}
                    hotelName={hotelName}
                    address={address}
                />
            </div>
        );
    }
);

HotelLocationSection.displayName = 'HotelLocationSection';

export default HotelLocationSection;
