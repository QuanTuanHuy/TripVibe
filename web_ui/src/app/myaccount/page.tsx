"use client";

import React from 'react';
import {
    CreditCard,
    User,
    Lock,
    Settings,
    BaggageClaim,
    Heart,
    MessageCircle,
    HelpCircle,
    Info,
    Shield,
    FileText,
    Home,
    ChevronRight
} from 'lucide-react';
import Link from 'next/link';
import Header from '@/components/Header';

interface AccountSectionProps {
    title: string;
    items: {
        icon: React.ReactNode;
        label: string;
        link: string;
    }[];
}

const AccountSection: React.FC<AccountSectionProps> = ({ title, items }) => {
    return (
        <div className="bg-white rounded-lg shadow-sm border border-gray-100 mb-5">
            <h2 className="text-xl font-bold px-6 pt-5 pb-2">{title}</h2>
            <div>
                {items.map((item, index) => (
                    <Link
                        href={item.link}
                        key={index}
                        className="flex items-center justify-between py-3.5 border-t hover:bg-gray-50 px-6 transition-colors"
                    >
                        <div className="flex items-center">
                            <div className="text-[#0071c2] mr-4">
                                {item.icon}
                            </div>
                            <span className="font-medium">{item.label}</span>
                        </div>
                        <div className="text-[#0071c2]">
                            <ChevronRight className="h-5 w-5" />
                        </div>
                    </Link>
                ))}
            </div>
        </div>
    );
};

export default function MyAccountPage() {
    const paymentSection = {
        title: "Thông tin thanh toán",
        items: [
            {
                icon: <CreditCard className="h-5 w-5" />,
                label: "Tặng thưởng & Ví",
                link: "/myaccount/rewards"
            },
            {
                icon: <CreditCard className="h-5 w-5" />,
                label: "Phương thức thanh toán",
                link: "/myaccount/payment-methods"
            }
        ]
    };

    const accountSection = {
        title: "Quản lý tài khoản",
        items: [
            {
                icon: <User className="h-5 w-5" />,
                label: "Thông tin cá nhân",
                link: "/myaccount/personal-details"
            },
            {
                icon: <Lock className="h-5 w-5" />,
                label: "Cài đặt bảo mật",
                link: "/myaccount/security"
            },
            {
                icon: <User className="h-5 w-5" />,
                label: "Người đi cùng",
                link: "/myaccount/travel-companions"
            }
        ]
    };

    const settingsSection = {
        title: "Cài đặt",
        items: [
            {
                icon: <Settings className="h-5 w-5" />,
                label: "Cài đặt chung",
                link: "/myaccount/general-settings"
            },
            {
                icon: <MessageCircle className="h-5 w-5" />,
                label: "Cài đặt email",
                link: "/myaccount/email-settings"
            }
        ]
    };

    const travelSection = {
        title: "Hoạt động du lịch",
        items: [
            {
                icon: <BaggageClaim className="h-5 w-5" />,
                label: "Chuyến đi và đơn đặt",
                link: "/mytrips"
            },
            {
                icon: <Heart className="h-5 w-5" />,
                label: "Danh sách đã lưu",
                link: "/mywhishlists"
            },
            {
                icon: <MessageCircle className="h-5 w-5" />,
                label: "Đánh giá của tôi",
                link: "/myreviews"
            }
        ]
    };

    const helpSection = {
        title: "Trợ giúp",
        items: [
            {
                icon: <HelpCircle className="h-5 w-5" />,
                label: "Liên hệ dịch vụ khách hàng",
                link: "/help/customer-service"
            },
            {
                icon: <Info className="h-5 w-5" />,
                label: "Trung tâm thông tin về bảo mật",
                link: "/help/security-info"
            },
            {
                icon: <Shield className="h-5 w-5" />,
                label: "Giải quyết khiếu nại",
                link: "/help/complaints"
            }
        ]
    };

    const legalSection = {
        title: "Pháp lý và quyền riêng tư",
        items: [
            {
                icon: <Shield className="h-5 w-5" />,
                label: "Quản lý quyền riêng tư và dữ liệu",
                link: "/myaccount/privacy"
            },
            {
                icon: <FileText className="h-5 w-5" />,
                label: "Hướng dẫn nội dung",
                link: "/content-guidelines"
            }
        ]
    };

    const hostSection = {
        title: "Dành cho chủ chỗ nghỉ",
        items: [
            {
                icon: <Home className="h-5 w-5" />,
                label: "Đăng chỗ nghỉ",
                link: "/host/register"
            }
        ]
    };

    return (
        <div className="bg-[#f5f5f5] min-h-screen text-[#333333]">
            <Header />
            <div className="container mx-auto px-4 max-w-7xl mt-8">
                <h1 className="text-2xl font-bold mb-6 px-1">Tài khoản của tôi</h1>

                <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
                    <div className="lg:col-span-2">
                        <AccountSection {...paymentSection} />
                        <AccountSection {...accountSection} />
                        <AccountSection {...settingsSection} />
                        <AccountSection {...travelSection} />
                        <AccountSection {...helpSection} />
                        <AccountSection {...legalSection} />
                    </div>

                    <div className="lg:col-span-1">
                        <AccountSection {...hostSection} />

                        {/* Thông tin người dùng */}
                        <div className="bg-white rounded-lg shadow-sm border border-gray-100 mb-5 p-6">
                            <div className="flex items-center mb-5">
                                <div className="w-14 h-14 bg-[#003b95] rounded-full flex items-center justify-center text-white font-bold text-xl">
                                    N
                                </div>
                                <div className="ml-4">
                                    <h3 className="font-bold text-lg">Quản Tuấn Huy</h3>
                                    <div className="flex items-center">
                                        <span className="text-sm font-medium text-[#0071c2]">Genius Cấp 1</span>
                                        <svg viewBox="0 0 28 28" height="14" width="14" aria-hidden="true" className="ml-1"><path d="M14 2.333a.9.9 0 0 0-.9.9v23.434a.9.9 0 0 0 .9.9.9.9 0 0 0 .9-.9V3.233a.9.9 0 0 0-.9-.9zm3.6 2.7a.9.9 0 0 0-.9.9v18.034a.9.9 0 0 0 .9.9.9.9 0 0 0 .9-.9V5.933a.9.9 0 0 0-.9-.9zm-7.2 0a.9.9 0 0 0-.9.9v18.034a.9.9 0 0 0 .9.9.9.9 0 0 0 .9-.9V5.933a.9.9 0 0 0-.9-.9zm10.8 2.7a.9.9 0 0 0-.9.9v12.634a.9.9 0 0 0 .9.9.9.9 0 0 0 .9-.9V8.633a.9.9 0 0 0-.9-.9zm-14.4 0a.9.9 0 0 0-.9.9v12.634a.9.9 0 0 0 .9.9.9.9 0 0 0 .9-.9V8.633a.9.9 0 0 0-.9-.9zm18 2.7a.9.9 0 0 0-.9.9v7.234a.9.9 0 0 0 .9.9.9.9 0 0 0 .9-.9v-7.234a.9.9 0 0 0-.9-.9zm-21.6 0a.9.9 0 0 0-.9.9v7.234a.9.9 0 0 0 .9.9.9.9 0 0 0 .9-.9v-7.234a.9.9 0 0 0-.9-.9z" fill="#0071c2"></path></svg>
                                    </div>
                                </div>
                            </div>

                            <div className="mt-6">
                                <p className="text-sm font-medium text-gray-700 mb-2">Bạn còn 5 đơn đặt để lên Genius Cấp 2</p>
                                <div className="h-2 w-full bg-gray-200 rounded-full mb-2">
                                    <div className="h-2 bg-[#0071c2] rounded-full" style={{ width: '16.7%' }}></div>
                                </div>
                                <Link href="/myaccount/genius" className="text-[#0071c2] hover:underline text-sm font-medium">
                                    Kiểm tra tiến độ của bạn
                                </Link>
                            </div>

                            <div className="mt-6 border-t pt-5">
                                <p className="flex justify-between items-center mb-2">
                                    <span className="text-gray-700 font-medium">Chưa có Tín dụng hay voucher</span>
                                    <span className="font-bold">0</span>
                                </p>
                                <Link href="/myaccount/credits" className="text-[#0071c2] hover:underline text-sm font-medium">
                                    Xem chi tiết
                                </Link>
                            </div>
                        </div>

                        {/* Phần hoàn tất hồ sơ */}
                        <div className="bg-white rounded-lg shadow-sm border border-gray-100 p-6">
                            <h3 className="font-bold text-lg mb-3">Hoàn tất hồ sơ của bạn</h3>
                            <p className="text-gray-700 mb-5 text-sm">Hoàn thành hồ sơ và sử dụng thông tin này cho đơn đặt tới</p>
                            <div className="flex flex-wrap gap-2">
                                <button className="bg-[#0071c2] text-white px-4 py-2.5 rounded-md hover:bg-[#00487a] font-medium text-sm">
                                    Hoàn tất ngay
                                </button>
                                <button className="text-gray-700 px-4 py-2.5 border rounded-md hover:bg-gray-100 font-medium text-sm">
                                    Không phải bây giờ
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}
