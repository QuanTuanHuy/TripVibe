"use client";

import React, { useState } from 'react';
import { User, Mail, Phone, MapPin, Calendar, Globe, MessageSquare, ArrowRight } from 'lucide-react';
import { useBooking } from '@/context/BookingContext';

interface FormErrors {
    [key: string]: string;
}

export default function GuestInformation() {
    const { state, setGuestInfo, goToStep } = useBooking();

    const [formData, setFormData] = useState({
        touristID: state.guestInfo?.touristID || '',
        firstName: state.guestInfo?.firstName || '',
        lastName: state.guestInfo?.lastName || '',
        email: state.guestInfo?.email || '',
        phoneNumber: state.guestInfo?.phoneNumber || '',
        address: state.guestInfo?.address || '',
        dateOfBirth: state.guestInfo?.dateOfBirth || '',
        nationality: state.guestInfo?.nationality || 'VN',
        specialRequests: state.guestInfo?.specialRequests || ''
    });

    const [errors, setErrors] = useState<FormErrors>({});
    const [isSubmitting, setIsSubmitting] = useState(false);

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));

        // Clear error when user starts typing
        if (errors[name]) {
            setErrors(prev => ({
                ...prev,
                [name]: ''
            }));
        }
    };

    const validateForm = (): boolean => {
        const newErrors: FormErrors = {};        // Required fields
        if (!formData.touristID.trim()) {
            newErrors.touristID = 'Vui lòng nhập CCCD/Passport';
        } else if (formData.touristID.length < 9) {
            newErrors.touristID = 'CCCD/Passport phải có ít nhất 9 ký tự';
        } else {
            // Validate that it can be converted to number for backend compatibility
            const touristIDNumber = parseInt(formData.touristID);
            if (isNaN(touristIDNumber) || touristIDNumber <= 0) {
                newErrors.touristID = 'CCCD/Passport phải là số hợp lệ';
            }
        }

        if (!formData.firstName.trim()) {
            newErrors.firstName = 'Vui lòng nhập tên';
        }

        if (!formData.lastName.trim()) {
            newErrors.lastName = 'Vui lòng nhập họ';
        }

        if (!formData.email.trim()) {
            newErrors.email = 'Vui lòng nhập email';
        } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
            newErrors.email = 'Email không hợp lệ';
        } if (!formData.phoneNumber.trim()) {
            newErrors.phoneNumber = 'Vui lòng nhập số điện thoại';
        } else if (!/^[0-9+\-\s()]{10,15}$/.test(formData.phoneNumber.replace(/\s/g, ''))) {
            newErrors.phoneNumber = 'Số điện thoại không hợp lệ';
        }

        if (formData.dateOfBirth) {
            const birthDate = new Date(formData.dateOfBirth);
            const today = new Date();
            const age = today.getFullYear() - birthDate.getFullYear();
            if (age < 18 || age > 120) {
                newErrors.dateOfBirth = 'Tuổi phải từ 18-120';
            }
        }

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        if (!validateForm()) {
            return;
        }

        setIsSubmitting(true);

        try {
            // Save guest information to context
            setGuestInfo(formData);

            // Navigate to next step
            setTimeout(() => {
                goToStep('review');
                setIsSubmitting(false);
            }, 500);

        } catch (error) {
            console.error('Error saving guest information:', error);
            setIsSubmitting(false);
        }
    };

    const countries = [
        { code: 'VN', name: 'Việt Nam' },
        { code: 'US', name: 'Hoa Kỳ' },
        { code: 'GB', name: 'Vương quốc Anh' },
        { code: 'JP', name: 'Nhật Bản' },
        { code: 'KR', name: 'Hàn Quốc' },
        { code: 'CN', name: 'Trung Quốc' },
        { code: 'TH', name: 'Thái Lan' },
        { code: 'SG', name: 'Singapore' },
        { code: 'MY', name: 'Malaysia' },
        { code: 'ID', name: 'Indonesia' },
        { code: 'PH', name: 'Philippines' },
        { code: 'AU', name: 'Úc' },
        { code: 'CA', name: 'Canada' },
        { code: 'DE', name: 'Đức' },
        { code: 'FR', name: 'Pháp' },
        { code: 'IT', name: 'Ý' },
        { code: 'ES', name: 'Tây Ban Nha' },
        { code: 'NL', name: 'Hà Lan' },
        { code: 'CH', name: 'Thụy Sĩ' },
        { code: 'SE', name: 'Thụy Điển' },
        { code: 'NO', name: 'Na Uy' },
        { code: 'DK', name: 'Đan Mạch' },
        { code: 'FI', name: 'Phần Lan' },
        { code: 'RU', name: 'Nga' },
        { code: 'IN', name: 'Ấn Độ' },
        { code: 'BR', name: 'Brazil' },
        { code: 'MX', name: 'Mexico' },
        { code: 'AR', name: 'Argentina' },
        { code: 'ZA', name: 'Nam Phi' },
        { code: 'EG', name: 'Ai Cập' },
        { code: 'AE', name: 'UAE' },
        { code: 'SA', name: 'Ả Rập Saudi' },
        { code: 'TR', name: 'Thổ Nhĩ Kỳ' },
        { code: 'IL', name: 'Israel' },
        { code: 'NZ', name: 'New Zealand' }
    ];

    return (
        <div className="max-w-4xl mx-auto">
            <div className="bg-white rounded-lg shadow-lg p-6">
                <div className="mb-6">
                    <h2 className="text-2xl font-bold text-gray-900 mb-2">Thông tin khách hàng</h2>
                    <p className="text-gray-600">
                        Vui lòng điền đầy đủ thông tin để hoàn tất quá trình đặt phòng
                    </p>
                </div>

                <form onSubmit={handleSubmit} className="space-y-6">
                    {/* Personal Information */}
                    <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                        {/* Tourist ID / Passport */}
                        <div className="lg:col-span-2">
                            <label htmlFor="touristID" className="block text-sm font-medium text-gray-700 mb-2">
                                <User size={16} className="inline mr-2" />
                                CCCD / Passport *
                            </label>
                            <input
                                type="text"
                                id="touristID"
                                name="touristID"
                                value={formData.touristID}
                                onChange={handleInputChange}
                                placeholder="Nhập số CCCD hoặc Passport"
                                className={`w-full px-4 py-3 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 ${errors.touristID ? 'border-red-500' : 'border-gray-300'
                                    }`}
                            />
                            {errors.touristID && (
                                <p className="mt-1 text-sm text-red-600">{errors.touristID}</p>
                            )}
                        </div>

                        {/* First Name */}
                        <div>
                            <label htmlFor="firstName" className="block text-sm font-medium text-gray-700 mb-2">
                                Tên *
                            </label>
                            <input
                                type="text"
                                id="firstName"
                                name="firstName"
                                value={formData.firstName}
                                onChange={handleInputChange}
                                placeholder="Nhập tên"
                                className={`w-full px-4 py-3 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 ${errors.firstName ? 'border-red-500' : 'border-gray-300'
                                    }`}
                            />
                            {errors.firstName && (
                                <p className="mt-1 text-sm text-red-600">{errors.firstName}</p>
                            )}
                        </div>

                        {/* Last Name */}
                        <div>
                            <label htmlFor="lastName" className="block text-sm font-medium text-gray-700 mb-2">
                                Họ *
                            </label>
                            <input
                                type="text"
                                id="lastName"
                                name="lastName"
                                value={formData.lastName}
                                onChange={handleInputChange}
                                placeholder="Nhập họ"
                                className={`w-full px-4 py-3 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 ${errors.lastName ? 'border-red-500' : 'border-gray-300'
                                    }`}
                            />
                            {errors.lastName && (
                                <p className="mt-1 text-sm text-red-600">{errors.lastName}</p>
                            )}
                        </div>

                        {/* Email */}
                        <div>
                            <label htmlFor="email" className="block text-sm font-medium text-gray-700 mb-2">
                                <Mail size={16} className="inline mr-2" />
                                Email *
                            </label>
                            <input
                                type="email"
                                id="email"
                                name="email"
                                value={formData.email}
                                onChange={handleInputChange}
                                placeholder="example@email.com"
                                className={`w-full px-4 py-3 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 ${errors.email ? 'border-red-500' : 'border-gray-300'
                                    }`}
                            />
                            {errors.email && (
                                <p className="mt-1 text-sm text-red-600">{errors.email}</p>
                            )}
                        </div>                        {/* Phone */}
                        <div>
                            <label htmlFor="phoneNumber" className="block text-sm font-medium text-gray-700 mb-2">
                                <Phone size={16} className="inline mr-2" />
                                Số điện thoại *
                            </label>
                            <input
                                type="tel"
                                id="phoneNumber"
                                name="phoneNumber"
                                value={formData.phoneNumber}
                                onChange={handleInputChange}
                                placeholder="+84 123 456 789"
                                className={`w-full px-4 py-3 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 ${errors.phoneNumber ? 'border-red-500' : 'border-gray-300'}`}
                            />                            {errors.phoneNumber && (
                                <p className="mt-1 text-sm text-red-600">{errors.phoneNumber}</p>
                            )}
                        </div>

                        {/* Date of Birth */}
                        <div>
                            <label htmlFor="dateOfBirth" className="block text-sm font-medium text-gray-700 mb-2">
                                <Calendar size={16} className="inline mr-2" />
                                Ngày sinh
                            </label>
                            <input
                                type="date"
                                id="dateOfBirth"
                                name="dateOfBirth"
                                value={formData.dateOfBirth}
                                onChange={handleInputChange}
                                max={new Date().toISOString().split('T')[0]}
                                className={`w-full px-4 py-3 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 ${errors.dateOfBirth ? 'border-red-500' : 'border-gray-300'
                                    }`}
                            />
                            {errors.dateOfBirth && (
                                <p className="mt-1 text-sm text-red-600">{errors.dateOfBirth}</p>
                            )}
                        </div>

                        {/* Nationality */}
                        <div>
                            <label htmlFor="nationality" className="block text-sm font-medium text-gray-700 mb-2">
                                <Globe size={16} className="inline mr-2" />
                                Quốc tịch
                            </label>
                            <select
                                id="nationality"
                                name="nationality"
                                value={formData.nationality}
                                onChange={handleInputChange}
                                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                            >
                                {countries.map((country) => (
                                    <option key={country.code} value={country.code}>
                                        {country.name}
                                    </option>
                                ))}
                            </select>
                        </div>

                        {/* Address */}
                        <div className="lg:col-span-2">
                            <label htmlFor="address" className="block text-sm font-medium text-gray-700 mb-2">
                                <MapPin size={16} className="inline mr-2" />
                                Địa chỉ
                            </label>
                            <input
                                type="text"
                                id="address"
                                name="address"
                                value={formData.address}
                                onChange={handleInputChange}
                                placeholder="Nhập địa chỉ (không bắt buộc)"
                                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                            />
                        </div>

                        {/* Special Requests */}
                        <div className="lg:col-span-2">
                            <label htmlFor="specialRequests" className="block text-sm font-medium text-gray-700 mb-2">
                                <MessageSquare size={16} className="inline mr-2" />
                                Yêu cầu đặc biệt
                            </label>
                            <textarea
                                id="specialRequests"
                                name="specialRequests"
                                value={formData.specialRequests}
                                onChange={handleInputChange}
                                rows={4}
                                placeholder="Ví dụ: Phòng tầng cao, giường đôi, không hút thuốc, v.v. (không bắt buộc)"
                                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 resize-none"
                            />
                        </div>
                    </div>

                    {/* Privacy Notice */}
                    <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
                        <h4 className="font-medium text-blue-900 mb-2">🔒 Bảo mật thông tin</h4>
                        <p className="text-sm text-blue-700">
                            Thông tin cá nhân của bạn sẽ được bảo mật và chỉ sử dụng cho mục đích đặt phòng.
                            Chúng tôi tuân thủ nghiêm ngặt các quy định về bảo vệ dữ liệu cá nhân.
                        </p>
                    </div>

                    {/* Submit Button */}
                    <div className="flex justify-end pt-6 border-t border-gray-200">
                        <button
                            type="submit"
                            disabled={isSubmitting}
                            className="flex items-center gap-2 bg-blue-600 text-white px-8 py-3 rounded-lg font-medium hover:bg-blue-700 focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
                        >
                            {isSubmitting ? (
                                <>
                                    <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
                                    Đang xử lý...
                                </>
                            ) : (
                                <>
                                    Tiếp tục
                                    <ArrowRight size={16} />
                                </>
                            )}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}
