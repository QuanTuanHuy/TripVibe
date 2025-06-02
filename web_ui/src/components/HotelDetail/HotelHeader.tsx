"use client";

import { MapPin, Heart, Share, Star } from 'lucide-react';

interface HotelHeaderProps {
    name: string;
    address: string;
    rating: number;
    reviewScore: number;
    hasAirportShuttle?: boolean;
    onShowMap?: () => void;
    onToggleFavorite?: () => void;
    onShare?: () => void;
    onBookNow?: () => void;
    isFavorite?: boolean;
}

export default function HotelHeader({
    name,
    address,
    rating,
    reviewScore,
    hasAirportShuttle = false,
    onShowMap,
    onToggleFavorite,
    onShare,
    onBookNow,
    isFavorite = false
}: HotelHeaderProps) {
    return (
        <div className="mt-6 flex justify-between items-start">
            <div>
                <div className="flex items-center gap-2 mb-3">
                    <div className="flex">
                        {[...Array(rating)].map((_, star) => (
                            <Star key={star} fill="#FFB700" stroke="#FFB700" size={20} />
                        ))}
                    </div>
                    <div className="bg-blue-500 text-white px-2 py-1 text-xs font-bold rounded">
                        {reviewScore}
                    </div>
                    {hasAirportShuttle && (
                        <span className="text-gray-600 text-sm">Xe đưa đón sân bay</span>
                    )}
                </div>
                <h1 className="text-3xl font-bold mb-4">{name}</h1>                <div className="flex items-start gap-2 text-sm">
                    <MapPin className="text-blue-600 mt-0.5" size={18} />
                    <div>
                        <p>{address}</p>
                        <button
                            className="text-blue-600 font-medium hover:underline"
                            onClick={onShowMap}
                        >
                            Vị trí xuất sắc - hiển thị bản đồ
                        </button>
                    </div>
                </div>
            </div>            <div className="flex gap-2">
                <button
                    className={`p-2 border border-gray-300 rounded-full hover:bg-gray-50 transition-colors ${isFavorite ? 'text-red-500' : 'text-gray-600'
                        }`}
                    onClick={onToggleFavorite}
                    title={isFavorite ? 'Xóa khỏi yêu thích' : 'Thêm vào yêu thích'}
                >
                    <Heart size={20} className={isFavorite ? 'fill-current' : ''} />
                </button>
                <button
                    className="p-2 border border-gray-300 rounded-full hover:bg-gray-50 transition-colors"
                    onClick={onShare}
                    title="Chia sẻ"
                >
                    <Share size={20} className="text-gray-600" />
                </button>
                <button
                    className="px-6 py-2 bg-blue-600 text-white font-medium rounded hover:bg-blue-700 transition-colors"
                    onClick={onBookNow}
                >
                    Đặt ngay
                </button>
            </div>
        </div>
    );
}
