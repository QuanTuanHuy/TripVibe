"use client";

import { forwardRef } from 'react';
import {
    Check,
    X,
    Wifi,
    Car,
    Users,
    Clock,
    Utensils,
    GlassWater,
    Sunrise
} from 'lucide-react';

// Interface for individual amenity item
interface AmenityItem {
    name: string;
    available: boolean;
    hasCharge?: boolean;
}

// Interface for amenity category
interface AmenityCategory {
    title: string;
    icon: React.ReactNode;
    items: AmenityItem[];
}

// Interface for popular amenities
interface PopularAmenity {
    name: string;
    icon: React.ReactNode;
}

// Props interface
interface DetailedAmenitiesProps {
    hotelName?: string;
    rating?: number;
    popularAmenities?: PopularAmenity[];
    amenityCategories?: AmenityCategory[];
}

// Default data
const defaultPopularAmenities: PopularAmenity[] = [
    { name: "Xe đưa đón sân bay", icon: <Car className="text-green-600" size={20} /> },
    { name: "Phòng không hút thuốc", icon: <Wifi className="text-green-600" size={20} /> },
    { name: "Trung tâm Spa & chăm sóc sức khỏe", icon: <Utensils className="text-green-600" size={20} /> },
    { name: "Dịch vụ phòng", icon: <Users className="text-green-600" size={20} /> },
    { name: "WiFi miễn phí", icon: <Wifi className="text-green-600" size={20} /> },
    { name: "Nhà hàng", icon: <Utensils className="text-green-600" size={20} /> },
    { name: "Phòng gia đình", icon: <Users className="text-green-600" size={20} /> },
    { name: "Lễ tân 24 giờ", icon: <Clock className="text-green-600" size={20} /> },
    { name: "Quầy bar", icon: <GlassWater className="text-green-600" size={20} /> },
    { name: "Bữa sáng rất tốt", icon: <Sunrise className="text-green-600" size={20} /> }
];

const defaultAmenityCategories: AmenityCategory[] = [
    {
        title: "Phòng tắm",
        icon: <GlassWater size={20} className="text-gray-600" />,
        items: [
            { name: "Giấy vệ sinh", available: true },
            { name: "Khăn tắm", available: true },
            { name: "Chậu rửa vệ sinh (bidet)", available: true },
            { name: "Khăn tắm/Bộ khăn trải giường", available: true, hasCharge: true },
            { name: "Bồn tắm hoặc Vòi sen", available: true },
            { name: "Dép", available: true },
            { name: "Phòng tắm riêng", available: true },
            { name: "Nhà vệ sinh", available: true },
            { name: "Đồ vệ sinh cá nhân miễn phí", available: true },
            { name: "Áo choàng tắm", available: true },
            { name: "Máy sấy tóc", available: true },
            { name: "Vòi sen", available: true }
        ]
    },
    {
        title: "Truyền thông & Công nghệ",
        icon: <Clock size={20} className="text-gray-600" />,
        items: [
            { name: "TV màn hình phẳng", available: true },
            { name: "Điện thoại", available: true },
            { name: "TV", available: true }
        ]
    },
    {
        title: "Đồ ăn & thức uống",
        icon: <Utensils size={20} className="text-gray-600" />,
        items: [
            { name: "Quán cà phê (trong khuôn viên)", available: true },
            { name: "Rượu vang/sâm panh", available: true, hasCharge: true },
            { name: "Bữa ăn tự chọn phù hợp với trẻ em", available: true },
            { name: "Bữa ăn trẻ em", available: true, hasCharge: true },
            { name: "Quầy bar (đồ ăn nhẹ)", available: true },
            { name: "Quầy bar", available: true },
            { name: "Minibar", available: true },
            { name: "Nhà hàng", available: true }
        ]
    },
    {
        title: "Internet",
        icon: <Wifi size={20} className="text-gray-600" />,
        items: [
            { name: "Wi-fi có ở toàn bộ khách sạn và miễn phí.", available: true }
        ]
    },
    {
        title: "Chỗ đậu xe",
        icon: <Car size={20} className="text-gray-600" />,
        items: [
            { name: "Không có chỗ đỗ xe.", available: false }
        ]
    },
    {
        title: "Phương tiện đi lại",
        icon: <Car size={20} className="text-gray-600" />,
        items: [
            { name: "Vé đi phương tiện công cộng", available: true, hasCharge: true }
        ]
    },
    {
        title: "Dịch vụ lễ tân",
        icon: <Users size={20} className="text-gray-600" />,
        items: [
            { name: "Có xuất hóa đơn", available: true }
        ]
    },
    {
        title: "An ninh",
        icon: <Clock size={20} className="text-gray-600" />,
        items: [
            { name: "Bình chữa cháy", available: true },
            { name: "Hệ thống CCTV bên ngoài chỗ nghỉ", available: true },
            { name: "Hệ thống CCTV trong khu vực chung", available: true },
            { name: "Thiết bị báo cháy", available: true },
            { name: "Báo động an ninh", available: true },
            { name: "Ổ khóa mở bằng thẻ", available: true },
            { name: "Ổ khóa", available: true },
            { name: "Bảo vệ 24/7", available: true },
            { name: "Két an toàn", available: true }
        ]
    },
    {
        title: "Tổng quát",
        icon: <Clock size={20} className="text-gray-600" />,
        items: [
            { name: "Dịch vụ đưa đón", available: true, hasCharge: true },
            { name: "Thiết bị báo carbon monoxide", available: true },
            { name: "Giao nhận đồ tạp hóa", available: true },
            { name: "Khu vực xem TV/sảnh chung", available: true },
            { name: "Không gây dị ứng", available: true },
            { name: "Khu vực cho phép hút thuốc", available: true },
            { name: "Điều hòa nhiệt độ", available: true },
            { name: "Phòng không gây dị ứng", available: true },
            { name: "Dịch vụ báo thức", available: true },
            { name: "Sân lát gỗ", available: true },
            { name: "Hệ thống sưởi", available: true },
            { name: "Hệ thống cách âm", available: true }
        ]
    },
    {
        title: "Phòng ngủ",
        icon: <Clock size={20} className="text-gray-600" />,
        items: [
            { name: "Ra trải giường", available: true },
            { name: "Tủ hoặc phòng để quần áo", available: true },
            { name: "Đồng hồ báo thức", available: true }
        ]
    },
    {
        title: "Ngoài trời",
        icon: <Utensils size={20} className="text-gray-600" />,
        items: [
            { name: "Bàn ghế ngoài trời", available: true },
            { name: "Sân hiên phơi nắng", available: true },
            { name: "Sân thượng / hiên", available: true }
        ]
    }
];

const DetailedAmenities = forwardRef<HTMLDivElement, DetailedAmenitiesProps>(
    ({
        hotelName = "La Passion Hanoi Hotel & Spa",
        rating = 9.2,
        popularAmenities = defaultPopularAmenities,
        amenityCategories = defaultAmenityCategories
    }, ref) => {
        return (
            <div ref={ref} className="mt-12">
                <h2 className="text-2xl font-bold mb-6">Các tiện nghi của {hotelName}</h2>
                <p className="text-base mb-6">Tiện nghi tuyệt với! Điểm đánh giá: {rating}</p>

                <h3 className="text-lg font-medium mb-4">Các tiện nghi được ưa chuộng nhất</h3>
                <div className="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-5 gap-4 mb-8">
                    {popularAmenities.map((amenity, index) => (
                        <div key={index} className="flex items-center gap-2">
                            {amenity.icon}
                            <span className="text-sm">{amenity.name}</span>
                        </div>
                    ))}
                </div>

                <div className="columns-1 md:columns-2 lg:columns-3 gap-8 space-y-0">
                    {amenityCategories.map((category, categoryIndex) => (
                        <div key={categoryIndex} className="break-inside-avoid mb-8">
                            <h3 className="font-medium text-lg flex items-center mb-4">
                                <span className="p-2 mr-3 rounded-full">
                                    {category.icon}
                                </span>
                                {category.title}
                            </h3>
                            <ul className="space-y-3">
                                {category.items.map((item, itemIndex) => (
                                    <li key={itemIndex} className="flex items-start gap-2">
                                        <div className="flex-shrink-0 mt-0.5">
                                            {item.available ? (
                                                <Check size={16} className="text-green-600" />
                                            ) : (
                                                <X size={16} className="text-red-500" />
                                            )}
                                        </div>
                                        <span className="text-sm leading-6">
                                            {item.name}
                                            {item.hasCharge && (
                                                <span className="text-gray-500 text-xs ml-1">(Phụ phí)</span>
                                            )}
                                        </span>
                                    </li>
                                ))}
                            </ul>
                        </div>
                    ))}
                </div>
            </div>
        );
    }
);

DetailedAmenities.displayName = 'DetailedAmenities';

export default DetailedAmenities;
