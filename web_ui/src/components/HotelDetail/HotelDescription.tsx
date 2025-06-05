"use client";

import { forwardRef } from 'react';
import { Wifi, Car, Users, Clock, Utensils, GlassWater, Sunrise } from 'lucide-react';
import { Accommodation } from '@/types/accommodation/accommodation/accommodation.types';
import { getAmenityIcon } from '@/utils/amenityUtils';

interface PopularAmenity {
    name: string;
    icon: React.ReactNode;
}

interface HotelDescriptionProps {
    accommodation?: Accommodation;
    geniusEligible?: boolean;
    onShowMore?: () => void;
}

const defaultPopularAmenities: PopularAmenity[] = [
    { name: "WiFi miễn phí", icon: <Wifi className="text-green-600" size={20} /> },
    { name: "Xe đưa đón sân bay", icon: <Car className="text-green-600" size={20} /> },
    { name: "Phòng gia đình", icon: <Users className="text-green-600" size={20} /> },
    { name: "Lễ tân 24 giờ", icon: <Clock className="text-green-600" size={20} /> },
    { name: "Nhà hàng", icon: <Utensils className="text-green-600" size={20} /> },
    { name: "Quầy bar", icon: <GlassWater className="text-green-600" size={20} /> },
    { name: "Bữa sáng rất tốt", icon: <Sunrise className="text-green-600" size={20} /> }
];

const defaultDescription = [
    "La Passion Classic Hotel cung cấp chỗ nghỉ tại trung tâm Khu Phố Cổ của thành phố Hà Nội, cách Chợ Đồng Xuân 300 m. Khách có thể dùng bữa tại nhà hàng trong khuôn viên hay nhâm nhi đồ uống ở quầy bar. Wi-Fi miễn phí có sẵn trong tất cả các phòng nghỉ.",
    "La Passion Classic Hotel nằm trong bán kính 600 m từ Ô Quan Chưởng và 1,1 km từ Đền Ngọc Sơn. Sân bay Quốc tế Nội Bài cách đó 20 km.",
    "Các phòng tại đây được trang bị TV màn hình phẳng và ấm đun nước. Phòng còn đi kèm phòng tắm riêng với bồn tắm. Khách sạn cung cấp áo choàng tắm và đồ vệ sinh cá nhân miễn phí.",
    "Du khách có thể đến lễ tân 24 giờ để được hỗ trợ với dịch vụ thu đổi ngoại tệ, sắp xếp tour du lịch, đặt vé cũng như trợ giúp đặc biệt. Khách sạn cho thuê xe hơi và xe đạp để du khách đi khám phá khu vực xung quanh."
];

const HotelDescription = forwardRef<HTMLDivElement, HotelDescriptionProps>(
    ({
        accommodation,
        geniusEligible = true,
        onShowMore
    }, ref) => {
        // Extract popular amenities from accommodation data
        const getPopularAmenities = (): PopularAmenity[] => {
            if (!accommodation?.amenities || accommodation.amenities.length === 0) {
                return defaultPopularAmenities;
            }

            // Filter out amenities without valid names and create popular amenities list
            const validAmenities = accommodation.amenities
                .filter(amenityData => amenityData.amenity?.name)
                .slice(0, 7)
                .map(amenityData => ({
                    name: amenityData.amenity?.name || '',
                    icon: getAmenityIcon(amenityData.amenity?.name || '', amenityData.amenity?.icon)
                }));

            // If we have less than 4 amenities, supplement with default ones
            if (validAmenities.length < 4) {
                const existingNames = new Set(validAmenities.map(a => a.name.toLowerCase()));
                const supplementAmenities = defaultPopularAmenities
                    .filter(defaultAmenity => !existingNames.has(defaultAmenity.name.toLowerCase()))
                    .slice(0, 7 - validAmenities.length);

                return [...validAmenities, ...supplementAmenities];
            }

            return validAmenities;
        };

        // Process description - split by newlines or use default
        const getDescriptionParagraphs = (): string[] => {
            if (!accommodation?.description || accommodation.description.trim().length === 0) {
                console.log('Using default description - no accommodation description found');
                return defaultDescription;
            }

            // Clean up the description and split into paragraphs
            const cleanDescription = accommodation.description
                .replace(/\s+/g, ' ') // Replace multiple spaces with single space
                .trim();

            // Split description by newlines, double newlines, or sentence endings followed by capital letters
            const paragraphs = cleanDescription
                .split(/\n\s*\n|\n|\. (?=[A-ZÀÁÂÃÈÉÊÌÍÒÓÔÕÙÚÝ])/)
                .map(p => p.trim())
                .filter(p => p.length > 10)
                .map(p => {
                    // Ensure paragraph ends with proper punctuation
                    if (!p.match(/[.!?]$/)) {
                        return p + '.';
                    }
                    return p;
                });

            return paragraphs.length > 0 ? paragraphs : defaultDescription;
        };

        const popularAmenities = getPopularAmenities();
        const descriptionParagraphs = getDescriptionParagraphs();
        const hotelName = accommodation?.name || "Khách sạn";

        return (
            <div ref={ref} className="col-span-2">
                {/* Genius Discount Banner */}
                {geniusEligible && (
                    <div className="bg-gradient-to-r from-yellow-50 to-amber-50 border border-yellow-200 p-4 rounded-lg mb-6 shadow-sm">
                        <div className="flex items-start gap-3">
                            <div className="bg-yellow-400 text-yellow-900 text-xs font-bold px-2 py-1 rounded">
                                GENIUS
                            </div>
                            <p className="text-sm text-gray-700">
                                Bạn có thể đủ điều kiện hưởng giảm giá Genius tại <span className="font-semibold">{hotelName}</span>.
                                Để biết giảm giá Genius có áp dụng cho ngày bạn đã chọn hay không, hãy{' '}
                                <a href="#" className="text-blue-600 font-medium hover:underline">đăng nhập</a>.
                            </p>
                        </div>
                    </div>
                )}

                {/* Hotel Description */}
                <div className="mb-8">
                    <h2 className="text-xl font-bold mb-4 text-gray-800">Mô tả {hotelName}</h2>
                    <div className="space-y-4">
                        {descriptionParagraphs.map((paragraph, index) => (
                            <p key={index} className="text-gray-700 leading-relaxed">
                                {paragraph.endsWith('.') ? paragraph : paragraph + '.'}
                            </p>
                        ))}
                    </div>
                </div>

                {/* Show More Button */}
                {onShowMore && (
                    <button
                        onClick={onShowMore}
                        className="text-blue-600 font-medium hover:underline hover:text-blue-700 transition-colors mb-8"
                    >
                        Tôi muốn xem thêm về {hotelName}
                    </button>
                )}

                {/* Popular Amenities */}
                <div className="mt-8">
                    <h3 className="font-bold text-lg mb-6 text-gray-800">Các tiện nghi được ưa chuộng nhất</h3>                    <div className="bg-gradient-to-br from-blue-50 to-indigo-50 rounded-xl p-6 border border-blue-100">
                        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                            {popularAmenities.map((amenity, index) => (
                                <div key={index} className="flex items-center gap-3 p-3 hover:bg-white/60 rounded-lg transition-colors duration-200">
                                    <div className="flex-shrink-0">
                                        {amenity.icon}
                                    </div>
                                    <span className="text-sm font-medium text-gray-700">{amenity.name}</span>
                                </div>
                            ))}
                        </div>

                        {/* Show all amenities link */}
                        <div className="mt-4 pt-4 border-t border-blue-200">
                            <button
                                onClick={() => {
                                    // Scroll to amenities section
                                    const amenitiesSection = document.querySelector('[data-section="amenities"]');
                                    amenitiesSection?.scrollIntoView({ behavior: 'smooth' });
                                }}
                                className="text-blue-600 font-medium hover:underline text-sm"
                            >
                                Xem tất cả tiện nghi →
                            </button>
                        </div>
                    </div>
                </div>

                {/* Location highlight */}
                {
                    // accommodation?.location && (
                    //     <div className="mt-8 bg-green-50 rounded-xl p-6 border border-green-100">
                    //         <h4 className="font-bold text-lg mb-3 text-gray-800">Vị trí thuận lợi</h4>
                    //         <p className="text-gray-700 mb-3">
                    //             <span className="font-medium">{hotelName}</span> tọa lạc tại:{' '}
                    //             <span className="text-green-700 font-medium">
                    //                 {accommodation.location.detailAddress}
                    //             </span>
                    //         </p>
                    //         {/* Check-in/Check-out times */}
                    //         <div className="grid grid-cols-2 gap-4 mt-4">
                    //             <div className="flex items-center gap-2">
                    //                 <Clock size={16} className="text-green-600" />
                    //                 <div className="text-sm">
                    //                     <div className="font-medium text-gray-700">Nhận phòng:</div>
                    //                     <div className="text-gray-600">
                    //                         {accommodation.checkInTimeFrom !== undefined && accommodation.checkInTimeTo !== undefined
                    //                             ? `${String(accommodation.checkInTimeFrom).padStart(2, '0')}:00 - ${String(accommodation.checkInTimeTo).padStart(2, '0')}:00`
                    //                             : '14:00 - 22:00'
                    //                         }
                    //                     </div>
                    //                 </div>
                    //             </div>
                    //             <div className="flex items-center gap-2">
                    //                 <Clock size={16} className="text-green-600" />
                    //                 <div className="text-sm">
                    //                     <div className="font-medium text-gray-700">Trả phòng:</div>
                    //                     <div className="text-gray-600">
                    //                         {accommodation.checkOutTimeFrom !== undefined && accommodation.checkOutTimeTo !== undefined
                    //                             ? `${String(accommodation.checkOutTimeFrom).padStart(2, '0')}:00 - ${String(accommodation.checkOutTimeTo).padStart(2, '0')}:00`
                    //                             : '08:00 - 12:00'
                    //                         }
                    //                     </div>
                    //                 </div>
                    //             </div>
                    //         </div>
                    //     </div>
                    // )
                }
            </div>
        );
    }
);

HotelDescription.displayName = 'HotelDescription';

export default HotelDescription;
