"use client";

import { useState, useEffect } from 'react';
import Image from 'next/image';
import {
    X,
    Check,
    Coffee,
    Users,
    ChevronDown,
    Plus,
    Minus,
    Bed,
    Maximize2,
    ChevronLeft,
    ChevronRight,
} from 'lucide-react';

// Interface definitions
interface Bed {
    type: string;
    count: number;
}

interface Occupancy {
    adults: number;
    children: number;
}

interface Room {
    id: number;
    name: string;
    size: string;
    beds: Bed[];
    occupancy: Occupancy;
    amenities: string[];
    breakfast: boolean;
    freeCancellation: boolean;
    prepayment: boolean;
    price: number;
    remainingRooms?: number;
    images: string[];
    specialOffers?: string[];
}

interface AvailableRoomsProps {
    rooms: Room[];
    onShowRoomDetails?: (roomId: number) => void;
}

// Format price function
const formatPrice = (price: number): string => {
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
    }).format(price);
};

export default function AvailableRooms({ rooms, onShowRoomDetails }: AvailableRoomsProps) {
    const [roomQuantities, setRoomQuantities] = useState<{ [key: number]: number }>({});
    const [showRoomDropdown, setShowRoomDropdown] = useState<{ [key: number]: boolean }>({});
    const [selectedRoomDetails, setSelectedRoomDetails] = useState<number | null>(null);
    const [selectedImageIndex, setSelectedImageIndex] = useState<{ [key: number]: number }>({});

    // Calculate total price for selected rooms
    const calculateTotalPrice = () => {
        return Object.entries(roomQuantities).reduce((total, [roomId, quantity]) => {
            const room = rooms.find(r => r.id === parseInt(roomId));
            return total + (room ? room.price * quantity : 0);
        }, 0);
    };

    // Get total number of selected rooms
    const getTotalSelectedRooms = () => {
        return Object.values(roomQuantities).reduce((total, quantity) => total + quantity, 0);
    };

    // Check if any rooms are selected
    const hasSelectedRooms = () => {
        return Object.values(roomQuantities).some(quantity => quantity > 0);
    };

    // Handle room quantity changes
    const handleRoomQuantityChange = (roomId: number, newQuantity: number) => {
        if (newQuantity < 0) return;
        setRoomQuantities(prev => ({
            ...prev,
            [roomId]: newQuantity
        }));
    };

    // Toggle room dropdown
    const toggleRoomDropdown = (roomId: number) => {
        setShowRoomDropdown(prev => ({
            ...prev,
            [roomId]: !prev[roomId]
        }));
    };

    // Handle show room details
    const handleShowRoomDetailsLocal = (roomId: number) => {
        setSelectedRoomDetails(roomId);
        // Reset selected image index when opening modal
        setSelectedImageIndex(prev => ({ ...prev, [roomId]: 0 }));
        if (onShowRoomDetails) {
            onShowRoomDetails(roomId);
        }
    };

    // Handle image selection in modal
    const handleImageSelect = (roomId: number, imageIndex: number) => {
        setSelectedImageIndex(prev => ({
            ...prev,
            [roomId]: imageIndex
        }));
    };

    // Close room details modal
    const handleCloseRoomDetails = () => {
        setSelectedRoomDetails(null);
    };

    // Close dropdowns when clicking outside
    useEffect(() => {
        const handleClickOutside = (event: MouseEvent) => {
            const target = event.target as HTMLElement;
            if (!target.closest('.room-quantity-dropdown')) {
                setShowRoomDropdown({});
            }
        };

        document.addEventListener('mousedown', handleClickOutside);
        return () => {
            document.removeEventListener('mousedown', handleClickOutside);
        };
    }, []);

    return (
        <>
            {/* Enhanced Room Display with Booking.com Style */}
            <div className="space-y-6">
                {rooms.map((room) => (
                    <div key={room.id} className="border border-gray-200 rounded-lg overflow-hidden shadow-sm">
                        {/* Room Header with Room Name */}
                        <div className="bg-blue-50 p-4 border-b border-gray-200">
                            <h3 className="text-lg font-bold text-blue-600">{room.name}</h3>
                        </div>

                        {/* Room Content */}
                        <div className="p-0">
                            <div className="flex flex-col md:flex-row">
                                {/* Room Image */}
                                <div className="md:w-1/4 p-4">
                                    <div className="relative h-48 w-full rounded-md overflow-hidden">
                                        <Image
                                            src={room.images[0]}
                                            alt={room.name}
                                            fill
                                            sizes="(max-width: 768px) 100vw, 25vw"
                                            className="object-cover rounded-md"
                                            priority={true}
                                        />
                                    </div>
                                </div>

                                {/* Room Details */}
                                <div className="md:w-2/4 p-4 border-t md:border-t-0 md:border-l md:border-r border-gray-200">
                                    {/* Room Size and Bed Info */}
                                    <div className="flex items-center gap-3 mb-3">
                                        <div className="flex items-center gap-1 text-gray-600">
                                            <Maximize2 size={16} />
                                            <span>{room.size}</span>
                                        </div>
                                        <div className="flex items-center gap-1 text-gray-600">
                                            <Bed size={16} />
                                            <span>
                                                {room.beds.map((bed, index) => (
                                                    <span key={index}>
                                                        {bed.count} {bed.type}{index < room.beds.length - 1 ? ', ' : ''}
                                                    </span>
                                                ))}
                                            </span>
                                        </div>
                                    </div>

                                    {/* Occupancy */}
                                    <div className="mb-3">
                                        <div className="flex items-center gap-2">
                                            <Users size={16} className="text-gray-600" />
                                            <span className="text-gray-600">
                                                {room.occupancy.adults} người lớn
                                                {room.occupancy.children > 0 ? `, ${room.occupancy.children} trẻ em` : ''}
                                            </span>
                                        </div>
                                    </div>

                                    {/* Amenities */}
                                    <div className="mb-4">
                                        <h4 className="text-sm font-medium mb-2">Tiện nghi phòng:</h4>
                                        <div className="grid grid-cols-2 gap-2">
                                            {room.amenities.slice(0, 6).map((amenity, index) => (
                                                <div key={index} className="flex items-center gap-2 text-sm">
                                                    <Check size={16} className="text-green-500" />
                                                    <span>{amenity}</span>
                                                </div>
                                            ))}
                                        </div>
                                        {room.amenities.length > 6 && (
                                            <button className="text-blue-600 text-sm font-medium mt-2">
                                                + {room.amenities.length - 6} tiện nghi khác
                                            </button>
                                        )}
                                    </div>

                                    {/* Meal Information */}
                                    {room.breakfast && (
                                        <div className="flex items-center gap-2 text-sm mb-3">
                                            <div className="flex items-center gap-2 text-green-700 bg-green-50 px-3 py-1 rounded-full">
                                                <Coffee size={16} />
                                                <span>Bao gồm bữa sáng</span>
                                            </div>
                                        </div>
                                    )}

                                    {/* Cancellation Policy */}
                                    {room.freeCancellation && (
                                        <div className="flex items-center gap-2 text-sm text-green-700">
                                            <Check size={16} className="text-green-700" />
                                            <span>Miễn phí hủy</span>
                                        </div>
                                    )}                                    {/* Special Offers */}
                                    {room.specialOffers && room.specialOffers.length > 0 && (
                                        <div className="mt-3 p-3 bg-green-50 rounded-lg">
                                            <p className="text-sm font-medium text-green-700">Ưu đãi đặc biệt:</p>
                                            <ul className="list-disc list-inside text-sm text-green-700 mt-1">
                                                {room.specialOffers.map((offer, index) => (
                                                    <li key={index}>{offer}</li>
                                                ))}
                                            </ul>
                                        </div>
                                    )}
                                </div>

                                {/* Pricing and Booking */}
                                <div className="md:w-1/4 p-4 bg-blue-50 flex flex-col justify-between">
                                    <div>
                                        <p className="text-sm text-gray-600 mb-1">Giá cho 1 đêm</p>
                                        <p className="text-xl font-bold text-gray-900 mb-2">
                                            {formatPrice(room.price)}
                                        </p>
                                        <p className="text-xs text-gray-500 mb-3">Đã bao gồm thuế & phí</p>

                                        {/* Remaining Rooms */}
                                        {room.remainingRooms && room.remainingRooms <= 5 && (
                                            <div className="mb-3 text-sm text-red-600 font-medium">
                                                Chỉ còn {room.remainingRooms} phòng với giá này!
                                            </div>
                                        )}                                        {/* Room Quantity Selector */}
                                        <div className="mt-3 mb-4">
                                            <div className="relative room-quantity-dropdown">
                                                <button
                                                    onClick={() => toggleRoomDropdown(room.id)}
                                                    className="w-full flex items-center justify-between border border-gray-300 bg-white rounded px-3 py-2 text-sm"
                                                >
                                                    <span>Số lượng phòng: {roomQuantities[room.id] || 0}</span>
                                                    <ChevronDown size={16} className={`transition-transform ${showRoomDropdown[room.id] ? 'rotate-180' : ''}`} />
                                                </button>

                                                {showRoomDropdown[room.id] && (
                                                    <div className="absolute z-10 mt-1 w-full bg-white border border-gray-200 rounded shadow-lg">
                                                        <div className="p-2">
                                                            <div className="flex items-center justify-between">
                                                                <span className="text-sm">Chọn số lượng:</span>
                                                                <div className="flex items-center border border-gray-300 rounded">
                                                                    <button
                                                                        className="px-2 py-1 text-gray-600 hover:bg-gray-100"
                                                                        onClick={(e) => {
                                                                            e.stopPropagation();
                                                                            handleRoomQuantityChange(room.id, (roomQuantities[room.id] || 0) - 1);
                                                                        }}
                                                                    >
                                                                        <Minus size={14} />
                                                                    </button>
                                                                    <span className="px-3">{roomQuantities[room.id] || 0}</span>
                                                                    <button
                                                                        className="px-2 py-1 text-gray-600 hover:bg-gray-100"
                                                                        onClick={(e) => {
                                                                            e.stopPropagation();
                                                                            handleRoomQuantityChange(room.id, (roomQuantities[room.id] || 0) + 1);
                                                                        }}
                                                                    >
                                                                        <Plus size={14} />
                                                                    </button>
                                                                </div>
                                                            </div>

                                                            {roomQuantities[room.id] > 0 && (
                                                                <div className="text-sm text-green-700 font-medium mt-2">
                                                                    Tổng: {formatPrice(room.price * roomQuantities[room.id])}
                                                                </div>
                                                            )}
                                                        </div>
                                                    </div>
                                                )}
                                            </div>
                                        </div>
                                    </div>

                                    <div>                                        <button
                                        className={`w-full ${roomQuantities[room.id] > 0 ? 'bg-blue-600 hover:bg-blue-700' : 'bg-gray-400 cursor-not-allowed'} text-white font-medium rounded-md px-4 py-2 mb-2 transition-colors`}
                                        disabled={!roomQuantities[room.id]}
                                        onClick={() => {
                                            if (roomQuantities[room.id] > 0) {
                                                console.log(`Đặt ${roomQuantities[room.id]} phòng ${room.name} với giá ${formatPrice(room.price * roomQuantities[room.id])}`);
                                                // Thêm logic đặt phòng riêng cho từng loại phòng
                                            }
                                        }}
                                    >
                                        {roomQuantities[room.id] > 0
                                            ? `Đặt ${roomQuantities[room.id]} phòng - ${formatPrice(room.price * roomQuantities[room.id])}`
                                            : 'Chọn số lượng phòng'
                                        }
                                    </button>
                                        <button
                                            onClick={() => handleShowRoomDetailsLocal(room.id)}
                                            className="w-full border border-blue-600 text-blue-600 hover:bg-blue-50 font-medium rounded-md px-4 py-2"
                                        >
                                            Xem chi tiết phòng
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                ))}            </div>

            {/* Booking Summary - Only show when rooms are selected */}
            {hasSelectedRooms() && (
                <div className="mt-8 bg-blue-50 border border-blue-200 rounded-lg p-6">
                    <h3 className="text-lg font-bold text-blue-800 mb-4">Tóm tắt đặt phòng</h3>

                    {/* Selected Rooms List */}
                    <div className="space-y-3 mb-4">                        {Object.entries(roomQuantities)
                        .filter(([, quantity]) => quantity > 0)
                        .map(([roomId, quantity]) => {
                            const room = rooms.find(r => r.id === parseInt(roomId));
                            if (!room) return null;

                            return (
                                <div key={roomId} className="flex justify-between items-center bg-white p-3 rounded-md">
                                    <div>
                                        <span className="font-medium">{room.name}</span>
                                        <span className="text-gray-600 ml-2">x {quantity}</span>
                                    </div>
                                    <div className="text-right">
                                        <div className="font-medium">{formatPrice(room.price * quantity)}</div>
                                        <div className="text-sm text-gray-600">{formatPrice(room.price)}/phòng</div>
                                    </div>
                                </div>
                            );
                        })}
                    </div>

                    {/* Total Price */}
                    <div className="border-t border-blue-200 pt-4">
                        <div className="flex justify-between items-center mb-4">
                            <div>
                                <span className="text-lg font-bold">Tổng cộng: </span>
                                <span className="text-sm text-gray-600">({getTotalSelectedRooms()} phòng)</span>
                            </div>
                            <div className="text-2xl font-bold text-blue-800">
                                {formatPrice(calculateTotalPrice())}
                            </div>
                        </div>

                        {/* Book Now Button */}
                        <button
                            className="w-full bg-blue-600 hover:bg-blue-700 text-white font-bold py-3 px-6 rounded-lg transition-colors duration-200"
                            onClick={() => {
                                const selectedRooms = Object.entries(roomQuantities)
                                    .filter(([, quantity]) => quantity > 0)
                                    .map(([roomId, quantity]) => ({
                                        roomId: parseInt(roomId),
                                        quantity,
                                        room: rooms.find(r => r.id === parseInt(roomId))
                                    }));

                                console.log('Đặt phòng:', selectedRooms);
                                console.log('Tổng giá:', calculateTotalPrice());
                                // Thêm logic đặt phòng ở đây
                            }}
                        >
                            Đặt phòng ngay - {formatPrice(calculateTotalPrice())}
                        </button>
                    </div>
                </div>
            )}

            <div className="flex justify-center mt-8">
                <button className="bg-gray-600 text-white px-8 py-3 rounded font-medium hover:bg-gray-700">
                    Xem tất cả phòng trống
                </button>
            </div>

            {/* Modal Room Details - Enhanced with more images and better design */}
            {selectedRoomDetails !== null && (
                <div className="fixed inset-0 bg-gradient-to-br from-gray-600/80 to-gray-900/80 backdrop-blur-sm flex items-center justify-center z-50 p-4">
                    <div className="bg-white rounded-lg max-w-5xl w-full max-h-[90vh] overflow-y-auto shadow-2xl">
                        {rooms.filter(room => room.id === selectedRoomDetails).map(room => (
                            <div key={`detail-${room.id}`} className="relative">
                                {/* Close button */}
                                <button
                                    onClick={handleCloseRoomDetails}
                                    className="absolute right-4 top-4 bg-white/80 hover:bg-white backdrop-blur-sm rounded-full p-2 shadow-md z-20 transition-all duration-200"
                                >
                                    <X size={24} />
                                </button>                                {/* Room gallery - Featured image and thumbnails */}
                                <div className="bg-gray-100">                                    {/* Main image */}
                                    <div className="relative h-96 w-full group">
                                        <Image
                                            src={room.images[selectedImageIndex[room.id] || 0]}
                                            alt={room.name}
                                            fill
                                            sizes="(max-width: 1024px) 100vw, 1024px"
                                            className="object-cover"
                                            priority={true}
                                        />

                                        {/* Image counter */}
                                        <div className="absolute top-4 left-4 bg-black/50 text-white px-3 py-1.5 rounded-full text-sm font-medium">
                                            {(selectedImageIndex[room.id] || 0) + 1} / {room.images.length}
                                        </div>

                                        {/* Price tag */}
                                        <div className="absolute bottom-4 right-4 bg-blue-600 text-white px-3 py-1.5 rounded-full text-sm font-medium shadow-md">
                                            {formatPrice(room.price)}/đêm
                                        </div>

                                        {/* Navigation arrows - only show if more than 1 image */}
                                        {room.images.length > 1 && (
                                            <>
                                                {/* Previous button */}
                                                <button
                                                    className="absolute left-4 top-1/2 transform -translate-y-1/2 bg-white/80 hover:bg-white backdrop-blur-sm rounded-full p-2 shadow-md opacity-0 group-hover:opacity-100 transition-opacity duration-200"
                                                    onClick={() => {
                                                        const currentIndex = selectedImageIndex[room.id] || 0;
                                                        const newIndex = currentIndex === 0 ? room.images.length - 1 : currentIndex - 1;
                                                        handleImageSelect(room.id, newIndex);
                                                    }}
                                                >
                                                    <ChevronLeft size={20} />
                                                </button>

                                                {/* Next button */}
                                                <button
                                                    className="absolute right-4 top-1/2 transform -translate-y-1/2 bg-white/80 hover:bg-white backdrop-blur-sm rounded-full p-2 shadow-md opacity-0 group-hover:opacity-100 transition-opacity duration-200"
                                                    onClick={() => {
                                                        const currentIndex = selectedImageIndex[room.id] || 0;
                                                        const newIndex = currentIndex === room.images.length - 1 ? 0 : currentIndex + 1;
                                                        handleImageSelect(room.id, newIndex);
                                                    }}
                                                >
                                                    <ChevronRight size={20} />
                                                </button>
                                            </>
                                        )}
                                    </div>

                                    {/* Photo gallery row */}
                                    <div className="grid grid-cols-5 gap-1 p-1">
                                        {room.images.map((image, index) => (
                                            <div
                                                key={index}
                                                className={`relative h-24 cursor-pointer transition-all duration-200 ${(selectedImageIndex[room.id] || 0) === index
                                                    ? 'ring-2 ring-blue-500 opacity-100'
                                                    : 'hover:opacity-90 opacity-70'
                                                    }`}
                                                onClick={() => handleImageSelect(room.id, index)}
                                            >
                                                <Image
                                                    src={image}
                                                    alt={`${room.name} - Ảnh ${index + 1}`}
                                                    fill
                                                    className="object-cover rounded-sm"
                                                />
                                                {/* Active indicator */}
                                                {(selectedImageIndex[room.id] || 0) === index && (
                                                    <div className="absolute inset-0 bg-blue-500/20 rounded-sm"></div>
                                                )}
                                            </div>
                                        ))}
                                    </div>
                                </div>

                                <div className="p-8">
                                    <div className="border-b pb-6 mb-6">
                                        <h2 className="text-3xl font-bold mb-2">{room.name}</h2>
                                        <div className="flex flex-wrap items-center gap-4 mb-4">
                                            <div className="flex items-center gap-2 bg-blue-50 px-3 py-1 rounded-full">
                                                <Maximize2 size={18} className="text-blue-600" />
                                                <span className="font-medium">{room.size}</span>
                                            </div>
                                            <div className="flex items-center gap-2 bg-blue-50 px-3 py-1 rounded-full">
                                                <Bed size={18} className="text-blue-600" />
                                                <span className="font-medium">
                                                    {room.beds.map((bed, idx) => (
                                                        <span key={idx}>
                                                            {bed.count} {bed.type}{idx < room.beds.length - 1 ? ', ' : ''}
                                                        </span>
                                                    ))}
                                                </span>
                                            </div>
                                            <div className="flex items-center gap-2 bg-blue-50 px-3 py-1 rounded-full">
                                                <Users size={18} className="text-blue-600" />
                                                <span className="font-medium">
                                                    {room.occupancy.adults} người lớn
                                                    {room.occupancy.children > 0 ? `, ${room.occupancy.children} trẻ em` : ''}
                                                </span>
                                            </div>
                                        </div>

                                        <p className="text-gray-600">
                                            Phòng {room.name} rộng rãi và sang trọng, được thiết kế để mang lại sự thoải mái tối đa cho du khách. Với không gian thoáng đãng và đầy đủ tiện nghi, phòng này là lựa chọn hoàn hảo cho cả chuyến công tác và kỳ nghỉ thư giãn.
                                        </p>
                                    </div>

                                    <div className="grid grid-cols-1 lg:grid-cols-2 gap-8 mb-8">
                                        <div>
                                            <h3 className="text-xl font-semibold mb-4 flex items-center gap-2">
                                                <div className="bg-blue-100 p-2 rounded-full">
                                                    <Check size={20} className="text-blue-600" />
                                                </div>
                                                Tiện nghi phòng
                                            </h3>
                                            <div className="grid grid-cols-1 md:grid-cols-2 gap-3 bg-gray-50 p-4 rounded-lg">
                                                {room.amenities.map((amenity, idx) => (
                                                    <div key={idx} className="flex items-center gap-2 py-1">
                                                        <Check size={18} className="text-green-600" />
                                                        <span>{amenity}</span>
                                                    </div>
                                                ))}
                                            </div>
                                        </div>

                                        <div>
                                            <h3 className="text-xl font-semibold mb-4 flex items-center gap-2">
                                                <div className="bg-blue-100 p-2 rounded-full">
                                                    <Coffee size={20} className="text-blue-600" />
                                                </div>
                                                Dịch vụ bao gồm
                                            </h3>
                                            <div className="bg-gray-50 p-4 rounded-lg">
                                                <div className="mb-4">
                                                    <h4 className="font-medium mb-2">Bao gồm trong giá phòng:</h4>
                                                    <ul className="space-y-2">
                                                        {room.breakfast && (
                                                            <li className="flex items-center gap-2">
                                                                <Check size={18} className="text-green-600" />
                                                                <span>Bữa sáng buffet</span>
                                                            </li>
                                                        )}
                                                        {room.freeCancellation && (
                                                            <li className="flex items-center gap-2">
                                                                <Check size={18} className="text-green-600" />
                                                                <span>Miễn phí hủy phòng (trước 24 giờ)</span>
                                                            </li>
                                                        )}
                                                        {!room.prepayment && (
                                                            <li className="flex items-center gap-2">
                                                                <Check size={18} className="text-green-600" />
                                                                <span>Không cần thanh toán trước</span>
                                                            </li>
                                                        )}
                                                        <li className="flex items-center gap-2">
                                                            <Check size={18} className="text-green-600" />
                                                            <span>WiFi miễn phí tốc độ cao</span>
                                                        </li>
                                                    </ul>
                                                </div>
                                                {room.specialOffers && room.specialOffers.length > 0 && (
                                                    <div>
                                                        <h4 className="font-medium mb-2">Ưu đãi đặc biệt:</h4>
                                                        <ul className="space-y-2">
                                                            {room.specialOffers.map((offer, idx) => (
                                                                <li key={idx} className="flex items-center gap-2">
                                                                    <div className="text-green-100 bg-green-600 p-1 rounded-full">
                                                                        <Check size={14} />
                                                                    </div>
                                                                    <span>{offer}</span>
                                                                </li>
                                                            ))}
                                                        </ul>
                                                    </div>
                                                )}
                                            </div>
                                        </div>
                                    </div>

                                    <div className="border-t border-gray-200 pt-8">
                                        <div className="bg-gradient-to-r from-blue-50 to-blue-100 p-6 rounded-xl shadow-sm">
                                            <div className="flex flex-col md:flex-row md:items-center md:justify-between mb-4 gap-4">
                                                <div>
                                                    <p className="text-lg font-medium mb-1">Giá phòng của bạn</p>
                                                    {room.remainingRooms && room.remainingRooms <= 5 && (
                                                        <p className="text-red-600 text-sm font-medium">
                                                            Chỉ còn {room.remainingRooms} phòng với giá này!
                                                        </p>
                                                    )}
                                                </div>
                                                <div className="text-right">
                                                    <div className="text-2xl font-bold text-blue-800">
                                                        {formatPrice(room.price)}
                                                        <span className="text-sm font-normal text-gray-600">/đêm</span>
                                                    </div>
                                                    <p className="text-sm text-gray-600">Đã bao gồm thuế & phí</p>
                                                </div>
                                            </div>

                                            <div className="relative py-4">
                                                <div className="absolute inset-0 flex items-center">
                                                    <div className="w-full border-t border-gray-300"></div>
                                                </div>
                                                <div className="relative flex justify-center">
                                                    <span className="bg-gradient-to-r from-blue-50 to-blue-100 px-4 text-sm text-gray-600">Tùy chọn đặt phòng</span>
                                                </div>
                                            </div>
                                            {/* 
                                            <div className="grid grid-cols-1 md:grid-cols-3 gap-4 items-end">
                                                <div className="md:col-span-1">
                                                    <label className="block text-sm font-medium mb-1">Chọn ngày</label>
                                                    <button className="w-full flex items-center justify-between bg-white border border-gray-300 rounded-md px-3 py-2 shadow-sm hover:border-blue-500 focus:outline-none focus:border-blue-500">
                                                        <div className="flex items-center gap-2">
                                                            <Calendar size={18} className="text-blue-600" />
                                                            <span>Chọn ngày</span>
                                                        </div>
                                                        <ChevronDown size={18} className="text-gray-500" />
                                                    </button>
                                                </div>

                                                <div>
                                                    <label className="block text-sm font-medium mb-1">Số lượng phòng</label>
                                                    <div className="flex items-center bg-white border border-gray-300 rounded-md shadow-sm">
                                                        <button
                                                            className="px-3 py-2 text-gray-600 hover:bg-gray-100 transition-colors rounded-l-md"
                                                            onClick={() => handleRoomQuantityChange(room.id, (roomQuantities[room.id] || 0) - 1)}
                                                        >
                                                            <Minus size={16} />
                                                        </button>
                                                        <span className="flex-1 text-center font-medium py-2">{roomQuantities[room.id] || 0}</span>
                                                        <button
                                                            className="px-3 py-2 text-gray-600 hover:bg-gray-100 transition-colors rounded-r-md"
                                                            onClick={() => handleRoomQuantityChange(room.id, (roomQuantities[room.id] || 0) + 1)}
                                                        >
                                                            <Plus size={16} />
                                                        </button>
                                                    </div>
                                                </div>

                                                <button
                                                    className={`${roomQuantities[room.id] > 0 ? 'bg-blue-600 hover:bg-blue-700 shadow-lg shadow-blue-200' : 'bg-gray-400 cursor-not-allowed'} text-white font-medium rounded-md py-3 px-4 transition-all duration-200`}
                                                    disabled={!roomQuantities[room.id]}
                                                >
                                                    {roomQuantities[room.id] > 0 ? (
                                                        <span className="flex items-center justify-center gap-2">
                                                            <span>Đặt ngay</span>
                                                            <span className="font-bold">{formatPrice(room.price * (roomQuantities[room.id] || 0))}</span>
                                                        </span>
                                                    ) : (
                                                        <span>Đặt ngay</span>
                                                    )}
                                                </button>
                                            </div> */}

                                            {room.freeCancellation && (
                                                <div className="mt-4 text-center">
                                                    <p className="text-green-700 text-sm flex items-center justify-center gap-1">
                                                        <Check size={16} />
                                                        <span>Có thể hủy miễn phí trước ngày nhận phòng 1 ngày</span>
                                                    </p>
                                                </div>
                                            )}
                                        </div>
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            )}
        </>
    );
}