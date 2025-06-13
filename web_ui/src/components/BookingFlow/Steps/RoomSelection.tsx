"use client";

import React, { useState, useEffect } from 'react';
import Image from 'next/image';
import {
    ChevronDown,
    Bed,
    Maximize2,
    Users,
    Check,
    Coffee,
    X,
    ChevronLeft,
    ChevronRight,
    Loader2,
    AlertCircle,
} from 'lucide-react';
import { useBooking, Room } from '@/context/BookingContext';
import accommodationService from '@/services/accommodation/accommodationService';
import { Unit } from '@/types/accommodation';

const formatPrice = (price: number): string => {
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
    }).format(price);
};


const transformUnitToRoom = (unit: Unit): Room => {
    // Extract bed information from bedrooms
    const beds = unit.bedrooms?.flatMap(bedroom =>
        bedroom.beds?.map(bed => ({
            type: bed.type?.name || 'bed',
            count: bed.quantity || 1
        })) || [{ type: 'bed', count: bedroom.quantity || 1 }]
    ) || [{ type: 'bed', count: 1 }];

    return {
        id: unit.id,
        name: unit.unitName?.name || `Unit ${unit.id}`,
        size: `${unit.maxAdults + unit.maxChildren} guests`,
        beds,
        occupancy: {
            adults: unit.maxAdults,
            children: unit.maxChildren
        },
        amenities: unit.amenities?.map(amenity => amenity.amenity?.name || 'Amenity').slice(0, 10) || [
            'Free WiFi', 'Air conditioning', 'Private bathroom'
        ],
        breakfast: unit.amenities?.some(amenity => amenity.amenity?.name?.toLowerCase().includes('breakfast')) || false,
        freeCancellation: true,
        prepayment: false,
        price: unit.pricePerNight,
        remainingRooms: unit.quantity,
        images: unit.images?.map(img => img.url) || ['/images/placeholder-room.jpg'],
        specialOffers: []
    };
};

export default function RoomSelection() {
    const { state, addRoom, updateRoomQuantity, goToStep, hasSelectedRooms } = useBooking();
    const [showRoomDropdown, setShowRoomDropdown] = useState<{ [key: number]: boolean }>({});
    const [selectedRoomDetails, setSelectedRoomDetails] = useState<number | null>(null);
    const [selectedImageIndex, setSelectedImageIndex] = useState<{ [key: number]: number }>({});

    // State for accommodation data
    const [rooms, setRooms] = useState<Room[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    // Fetch accommodation units and transform to rooms
    useEffect(() => {
        const fetchRooms = async () => {
            try {
                setLoading(true);
                setError(null);
                const accommodationId = state.hotelId ? parseInt(state.hotelId) : 1;
                const units = await accommodationService.getUnitsByAccommodationId(accommodationId);

                const transformedRooms = units.map(transformUnitToRoom);
                setRooms(transformedRooms);
            } catch (err) {
                console.error('Error fetching rooms:', err);
                setError('Failed to load rooms. Please try again.');
            } finally {
                setLoading(false);
            }
        };

        fetchRooms();
    }, [state.hotelId]);

    // Get room quantities from booking context
    const getRoomQuantity = (roomId: number) => {
        const selectedRoom = state.selectedRooms.find(r => r.roomId === roomId);
        return selectedRoom?.quantity || 0;
    };

    // Handle room quantity changes
    const handleRoomQuantityChange = (room: Room, newQuantity: number) => {
        if (newQuantity < 0) return;

        if (newQuantity === 0) {
            updateRoomQuantity(room.id, 0);
        } else {
            const currentQuantity = getRoomQuantity(room.id);
            if (currentQuantity === 0) {
                addRoom(room, newQuantity);
            } else {
                updateRoomQuantity(room.id, newQuantity);
            }
        }
    };

    // Toggle room dropdown
    const toggleRoomDropdown = (roomId: number) => {
        setShowRoomDropdown(prev => ({
            ...prev,
            [roomId]: !prev[roomId]
        }));
    };

    // Handle show room details
    const handleShowRoomDetails = (roomId: number) => {
        setSelectedRoomDetails(roomId);
        setSelectedImageIndex(prev => ({ ...prev, [roomId]: 0 }));
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

    // Handle proceed to guest info
    const handleProceedToGuestInfo = () => {
        if (hasSelectedRooms()) {
            goToStep('guest-info');
        }
    }; return (
        <div>
            <div className="mb-6">
                <h2 className="text-2xl font-bold text-gray-900 mb-2">Chọn phòng</h2>
                <p className="text-gray-600">Chọn loại phòng và số lượng phù hợp với nhu cầu của bạn</p>
            </div>

            {loading && (
                <div className="flex justify-center items-center py-8">
                    <Loader2 className="w-8 h-8 animate-spin text-blue-600" />
                    <span className="ml-2 text-gray-600">Đang tải danh sách phòng...</span>
                </div>
            )}

            {error && (
                <div className="flex items-center gap-2 p-4 bg-red-50 border border-red-200 rounded-lg mb-6">
                    <AlertCircle className="w-5 h-5 text-red-600" />
                    <p className="text-red-800">{error}</p>
                </div>
            )}

            {!loading && !error && rooms.length === 0 && (
                <div className="text-center py-8">
                    <p className="text-gray-600">Không có phòng nào có sẵn.</p>
                </div>
            )}

            <div className="space-y-6">
                {rooms.map((room) => (
                    <div key={room.id} className="border border-gray-200 rounded-lg overflow-hidden shadow-sm bg-white">
                        {/* Room Header */}
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
                                            <button
                                                onClick={() => handleShowRoomDetails(room.id)}
                                                className="text-blue-600 text-sm font-medium mt-2 hover:underline"
                                            >
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
                                    )}

                                    {/* Special Offers */}
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
                                        )}

                                        {/* Room Quantity Selector */}
                                        <div className="mt-3 mb-4">
                                            <div className="relative">
                                                <button
                                                    onClick={() => toggleRoomDropdown(room.id)}
                                                    className="w-full flex items-center justify-between border border-gray-300 bg-white rounded px-3 py-2 text-sm"
                                                >
                                                    <span>Số lượng phòng: {getRoomQuantity(room.id)}</span>
                                                    <ChevronDown size={16} className={`transition-transform ${showRoomDropdown[room.id] ? 'rotate-180' : ''}`} />
                                                </button>

                                                {showRoomDropdown[room.id] && (
                                                    <div className="absolute z-10 mt-1 w-full bg-white border border-gray-200 rounded shadow-lg">
                                                        {[...Array(Math.min(room.remainingRooms || 10, 5))].map((_, index) => (
                                                            <button
                                                                key={index}
                                                                onClick={() => {
                                                                    handleRoomQuantityChange(room, index + 1);
                                                                    setShowRoomDropdown(prev => ({ ...prev, [room.id]: false }));
                                                                }}
                                                                className="w-full px-3 py-2 text-left text-sm hover:bg-blue-50 flex justify-between items-center"
                                                            >
                                                                <span>{index + 1} phòng</span>
                                                                {getRoomQuantity(room.id) === index && (
                                                                    <Check size={16} className="text-blue-600" />
                                                                )}
                                                            </button>
                                                        ))}
                                                    </div>
                                                )}
                                            </div>
                                        </div>
                                    </div>

                                    <div>
                                        <button
                                            onClick={() => handleShowRoomDetails(room.id)}
                                            className="w-full border border-blue-600 text-blue-600 hover:bg-blue-50 font-medium rounded-md px-4 py-2 mb-2"
                                        >
                                            Xem chi tiết phòng
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                ))}
            </div>

            {/* Continue Button */}
            {hasSelectedRooms() && (
                <div className="mt-8 text-center">
                    <button
                        onClick={handleProceedToGuestInfo}
                        className="bg-blue-600 hover:bg-blue-700 text-white font-bold py-3 px-8 rounded-lg transition-colors duration-200"
                    >
                        Tiếp tục - Điền thông tin khách
                    </button>
                </div>
            )}            {/* Modal Room Details */}
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
                                </button>

                                {/* Room gallery */}
                                <div className="bg-gray-100">
                                    <div className="relative h-96">
                                        <Image
                                            src={room.images[selectedImageIndex[room.id] || 0]}
                                            alt={room.name}
                                            fill
                                            className="object-cover"
                                        />

                                        {/* Navigation arrows */}
                                        {room.images.length > 1 && (
                                            <>
                                                <button
                                                    onClick={() => handleImageSelect(room.id, Math.max(0, (selectedImageIndex[room.id] || 0) - 1))}
                                                    className="absolute left-4 top-1/2 -translate-y-1/2 bg-white/80 hover:bg-white rounded-full p-2 shadow-md"
                                                >
                                                    <ChevronLeft size={20} />
                                                </button>
                                                <button
                                                    onClick={() => handleImageSelect(room.id, Math.min(room.images.length - 1, (selectedImageIndex[room.id] || 0) + 1))}
                                                    className="absolute right-4 top-1/2 -translate-y-1/2 bg-white/80 hover:bg-white rounded-full p-2 shadow-md"
                                                >
                                                    <ChevronRight size={20} />
                                                </button>
                                            </>
                                        )}
                                    </div>

                                    {/* Thumbnail gallery */}
                                    {room.images.length > 1 && (
                                        <div className="flex gap-2 p-4 overflow-x-auto">
                                            {room.images.map((image, index) => (
                                                <button
                                                    key={index}
                                                    onClick={() => handleImageSelect(room.id, index)}
                                                    className={`relative w-20 h-16 rounded-md overflow-hidden flex-shrink-0 border-2 ${(selectedImageIndex[room.id] || 0) === index
                                                        ? 'border-blue-500'
                                                        : 'border-transparent'
                                                        }`}
                                                >
                                                    <Image
                                                        src={image}
                                                        alt={`${room.name} ${index + 1}`}
                                                        fill
                                                        className="object-cover"
                                                    />
                                                </button>
                                            ))}
                                        </div>
                                    )}
                                </div>

                                {/* Room details */}
                                <div className="p-6">
                                    <h2 className="text-2xl font-bold text-gray-900 mb-4">{room.name}</h2>

                                    {/* Full amenities list */}
                                    <div className="mb-6">
                                        <h3 className="text-lg font-semibold mb-3">Tiện nghi phòng</h3>
                                        <div className="grid grid-cols-2 md:grid-cols-3 gap-3">
                                            {room.amenities.map((amenity, index) => (
                                                <div key={index} className="flex items-center gap-2 text-sm">
                                                    <Check size={16} className="text-green-500" />
                                                    <span>{amenity}</span>
                                                </div>
                                            ))}
                                        </div>
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            )}
        </div>
    );
}
