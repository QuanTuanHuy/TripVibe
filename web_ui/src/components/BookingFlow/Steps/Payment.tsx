"use client";

import React, { useState } from 'react';
import {
    CreditCard,
    Calendar,
    Lock,
    User,
    ArrowRight,
    ArrowLeft,
    Check,
    Shield,
    AlertCircle,
    Smartphone,
    Building2
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

interface PaymentFormErrors {
    [key: string]: string;
}

export default function Payment() {
    const { state, goToStep, setPaymentInfo, confirmBooking } = useBooking();

    const [paymentMethod, setPaymentMethod] = useState<'credit-card' | 'internet-banking' | 'e-wallet'>('credit-card');
    const [paymentData, setPaymentData] = useState({
        cardNumber: '',
        expiryDate: '',
        cvv: '',
        cardholderName: '',
        billingAddress: '',
        saveCard: false
    });
    const [errors, setErrors] = useState<PaymentFormErrors>({});
    const [isProcessing, setIsProcessing] = useState(false);
    const [showSuccess, setShowSuccess] = useState(false);    // Use calculated pricing from context
    const pricing = {
        subtotal: state.subtotal,
        tax: state.taxes,
        service: state.fees,
        total: state.total
    };

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
        const { name, value, type } = e.target;

        if (type === 'checkbox') {
            const checked = (e.target as HTMLInputElement).checked;
            setPaymentData(prev => ({
                ...prev,
                [name]: checked
            }));
        } else {
            let formattedValue = value;

            // Format card number
            if (name === 'cardNumber') {
                formattedValue = value.replace(/\s/g, '').replace(/(\d{4})/g, '$1 ').trim();
                if (formattedValue.length > 19) formattedValue = formattedValue.slice(0, 19);
            }

            // Format expiry date
            if (name === 'expiryDate') {
                formattedValue = value.replace(/\D/g, '').replace(/(\d{2})(\d)/, '$1/$2');
                if (formattedValue.length > 5) formattedValue = formattedValue.slice(0, 5);
            }

            // Format CVV
            if (name === 'cvv') {
                formattedValue = value.replace(/\D/g, '').slice(0, 4);
            }

            setPaymentData(prev => ({
                ...prev,
                [name]: formattedValue
            }));
        }

        // Clear error when user starts typing
        if (errors[name]) {
            setErrors(prev => ({
                ...prev,
                [name]: ''
            }));
        }
    };

    const validatePaymentForm = (): boolean => {
        const newErrors: PaymentFormErrors = {};

        if (paymentMethod === 'credit-card') {
            // Card number validation
            const cardNumber = paymentData.cardNumber.replace(/\s/g, '');
            if (!cardNumber) {
                newErrors.cardNumber = 'Vui lòng nhập số thẻ';
            } else if (cardNumber.length < 13 || cardNumber.length > 19) {
                newErrors.cardNumber = 'Số thẻ không hợp lệ';
            }

            // Expiry date validation
            if (!paymentData.expiryDate) {
                newErrors.expiryDate = 'Vui lòng nhập ngày hết hạn';
            } else if (!/^\d{2}\/\d{2}$/.test(paymentData.expiryDate)) {
                newErrors.expiryDate = 'Định dạng không hợp lệ (MM/YY)';
            } else {
                const [month, year] = paymentData.expiryDate.split('/');
                const currentDate = new Date();
                const expiryDate = new Date(2000 + parseInt(year), parseInt(month) - 1);
                if (expiryDate < currentDate) {
                    newErrors.expiryDate = 'Thẻ đã hết hạn';
                }
            }

            // CVV validation
            if (!paymentData.cvv) {
                newErrors.cvv = 'Vui lòng nhập mã CVV';
            } else if (paymentData.cvv.length < 3) {
                newErrors.cvv = 'Mã CVV không hợp lệ';
            }

            // Cardholder name validation
            if (!paymentData.cardholderName.trim()) {
                newErrors.cardholderName = 'Vui lòng nhập tên chủ thẻ';
            }
        }

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    }; const handlePayment = async () => {
        if (!validatePaymentForm()) {
            return;
        }

        setIsProcessing(true);

        try {
            // Save payment info first
            setPaymentInfo({
                method: paymentMethod,
                cardNumber: paymentData.cardNumber,
                cardHolder: paymentData.cardholderName,
                expiryDate: paymentData.expiryDate,
                cvv: paymentData.cvv
            });

            // Call API to confirm booking
            await confirmBooking();

            setShowSuccess(true);

            // The confirmBooking function will automatically navigate to confirmation step on success
        } catch (error) {
            console.error('Payment failed:', error);
            setErrors({ general: 'Thanh toán thất bại. Vui lòng thử lại.' });
        } finally {
            setIsProcessing(false);
        }
    };

    const handleBackToReview = () => {
        goToStep('review');
    };

    const getCardBrand = (cardNumber: string) => {
        const number = cardNumber.replace(/\s/g, '');
        if (number.startsWith('4')) return 'visa';
        if (number.startsWith('5') || number.startsWith('2')) return 'mastercard';
        if (number.startsWith('3')) return 'amex';
        return 'unknown';
    };

    if (showSuccess) {
        return (
            <div className="max-w-2xl mx-auto text-center py-12">
                <div className="bg-white rounded-lg shadow-lg p-8">
                    <div className="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-4">
                        <Check size={32} className="text-green-600" />
                    </div>
                    <h2 className="text-2xl font-bold text-gray-900 mb-2">Thanh toán thành công!</h2>
                    <p className="text-gray-600 mb-4">
                        Đang chuyển hướng đến trang xác nhận...
                    </p>
                    <div className="animate-spin rounded-full h-6 w-6 border-b-2 border-blue-600 mx-auto"></div>
                </div>
            </div>
        );
    }

    return (
        <div className="max-w-4xl mx-auto space-y-6">
            {/* Header */}
            <div className="bg-white rounded-lg shadow-lg p-6">
                <h2 className="text-2xl font-bold text-gray-900 mb-2">Thanh toán</h2>
                <p className="text-gray-600">
                    Hoàn tất thanh toán để xác nhận đặt phòng của bạn
                </p>
            </div>

            <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
                {/* Payment Form */}
                <div className="lg:col-span-2 space-y-6">
                    {/* Payment Method Selection */}
                    <div className="bg-white rounded-lg shadow-lg p-6">
                        <h3 className="text-lg font-semibold text-gray-900 mb-4">Phương thức thanh toán</h3>

                        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">                            {/* Credit Card */}
                            <button
                                onClick={() => setPaymentMethod('credit-card')}
                                className={`p-4 border-2 rounded-lg text-center transition-colors ${paymentMethod === 'credit-card'
                                    ? 'border-blue-500 bg-blue-50'
                                    : 'border-gray-200 hover:border-gray-300'
                                    }`}
                            >
                                <CreditCard size={24} className="mx-auto mb-2 text-gray-600" />
                                <span className="text-sm font-medium">Thẻ tín dụng</span>
                            </button>

                            {/* Internet Banking */}
                            <button
                                onClick={() => setPaymentMethod('internet-banking')}
                                className={`p-4 border-2 rounded-lg text-center transition-colors ${paymentMethod === 'internet-banking'
                                    ? 'border-blue-500 bg-blue-50'
                                    : 'border-gray-200 hover:border-gray-300'
                                    }`}
                            >
                                <Building2 size={24} className="mx-auto mb-2 text-gray-600" />
                                <span className="text-sm font-medium">Internet Banking</span>
                            </button>

                            {/* E-Wallet */}
                            <button
                                onClick={() => setPaymentMethod('e-wallet')}
                                className={`p-4 border-2 rounded-lg text-center transition-colors ${paymentMethod === 'e-wallet'
                                    ? 'border-blue-500 bg-blue-50'
                                    : 'border-gray-200 hover:border-gray-300'
                                    }`}
                            >
                                <Smartphone size={24} className="mx-auto mb-2 text-gray-600" />
                                <span className="text-sm font-medium">Ví điện tử</span>
                            </button>
                        </div>
                    </div>                    {/* Payment Form */}
                    {paymentMethod === 'credit-card' && (
                        <div className="bg-white rounded-lg shadow-lg p-6">
                            <h3 className="text-lg font-semibold text-gray-900 mb-4">Thông tin thẻ tín dụng</h3>

                            <div className="space-y-4">
                                {/* Card Number */}
                                <div>
                                    <label htmlFor="cardNumber" className="block text-sm font-medium text-gray-700 mb-2">
                                        <CreditCard size={16} className="inline mr-2" />
                                        Số thẻ *
                                    </label>
                                    <div className="relative">
                                        <input
                                            type="text"
                                            id="cardNumber"
                                            name="cardNumber"
                                            value={paymentData.cardNumber}
                                            onChange={handleInputChange}
                                            placeholder="1234 5678 9012 3456"
                                            className={`w-full px-4 py-3 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 ${errors.cardNumber ? 'border-red-500' : 'border-gray-300'
                                                }`}
                                        />
                                        {paymentData.cardNumber && (
                                            <div className="absolute right-3 top-1/2 transform -translate-y-1/2">
                                                <span className="text-xs text-gray-500 uppercase">
                                                    {getCardBrand(paymentData.cardNumber)}
                                                </span>
                                            </div>
                                        )}
                                    </div>
                                    {errors.cardNumber && (
                                        <p className="mt-1 text-sm text-red-600">{errors.cardNumber}</p>
                                    )}
                                </div>

                                <div className="grid grid-cols-2 gap-4">
                                    {/* Expiry Date */}
                                    <div>
                                        <label htmlFor="expiryDate" className="block text-sm font-medium text-gray-700 mb-2">
                                            <Calendar size={16} className="inline mr-2" />
                                            Ngày hết hạn *
                                        </label>
                                        <input
                                            type="text"
                                            id="expiryDate"
                                            name="expiryDate"
                                            value={paymentData.expiryDate}
                                            onChange={handleInputChange}
                                            placeholder="MM/YY"
                                            className={`w-full px-4 py-3 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 ${errors.expiryDate ? 'border-red-500' : 'border-gray-300'
                                                }`}
                                        />
                                        {errors.expiryDate && (
                                            <p className="mt-1 text-sm text-red-600">{errors.expiryDate}</p>
                                        )}
                                    </div>

                                    {/* CVV */}
                                    <div>
                                        <label htmlFor="cvv" className="block text-sm font-medium text-gray-700 mb-2">
                                            <Lock size={16} className="inline mr-2" />
                                            CVV *
                                        </label>
                                        <input
                                            type="text"
                                            id="cvv"
                                            name="cvv"
                                            value={paymentData.cvv}
                                            onChange={handleInputChange}
                                            placeholder="123"
                                            className={`w-full px-4 py-3 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 ${errors.cvv ? 'border-red-500' : 'border-gray-300'
                                                }`}
                                        />
                                        {errors.cvv && (
                                            <p className="mt-1 text-sm text-red-600">{errors.cvv}</p>
                                        )}
                                    </div>
                                </div>

                                {/* Cardholder Name */}
                                <div>
                                    <label htmlFor="cardholderName" className="block text-sm font-medium text-gray-700 mb-2">
                                        <User size={16} className="inline mr-2" />
                                        Tên chủ thẻ *
                                    </label>
                                    <input
                                        type="text"
                                        id="cardholderName"
                                        name="cardholderName"
                                        value={paymentData.cardholderName}
                                        onChange={handleInputChange}
                                        placeholder="NGUYEN VAN A"
                                        className={`w-full px-4 py-3 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 ${errors.cardholderName ? 'border-red-500' : 'border-gray-300'
                                            }`}
                                    />
                                    {errors.cardholderName && (
                                        <p className="mt-1 text-sm text-red-600">{errors.cardholderName}</p>
                                    )}
                                </div>

                                {/* Save Card Option */}
                                <div className="flex items-center">
                                    <input
                                        type="checkbox"
                                        id="saveCard"
                                        name="saveCard"
                                        checked={paymentData.saveCard}
                                        onChange={handleInputChange}
                                        className="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
                                    />
                                    <label htmlFor="saveCard" className="ml-2 block text-sm text-gray-700">
                                        Lưu thông tin thẻ cho lần thanh toán tiếp theo
                                    </label>
                                </div>
                            </div>
                        </div>
                    )}                    {/* Alternative Payment Methods */}
                    {paymentMethod === 'internet-banking' && (
                        <div className="bg-white rounded-lg shadow-lg p-6">
                            <h3 className="text-lg font-semibold text-gray-900 mb-4">Internet Banking</h3>
                            <div className="text-center py-8">
                                <Building2 size={48} className="mx-auto mb-4 text-gray-400" />                                <p className="text-gray-600 mb-4">
                                    Bạn sẽ được chuyển hướng đến trang ngân hàng để hoàn tất thanh toán
                                </p>
                                <div className="flex justify-center gap-4">
                                    <div className="h-8 w-16 bg-gray-200 rounded flex items-center justify-center text-xs font-medium">VCB</div>
                                    <div className="h-8 w-16 bg-gray-200 rounded flex items-center justify-center text-xs font-medium">BIDV</div>
                                    <div className="h-8 w-16 bg-gray-200 rounded flex items-center justify-center text-xs font-medium">VTB</div>
                                    <div className="h-8 w-16 bg-gray-200 rounded flex items-center justify-center text-xs font-medium">TCB</div>
                                </div>
                            </div>
                        </div>
                    )}

                    {paymentMethod === 'e-wallet' && (
                        <div className="bg-white rounded-lg shadow-lg p-6">
                            <h3 className="text-lg font-semibold text-gray-900 mb-4">Ví điện tử</h3>
                            <div className="text-center py-8">
                                <Smartphone size={48} className="mx-auto mb-4 text-gray-400" />                                <p className="text-gray-600 mb-4">
                                    Chọn ví điện tử để thanh toán
                                </p>
                                <div className="flex justify-center gap-4">
                                    <div className="h-8 w-16 bg-gray-200 rounded flex items-center justify-center text-xs font-medium">MoMo</div>
                                    <div className="h-8 w-16 bg-gray-200 rounded flex items-center justify-center text-xs font-medium">ZaloPay</div>
                                    <div className="h-8 w-16 bg-gray-200 rounded flex items-center justify-center text-xs font-medium">VNPay</div>
                                    <div className="h-8 w-16 bg-gray-200 rounded flex items-center justify-center text-xs font-medium">ShopeePay</div>
                                </div>
                            </div>
                        </div>
                    )}

                    {/* Security Notice */}
                    <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
                        <div className="flex items-start gap-3">
                            <Shield className="text-blue-600 mt-1" size={20} />
                            <div>
                                <h4 className="font-medium text-blue-900 mb-1">🔒 Thanh toán an toàn</h4>
                                <ul className="text-sm text-blue-700 space-y-1">
                                    <li>• Thông tin thẻ được mã hóa SSL 256-bit</li>
                                    <li>• Tuân thủ chuẩn bảo mật PCI DSS</li>
                                    <li>• Không lưu trữ thông tin thẻ trên hệ thống</li>
                                    <li>• Được bảo vệ bởi hệ thống phát hiện gian lận</li>
                                </ul>
                            </div>
                        </div>
                    </div>
                </div>

                {/* Payment Summary */}
                <div className="lg:col-span-1">
                    <div className="bg-white rounded-lg shadow-lg p-6 sticky top-6">
                        <h3 className="text-lg font-semibold text-gray-900 mb-4">Tóm tắt đơn hàng</h3>

                        <div className="space-y-3">
                            {/* Room costs */}
                            {state.selectedRooms.map((selectedRoom, index) => {
                                const room = sampleRooms.find(r => r.id === selectedRoom.roomId);
                                const nights = state.bookingDates?.nights || 0;
                                return (
                                    <div key={index} className="flex justify-between text-sm">
                                        <span className="text-gray-600">
                                            {room?.name} × {selectedRoom.quantity} × {nights} đêm
                                        </span>
                                        <span className="font-medium">
                                            {formatPrice((room?.price || 0) * selectedRoom.quantity * nights)}
                                        </span>
                                    </div>
                                );
                            })}

                            <hr className="my-3" />

                            <div className="flex justify-between text-sm">
                                <span className="text-gray-600">Tạm tính</span>
                                <span className="font-medium">{formatPrice(pricing.subtotal)}</span>
                            </div>

                            <div className="flex justify-between text-sm">
                                <span className="text-gray-600">Phí dịch vụ (5%)</span>
                                <span className="font-medium">{formatPrice(pricing.service)}</span>
                            </div>

                            <div className="flex justify-between text-sm">
                                <span className="text-gray-600">Thuế VAT (10%)</span>
                                <span className="font-medium">{formatPrice(pricing.tax)}</span>
                            </div>

                            <hr className="my-3" />

                            <div className="flex justify-between text-lg font-bold">
                                <span>Tổng cộng</span>
                                <span className="text-blue-600">{formatPrice(pricing.total)}</span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            {/* Error Message */}
            {errors.general && (
                <div className="bg-red-50 border border-red-200 rounded-lg p-4">
                    <div className="flex items-center gap-2">
                        <AlertCircle className="text-red-600" size={20} />
                        <p className="text-red-700">{errors.general}</p>
                    </div>
                </div>
            )}            {/* Error Messages */}
            {(errors.general || state.error) && (
                <div className="bg-red-50 border border-red-200 rounded-lg p-4">
                    <div className="flex">
                        <AlertCircle className="h-5 w-5 text-red-400 flex-shrink-0" />
                        <div className="ml-3">
                            <h3 className="text-sm font-medium text-red-800">
                                Có lỗi xảy ra
                            </h3>
                            <div className="mt-2 text-sm text-red-700">
                                <p>{errors.general || state.error}</p>
                            </div>
                        </div>
                    </div>
                </div>
            )}

            {/* Action Buttons */}
            <div className="flex flex-col sm:flex-row justify-between gap-4 pt-6">
                <button
                    onClick={handleBackToReview}
                    disabled={state.isLoading || isProcessing}
                    className="flex items-center justify-center gap-2 px-6 py-3 border border-gray-300 text-gray-700 rounded-lg font-medium hover:bg-gray-50 transition-colors disabled:opacity-50"
                >
                    <ArrowLeft size={16} />
                    Quay lại
                </button>

                <button
                    onClick={handlePayment}
                    disabled={state.isLoading || isProcessing}
                    className="flex items-center justify-center gap-2 bg-blue-600 text-white px-8 py-3 rounded-lg font-medium hover:bg-blue-700 focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 transition-colors disabled:opacity-50"
                >
                    {(state.isLoading || isProcessing) ? (
                        <>
                            <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
                            Đang xử lý...
                        </>
                    ) : (
                        <>
                            Thanh toán {formatPrice(pricing.total)}
                            <ArrowRight size={16} />
                        </>
                    )}
                </button>
            </div>
        </div>
    );
}
