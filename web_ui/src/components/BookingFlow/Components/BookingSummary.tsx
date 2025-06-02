"use client";

import React from 'react';
import { Calendar, MapPin, Users, Clock, Trash2 } from 'lucide-react';
import { useBooking } from '@/context/BookingContext';

// Format price function
const formatPrice = (price: number): string => {
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
    }).format(price);
};

// Format date function
const formatDate = (date: Date): string => {
    return new Intl.DateTimeFormat('vi-VN', {
        weekday: 'short',
        day: 'numeric',
        month: 'short',
        year: 'numeric'
    }).format(date);
};

export default function BookingSummary() {
    const { state, updateRoomQuantity, removeRoom, hasSelectedRooms } = useBooking();
    
    if (!hasSelectedRooms()) {
        return (
            <div className="bg-white rounded-lg shadow-lg p-6">
                <h3 className="text-lg font-bold text-gray-900 mb-4">Tóm tắt đặt phòng</h3>
                <div className="text-center py-8">
                    <div className="text-gray-400 mb-2">
                        <Calendar size={48} className="mx-auto" />
                    </div>
                    <p className="text-gray-500">Chưa có phòng nào được chọn</p>
                </div>
            </div>
        );
    }
    
    return (
        <div className="bg-white rounded-lg shadow-lg p-6 sticky top-6">
            <h3 className="text-lg font-bold text-gray-900 mb-4">Tóm tắt đặt phòng</h3>
            
            {/* Hotel Info */}
            <div className="mb-6 pb-4 border-b border-gray-200">
                <div className="flex items-start gap-3 mb-3">
                    <MapPin size={16} className="text-blue-600 mt-1 flex-shrink-0" />
                    <div>
                        <h4 className="font-semibold text-gray-900">{state.hotelName}</h4>
                        <p className="text-sm text-gray-600">La Passion Classic Hotel</p>
                    </div>
                </div>
                
                {/* Booking Dates */}
                {state.bookingDates && (
                    <div className="flex items-center gap-3 text-sm">
                        <Calendar size={16} className="text-blue-600" />
                        <div>
                            <span className="font-medium">
                                {formatDate(state.bookingDates.checkIn)}
                            </span>
                            <span className="mx-2 text-gray-400">→</span>
                            <span className="font-medium">
                                {formatDate(state.bookingDates.checkOut)}
                            </span>
                            <div className="text-gray-600">
                                {state.bookingDates.nights} đêm
                            </div>
                        </div>
                    </div>
                )}
            </div>
            
            {/* Selected Rooms */}
            <div className="mb-6">
                <h4 className="font-semibold text-gray-900 mb-3">Phòng đã chọn</h4>
                <div className="space-y-3">
                    {state.selectedRooms.map((selectedRoom) => (
                        <div key={selectedRoom.roomId} className="bg-gray-50 rounded-lg p-3">
                            <div className="flex justify-between items-start mb-2">
                                <div className="flex-1">
                                    <h5 className="font-medium text-gray-900 text-sm">
                                        {selectedRoom.roomName}
                                    </h5>
                                    <div className="flex items-center gap-2 text-xs text-gray-600 mt-1">
                                        <Users size={12} />
                                        <span>
                                            {selectedRoom.room.occupancy.adults} người lớn
                                            {selectedRoom.room.occupancy.children > 0 && 
                                                `, ${selectedRoom.room.occupancy.children} trẻ em`
                                            }
                                        </span>
                                    </div>
                                </div>
                                <button
                                    onClick={() => removeRoom(selectedRoom.roomId)}
                                    className="text-red-500 hover:text-red-700 p-1"
                                    title="Xóa phòng"
                                >
                                    <Trash2 size={14} />
                                </button>
                            </div>
                            
                            {/* Quantity Selector */}
                            <div className="flex items-center justify-between">
                                <div className="flex items-center gap-2">
                                    <span className="text-sm text-gray-600">Số lượng:</span>
                                    <div className="flex items-center border border-gray-300 rounded">
                                        <button
                                            onClick={() => updateRoomQuantity(selectedRoom.roomId, selectedRoom.quantity - 1)}
                                            className="px-2 py-1 text-gray-600 hover:bg-gray-100 text-sm"
                                            disabled={selectedRoom.quantity <= 1}
                                        >
                                            -
                                        </button>
                                        <span className="px-3 py-1 text-sm font-medium">
                                            {selectedRoom.quantity}
                                        </span>
                                        <button
                                            onClick={() => updateRoomQuantity(selectedRoom.roomId, selectedRoom.quantity + 1)}
                                            className="px-2 py-1 text-gray-600 hover:bg-gray-100 text-sm"
                                            disabled={selectedRoom.quantity >= (selectedRoom.room.remainingRooms || 10)}
                                        >
                                            +
                                        </button>
                                    </div>
                                </div>
                                
                                <div className="text-right">
                                    <div className="font-semibold text-sm">
                                        {formatPrice(selectedRoom.totalPrice)}
                                    </div>
                                    <div className="text-xs text-gray-600">
                                        {formatPrice(selectedRoom.price)}/phòng
                                    </div>
                                </div>
                            </div>
                            
                            {/* Room Features */}
                            <div className="mt-2 flex flex-wrap gap-1">
                                {selectedRoom.room.breakfast && (
                                    <span className="text-xs bg-green-100 text-green-700 px-2 py-1 rounded">
                                        Bữa sáng
                                    </span>
                                )}
                                {selectedRoom.room.freeCancellation && (
                                    <span className="text-xs bg-blue-100 text-blue-700 px-2 py-1 rounded">
                                        Hủy miễn phí
                                    </span>
                                )}
                            </div>
                        </div>
                    ))}
                </div>
            </div>
            
            {/* Price Breakdown */}
            <div className="border-t border-gray-200 pt-4">
                <div className="space-y-2 text-sm">
                    <div className="flex justify-between">
                        <span className="text-gray-600">
                            Tổng phòng ({state.bookingDates?.nights || 1} đêm)
                        </span>
                        <span className="font-medium">{formatPrice(state.subtotal)}</span>
                    </div>
                    
                    <div className="flex justify-between">
                        <span className="text-gray-600">Thuế & phí (15%)</span>
                        <span className="font-medium">{formatPrice(state.taxes + state.fees)}</span>
                    </div>
                </div>
                
                <div className="border-t border-gray-200 mt-3 pt-3">
                    <div className="flex justify-between items-center">
                        <span className="text-lg font-bold text-gray-900">Tổng cộng</span>
                        <span className="text-xl font-bold text-blue-600">
                            {formatPrice(state.total)}
                        </span>
                    </div>
                </div>
            </div>
            
            {/* Booking Progress */}
            {state.currentStep !== 'room-selection' && (
                <div className="mt-6 pt-4 border-t border-gray-200">
                    <div className="flex items-center gap-2 text-sm">
                        <Clock size={16} className="text-orange-500" />
                        <span className="text-gray-600">Thời gian giữ phòng:</span>
                        <span className="font-medium text-orange-600">15:00</span>
                    </div>
                    <div className="mt-2 bg-orange-100 rounded-full h-2">
                        <div className="bg-orange-500 h-2 rounded-full w-3/4"></div>
                    </div>
                    <p className="text-xs text-gray-500 mt-1">
                        Vui lòng hoàn tất thanh toán trong 15 phút
                    </p>
                </div>
            )}
        </div>
    );
}
