"use client";

import { forwardRef } from 'react';
import { Wifi, Car, Users, Clock, Utensils, GlassWater, Sunrise } from 'lucide-react';

// Interface for popular amenity
interface PopularAmenity {
    name: string;
    icon: React.ReactNode;
}

// Props interface
interface HotelDescriptionProps {
    hotelName?: string;
    geniusEligible?: boolean;
    description?: string[];
    popularAmenities?: PopularAmenity[];
    showMoreText?: string;
    onShowMore?: () => void;
}

// Default popular amenities
const defaultPopularAmenities: PopularAmenity[] = [
    { name: "WiFi miễn phí", icon: <Wifi className="text-green-600" size={20} /> },
    { name: "Xe đưa đón sân bay", icon: <Car className="text-green-600" size={20} /> },
    { name: "Phòng gia đình", icon: <Users className="text-green-600" size={20} /> },
    { name: "Lễ tân 24 giờ", icon: <Clock className="text-green-600" size={20} /> },
    { name: "Nhà hàng", icon: <Utensils className="text-green-600" size={20} /> },
    { name: "Quầy bar", icon: <GlassWater className="text-green-600" size={20} /> },
    { name: "Bữa sáng rất tốt", icon: <Sunrise className="text-green-600" size={20} /> }
];

// Default description paragraphs
const defaultDescription = [
    "La Passion Classic Hotel cung cấp chỗ nghỉ tại trung tâm Khu Phố Cổ của thành phố Hà Nội, cách Chợ Đồng Xuân 300 m. Khách có thể dùng bữa tại nhà hàng trong khuôn viên hay nhâm nhi đồ uống ở quầy bar. Wi-Fi miễn phí có sẵn trong tất cả các phòng nghỉ.",
    "La Passion Classic Hotel nằm trong bán kính 600 m từ Ô Quan Chưởng và 1,1 km từ Đền Ngọc Sơn. Sân bay Quốc tế Nội Bài cách đó 20 km.",
    "Các phòng tại đây được trang bị TV màn hình phẳng và ấm đun nước. Phòng còn đi kèm phòng tắm riêng với bồn tắm. Khách sạn cung cấp áo choàng tắm và đồ vệ sinh cá nhân miễn phí.",
    "Du khách có thể đến lễ tân 24 giờ để được hỗ trợ với dịch vụ thu đổi ngoại tệ, sắp xếp tour du lịch, đặt vé cũng như trợ giúp đặc biệt. Khách sạn cho thuê xe hơi và xe đạp để du khách đi khám phá khu vực xung quanh."
];

const HotelDescription = forwardRef<HTMLDivElement, HotelDescriptionProps>(
    ({
        hotelName = "La Passion Classic Hotel",
        geniusEligible = true,
        description = defaultDescription,
        popularAmenities = defaultPopularAmenities,
        showMoreText = "Tôi muốn xem thêm",
        onShowMore
    }, ref) => {
        return (
            <div ref={ref} className="col-span-2">
                {/* Genius Discount Banner */}
                {geniusEligible && (
                    <div className="bg-yellow-50 border border-yellow-200 p-4 rounded mb-6">
                        <p className="text-sm">
                            Bạn có thể đủ điều kiện hưởng giảm giá Genius tại {hotelName}. Để biết giảm giá Genius có áp dụng cho ngày bạn đã chọn hay không, hãy{' '}
                            <a href="#" className="text-blue-600 font-medium">đăng nhập</a>.
                        </p>
                    </div>
                )}

                {/* Hotel Description Paragraphs */}
                <div className="mb-6">
                    {description.map((paragraph, index) => (
                        <p key={index} className="mb-4">
                            {paragraph}
                        </p>
                    ))}
                </div>

                {/* Show More Button */}
                {onShowMore && (
                    <button 
                        onClick={onShowMore}
                        className="text-blue-600 font-medium hover:underline mb-8"
                    >
                        {showMoreText}
                    </button>
                )}

                {/* Popular Amenities */}
                <div className="mt-8">
                    <h3 className="font-bold text-lg mb-4">Các tiện nghi được ưa chuộng nhất</h3>
                    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
                        {popularAmenities.map((amenity, index) => (
                            <div key={index} className="flex items-center gap-2">
                                {amenity.icon}
                                <span className="text-sm">{amenity.name}</span>
                            </div>
                        ))}
                    </div>
                </div>
            </div>
        );
    }
);

HotelDescription.displayName = 'HotelDescription';

export default HotelDescription;
