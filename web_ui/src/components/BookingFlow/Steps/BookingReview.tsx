"use client";

import React from 'react';
import {
    Calendar,
    Users,
    MapPin,
    Check,
    Edit,
    User,
    Mail,
    Phone,
    Globe,
    ArrowRight,
    ArrowLeft,
    Coffee,
    Wifi
} from 'lucide-react';
import { useBooking } from '@/context/BookingContext';
import { sampleRooms } from '@/data/sampleRooms';

// Format price function
const formatPrice = (price: number): string => {
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
    }).format(price);
};

// Format date function
const formatDate = (dateString: string): string => {
    const date = new Date(dateString);
    return date.toLocaleDateString('vi-VN', {
        weekday: 'long',
        day: '2-digit',
        month: '2-digit',
        year: 'numeric'
    });
};

export default function BookingReview() {
    const { state, goToStep, createBooking } = useBooking();

    // Use calculated pricing from context
    const nights = state.bookingDates ? state.bookingDates.nights : 0;
    const totalAmount = state.subtotal;
    const taxAmount = state.taxes;
    const serviceCharge = state.fees;
    const finalAmount = state.total;

    // Get selected room details
    const selectedRoomDetails = state.selectedRooms.map(selectedRoom => {
        const room = sampleRooms.find(r => r.id === selectedRoom.roomId);
        return {
            ...selectedRoom,
            roomDetails: room
        };
    });

    const handleProceedToPayment = async () => {
        try {
            await createBooking();
            // The createBooking function will automatically navigate to payment step on success
        } catch (error) {
            console.error('Failed to create booking:', error);
            // Error handling is done in the context
        }
    };

    const handleBackToGuestInfo = () => {
        goToStep('guest-info');
    };

    const handleEditRooms = () => {
        goToStep('room-selection');
    };

    const handleEditGuestInfo = () => {
        goToStep('guest-info');
    };

    return (
        <div className="max-w-4xl mx-auto space-y-6">
            {/* Header */}
            <div className="bg-white rounded-lg shadow-lg p-6">
                <h2 className="text-2xl font-bold text-gray-900 mb-2">Xem lại đặt phòng</h2>
                <p className="text-gray-600">
                    Vui lòng kiểm tra lại thông tin đặt phòng trước khi tiến hành thanh toán
                </p>
            </div>

            {/* Booking Summary */}
            <div className="bg-white rounded-lg shadow-lg p-6">
                <div className="flex items-center justify-between mb-4">
                    <h3 className="text-lg font-semibold text-gray-900">Thông tin đặt phòng</h3>
                    <button
                        onClick={handleEditRooms}
                        className="flex items-center gap-2 text-blue-600 hover:text-blue-700 font-medium"
                    >
                        <Edit size={16} />
                        Chỉnh sửa
                    </button>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
                    {/* Check-in */}
                    <div className="flex items-center gap-3 p-4 bg-gray-50 rounded-lg">
                        <Calendar className="text-blue-600" size={20} />
                        <div>
                            <p className="text-sm text-gray-600">Nhận phòng</p>
                            <p className="font-medium">
                                {state.bookingDates ? formatDate(state.bookingDates.checkIn.toISOString()) : 'Chưa chọn'}
                            </p>
                            <p className="text-xs text-gray-500">Từ 14:00</p>
                        </div>
                    </div>

                    {/* Check-out */}
                    <div className="flex items-center gap-3 p-4 bg-gray-50 rounded-lg">
                        <Calendar className="text-blue-600" size={20} />
                        <div>
                            <p className="text-sm text-gray-600">Trả phòng</p>
                            <p className="font-medium">
                                {state.bookingDates ? formatDate(state.bookingDates.checkOut.toISOString()) : 'Chưa chọn'}
                            </p>
                            <p className="text-xs text-gray-500">Trước 12:00</p>
                        </div>
                    </div>

                    {/* Duration */}
                    <div className="flex items-center gap-3 p-4 bg-gray-50 rounded-lg">
                        <Users className="text-blue-600" size={20} />
                        <div>
                            <p className="text-sm text-gray-600">Thời gian lưu trú</p>
                            <p className="font-medium">{nights} đêm</p>
                            <p className="text-xs text-gray-500">Khách hàng</p>
                        </div>
                    </div>
                </div>

                {/* Selected Rooms */}
                <div>
                    <h4 className="font-medium text-gray-900 mb-3">Phòng đã chọn</h4>
                    <div className="space-y-3">
                        {selectedRoomDetails.map((selectedRoom, index) => (
                            <div key={index} className="border border-gray-200 rounded-lg p-4">
                                <div className="flex justify-between items-start">
                                    <div className="flex-1">
                                        <h5 className="font-medium text-gray-900">{selectedRoom.roomDetails?.name}</h5>
                                        <div className="flex items-center gap-4 text-sm text-gray-600 mt-1">
                                            <span>Số lượng: {selectedRoom.quantity} phòng</span>
                                            <span>•</span>
                                            <span>{selectedRoom.roomDetails?.size}</span>
                                            <span>•</span>
                                            <span>{selectedRoom.roomDetails?.occupancy.adults} người lớn</span>
                                        </div>

                                        {/* Room amenities highlights */}
                                        <div className="flex items-center gap-3 mt-2">
                                            {selectedRoom.roomDetails?.breakfast && (
                                                <div className="flex items-center gap-1 text-green-600 text-xs">
                                                    <Coffee size={12} />
                                                    <span>Bữa sáng</span>
                                                </div>
                                            )}
                                            {selectedRoom.roomDetails?.amenities.includes('Wi-Fi miễn phí') && (
                                                <div className="flex items-center gap-1 text-blue-600 text-xs">
                                                    <Wifi size={12} />
                                                    <span>WiFi</span>
                                                </div>
                                            )}
                                            {selectedRoom.roomDetails?.freeCancellation && (
                                                <div className="flex items-center gap-1 text-green-600 text-xs">
                                                    <Check size={12} />
                                                    <span>Miễn phí hủy</span>
                                                </div>
                                            )}
                                        </div>
                                    </div>
                                    <div className="text-right">
                                        <p className="font-medium text-gray-900">
                                            {formatPrice(selectedRoom.roomDetails?.price || 0)} × {selectedRoom.quantity}
                                        </p>
                                        <p className="text-sm text-gray-600">mỗi đêm</p>
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            </div>

            {/* Guest Information */}
            <div className="bg-white rounded-lg shadow-lg p-6">
                <div className="flex items-center justify-between mb-4">
                    <h3 className="text-lg font-semibold text-gray-900">Thông tin khách hàng</h3>
                    <button
                        onClick={handleEditGuestInfo}
                        className="flex items-center gap-2 text-blue-600 hover:text-blue-700 font-medium"
                    >
                        <Edit size={16} />
                        Chỉnh sửa
                    </button>
                </div>

                {state.guestInfo ? (
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                        <div className="flex items-center gap-3">
                            <User className="text-gray-400" size={20} />
                            <div>
                                <p className="text-sm text-gray-600">Họ và tên</p>
                                <p className="font-medium">{state.guestInfo.lastName} {state.guestInfo.firstName}</p>
                            </div>
                        </div>

                        <div className="flex items-center gap-3">
                            <Mail className="text-gray-400" size={20} />
                            <div>
                                <p className="text-sm text-gray-600">Email</p>
                                <p className="font-medium">{state.guestInfo.email}</p>
                            </div>
                        </div>

                        <div className="flex items-center gap-3">
                            <Phone className="text-gray-400" size={20} />
                            <div>
                                <p className="text-sm text-gray-600">Số điện thoại</p>
                                <p className="font-medium">{state.guestInfo.phoneNumber}</p>
                            </div>
                        </div>

                        <div className="flex items-center gap-3">
                            <Globe className="text-gray-400" size={20} />
                            <div>
                                <p className="text-sm text-gray-600">Quốc tịch</p>
                                <p className="font-medium">{state.guestInfo.nationality}</p>
                            </div>
                        </div>

                        {state.guestInfo.specialRequests && (
                            <div className="md:col-span-2 flex items-start gap-3">
                                <MapPin className="text-gray-400 mt-1" size={20} />
                                <div>
                                    <p className="text-sm text-gray-600">Yêu cầu đặc biệt</p>
                                    <p className="font-medium">{state.guestInfo.specialRequests}</p>
                                </div>
                            </div>
                        )}
                    </div>
                ) : (
                    <div className="text-center py-8">
                        <p className="text-gray-500">Chưa có thông tin khách hàng</p>
                        <button
                            onClick={handleEditGuestInfo}
                            className="mt-2 text-blue-600 hover:text-blue-700 font-medium"
                        >
                            Nhập thông tin khách hàng
                        </button>
                    </div>
                )}
            </div>

            {/* Price Breakdown */}
            <div className="bg-white rounded-lg shadow-lg p-6">
                <h3 className="text-lg font-semibold text-gray-900 mb-4">Chi tiết thanh toán</h3>

                <div className="space-y-3">
                    {/* Room costs */}
                    {selectedRoomDetails.map((selectedRoom, index) => (
                        <div key={index} className="flex justify-between items-center py-2">
                            <span className="text-gray-600">
                                {selectedRoom.roomDetails?.name} × {selectedRoom.quantity} × {nights} đêm
                            </span>
                            <span className="font-medium">
                                {formatPrice((selectedRoom.roomDetails?.price || 0) * selectedRoom.quantity * nights)}
                            </span>
                        </div>
                    ))}

                    <hr className="my-3" />

                    {/* Subtotal */}
                    <div className="flex justify-between items-center py-2">
                        <span className="text-gray-600">Tạm tính</span>
                        <span className="font-medium">{formatPrice(totalAmount)}</span>
                    </div>

                    {/* Service charge */}
                    <div className="flex justify-between items-center py-2">
                        <span className="text-gray-600">Phí dịch vụ (5%)</span>
                        <span className="font-medium">{formatPrice(serviceCharge)}</span>
                    </div>

                    {/* Tax */}
                    <div className="flex justify-between items-center py-2">
                        <span className="text-gray-600">Thuế VAT (10%)</span>
                        <span className="font-medium">{formatPrice(taxAmount)}</span>
                    </div>

                    <hr className="my-3" />

                    {/* Total */}
                    <div className="flex justify-between items-center py-2 text-lg font-bold">
                        <span>Tổng cộng</span>
                        <span className="text-blue-600">{formatPrice(finalAmount)}</span>
                    </div>
                </div>

                {/* Payment info */}
                <div className="mt-4 p-4 bg-green-50 border border-green-200 rounded-lg">
                    <h4 className="font-medium text-green-900 mb-2">💳 Thông tin thanh toán</h4>
                    <ul className="text-sm text-green-700 space-y-1">
                        <li>• Thanh toán an toàn và bảo mật</li>
                        <li>• Hỗ trợ thẻ Visa, MasterCard, JCB</li>
                        <li>• Không tính phí thêm khi thanh toán bằng thẻ</li>
                        <li>• Có thể hủy miễn phí trong 24h</li>
                    </ul>
                </div>
            </div>            {/* Error Message */}
            {state.error && (
                <div className="bg-red-50 border border-red-200 rounded-lg p-4">
                    <div className="flex">
                        <div className="flex-shrink-0">
                            <svg className="h-5 w-5 text-red-400" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor">
                                <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clipRule="evenodd" />
                            </svg>
                        </div>
                        <div className="ml-3">
                            <h3 className="text-sm font-medium text-red-800">
                                Có lỗi xảy ra
                            </h3>
                            <div className="mt-2 text-sm text-red-700">
                                <p>{state.error}</p>
                            </div>
                        </div>
                    </div>
                </div>
            )}

            {/* Action Buttons */}
            <div className="flex flex-col sm:flex-row justify-between gap-4 pt-6">
                <button
                    onClick={handleBackToGuestInfo}
                    disabled={state.isLoading}
                    className="flex items-center justify-center gap-2 px-6 py-3 border border-gray-300 text-gray-700 rounded-lg font-medium hover:bg-gray-50 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                >
                    <ArrowLeft size={16} />
                    Quay lại
                </button>

                <button
                    onClick={handleProceedToPayment}
                    disabled={state.isLoading}
                    className="flex items-center justify-center gap-2 bg-blue-600 text-white px-8 py-3 rounded-lg font-medium hover:bg-blue-700 focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                >
                    {state.isLoading ? (
                        <>
                            <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
                            Đang xử lý...
                        </>
                    ) : (
                        <>
                            Tiến hành thanh toán
                            <ArrowRight size={16} />
                        </>
                    )}
                </button>
            </div>
        </div>
    );
}
