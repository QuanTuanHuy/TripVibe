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
    ChevronLeft,
    ChevronRight,
    Bath,
    CreditCard,
} from 'lucide-react';
import { Unit } from '@/types/accommodation/accommodation/unit.types';

interface AvailableRoomsProps {
    units: Unit[];
    onShowRoomDetails?: (unitId: number) => void;
    hotelId?: string;
    hotelName?: string;
    checkIn?: Date;
    checkOut?: Date;
}

// Selected room interface for booking summary
interface SelectedRoom {
    unitId: number;
    unitName: string;
    quantity: number;
    price: number;
    totalPrice: number;
}

const formatPrice = (price: number): string => {
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
    }).format(price);
};

const getBedInfo = (unit: Unit): string => {
    if (!unit.bedrooms || unit.bedrooms.length === 0) {
        return 'Thông tin giường đang cập nhật';
    }

    return unit.bedrooms.map(bedroom =>
        bedroom.beds?.map(bed => `${bed.quantity} ${bed.type?.name || 'giường'}`).join(', ')
    ).join(', ');
};

const getAmenityNames = (unit: Unit): string[] => {
    if (!unit.amenities || unit.amenities.length === 0) {
        return [];
    }

    return unit.amenities
        .filter(amenity => amenity.amenity?.name)
        .map(amenity => amenity.amenity!.name);
};

const hasBreakfast = (unit: Unit): boolean => {
    if (!unit.amenities) return false;
    return unit.amenities.some(amenity =>
        amenity.amenity?.name?.toLowerCase().includes('bữa sáng') ||
        amenity.amenity?.name?.toLowerCase().includes('breakfast')
    );
};

const getUnitImage = (unit: Unit): string => {
    if (unit.images && unit.images.length > 0) {
        return unit.images[0].url;
    }
    return '/images/default-room.jpg'; // Fallback image
};

export default function AvailableRooms({ units, onShowRoomDetails, hotelId, hotelName, checkIn, checkOut }: AvailableRoomsProps) {
    const [roomQuantities, setRoomQuantities] = useState<{ [key: number]: number }>({});
    const [showRoomDropdown, setShowRoomDropdown] = useState<{ [key: number]: boolean }>({});
    const [selectedRoomDetails, setSelectedRoomDetails] = useState<number | null>(null);
    const [selectedImageIndex, setSelectedImageIndex] = useState<{ [key: number]: number }>({});

    // Calculate total price for selected rooms
    const calculateTotalPrice = () => {
        return Object.entries(roomQuantities).reduce((total, [unitId, quantity]) => {
            const unit = units.find(u => u.id === parseInt(unitId));
            return total + (unit ? unit.pricePerNight * quantity : 0);
        }, 0);
    };

    const getTotalSelectedRooms = () => {
        return Object.values(roomQuantities).reduce((total, quantity) => total + quantity, 0);
    };

    const hasSelectedRooms = () => {
        return Object.values(roomQuantities).some(quantity => quantity > 0);
    };

    const handleRoomQuantityChange = (unitId: number, newQuantity: number) => {
        if (newQuantity < 0) return;
        const unit = units.find(u => u.id === unitId);
        const maxQuantity = unit?.quantity || 1;
        if (newQuantity > maxQuantity) return;

        setRoomQuantities(prev => ({
            ...prev,
            [unitId]: newQuantity
        }));
    };

    const toggleRoomDropdown = (unitId: number) => {
        setShowRoomDropdown(prev => ({
            ...prev,
            [unitId]: !prev[unitId]
        }));
    };

    const handleShowRoomDetailsLocal = (unitId: number) => {
        setSelectedRoomDetails(unitId);
        // Reset selected image index when opening modal
        setSelectedImageIndex(prev => ({ ...prev, [unitId]: 0 }));
        if (onShowRoomDetails) {
            onShowRoomDetails(unitId);
        }
    };

    const handleImageSelect = (unitId: number, imageIndex: number) => {
        setSelectedImageIndex(prev => ({
            ...prev,
            [unitId]: imageIndex
        }));
    };

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

    useEffect(() => {
        if (selectedRoomDetails !== null) {
            document.body.style.overflow = 'hidden';
        } else {
            document.body.style.overflow = 'unset';
        }
        return () => {
            document.body.style.overflow = 'unset';
        }
    }, [selectedRoomDetails])

    useEffect(() => {
        const handleEscapeKey = (event: KeyboardEvent) => {
            if (event.key === 'Escape' && selectedRoomDetails !== null) {
                handleCloseRoomDetails();
            }
        }

        document.addEventListener('keydown', handleEscapeKey);
        return () => {
            document.removeEventListener('keydown', handleEscapeKey);
        }
    }, [selectedRoomDetails])

    // Get selected rooms for guest form
    const getSelectedRooms = (): SelectedRoom[] => {
        return Object.entries(roomQuantities)
            .filter(([, quantity]) => quantity > 0)
            .map(([unitId, quantity]) => {
                const unit = units.find(u => u.id === parseInt(unitId));
                return {
                    unitId: parseInt(unitId),
                    unitName: unit?.unitName?.name || `Phòng ${unitId}`,
                    quantity,
                    price: unit?.pricePerNight || 0,
                    totalPrice: (unit?.pricePerNight || 0) * quantity
                };
            });
    };

    // Handle proceed to booking
    const handleProceedToBooking = () => {
        if (hasSelectedRooms()) {
            // Redirect to booking page with selected rooms data
            const selectedRooms = getSelectedRooms();
            const accommodationId = hotelId || "1";
            const accommodationName = hotelName || "Hotel";
            const checkInDate = checkIn
                ? checkIn.toISOString().split('T')[0]
                : new Date().toISOString().split('T')[0];
            const checkOutDate = checkOut
                ? checkOut.toISOString().split('T')[0]
                : new Date(new Date().setDate(new Date().getDate() + 1)).toISOString().split('T')[0];

            localStorage.setItem('selectedRooms', JSON.stringify(selectedRooms));
            window.location.href = `/booking?hotelId=${accommodationId}&hotelName=${encodeURIComponent(accommodationName)}&checkIn=${checkInDate}&checkOut=${checkOutDate}`;
        }
    };

    if (!units || units.length === 0) {
        return (
            <div className="text-center py-12 text-gray-500">
                <p>Không có phòng trống cho ngày đã chọn.</p>
            </div>
        );
    }

    return (
        <>
            {/* Enhanced Room Display with Booking.com Style */}
            <div className="space-y-6">
                {units.map((unit) => (
                    <div key={unit.id} className="border border-gray-200 rounded-lg overflow-hidden shadow-sm">
                        {/* Room Header with Room Name */}
                        <div className="bg-blue-50 p-4 border-b border-gray-200">
                            <h3 className="text-lg font-bold text-blue-600">
                                {unit.unitName?.name || `Phòng ${unit.id}`}
                            </h3>
                        </div>

                        {/* Room Content */}
                        <div className="p-0">
                            <div className="flex flex-col md:flex-row">
                                {/* Room Image */}
                                <div className="md:w-1/4 p-4">
                                    <div className="relative h-48 w-full rounded-md overflow-hidden">
                                        <Image
                                            src={getUnitImage(unit)}
                                            alt={unit.unitName?.name || `Phòng ${unit.id}`}
                                            fill
                                            sizes="(max-width: 768px) 100vw, 25vw"
                                            className="object-cover rounded-md"
                                            priority={true}
                                        />
                                    </div>
                                </div>

                                {/* Room Details */}
                                <div className="md:w-2/4 p-4 border-t md:border-t-0 md:border-l md:border-r border-gray-200">
                                    {/* Room Bed Info */}
                                    <div className="flex items-center gap-3 mb-3">
                                        <div className="flex items-center gap-1 text-gray-600">
                                            <Bed size={16} />
                                            <span>{getBedInfo(unit)}</span>
                                        </div>
                                    </div>

                                    {/* Occupancy */}
                                    <div className="mb-3">
                                        <div className="flex items-center gap-2">
                                            <Users size={16} className="text-gray-600" />
                                            <span className="text-gray-600">
                                                {unit.maxAdults} người lớn
                                                {unit.maxChildren > 0 ? `, ${unit.maxChildren} trẻ em` : ''}
                                            </span>
                                        </div>
                                    </div>

                                    {/* Description */}
                                    {unit.description && (
                                        <div className="mb-3">
                                            <p className="text-sm text-gray-600">{unit.description}</p>
                                        </div>
                                    )}

                                    {/* Amenities */}
                                    {getAmenityNames(unit).length > 0 && (
                                        <div className="mb-4">
                                            <h4 className="text-sm font-medium mb-2">Tiện nghi phòng:</h4>
                                            <div className="grid grid-cols-2 gap-2">
                                                {getAmenityNames(unit).slice(0, 6).map((amenityName, index) => (
                                                    <div key={index} className="flex items-center gap-2 text-sm">
                                                        <Check size={16} className="text-green-500" />
                                                        <span>{amenityName}</span>
                                                    </div>
                                                ))}
                                            </div>
                                            {getAmenityNames(unit).length > 6 && (
                                                <button
                                                    className="text-blue-600 text-sm font-medium mt-2"
                                                    onClick={() => handleShowRoomDetailsLocal(unit.id)}
                                                >
                                                    + {getAmenityNames(unit).length - 6} tiện nghi khác
                                                </button>
                                            )}
                                        </div>
                                    )}

                                    {/* Meal Information */}
                                    {hasBreakfast(unit) && (
                                        <div className="flex items-center gap-2 text-sm mb-3">
                                            <div className="flex items-center gap-2 text-green-700 bg-green-50 px-3 py-1 rounded-full">
                                                <Coffee size={16} />
                                                <span>Bao gồm bữa sáng</span>
                                            </div>
                                        </div>
                                    )}

                                    {/* Bathroom Information */}
                                    <div className="flex items-center gap-2 text-sm mb-3">
                                        <Check size={16} className="text-green-700" />
                                        <span>
                                            {unit.useSharedBathroom ? 'Phòng tắm chung' : 'Phòng tắm riêng'}
                                        </span>
                                    </div>
                                </div>

                                {/* Pricing and Booking */}
                                <div className="md:w-1/4 p-4 bg-blue-50 flex flex-col justify-between">
                                    <div>
                                        <p className="text-sm text-gray-600 mb-1">Giá cho 1 đêm</p>
                                        <p className="text-xl font-bold text-gray-900 mb-2">
                                            {formatPrice(unit.pricePerNight)}
                                        </p>
                                        <p className="text-xs text-gray-500 mb-3">Đã bao gồm thuế & phí</p>

                                        {/* Remaining Rooms */}
                                        {unit.quantity && unit.quantity <= 5 && (
                                            <div className="mb-3 text-sm text-red-600 font-medium">
                                                Chỉ còn {unit.quantity} phòng với giá này!
                                            </div>
                                        )}

                                        {/* Room Quantity Selector */}
                                        <div className="mt-3 mb-4">
                                            <div className="relative room-quantity-dropdown">
                                                <button
                                                    onClick={() => toggleRoomDropdown(unit.id)}
                                                    className="w-full flex items-center justify-between border border-gray-300 bg-white rounded px-3 py-2 text-sm"
                                                >
                                                    <span>Số lượng phòng: {roomQuantities[unit.id] || 0}</span>
                                                    <ChevronDown size={16} className={`transition-transform ${showRoomDropdown[unit.id] ? 'rotate-180' : ''}`} />
                                                </button>

                                                {showRoomDropdown[unit.id] && (
                                                    <div className="absolute z-10 mt-1 w-full bg-white border border-gray-200 rounded shadow-lg">
                                                        <div className="p-2">
                                                            <div className="flex items-center justify-between">
                                                                <span className="text-sm">Chọn số lượng:</span>
                                                                <div className="flex items-center border border-gray-300 rounded">
                                                                    <button
                                                                        className="px-2 py-1 text-gray-600 hover:bg-gray-100"
                                                                        onClick={(e) => {
                                                                            e.stopPropagation();
                                                                            handleRoomQuantityChange(unit.id, (roomQuantities[unit.id] || 0) - 1);
                                                                        }}
                                                                    >
                                                                        <Minus size={14} />
                                                                    </button>
                                                                    <span className="px-3">{roomQuantities[unit.id] || 0}</span>
                                                                    <button
                                                                        className="px-2 py-1 text-gray-600 hover:bg-gray-100"
                                                                        onClick={(e) => {
                                                                            e.stopPropagation();
                                                                            handleRoomQuantityChange(unit.id, (roomQuantities[unit.id] || 0) + 1);
                                                                        }}
                                                                    >
                                                                        <Plus size={14} />
                                                                    </button>
                                                                </div>
                                                            </div>

                                                            {roomQuantities[unit.id] > 0 && (
                                                                <div className="text-sm text-green-700 font-medium mt-2">
                                                                    Tổng: {formatPrice(unit.pricePerNight * roomQuantities[unit.id])}
                                                                </div>
                                                            )}
                                                        </div>
                                                    </div>
                                                )}
                                            </div>
                                        </div>
                                    </div>

                                    <div>
                                        <button
                                            className={`w-full ${roomQuantities[unit.id] > 0 ? 'bg-blue-600 hover:bg-blue-700' : 'bg-gray-400 cursor-not-allowed'} text-white font-medium rounded-md px-4 py-2 mb-2 transition-colors`}
                                            disabled={!roomQuantities[unit.id]}
                                            onClick={() => {
                                                if (roomQuantities[unit.id] > 0) {
                                                    console.log(`Đặt ${roomQuantities[unit.id]} phòng ${unit.unitName?.name} với giá ${formatPrice(unit.pricePerNight * roomQuantities[unit.id])}`);
                                                    // Thêm logic đặt phòng riêng cho từng loại phòng
                                                }
                                            }}
                                        >
                                            {roomQuantities[unit.id] > 0
                                                ? `Đặt ${roomQuantities[unit.id]} phòng - ${formatPrice(unit.pricePerNight * roomQuantities[unit.id])}`
                                                : 'Chọn số lượng phòng'
                                            }
                                        </button>
                                        <button
                                            onClick={() => handleShowRoomDetailsLocal(unit.id)}
                                            className="w-full border border-blue-600 text-blue-600 hover:bg-blue-50 font-medium rounded-md px-4 py-2"
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

            {/* Booking Summary - Only show when rooms are selected */}
            {hasSelectedRooms() && (
                <div className="mt-8 bg-blue-50 border border-blue-200 rounded-lg p-6">
                    <h3 className="text-lg font-bold text-blue-800 mb-4">Tóm tắt đặt phòng</h3>

                    {/* Selected Rooms List */}
                    <div className="space-y-3 mb-4">
                        {Object.entries(roomQuantities)
                            .filter(([, quantity]) => quantity > 0)
                            .map(([unitId, quantity]) => {
                                const unit = units.find(u => u.id === parseInt(unitId));
                                if (!unit) return null;

                                return (
                                    <div key={unitId} className="flex justify-between items-center bg-white p-3 rounded-md">
                                        <div>
                                            <span className="font-medium">{unit.unitName?.name || `Phòng ${unit.id}`}</span>
                                            <span className="text-gray-600 ml-2">x {quantity}</span>
                                        </div>
                                        <div className="text-right">
                                            <div className="font-medium">{formatPrice(unit.pricePerNight * quantity)}</div>
                                            <div className="text-sm text-gray-600">{formatPrice(unit.pricePerNight)}/phòng</div>
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
                            onClick={handleProceedToBooking}
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
                        {units.filter(unit => unit.id === selectedRoomDetails).map(unit => (
                            <div key={`detail-${unit.id}`} className="relative">
                                {/* Close button */}
                                <button
                                    onClick={handleCloseRoomDetails}
                                    className="absolute right-4 top-4 bg-white/80 hover:bg-white backdrop-blur-sm rounded-full p-2 shadow-md z-20 transition-all duration-200"
                                >
                                    <X size={24} />
                                </button>

                                {/* Room gallery - Featured image and thumbnails */}
                                <div className="bg-gray-100">
                                    {/* Main image */}
                                    <div className="relative h-96 w-full group">
                                        <Image
                                            src={unit.images && unit.images.length > 0
                                                ? unit.images[selectedImageIndex[unit.id] || 0].url
                                                : '/images/default-room.jpg'
                                            }
                                            alt={unit.unitName?.name || `Phòng ${unit.id}`}
                                            fill
                                            sizes="(max-width: 1024px) 100vw, 1024px"
                                            className="object-cover"
                                            priority={true}
                                        />

                                        {/* Image counter */}
                                        {unit.images && unit.images.length > 0 && (
                                            <div className="absolute top-4 left-4 bg-black/50 text-white px-3 py-1.5 rounded-full text-sm font-medium">
                                                {(selectedImageIndex[unit.id] || 0) + 1} / {unit.images.length}
                                            </div>
                                        )}

                                        {/* Price tag */}
                                        <div className="absolute bottom-4 right-4 bg-blue-600 text-white px-3 py-1.5 rounded-full text-sm font-medium shadow-md">
                                            {formatPrice(unit.pricePerNight)}/đêm
                                        </div>

                                        {/* Navigation arrows - only show if more than 1 image */}
                                        {unit.images && unit.images.length > 1 && (
                                            <>
                                                {/* Previous button */}
                                                <button
                                                    className="absolute left-4 top-1/2 transform -translate-y-1/2 bg-white/80 hover:bg-white backdrop-blur-sm rounded-full p-2 shadow-md opacity-0 group-hover:opacity-100 transition-opacity duration-200"
                                                    onClick={() => {
                                                        const currentIndex = selectedImageIndex[unit.id] || 0;
                                                        const newIndex = currentIndex === 0 ? unit.images!.length - 1 : currentIndex - 1;
                                                        handleImageSelect(unit.id, newIndex);
                                                    }}
                                                >
                                                    <ChevronLeft size={20} />
                                                </button>

                                                {/* Next button */}
                                                <button
                                                    className="absolute right-4 top-1/2 transform -translate-y-1/2 bg-white/80 hover:bg-white backdrop-blur-sm rounded-full p-2 shadow-md opacity-0 group-hover:opacity-100 transition-opacity duration-200"
                                                    onClick={() => {
                                                        const currentIndex = selectedImageIndex[unit.id] || 0;
                                                        const newIndex = currentIndex === unit.images!.length - 1 ? 0 : currentIndex + 1;
                                                        handleImageSelect(unit.id, newIndex);
                                                    }}
                                                >
                                                    <ChevronRight size={20} />
                                                </button>
                                            </>
                                        )}
                                    </div>

                                    {/* Photo gallery row */}
                                    {unit.images && unit.images.length > 1 && (
                                        <div className="grid grid-cols-5 gap-1 p-1">
                                            {unit.images.map((image, index) => (
                                                <div
                                                    key={index}
                                                    className={`relative h-24 cursor-pointer transition-all duration-200 ${(selectedImageIndex[unit.id] || 0) === index
                                                        ? 'ring-2 ring-blue-500 opacity-100'
                                                        : 'hover:opacity-90 opacity-70'
                                                        }`}
                                                    onClick={() => handleImageSelect(unit.id, index)}
                                                >
                                                    <Image
                                                        src={image.url}
                                                        alt={`${unit.unitName?.name} - Ảnh ${index + 1}`}
                                                        fill
                                                        className="object-cover rounded-sm"
                                                    />
                                                    {/* Active indicator */}
                                                    {(selectedImageIndex[unit.id] || 0) === index && (
                                                        <div className="absolute inset-0 bg-blue-500/20 rounded-sm"></div>
                                                    )}
                                                </div>
                                            ))}
                                        </div>
                                    )}
                                </div>

                                <div className="p-8">
                                    <div className="border-b pb-6 mb-6">
                                        <h2 className="text-3xl font-bold mb-2">{unit.unitName?.name || `Phòng ${unit.id}`}</h2>
                                        <div className="flex flex-wrap items-center gap-4 mb-4">
                                            <div className="flex items-center gap-2 bg-blue-50 px-3 py-1 rounded-full">
                                                <Bed size={18} className="text-blue-600" />
                                                <span className="font-medium">{getBedInfo(unit)}</span>
                                            </div>
                                            <div className="flex items-center gap-2 bg-blue-50 px-3 py-1 rounded-full">
                                                <Users size={18} className="text-blue-600" />
                                                <span className="font-medium">
                                                    {unit.maxAdults} người lớn
                                                    {unit.maxChildren > 0 ? `, ${unit.maxChildren} trẻ em` : ''}
                                                </span>
                                            </div>
                                        </div>

                                        {unit.description && (
                                            <p className="text-gray-600">{unit.description}</p>
                                        )}

                                        {!unit.description && (
                                            <p className="text-gray-600">
                                                Phòng {unit.unitName?.name} rộng rãi và sang trọng, được thiết kế để mang lại sự thoải mái tối đa cho du khách.
                                                Với không gian thoáng đãng và đầy đủ tiện nghi, phòng này là lựa chọn hoàn hảo cho cả chuyến công tác và kỳ nghỉ thư giãn.
                                            </p>
                                        )}
                                    </div>
                                    <div className="grid grid-cols-1 lg:grid-cols-2 gap-8 mb-8">
                                        {/* Tiện nghi phòng */}
                                        <div className="rounded-xl p-6 border border-blue-100 shadow-sm">
                                            <div className="flex items-center gap-3 mb-6">
                                                <div className="rounded-lg p-2">
                                                    <Check size={20} className="text-green-600"></Check>
                                                </div>
                                                <h3 className="text-xl font-bold text-gray-800">Tiện nghi phòng</h3>
                                            </div>
                                            <div className="space-y-3">
                                                {getAmenityNames(unit).map((amenityName, index) => (
                                                    <div key={index} className="flex items-center gap-3 p-2 hover:bg-white/60 rounded-lg transition-colors duration-200">
                                                        <div className="">
                                                            <Check size={16} className="text-green-600"></Check>
                                                        </div>
                                                        <span className="text-gray-700 font-medium">{amenityName}</span>
                                                    </div>
                                                ))}
                                                {getAmenityNames(unit).length === 0 && (
                                                    <div className="text-center py-8">
                                                        <div className="bg-gray-100 rounded-full w-16 h-16 flex items-center justify-center mx-auto mb-3">
                                                            <Coffee size={24} className="text-gray-400" />
                                                        </div>
                                                        <p className="text-gray-500 font-medium">Thông tin tiện nghi đang được cập nhật</p>
                                                    </div>
                                                )}
                                            </div>
                                        </div>

                                        {/* Thông tin phòng */}
                                        <div className="rounded-xl p-6 border border-blue-100 shadow-sm">
                                            <div className="flex items-center gap-3 mb-6">
                                                <div className=" rounded-lg p-2">
                                                    <Bed size={20} className="text-green-600" />
                                                </div>
                                                <h3 className="text-xl font-bold text-gray-800">Thông tin phòng</h3>
                                            </div>
                                            <div className="space-y-4">
                                                <div className="bg-white/70 rounded-lg p-4 flex justify-between items-center hover:bg-white/90 transition-colors duration-200">
                                                    <div className="flex items-center gap-3">
                                                        <div className="rounded-full p-2">
                                                            <Users size={16} className="text-green-600" />
                                                        </div>
                                                        <span className="text-gray-600 font-medium">Số lượng phòng:</span>
                                                    </div>
                                                    <span className="font-bold text-lg ">{unit.quantity}</span>
                                                </div>

                                                <div className="bg-white/70 rounded-lg p-4 flex justify-between items-center hover:bg-white/90 transition-colors duration-200">
                                                    <div className="flex items-center gap-3">
                                                        <div className="rounded-full p-2">
                                                            <Bath size={16} className="text-green-600" />
                                                        </div>
                                                        <span className="text-gray-600 font-medium">Phòng tắm:</span>
                                                    </div>
                                                    <span className="font-bold">
                                                        {unit.useSharedBathroom ? 'Phòng tắm chung' : 'Phòng tắm riêng'}
                                                    </span>
                                                </div>

                                                <div className="bg-white/70 rounded-lg p-4 flex justify-between items-center hover:bg-white/90 transition-colors duration-200">
                                                    <div className="flex items-center gap-3">
                                                        <div className="rounded-full p-2">
                                                            <CreditCard size={16} className="text-green-600" />
                                                        </div>
                                                        <span className="text-gray-600 font-medium">Giá/đêm:</span>
                                                    </div>
                                                    <div className="text-right">
                                                        <div className="font-bold text-xl ">
                                                            {formatPrice(unit.pricePerNight)}
                                                        </div>
                                                        <div className="text-xs text-gray-500">Đã bao gồm thuế</div>
                                                    </div>
                                                </div>

                                                {/* Thông tin giường */}
                                                <div className="bg-white/70 rounded-lg p-4 hover:bg-white/90 transition-colors duration-200">
                                                    <div className="flex items-center gap-3 mb-2">
                                                        <div className="rounded-full p-2">
                                                            <Bed size={16} className="text-green-600" />
                                                        </div>
                                                        <span className="text-gray-600 font-medium">Loại giường:</span>
                                                    </div>
                                                    <div className="ml-10">
                                                        <span className="font-bold">{getBedInfo(unit)}</span>
                                                    </div>
                                                </div>

                                                {/* Sức chứa */}
                                                <div className="bg-white/70 rounded-lg p-4 hover:bg-white/90 transition-colors duration-200">
                                                    <div className="flex items-center gap-3 mb-2">
                                                        <div className="rounded-full p-2">
                                                            <Users size={16} className="text-green-600" />
                                                        </div>
                                                        <span className="text-gray-600 font-medium">Sức chứa:</span>
                                                    </div>
                                                    <div className="ml-10">
                                                        <span className="font-bold">
                                                            {unit.maxAdults} người lớn
                                                            {unit.maxChildren > 0 ? `, ${unit.maxChildren} trẻ em` : ''}
                                                        </span>
                                                    </div>
                                                </div>
                                            </div>
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