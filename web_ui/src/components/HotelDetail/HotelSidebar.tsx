"use client";

import Image from 'next/image';
import { MapPin, Coffee } from 'lucide-react';
import HotelMap from './HotelMap';
import MapModal from './MapModal';
import { Location } from '@/types/location';
import { useState } from 'react';

// Interface for hotel rating info
interface HotelRating {
    score: number;
    label: string;
    reviewCount: number;
}

// Interface for guest review
interface GuestReview {
    name: string;
    country: string;
    flagSrc?: string;
    comment: string;
    avatar: string;
}

interface StaffRating {
    label: string;
    score: number;
}

interface HighlightPoint {
    icon: React.ReactNode;
    text: string;
}

interface HotelSidebarProps {
    rating?: HotelRating;
    guestReview?: GuestReview;
    staffRating?: StaffRating;
    highlights?: HighlightPoint[];
    breakfastInfo?: string;
    location?: Location;
    hotelName?: string;
    onShowMap?: () => void;
}

// Default data
const defaultRating: HotelRating = {
    score: 9.1,
    label: "Tuyệt hảo",
    reviewCount: 689
};

const defaultGuestReview: GuestReview = {
    name: "Thi",
    country: "Việt Nam",
    flagSrc: "/api/placeholder/20/15",
    comment: "Khách sạn vị trí thuận lợi, bữa sáng đầy đủ nhân viên nhiệt tình Linh",
    avatar: "T"
};

const defaultStaffRating: StaffRating = {
    label: "Nhân viên phục vụ",
    score: 9.5
};

const defaultHighlights: HighlightPoint[] = [
    {
        icon: <MapPin size={20} className="text-gray-600 shrink-0 mt-1" />,
        text: "Nằm ngay trung tâm Hà Nội, khách sạn này có điểm vị trí tuyệt với 9,5"
    },
    {
        icon: <Coffee size={20} className="text-gray-600 shrink-0 mt-1" />,
        text: "Bạn muốn ngủ thật ngon? Khách sạn này được đánh giá cao nhờ những chiếc giường thoải mái."
    }
];

const HotelSidebar: React.FC<HotelSidebarProps> = ({
    rating = defaultRating,
    guestReview = defaultGuestReview,
    staffRating = defaultStaffRating,
    highlights = defaultHighlights,
    breakfastInfo = "Kiểu lục địa, Tự chọn, Bữa sáng mang đi",
    location,
    hotelName = "Khách sạn",
}) => {
    const [isMapModalOpen, setIsMapModalOpen] = useState(false);
    return (
        <div>
            <div className="bg-gray-50 border border-gray-200 rounded p-4">
                {/* Rating Section */}
                <div className="flex justify-between items-center mb-4">
                    <div>
                        <h3 className="font-bold">{rating.label}</h3>
                        <p className="text-sm text-gray-600">{rating.reviewCount} đánh giá thật</p>
                    </div>
                    <div className="bg-blue-700 text-white font-bold text-lg px-2 py-1 rounded">
                        {rating.score}
                    </div>
                </div>

                {/* Guest Review Section */}
                <div className="mb-4">
                    <h4 className="font-bold mb-2">Khách lưu trú ở đây thích điều gì?</h4>
                    <p className="text-sm italic">
                        &quot;{guestReview.comment}&quot;
                    </p>
                </div>

                {/* Guest Info */}
                <div className="flex items-center gap-2 mb-4">
                    <div className="w-8 h-8 bg-orange-500 rounded-full flex items-center justify-center text-white font-bold">
                        {guestReview.avatar}
                    </div>
                    <span>{guestReview.name}</span>
                    <div className="flex items-center">
                        {guestReview.flagSrc && (
                            <div className="relative w-[20px] h-[15px] mr-1">
                                <Image
                                    src={guestReview.flagSrc}
                                    alt={`${guestReview.country} flag`}
                                    fill
                                    className="object-contain"
                                />
                            </div>
                        )}
                        <span className="text-sm">{guestReview.country}</span>
                    </div>
                </div>

                {/* Staff Rating */}
                <div className="flex justify-between items-center mb-2">
                    <p className="font-bold">{staffRating.label}</p>
                    <div className="bg-green-700 text-white font-bold px-2 py-1 rounded">
                        {staffRating.score}
                    </div>
                </div>

                {/* Map Section */}
                <div className="mt-4"
                    onClick={() => setIsMapModalOpen(true)}
                >
                    <HotelMap
                        location={location}
                        hotelName={hotelName}
                        className="rounded-lg"
                        isModalOpen={isMapModalOpen}
                    />
                </div>

                {/* Highlights Section */}
                <div className="mt-4">
                    <h4 className="font-bold mb-2">Điểm nổi bật của chỗ nghỉ</h4>
                    {highlights.map((highlight, index) => (
                        <div key={index} className="flex items-start gap-2 mb-3 last:mb-0">
                            {highlight.icon}
                            <p className="text-sm">{highlight.text}</p>
                        </div>
                    ))}
                </div>

                {/* Breakfast Info */}
                {breakfastInfo && (
                    <div className="mt-4">
                        <h4 className="font-bold mb-2">Thông tin về bữa sáng</h4>
                        <p className="text-sm">{breakfastInfo}</p>
                    </div>
                )}
            </div>

            <MapModal
                isOpen={isMapModalOpen}
                onClose={() => setIsMapModalOpen(false)}
                location={location}
                hotelName={hotelName}
                address={''}
            />
        </div>
    );
};

export default HotelSidebar;
