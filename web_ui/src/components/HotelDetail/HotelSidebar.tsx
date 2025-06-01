"use client";

import Image from 'next/image';
import { MapPin, Coffee } from 'lucide-react';

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

// Interface for staff rating
interface StaffRating {
    label: string;
    score: number;
}

// Interface for highlight point
interface HighlightPoint {
    icon: React.ReactNode;
    text: string;
}

// Props interface
interface HotelSidebarProps {
    rating?: HotelRating;
    guestReview?: GuestReview;
    staffRating?: StaffRating;
    highlights?: HighlightPoint[];
    breakfastInfo?: string;
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
    onShowMap
}) => {
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
                <div className="mt-4">
                    <div className="w-full h-48 bg-blue-100 rounded mb-2">
                        <div className="h-full flex items-center justify-center text-blue-800">
                            Bản đồ
                        </div>
                    </div>
                    <button
                        onClick={onShowMap}
                        className="w-full text-center bg-blue-600 text-white py-2 rounded font-medium hover:bg-blue-700 transition-colors"
                    >
                        Hiển thị trên bản đồ
                    </button>
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
        </div>
    );
};

export default HotelSidebar;
