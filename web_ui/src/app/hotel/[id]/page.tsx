"use client";

import { useState, useRef, useEffect } from 'react';
import Image from 'next/image';
import { ChevronRight, MapPin, Heart, Share, Star, X, Check, Coffee, Wifi, Car, Users, Clock, Utensils, GlassWater, Sunrise, Calendar, ChevronDown, Plus, Minus } from 'lucide-react';
import { BsGrid3X3Gap } from 'react-icons/bs';
import Header from '@/components/Header';
import { format } from 'date-fns';
import { vi } from 'date-fns/locale';
import { DayPicker } from 'react-day-picker';
import 'react-day-picker/dist/style.css';

export default function HotelDetailPage() {
    const [activeTab, setActiveTab] = useState('overview');
    // const [selectedRoom, setSelectedRoom] = useState(null);
    const [showAllPhotos, setShowAllPhotos] = useState(false);

    // Refs cho các phần tương ứng với tab
    const overviewRef = useRef<HTMLDivElement>(null);
    const infoRef = useRef<HTMLDivElement>(null);
    const amenitiesRef = useRef<HTMLDivElement>(null);
    const rulesRef = useRef<HTMLDivElement>(null);
    const reviewsRef = useRef<HTMLDivElement>(null);

    // Thêm state cho DatePicker và GuestSelector
    const [dateRange, setDateRange] = useState<{
        from: Date | undefined;
        to: Date | undefined;
    }>({ from: undefined, to: undefined });
    const [showDatePicker, setShowDatePicker] = useState(false);
    const [adults, setAdults] = useState(2);
    const [children, setChildren] = useState(0);
    const [rooms, setRooms] = useState(1);
    const [showGuestSelector, setShowGuestSelector] = useState(false);

    const datePickerRef = useRef<HTMLDivElement>(null);
    const guestSelectorRef = useRef<HTMLDivElement>(null);

    // Cập nhật hàm setActiveTab để cuộn đến phần tương ứng
    const handleTabClick = (tab: string) => {
        setActiveTab(tab);

        // Cuộn đến phần tương ứng
        switch (tab) {
            case 'overview':
                overviewRef.current?.scrollIntoView({ behavior: 'smooth' });
                break;
            case 'info':
                infoRef.current?.scrollIntoView({ behavior: 'smooth' });
                break;
            case 'amenities':
                amenitiesRef.current?.scrollIntoView({ behavior: 'smooth' });
                break;
            case 'rules':
                rulesRef.current?.scrollIntoView({ behavior: 'smooth' });
                break;
            case 'reviews':
                reviewsRef.current?.scrollIntoView({ behavior: 'smooth' });
                break;
            default:
                break;
        }
    };

    // Handle outside click for date picker and guest selector
    useEffect(() => {
        const handleClickOutside = (event: MouseEvent) => {
            if (datePickerRef.current && !datePickerRef.current.contains(event.target as Node)) {
                setShowDatePicker(false);
            }
            if (guestSelectorRef.current && !guestSelectorRef.current.contains(event.target as Node)) {
                setShowGuestSelector(false);
            }
        };

        document.addEventListener('mousedown', handleClickOutside);
        return () => {
            document.removeEventListener('mousedown', handleClickOutside);
        };
    }, []);

    // Tạo DateRangePicker component
    const DateRangePicker = () => {
        const today = new Date();

        return (
            <div ref={datePickerRef} className="relative">
                <div
                    className="flex items-center cursor-pointer"
                    onClick={() => setShowDatePicker(!showDatePicker)}
                >
                    <Calendar className="w-6 h-6 text-gray-500 mr-2" />
                    <div className="text-gray-800">
                        {dateRange.from ? (
                            <>
                                {format(dateRange.from, 'EEE, dd/MM/yyyy', { locale: vi })}
                                {dateRange.to ? ` — ${format(dateRange.to, 'EEE, dd/MM/yyyy', { locale: vi })}` : ''}
                            </>
                        ) : (
                            "Chọn ngày"
                        )}
                    </div>
                </div>

                {showDatePicker && (
                    <div className="absolute left-0 top-full mt-1 bg-white shadow-lg rounded-lg border border-gray-200 p-4 z-[999]">
                        <DayPicker
                            mode="range"
                            locale={vi}
                            selected={dateRange}
                            onSelect={(range) => {
                                if (range) {
                                    setDateRange({
                                        from: range.from,
                                        to: range.to || undefined
                                    });
                                } else {
                                    setDateRange({ from: undefined, to: undefined });
                                }
                                // Đã bỏ việc tự động đóng DatePicker khi chọn xong để người dùng có thể xem lại lựa chọn
                            }}
                            numberOfMonths={2}
                            fromMonth={today}
                            modifiers={{
                                disabled: { before: today }
                            }}
                            styles={{
                                caption: { color: '#2563eb' },
                                day: { margin: '2px' }
                            }}
                            footer={
                                <div className="pt-4 flex justify-end">
                                    <button
                                        onClick={() => setShowDatePicker(false)}
                                        className="px-4 py-2 bg-blue-600 text-white rounded font-medium hover:bg-blue-700"
                                    >
                                        Xong
                                    </button>
                                </div>
                            }
                        />
                    </div>
                )}
            </div>
        );
    };

    // Tạo GuestSelector component
    const GuestSelector = () => {
        // Format display text
        const formatGuestCount = () => {
            const roomsText = rooms === 1 ? '1 phòng' : `${rooms} phòng`;
            const adultsText = adults === 1 ? '1 người lớn' : `${adults} người lớn`;
            const childrenText = children === 0 ? '0 trẻ em' : children === 1 ? '1 trẻ em' : `${children} trẻ em`;

            return `${adultsText} · ${childrenText} · ${roomsText}`;
        };

        // Increment/decrement counter helpers
        const increment = (value: number, setValue: (count: number) => void, max = 30) => {
            if (value < max) setValue(value + 1);
        };

        const decrement = (value: number, setValue: (count: number) => void, min = 0) => {
            if (value > min) setValue(value - 1);
        };

        return (
            <div ref={guestSelectorRef} className="relative">
                <div
                    className="flex items-center cursor-pointer"
                    onClick={() => setShowGuestSelector(!showGuestSelector)}
                >
                    <Users className="w-6 h-6 text-gray-500 mr-2" />
                    <div className="text-gray-800">
                        {formatGuestCount()}
                    </div>
                    <ChevronDown className="w-4 h-4 ml-2 text-gray-500" />
                </div>

                {showGuestSelector && (
                    <div className="absolute right-0 mt-1 z-[999]">
                        <div
                            className="bg-white shadow-lg rounded-md border border-gray-200 p-4 min-w-[300px]"
                            onClick={(e) => e.stopPropagation()}
                        >
                            <div className="space-y-4">
                                {/* Adults */}
                                <div className="flex items-center justify-between">
                                    <div>
                                        <h3 className="font-medium">Người lớn</h3>
                                    </div>
                                    <div className="flex items-center gap-3">
                                        <button
                                            onClick={() => decrement(adults, setAdults, 1)}
                                            disabled={adults <= 1}
                                            className={`w-8 h-8 rounded-full flex items-center justify-center border ${adults <= 1 ? 'border-gray-200 text-gray-300' : 'border-blue-500 text-blue-500 hover:bg-blue-50'}`}
                                        >
                                            <Minus size={16} />
                                        </button>
                                        <span className="w-6 text-center">{adults}</span>
                                        <button
                                            onClick={() => increment(adults, setAdults, 30)}
                                            className="w-8 h-8 rounded-full flex items-center justify-center border border-blue-500 text-blue-500 hover:bg-blue-50"
                                        >
                                            <Plus size={16} />
                                        </button>
                                    </div>
                                </div>

                                {/* Children */}
                                <div className="flex items-center justify-between">
                                    <div>
                                        <h3 className="font-medium">Trẻ em</h3>
                                        <p className="text-xs text-gray-500">0-17 tuổi</p>
                                    </div>
                                    <div className="flex items-center gap-3">
                                        <button
                                            onClick={() => decrement(children, setChildren)}
                                            disabled={children <= 0}
                                            className={`w-8 h-8 rounded-full flex items-center justify-center border ${children <= 0 ? 'border-gray-200 text-gray-300' : 'border-blue-500 text-blue-500 hover:bg-blue-50'}`}
                                        >
                                            <Minus size={16} />
                                        </button>
                                        <span className="w-6 text-center">{children}</span>
                                        <button
                                            onClick={() => increment(children, setChildren, 10)}
                                            className="w-8 h-8 rounded-full flex items-center justify-center border border-blue-500 text-blue-500 hover:bg-blue-50"
                                        >
                                            <Plus size={16} />
                                        </button>
                                    </div>
                                </div>

                                {/* Rooms */}
                                <div className="flex items-center justify-between">
                                    <div>
                                        <h3 className="font-medium">Phòng</h3>
                                    </div>
                                    <div className="flex items-center gap-3">
                                        <button
                                            onClick={() => decrement(rooms, setRooms, 1)}
                                            disabled={rooms <= 1}
                                            className={`w-8 h-8 rounded-full flex items-center justify-center border ${rooms <= 1 ? 'border-gray-200 text-gray-300' : 'border-blue-500 text-blue-500 hover:bg-blue-50'}`}
                                        >
                                            <Minus size={16} />
                                        </button>
                                        <span className="w-6 text-center">{rooms}</span>
                                        <button
                                            onClick={() => increment(rooms, setRooms, 30)}
                                            className="w-8 h-8 rounded-full flex items-center justify-center border border-blue-500 text-blue-500 hover:bg-blue-50"
                                        >
                                            <Plus size={16} />
                                        </button>
                                    </div>
                                </div>

                                <div className="pt-4 flex justify-end">
                                    <button
                                        onClick={() => setShowGuestSelector(false)}
                                        className="px-4 py-2 bg-blue-600 text-white rounded font-medium hover:bg-blue-700"
                                    >
                                        Xong
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                )}
            </div>
        );
    };

    const hotelImages = [
        'https://cf.bstatic.com/xdata/images/hotel/max1024x768/671195663.jpg?k=39185645c6898d5af4b1a1b80e99a7f8dd1952499129aae3b80976710954514a&o=',
        'https://cf.bstatic.com/xdata/images/hotel/max500/671195647.jpg?k=feb8733e673a00c6eebbc39596e2589b545220aa2275ada0ca89e69a5e7e7194&o=',
        'https://cf.bstatic.com/xdata/images/hotel/max500/671195405.jpg?k=b7437d39af4079c50880469a7677471b66334a2f0a2d3255d14b8737299379dd&o=',
        'https://cf.bstatic.com/xdata/images/hotel/max300/671195288.jpg?k=fac36e0c2dac5c462917c7d918be2cf8a9500a6b4e814e8bcbafc6273ae38dc6&o=',
        'https://cf.bstatic.com/xdata/images/hotel/max300/671195751.jpg?k=aa6f66b5f398592d25067abbcb6b941813302e33a770cc9c5b993a5c15cc8e2c&o=',
        'https://cf.bstatic.com/xdata/images/hotel/max300/671195288.jpg?k=fac36e0c2dac5c462917c7d918be2cf8a9500a6b4e814e8bcbafc6273ae38dc6&o=',
        'https://cf.bstatic.com/xdata/images/hotel/max300/671195751.jpg?k=aa6f66b5f398592d25067abbcb6b941813302e33a770cc9c5b993a5c15cc8e2c&o=',
        'https://cf.bstatic.com/xdata/images/hotel/max300/671195288.jpg?k=fac36e0c2dac5c462917c7d918be2cf8a9500a6b4e814e8bcbafc6273ae38dc6&o=',
        'https://cf.bstatic.com/xdata/images/hotel/max300/671195751.jpg?k=aa6f66b5f398592d25067abbcb6b941813302e33a770cc9c5b993a5c15cc8e2c&o=',
        'https://cf.bstatic.com/xdata/images/hotel/max300/671195288.jpg?k=fac36e0c2dac5c462917c7d918be2cf8a9500a6b4e814e8bcbafc6273ae38dc6&o=',
        'https://cf.bstatic.com/xdata/images/hotel/max300/671195751.jpg?k=aa6f66b5f398592d25067abbcb6b941813302e33a770cc9c5b993a5c15cc8e2c&o=',
        'https://cf.bstatic.com/xdata/images/hotel/max300/671195288.jpg?k=fac36e0c2dac5c462917c7d918be2cf8a9500a6b4e814e8bcbafc6273ae38dc6&o=',
        'https://cf.bstatic.com/xdata/images/hotel/max300/671195751.jpg?k=aa6f66b5f398592d25067abbcb6b941813302e33a770cc9c5b993a5c15cc8e2c&o=',
    ];

    return (
        <div>
            <Header />
            <div className="min-h-screen bg-gray-50 text-gray-800">
                {/* Navigation Breadcrumb */}
                <div className="bg-white py-3 px-4 border-b border-gray-200">
                    <div className="max-w-7xl mx-auto flex items-center text-sm gap-1">
                        <a href="#" className="text-blue-600 hover:underline">Trang chủ</a>
                        <ChevronRight size={14} className="text-gray-400" />
                        <a href="#" className="text-blue-600 hover:underline">Khách sạn</a>
                        <ChevronRight size={14} className="text-gray-400" />
                        <a href="#" className="text-blue-600 hover:underline">Việt Nam</a>
                        <ChevronRight size={14} className="text-gray-400" />
                        <a href="#" className="text-blue-600 hover:underline">Hà Nội</a>
                        <ChevronRight size={14} className="text-gray-400" />
                        <a href="#" className="text-blue-600 hover:underline">Quận Hoàn Kiếm</a>
                        <ChevronRight size={14} className="text-gray-400" />
                        <span className="text-gray-500">La Passion Classic Hotel (Khách sạn), Hà Nội (Việt Nam)</span>
                    </div>
                </div>

                {/* Main Content */}
                <div className="max-w-7xl mx-auto pt-4 px-4">
                    {/* Tabs */}
                    <div className="flex border-b border-gray-200">
                        <button
                            onClick={() => handleTabClick('overview')}
                            className={`px-6 py-4 font-medium ${activeTab === 'overview' ? 'text-blue-600 border-b-2 border-blue-600' : 'text-gray-600'}`}
                        >
                            Tổng quan
                        </button>
                        <button
                            onClick={() => handleTabClick('info')}
                            className={`px-6 py-4 font-medium ${activeTab === 'info' ? 'text-blue-600 border-b-2 border-blue-600' : 'text-gray-600'}`}
                        >
                            Thông tin & giá
                        </button>
                        <button
                            onClick={() => handleTabClick('amenities')}
                            className={`px-6 py-4 font-medium ${activeTab === 'amenities' ? 'text-blue-600 border-b-2 border-blue-600' : 'text-gray-600'}`}
                        >
                            Tiện nghi
                        </button>
                        <button
                            onClick={() => handleTabClick('rules')}
                            className={`px-6 py-4 font-medium ${activeTab === 'rules' ? 'text-blue-600 border-b-2 border-blue-600' : 'text-gray-600'}`}
                        >
                            Quy tắc chung
                        </button>
                        <button
                            onClick={() => handleTabClick('reviews')}
                            className={`px-6 py-4 font-medium ${activeTab === 'reviews' ? 'text-blue-600 border-b-2 border-blue-600' : 'text-gray-600'}`}
                        >
                            Đánh giá của khách (689)
                        </button>
                    </div>

                    {/* Hotel Title & Rating */}
                    <div className="mt-6 flex justify-between items-start">
                        <div>
                            <div className="flex items-center gap-2 mb-3">
                                <div className="flex">
                                    {[1, 2, 3, 4].map(star => (
                                        <Star key={star} fill="#FFB700" stroke="#FFB700" size={20} />
                                    ))}
                                </div>
                                <div className="bg-blue-500 text-white px-2 py-1 text-xs font-bold rounded">
                                    8.8
                                </div>
                                <span className="text-gray-600 text-sm">Xe đưa đón sân bay</span>
                            </div>
                            <h1 className="text-3xl font-bold mb-4">La Passion Classic Hotel</h1>
                            <div className="flex items-start gap-2 text-sm">
                                <MapPin className="text-blue-600 mt-0.5" size={18} />
                                <div>
                                    <p>No 1 Phan Dinh Phung street, Hoan Kiem district, Quận Hoàn Kiếm, Hà Nội, Việt Nam</p>
                                    <button className="text-blue-600 font-medium">Vị trí xuất sắc - hiển thị bản đồ</button>
                                </div>
                            </div>
                        </div>
                        <div className="flex gap-2">
                            <button className="p-2 border border-gray-300 rounded-full">
                                <Heart size={20} className="text-gray-600" />
                            </button>
                            <button className="p-2 border border-gray-300 rounded-full">
                                <Share size={20} className="text-gray-600" />
                            </button>
                            <button className="px-6 py-2 bg-blue-600 text-white font-medium rounded">
                                Đặt ngay
                            </button>
                        </div>
                    </div>

                    {/* Gallery */}
                    <div className="mb-8 relative">
                        {showAllPhotos ? (
                            <div className="fixed inset-0 bg-white z-50 overflow-y-auto">
                                <div className="sticky top-0 bg-white p-4 border-b flex justify-between items-center">
                                    <h2 className="text-xl font-bold">La Passion Classic Hotel - Tất cả hình ảnh</h2>
                                    <button
                                        onClick={() => setShowAllPhotos(false)}
                                        className="bg-gray-200 hover:bg-gray-300 text-gray-800 px-4 py-2 rounded-md"
                                    >
                                        Đóng
                                    </button>
                                </div>
                                <div className="p-4">
                                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                                        {hotelImages.map((image, idx) => (
                                            <div key={idx} className="relative h-64 rounded-md overflow-hidden">
                                                <Image
                                                    src={image}
                                                    alt={`La Passion Classic Hotel - Ảnh ${idx + 1}`}
                                                    fill
                                                    className="object-cover"
                                                />
                                            </div>
                                        ))}
                                    </div>
                                </div>
                            </div>
                        ) : (
                            <div className="grid grid-cols-12 gap-2 h-96">
                                {/* Main large image */}
                                <div className="col-span-12 md:col-span-6 relative rounded-lg overflow-hidden cursor-pointer" onClick={() => setShowAllPhotos(true)}>
                                    <Image
                                        src={hotelImages[0]}
                                        alt="La Passion Classic Hotel - Ảnh chính"
                                        fill
                                        className="object-cover"
                                        priority
                                    />
                                </div>

                                {/* Right side smaller images */}
                                <div className="hidden md:grid md:col-span-6 grid-cols-2 gap-2">
                                    {hotelImages.slice(1, 5).map((image, idx) => (
                                        <div key={idx} className="relative rounded-lg overflow-hidden cursor-pointer" onClick={() => setShowAllPhotos(true)}>
                                            <Image
                                                src={image}
                                                alt={`La Passion Classic Hotel - Ảnh ${idx + 2}`}
                                                fill
                                                className="object-cover"
                                            />
                                        </div>
                                    ))}
                                </div>

                                {/* View all photos button */}
                                <button
                                    onClick={() => setShowAllPhotos(true)}
                                    className="absolute bottom-4 right-4 bg-white text-gray-800 px-4 py-2 rounded-md shadow-md flex items-center font-medium text-sm"
                                >
                                    <BsGrid3X3Gap className="mr-2" />
                                    Xem tất cả ảnh
                                </button>
                            </div>
                        )}
                    </div>

                    {/* Hotel Description */}
                    <div className="grid grid-cols-3 gap-6 mt-10">
                        <div className="col-span-2">
                            <div ref={overviewRef} className="bg-yellow-50 border border-yellow-200 p-4 rounded mb-6">
                                <p className="text-sm">
                                    Bạn có thể đủ điều kiện hưởng giảm giá Genius tại La Passion Classic Hotel. Để biết giảm giá Genius có áp dụng cho ngày bạn đã chọn hay không, hãy <a href="#" className="text-blue-600 font-medium">đăng nhập</a>.
                                </p>
                            </div>

                            <p className="mb-4">
                                La Passion Classic Hotel cung cấp chỗ nghỉ tại trung tâm Khu Phố Cổ của thành phố Hà Nội, cách Chợ Đồng Xuân 300 m. Khách có thể dùng bữa tại nhà hàng trong khuôn viên hay nhâm nhi đồ uống ở quầy bar. Wi-Fi miễn phí có sẵn trong tất cả các phòng nghỉ.
                            </p>

                            <p className="mb-4">
                                La Passion Classic Hotel nằm trong bán kính 600 m từ Ô Quan Chưởng và 1,1 km từ Đền Ngọc Sơn. Sân bay Quốc tế Nội Bài cách đó 20 km.
                            </p>

                            <p className="mb-4">
                                Các phòng tại đây được trang bị TV màn hình phẳng và ấm đun nước. Phòng còn đi kèm phòng tắm riêng với bồn tắm. Khách sạn cung cấp áo choàng tắm và đồ vệ sinh cá nhân miễn phí.
                            </p>

                            <p className="mb-6">
                                Du khách có thể đến lễ tân 24 giờ để được hỗ trợ với dịch vụ thu đổi ngoại tệ, sắp xếp tour du lịch, đặt vé cũng như trợ giúp đặc biệt. Khách sạn cho thuê xe hơi và xe đạp để du khách đi khám phá khu vực xung quanh.
                            </p>

                            <button className="text-blue-600 font-medium">Tôi muốn xem thêm</button>

                            <h3 className="font-bold text-lg mt-8 mb-4">Các tiện nghi được ưa chuộng nhất</h3>

                            <div className="grid grid-cols-3 gap-4">
                                <div className="flex items-center gap-2">
                                    <Wifi className="text-green-600" size={20} />
                                    <span>WiFi miễn phí</span>
                                </div>
                                <div className="flex items-center gap-2">
                                    <Car className="text-green-600" size={20} />
                                    <span>Xe đưa đón sân bay</span>
                                </div>
                                <div className="flex items-center gap-2">
                                    <Users className="text-green-600" size={20} />
                                    <span>Phòng gia đình</span>
                                </div>
                                <div className="flex items-center gap-2">
                                    <Clock className="text-green-600" size={20} />
                                    <span>Lễ tân 24 giờ</span>
                                </div>
                                <div className="flex items-center gap-2">
                                    <Utensils className="text-green-600" size={20} />
                                    <span>Nhà hàng</span>
                                </div>
                                <div className="flex items-center gap-2">
                                    <GlassWater className="text-green-600" size={20} />
                                    <span>Quầy bar</span>
                                </div>
                                <div className="flex items-center gap-2">
                                    <Sunrise className="text-green-600" size={20} />
                                    <span>Bữa sáng rất tốt</span>
                                </div>
                            </div>
                        </div>

                        <div>
                            <div className="bg-gray-50 border border-gray-200 rounded p-4">
                                <div className="flex justify-between items-center mb-4">
                                    <div>
                                        <h3 className="font-bold">Tuyệt hảo</h3>
                                        <p className="text-sm text-gray-600">689 đánh giá thật</p>
                                    </div>
                                    <div className="bg-blue-700 text-white font-bold text-lg px-2 py-1 rounded">
                                        9.1
                                    </div>
                                </div>

                                <div className="mb-4">
                                    <h4 className="font-bold mb-2">Khách lưu trú ở đây thích điều gì?</h4>
                                    <p className="text-sm italic">
                                        &quot;Khách sạn vị trí thuận lợi, bữa sáng đầy đủ nhân viên nhiệt tình Linh&quot;
                                    </p>
                                </div>

                                <div className="flex items-center gap-2 mb-4">
                                    <div className="w-8 h-8 bg-orange-500 rounded-full flex items-center justify-center text-white font-bold">
                                        T
                                    </div>
                                    <span>Thi</span>
                                    <div className="flex items-center">
                                        <img src="/api/placeholder/20/15" alt="Vietnam flag" className="mr-1" />
                                        <span className="text-sm">Việt Nam</span>
                                    </div>
                                </div>

                                <div className="flex justify-between items-center mb-2">
                                    <p className="font-bold">Nhân viên phục vụ</p>
                                    <div className="bg-green-700 text-white font-bold px-2 py-1 rounded">
                                        9.5
                                    </div>
                                </div>

                                <div className="mt-4">
                                    <div className="w-full h-48 bg-blue-100 rounded mb-2">
                                        <div className="h-full flex items-center justify-center text-blue-800">
                                            Bản đồ
                                        </div>
                                    </div>
                                    <button className="w-full text-center bg-blue-600 text-white py-2 rounded font-medium">
                                        Hiển thị trên bản đồ
                                    </button>
                                </div>

                                <div className="mt-4">
                                    <h4 className="font-bold mb-2">Điểm nổi bật của chỗ nghỉ</h4>
                                    <div className="flex items-start gap-2 mb-3">
                                        <MapPin size={20} className="text-gray-600 shrink-0 mt-1" />
                                        <p className="text-sm">Nằm ngay trung tâm Hà Nội, khách sạn này có điểm vị trí tuyệt với 9,5</p>
                                    </div>
                                    <div className="flex items-start gap-2">
                                        <Coffee size={20} className="text-gray-600 shrink-0 mt-1" />
                                        <p className="text-sm">Bạn muốn ngủ thật ngon? Khách sạn này được đánh giá cao nhờ những chiếc giường thoải mái.</p>
                                    </div>
                                </div>

                                <div className="mt-4">
                                    <h4 className="font-bold mb-2">Thông tin về bữa sáng</h4>
                                    <p className="text-sm">Kiểu lục địa, Tự chọn, Bữa sáng mang đi</p>
                                </div>
                            </div>
                        </div>
                    </div>

                    {/* Detailed Amenities Section - Added based on image */}
                    <div ref={amenitiesRef} className="mt-12">
                        <h2 className="text-2xl font-bold mb-6">Các tiện nghi của La Passion Hanoi Hotel & Spa</h2>
                        <p className="text-base mb-6">Tiện nghi tuyệt với! Điểm đánh giá: 9.2</p>

                        <h3 className="text-lg font-medium mb-4">Các tiện nghi được ưa chuộng nhất</h3>
                        <div className="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-5 gap-4 mb-8">
                            <div className="flex items-center gap-2">
                                <Car className="text-green-600" size={20} />
                                <span>Xe đưa đón sân bay</span>
                            </div>
                            <div className="flex items-center gap-2">
                                <Wifi className="text-green-600" size={20} />
                                <span>Phòng không hút thuốc</span>
                            </div>
                            <div className="flex items-center gap-2">
                                <Utensils className="text-green-600" size={20} />
                                <span>Trung tâm Spa & chăm sóc sức khỏe</span>
                            </div>
                            <div className="flex items-center gap-2">
                                <Users className="text-green-600" size={20} />
                                <span>Dịch vụ phòng</span>
                            </div>
                            <div className="flex items-center gap-2">
                                <Wifi className="text-green-600" size={20} />
                                <span>WiFi miễn phí</span>
                            </div>
                            <div className="flex items-center gap-2">
                                <Utensils className="text-green-600" size={20} />
                                <span>Nhà hàng</span>
                            </div>
                            <div className="flex items-center gap-2">
                                <Users className="text-green-600" size={20} />
                                <span>Phòng gia đình</span>
                            </div>
                            <div className="flex items-center gap-2">
                                <Clock className="text-green-600" size={20} />
                                <span>Lễ tân 24 giờ</span>
                            </div>
                            <div className="flex items-center gap-2">
                                <GlassWater className="text-green-600" size={20} />
                                <span>Quầy bar</span>
                            </div>
                            <div className="flex items-center gap-2">
                                <Sunrise className="text-green-600" size={20} />
                                <span>Bữa sáng rất tốt</span>
                            </div>
                        </div>

                        <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
                            {/* Phòng tắm */}
                            <div>
                                <h3 className="font-medium text-lg flex items-center mb-4">
                                    <span className="bg-gray-100 p-2 mr-3 rounded-full">
                                        <GlassWater size={20} className="text-gray-600" />
                                    </span>
                                    Phòng tắm
                                </h3>
                                <ul className="space-y-3">
                                    <li className="flex items-center gap-2">
                                        <Check size={16} className="text-green-600" />
                                        <span>Giấy vệ sinh</span>
                                    </li>
                                    <li className="flex items-center gap-2">
                                        <Check size={16} className="text-green-600" />
                                        <span>Khăn tắm</span>
                                    </li>
                                    <li className="flex items-center gap-2">
                                        <Check size={16} className="text-green-600" />
                                        <span>Chậu rửa vệ sinh (bidet)</span>
                                    </li>
                                    <li className="flex items-center gap-2">
                                        <Check size={16} className="text-green-600" />
                                        <span>Khăn tắm/Bộ khăn trải giường (có thu phí)</span>
                                    </li>
                                    <li className="flex items-center gap-2">
                                        <Check size={16} className="text-green-600" />
                                        <span>Bồn tắm hoặc Vòi sen</span>
                                    </li>
                                    <li className="flex items-center gap-2">
                                        <Check size={16} className="text-green-600" />
                                        <span>Dép</span>
                                    </li>
                                    <li className="flex items-center gap-2">
                                        <Check size={16} className="text-green-600" />
                                        <span>Phòng tắm riêng</span>
                                    </li>
                                    <li className="flex items-center gap-2">
                                        <Check size={16} className="text-green-600" />
                                        <span>Nhà vệ sinh</span>
                                    </li>
                                    <li className="flex items-center gap-2">
                                        <Check size={16} className="text-green-600" />
                                        <span>Đồ vệ sinh cá nhân miễn phí</span>
                                    </li>
                                    <li className="flex items-center gap-2">
                                        <Check size={16} className="text-green-600" />
                                        <span>Áo choàng tắm</span>
                                    </li>
                                    <li className="flex items-center gap-2">
                                        <Check size={16} className="text-green-600" />
                                        <span>Máy sấy tóc</span>
                                    </li>
                                    <li className="flex items-center gap-2">
                                        <Check size={16} className="text-green-600" />
                                        <span>Vòi sen</span>
                                    </li>
                                </ul>
                            </div>

                            {/* Truyền thông & Công nghệ */}
                            <div>
                                <h3 className="font-medium text-lg flex items-center mb-4">
                                    <span className="bg-gray-100 p-2 mr-3 rounded-full">
                                        <Clock size={20} className="text-gray-600" />
                                    </span>
                                    Truyền thông & Công nghệ
                                </h3>
                                <ul className="space-y-3">
                                    <li className="flex items-center gap-2">
                                        <Check size={16} className="text-green-600" />
                                        <span>TV màn hình phẳng</span>
                                    </li>
                                    <li className="flex items-center gap-2">
                                        <Check size={16} className="text-green-600" />
                                        <span>Điện thoại</span>
                                    </li>
                                    <li className="flex items-center gap-2">
                                        <Check size={16} className="text-green-600" />
                                        <span>TV</span>
                                    </li>
                                </ul>

                                <h3 className="font-medium text-lg flex items-center mt-8 mb-4">
                                    <span className="bg-gray-100 p-2 mr-3 rounded-full">
                                        <Utensils size={20} className="text-gray-600" />
                                    </span>
                                    Đồ ăn & thức uống
                                </h3>
                                <ul className="space-y-3">
                                    <li className="flex items-center gap-2">
                                        <Check size={16} className="text-green-600" />
                                        <span>Quán cà phê (trong khuôn viên)</span>
                                    </li>
                                    <li className="flex items-center gap-2">
                                        <Check size={16} className="text-green-600" />
                                        <span>Rượu vang/sâm panh <span className="text-gray-500 text-xs">(Phụ phí)</span></span>
                                    </li>
                                    <li className="flex items-center gap-2">
                                        <Check size={16} className="text-green-600" />
                                        <span>Bữa ăn tự chọn phù hợp với trẻ em</span>
                                    </li>
                                    <li className="flex items-center gap-2">
                                        <Check size={16} className="text-green-600" />
                                        <span>Bữa ăn trẻ em <span className="text-gray-500 text-xs">(Phụ phí)</span></span>
                                    </li>
                                    <li className="flex items-center gap-2">
                                        <Check size={16} className="text-green-600" />
                                        <span>Quầy bar (đồ ăn nhẹ)</span>
                                    </li>
                                    <li className="flex items-center gap-2">
                                        <Check size={16} className="text-green-600" />
                                        <span>Quầy bar</span>
                                    </li>
                                    <li className="flex items-center gap-2">
                                        <Check size={16} className="text-green-600" />
                                        <span>Minibar</span>
                                    </li>
                                    <li className="flex items-center gap-2">
                                        <Check size={16} className="text-green-600" />
                                        <span>Nhà hàng</span>
                                    </li>
                                </ul>
                            </div>

                            {/* Internet */}
                            <div>
                                <h3 className="font-medium text-lg flex items-center mb-4">
                                    <span className="bg-gray-100 p-2 mr-3 rounded-full">
                                        <Wifi size={20} className="text-gray-600" />
                                    </span>
                                    Internet
                                </h3>
                                <ul className="space-y-3">
                                    <li className="flex items-center gap-2">
                                        <Check size={16} className="text-green-600" />
                                        <span>Wi-fi có ở toàn bộ khách sạn và miễn phí.</span>
                                    </li>
                                </ul>

                                <h3 className="font-medium text-lg flex items-center mt-8 mb-4">
                                    <span className="bg-gray-100 p-2 mr-3 rounded-full">
                                        <Car size={20} className="text-gray-600" />
                                    </span>
                                    Chỗ đậu xe
                                </h3>
                                <ul className="space-y-3">
                                    <li className="flex items-center gap-2">
                                        <X size={16} className="text-red-500" />
                                        <span>Không có chỗ đỗ xe.</span>
                                    </li>
                                </ul>

                                <h3 className="font-medium text-lg flex items-center mt-8 mb-4">
                                    <span className="bg-gray-100 p-2 mr-3 rounded-full">
                                        <Car size={20} className="text-gray-600" />
                                    </span>
                                    Phương tiện đi lại
                                </h3>
                                <ul className="space-y-3">
                                    <li className="flex items-center gap-2">
                                        <Check size={16} className="text-green-600" />
                                        <span>Vé đi phương tiện công cộng <span className="text-gray-500 text-xs">(Phụ phí)</span></span>
                                    </li>
                                </ul>

                                <h3 className="font-medium text-lg flex items-center mt-8 mb-4">
                                    <span className="bg-gray-100 p-2 mr-3 rounded-full">
                                        <Users size={20} className="text-gray-600" />
                                    </span>
                                    Dịch vụ lễ tân
                                </h3>
                                <ul className="space-y-3">
                                    <li className="flex items-center gap-2">
                                        <Check size={16} className="text-green-600" />
                                        <span>Có xuất hóa đơn</span>
                                    </li>
                                </ul>
                            </div>

                            {/* An ninh */}
                            <div>
                                <h3 className="font-medium text-lg flex items-center mb-4">
                                    <span className="bg-gray-100 p-2 mr-3 rounded-full">
                                        <Clock size={20} className="text-gray-600" />
                                    </span>
                                    An ninh
                                </h3>
                                <ul className="space-y-3">
                                    <li className="flex items-center gap-2">
                                        <Check size={16} className="text-green-600" />
                                        <span>Bình chữa cháy</span>
                                    </li>
                                    <li className="flex items-center gap-2">
                                        <Check size={16} className="text-green-600" />
                                        <span>Hệ thống CCTV bên ngoài chỗ nghỉ</span>
                                    </li>
                                    <li className="flex items-center gap-2">
                                        <Check size={16} className="text-green-600" />
                                        <span>Hệ thống CCTV trong khu vực chung</span>
                                    </li>
                                    <li className="flex items-center gap-2">
                                        <Check size={16} className="text-green-600" />
                                        <span>Thiết bị báo cháy</span>
                                    </li>
                                    <li className="flex items-center gap-2">
                                        <Check size={16} className="text-green-600" />
                                        <span>Báo động an ninh</span>
                                    </li>
                                    <li className="flex items-center gap-2">
                                        <Check size={16} className="text-green-600" />
                                        <span>Ổ khóa mở bằng thẻ</span>
                                    </li>
                                    <li className="flex items-center gap-2">
                                        <Check size={16} className="text-green-600" />
                                        <span>Ổ khóa</span>
                                    </li>
                                    <li className="flex items-center gap-2">
                                        <Check size={16} className="text-green-600" />
                                        <span>Bảo vệ 24/7</span>
                                    </li>
                                    <li className="flex items-center gap-2">
                                        <Check size={16} className="text-green-600" />
                                        <span>Két an toàn</span>
                                    </li>
                                </ul>
                            </div>

                            {/* Tổng quát */}
                            <div>
                                <h3 className="font-medium text-lg flex items-center mb-4">
                                    <span className="bg-gray-100 p-2 mr-3 rounded-full">
                                        <Clock size={20} className="text-gray-600" />
                                    </span>
                                    Tổng quát
                                </h3>
                                <ul className="space-y-3">
                                    <li className="flex items-center gap-2">
                                        <Check size={16} className="text-green-600" />
                                        <span>Dịch vụ đưa đón <span className="text-gray-500 text-xs">(Phụ phí)</span></span>
                                    </li>
                                    <li className="flex items-center gap-2">
                                        <Check size={16} className="text-green-600" />
                                        <span>Thiết bị báo carbon monoxide</span>
                                    </li>
                                    <li className="flex items-center gap-2">
                                        <Check size={16} className="text-green-600" />
                                        <span>Giao nhận đồ tạp hóa</span>
                                    </li>
                                    <li className="flex items-center gap-2">
                                        <Check size={16} className="text-green-600" />
                                        <span>Khu vực xem TV/sảnh chung</span>
                                    </li>
                                    <li className="flex items-center gap-2">
                                        <Check size={16} className="text-green-600" />
                                        <span>Không gây dị ứng</span>
                                    </li>
                                    <li className="flex items-center gap-2">
                                        <Check size={16} className="text-green-600" />
                                        <span>Khu vực cho phép hút thuốc</span>
                                    </li>
                                    <li className="flex items-center gap-2">
                                        <Check size={16} className="text-green-600" />
                                        <span>Điều hòa nhiệt độ</span>
                                    </li>
                                    <li className="flex items-center gap-2">
                                        <Check size={16} className="text-green-600" />
                                        <span>Phòng không gây dị ứng</span>
                                    </li>
                                    <li className="flex items-center gap-2">
                                        <Check size={16} className="text-green-600" />
                                        <span>Dịch vụ báo thức</span>
                                    </li>
                                    <li className="flex items-center gap-2">
                                        <Check size={16} className="text-green-600" />
                                        <span>Sân lát gỗ</span>
                                    </li>
                                    <li className="flex items-center gap-2">
                                        <Check size={16} className="text-green-600" />
                                        <span>Hệ thống sưởi</span>
                                    </li>
                                    <li className="flex items-center gap-2">
                                        <Check size={16} className="text-green-600" />
                                        <span>Hệ thống cách âm</span>
                                    </li>
                                </ul>
                            </div>

                            {/* Phòng ngủ */}
                            <div>
                                <h3 className="font-medium text-lg flex items-center mb-4">
                                    <span className="bg-gray-100 p-2 mr-3 rounded-full">
                                        <Clock size={20} className="text-gray-600" />
                                    </span>
                                    Phòng ngủ
                                </h3>
                                <ul className="space-y-3">
                                    <li className="flex items-center gap-2">
                                        <Check size={16} className="text-green-600" />
                                        <span>Ra trải giường</span>
                                    </li>
                                    <li className="flex items-center gap-2">
                                        <Check size={16} className="text-green-600" />
                                        <span>Tủ hoặc phòng để quần áo</span>
                                    </li>
                                    <li className="flex items-center gap-2">
                                        <Check size={16} className="text-green-600" />
                                        <span>Đồng hồ báo thức</span>
                                    </li>
                                </ul>

                                <h3 className="font-medium text-lg flex items-center mt-8 mb-4">
                                    <span className="bg-gray-100 p-2 mr-3 rounded-full">
                                        <Utensils size={20} className="text-gray-600" />
                                    </span>
                                    Ngoài trời
                                </h3>
                                <ul className="space-y-3">
                                    <li className="flex items-center gap-2">
                                        <Check size={16} className="text-green-600" />
                                        <span>Bàn ghế ngoài trời</span>
                                    </li>
                                    <li className="flex items-center gap-2">
                                        <Check size={16} className="text-green-600" />
                                        <span>Sân hiên phơi nắng</span>
                                    </li>
                                    <li className="flex items-center gap-2">
                                        <Check size={16} className="text-green-600" />
                                        <span>Sân thượng / hiên</span>
                                    </li>
                                </ul>

                                <h3 className="font-medium text-lg flex items-center mt-8 mb-4">
                                    <span className="bg-gray-100 p-2 mr-3 rounded-full">
                                        <Utensils size={20} className="text-gray-600" />
                                    </span>
                                    Nhà bếp
                                </h3>
                            </div>
                        </div>

                        <div ref={rulesRef} className="mt-12">
                            <h2 className="text-2xl font-bold mb-6">Quy tắc chung</h2>
                            <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
                                <div className="border-b pb-6">
                                    <h3 className="font-bold mb-4">Nhận phòng</h3>
                                    <div className="flex justify-between mb-2">
                                        <span>Từ</span>
                                        <span className="font-medium">14:00</span>
                                    </div>
                                    <div className="flex justify-between">
                                        <span>Đến</span>
                                        <span className="font-medium">00:00</span>
                                    </div>
                                </div>

                                <div className="border-b pb-6">
                                    <h3 className="font-bold mb-4">Trả phòng</h3>
                                    <div className="flex justify-between mb-2">
                                        <span>Từ</span>
                                        <span className="font-medium">07:00</span>
                                    </div>
                                    <div className="flex justify-between">
                                        <span>Đến</span>
                                        <span className="font-medium">12:00</span>
                                    </div>
                                </div>

                                <div className="border-b pb-6">
                                    <h3 className="font-bold mb-4">Hủy/Thanh toán trước</h3>
                                    <p className="text-gray-700">Các chính sách hủy và thanh toán trước có khác biệt dựa trên loại chỗ nghỉ. Vui lòng nhập ngày lưu trú và kiểm tra các điều kiện của phòng mà bạn đã chọn.</p>
                                </div>

                                <div className="border-b pb-6">
                                    <h3 className="font-bold mb-4">Trẻ em và giường</h3>
                                    <p className="text-gray-700 mb-2">Chính sách trẻ em:</p>
                                    <p className="text-gray-700">Phù hợp cho tất cả trẻ em.</p>
                                    <p className="text-gray-700 mt-2">Trẻ em từ 0 đến 5 tuổi được ở miễn phí khi sử dụng giường sẵn có.</p>
                                </div>

                                <div className="border-b pb-6 col-span-full">
                                    <h3 className="font-bold mb-4">Thú cưng</h3>
                                    <p className="text-gray-700">Không cho phép mang theo vật nuôi.</p>
                                </div>
                            </div>
                        </div>
                    </div>

                    {/* Genius Discount Banner */}
                    <div className="border border-gray-200 rounded p-6 mt-8 flex justify-between items-center">
                        <div>
                            <h3 className="text-xl font-bold mb-2">Đăng nhập để tiết kiệm</h3>
                            <p>Để xem bạn có thể tiết kiệm ít nhất 10% tại chỗ nghỉ này hay không, hãy đăng nhập</p>
                        </div>
                        <div className="flex gap-4 items-center">
                            <div className="flex gap-2">
                                <button className="px-4 py-2 border border-blue-600 text-blue-600 font-medium rounded">
                                    Đăng nhập
                                </button>
                                <button className="px-4 py-2 border border-gray-300 text-gray-700 font-medium rounded">
                                    Tạo tài khoản
                                </button>
                            </div>
                            <img src="/api/placeholder/100/80" alt="Genius logo" />
                        </div>
                    </div>

                    {/* Available Rooms - Updated to match Booking.com design */}
                    <div ref={infoRef} className="mt-12">
                        <h2 className="text-2xl font-bold mb-6">Phòng trống</h2>

                        <div className="mb-4 flex items-center text-red-600 gap-2">
                            <X size={20} />
                            <p>Chọn ngày để xem phòng trống và giá tại chỗ nghỉ này</p>
                        </div>

                        <div className="flex mb-6">
                            <div className="border border-gray-300 rounded-l p-3 flex-grow">
                                <DateRangePicker />
                            </div>
                            <div className="border-t border-b border-r border-gray-300 p-3">
                                <GuestSelector />
                            </div>
                            <button className="bg-blue-600 text-white font-medium px-6 rounded-r flex items-center">
                                Tìm
                            </button>
                        </div>

                        {/* Room Type Table - Updated to match the provided image */}
                        <div className="border rounded-lg overflow-hidden">
                            <table className="w-full border-collapse">
                                <thead>
                                    <tr className="bg-blue-700 text-white">
                                        <th className="text-left p-4 font-medium">Loại chỗ nghỉ</th>
                                        <th className="text-center p-4 font-medium w-32">Số lượng khách</th>
                                        <th className="w-44"></th>
                                    </tr>
                                </thead>
                                <tbody className="divide-y divide-gray-200">
                                    <tr className="hover:bg-gray-50">
                                        <td className="p-4">
                                            <div className="mb-2">
                                                <button className="text-blue-600 font-bold flex items-center gap-1">
                                                    <ChevronRight size={20} className="text-blue-600" />
                                                    Phòng Deluxe Giường Đôi/2 Giường Đơn
                                                </button>
                                            </div>
                                            <p className="text-sm text-gray-600 ml-6">2 giường đơn</p>
                                        </td>
                                        <td className="text-center">
                                            <div className="flex justify-center">
                                                <Users size={18} className="mr-1" />
                                                <Users size={18} />
                                            </div>
                                        </td>
                                        <td className="text-right p-4">
                                            <button className="bg-blue-600 text-white px-4 py-2 rounded font-medium">
                                                Hiển thị giá
                                            </button>
                                        </td>
                                    </tr>
                                    <tr className="hover:bg-gray-50">
                                        <td className="p-4">
                                            <div className="mb-2">
                                                <button className="text-blue-600 font-bold flex items-center gap-1">
                                                    <ChevronRight size={20} className="text-blue-600" />
                                                    Phòng Executive nhìn ra Quang cảnh Thành phố
                                                </button>
                                            </div>
                                            <p className="text-sm text-gray-600 ml-6">1 giường đôi lớn hoặc 2 giường đơn</p>
                                        </td>
                                        <td className="text-center">
                                            <div className="flex justify-center">
                                                <Users size={18} className="mr-1" />
                                                <Users size={18} />
                                            </div>
                                        </td>
                                        <td className="text-right p-4">
                                            <button className="bg-blue-600 text-white px-4 py-2 rounded font-medium">
                                                Hiển thị giá
                                            </button>
                                        </td>
                                    </tr>
                                    <tr className="hover:bg-gray-50">
                                        <td className="p-4">
                                            <div className="mb-2">
                                                <button className="text-blue-600 font-bold flex items-center gap-1">
                                                    <ChevronRight size={20} className="text-blue-600" />
                                                    Suite Junior Nhìn Ra Công Viên
                                                </button>
                                            </div>
                                            <p className="text-sm text-gray-600 ml-6">1 giường đôi lớn hoặc 2 giường đơn</p>
                                        </td>
                                        <td className="text-center">
                                            <div className="flex justify-center">
                                                <Users size={18} className="mr-1" />
                                                <Users size={18} />
                                            </div>
                                        </td>
                                        <td className="text-right p-4">
                                            <button className="bg-blue-600 text-white px-4 py-2 rounded font-medium">
                                                Hiển thị giá
                                            </button>
                                        </td>
                                    </tr>
                                    <tr className="hover:bg-gray-50">
                                        <td className="p-4">
                                            <div className="mb-2">
                                                <button className="text-blue-600 font-bold flex items-center gap-1">
                                                    <ChevronRight size={20} className="text-blue-600" />
                                                    Phòng Deluxe Nhìn ra Thành phố
                                                </button>
                                            </div>
                                            <p className="text-sm text-gray-600 ml-6">2 giường đơn hoặc 1 giường đôi lớn</p>
                                        </td>
                                        <td className="text-center">
                                            <div className="flex justify-center">
                                                <Users size={18} className="mr-1" />
                                                <Users size={18} />
                                            </div>
                                        </td>
                                        <td className="text-right p-4">
                                            <button className="bg-blue-600 text-white px-4 py-2 rounded font-medium">
                                                Hiển thị giá
                                            </button>
                                        </td>
                                    </tr>
                                    <tr className="hover:bg-gray-50">
                                        <td className="p-4">
                                            <div className="mb-2">
                                                <button className="text-blue-600 font-bold flex items-center gap-1">
                                                    <ChevronRight size={20} className="text-blue-600" />
                                                    Phòng Gia Đình
                                                </button>
                                            </div>
                                            <p className="text-sm text-gray-600 ml-6">1 giường đơn và 1 giường đôi lớn</p>
                                        </td>
                                        <td className="text-center">
                                            <div className="flex justify-center">
                                                <Users size={18} className="mr-1" />
                                                <Users size={18} />
                                                <Users size={18} />
                                            </div>
                                        </td>
                                        <td className="text-right p-4">
                                            <button className="bg-blue-600 text-white px-4 py-2 rounded font-medium">
                                                Hiển thị giá
                                            </button>
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>

                        <div className="flex justify-center mt-6">
                            <button className="bg-blue-600 text-white px-8 py-3 rounded font-medium">
                                Xem phòng trống
                            </button>
                        </div>
                    </div>

                    {/* Reviews Section - Updated to match Booking.com */}
                    <div ref={reviewsRef} className="mt-16 pb-8">
                        <h2 className="text-2xl font-bold mb-6">Đánh giá của khách</h2>

                        <div className="bg-white rounded-lg shadow-sm p-6 mb-8">
                            <div className="flex items-center gap-4 mb-6">
                                <div className="bg-blue-700 text-white font-bold text-xl px-3 py-1 rounded">
                                    9.1
                                </div>
                                <div>
                                    <h3 className="font-bold text-lg">Tuyệt hảo</h3>
                                    <p className="text-sm text-gray-600">689 đánh giá thật</p>
                                </div>
                                <div className="ml-auto">
                                    <button className="text-blue-600 font-medium">Đọc tất cả đánh giá</button>
                                </div>
                            </div>

                            <h3 className="font-bold mb-4">Hạng mục:</h3>

                            <div className="grid grid-cols-1 md:grid-cols-2 gap-x-8 gap-y-4 mb-8">
                                <div>
                                    <div className="flex justify-between items-center mb-2">
                                        <div className="text-sm">Nhân viên phục vụ</div>
                                        <div className="flex items-center">
                                            <div className="bg-blue-700 text-white font-bold text-sm px-2 py-0.5 rounded mr-2">9.5</div>
                                            <div className="w-32 h-2 bg-gray-200 rounded-full">
                                                <div className="h-full bg-blue-700 rounded-full" style={{ width: "95%" }}></div>
                                            </div>
                                        </div>
                                    </div>
                                    <div className="flex justify-between items-center mb-2">
                                        <div className="text-sm">Thoải mái</div>
                                        <div className="flex items-center">
                                            <div className="bg-blue-700 text-white font-bold text-sm px-2 py-0.5 rounded mr-2">9.2</div>
                                            <div className="w-32 h-2 bg-gray-200 rounded-full">
                                                <div className="h-full bg-blue-700 rounded-full" style={{ width: "92%" }}></div>
                                            </div>
                                        </div>
                                    </div>
                                    <div className="flex justify-between items-center mb-2">
                                        <div className="text-sm">WiFi miễn phí</div>
                                        <div className="flex items-center">
                                            <div className="bg-blue-700 text-white font-bold text-sm px-2 py-0.5 rounded mr-2">9.0</div>
                                            <div className="w-32 h-2 bg-gray-200 rounded-full">
                                                <div className="h-full bg-blue-700 rounded-full" style={{ width: "90%" }}></div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div>
                                    <div className="flex justify-between items-center mb-2">
                                        <div className="text-sm">Tiện nghi</div>
                                        <div className="flex items-center">
                                            <div className="bg-blue-700 text-white font-bold text-sm px-2 py-0.5 rounded mr-2">9.1</div>
                                            <div className="w-32 h-2 bg-gray-200 rounded-full">
                                                <div className="h-full bg-blue-700 rounded-full" style={{ width: "91%" }}></div>
                                            </div>
                                        </div>
                                    </div>
                                    <div className="flex justify-between items-center mb-2">
                                        <div className="text-sm">Sạch sẽ</div>
                                        <div className="flex items-center">
                                            <div className="bg-blue-700 text-white font-bold text-sm px-2 py-0.5 rounded mr-2">9.2</div>
                                            <div className="w-32 h-2 bg-gray-200 rounded-full">
                                                <div className="h-full bg-blue-700 rounded-full" style={{ width: "92%" }}></div>
                                            </div>
                                        </div>
                                    </div>
                                    <div className="flex justify-between items-center mb-2">
                                        <div className="text-sm">Đáng giá tiền</div>
                                        <div className="flex items-center">
                                            <div className="bg-blue-700 text-white font-bold text-sm px-2 py-0.5 rounded mr-2">9.3</div>
                                            <div className="w-32 h-2 bg-gray-200 rounded-full">
                                                <div className="h-full bg-blue-700 rounded-full" style={{ width: "93%" }}></div>
                                            </div>
                                        </div>
                                    </div>
                                    <div className="flex justify-between items-center mb-2">
                                        <div className="text-sm">Địa điểm</div>
                                        <div className="flex items-center">
                                            <div className="bg-blue-700 text-white font-bold text-sm px-2 py-0.5 rounded mr-2">9.5</div>
                                            <div className="w-32 h-2 bg-gray-200 rounded-full">
                                                <div className="h-full bg-blue-700 rounded-full" style={{ width: "95%" }}></div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <h3 className="font-bold mb-4">Chọn chủ đề để đọc đánh giá:</h3>
                            <div className="flex flex-wrap gap-2 mb-8">
                                <button className="px-3 py-2 bg-white border border-gray-300 rounded-full text-sm hover:bg-gray-50 flex items-center gap-1">
                                    <MapPin size={14} />
                                    Vị trí
                                </button>
                                <button className="px-3 py-2 bg-white border border-gray-300 rounded-full text-sm hover:bg-gray-50">
                                    Phòng
                                </button>
                                <button className="px-3 py-2 bg-white border border-gray-300 rounded-full text-sm hover:bg-gray-50">
                                    Sạch sẽ
                                </button>
                                <button className="px-3 py-2 bg-white border border-gray-300 rounded-full text-sm hover:bg-gray-50">
                                    Bữa sáng
                                </button>
                                <button className="px-3 py-2 bg-white border border-gray-300 rounded-full text-sm hover:bg-gray-50">
                                    Giường
                                </button>
                            </div>
                        </div>

                        <div className="space-y-6">
                            {/* Sample reviews */}
                            <div className="border-b pb-6">
                                <div className="flex items-start gap-3 mb-3">
                                    <div className="w-10 h-10 rounded-full bg-blue-50 flex items-center justify-center text-blue-600 font-bold">
                                        NT
                                    </div>
                                    <div>
                                        <h4 className="font-semibold">Nguyễn Thành</h4>
                                        <div className="flex items-center gap-2 text-sm text-gray-600">
                                            <div className="flex">
                                                <img src="/images/vietnam-flag.png" alt="Vietnam" width={16} height={12} className="mr-1" />
                                                <span>Việt Nam</span>
                                            </div>
                                            <span className="text-gray-400">•</span>
                                            <div className="flex items-center">
                                                <Users size={14} className="mr-1" />
                                                <span>Gia đình có trẻ nhỏ</span>
                                            </div>
                                        </div>
                                        <div className="mt-2">
                                            <div className="flex items-center gap-1">
                                                <div className="bg-blue-700 text-white font-bold text-sm px-2 py-0.5 rounded">
                                                    10
                                                </div>
                                                <Star fill="#FFB700" stroke="#FFB700" size={16} />
                                                <Star fill="#FFB700" stroke="#FFB700" size={16} />
                                                <Star fill="#FFB700" stroke="#FFB700" size={16} />
                                                <Star fill="#FFB700" stroke="#FFB700" size={16} />
                                                <Star fill="#FFB700" stroke="#FFB700" size={16} />
                                            </div>
                                            <p className="text-sm text-gray-500 mt-1">2 tuần trước</p>
                                        </div>
                                    </div>
                                </div>
                                <p className="text-gray-700">
                                    Khách sạn rất tuyệt vời, phòng sạch sẽ và thoải mái. Nhân viên thân thiện và nhiệt tình. Vị trí thuận tiện, gần các điểm tham quan. Tôi sẽ quay lại lần sau!
                                </p>
                            </div>

                            <div>
                                <div className="flex items-start gap-3 mb-3">
                                    <div className="w-10 h-10 rounded-full bg-green-50 flex items-center justify-center text-green-600 font-bold">
                                        TL
                                    </div>
                                    <div>
                                        <h4 className="font-semibold">Trần Linh</h4>
                                        <div className="flex items-center gap-2 text-sm text-gray-600">
                                            <div className="flex">
                                                <img src="/images/vietnam-flag.png" alt="Vietnam" width={16} height={12} className="mr-1" />
                                                <span>Việt Nam</span>
                                            </div>
                                            <span className="text-gray-400">•</span>
                                            <div className="flex items-center">
                                                <Users size={14} className="mr-1" />
                                                <span>Cặp đôi</span>
                                            </div>
                                        </div>
                                        <div className="mt-2">
                                            <div className="flex items-center gap-1">
                                                <div className="bg-blue-700 text-white font-bold text-sm px-2 py-0.5 rounded">
                                                    8
                                                </div>
                                                <Star fill="#FFB700" stroke="#FFB700" size={16} />
                                                <Star fill="#FFB700" stroke="#FFB700" size={16} />
                                                <Star fill="#FFB700" stroke="#FFB700" size={16} />
                                                <Star fill="#FFB700" stroke="#FFB700" size={16} />
                                                <Star fill="transparent" stroke="#FFB700" size={16} />
                                            </div>
                                            <p className="text-sm text-gray-500 mt-1">1 tháng trước</p>
                                        </div>
                                    </div>
                                </div>
                                <p className="text-gray-700">
                                    Phòng đẹp, sạch sẽ và đầy đủ tiện nghi. Nhân viên phục vụ tốt. Bữa sáng khá ngon với nhiều lựa chọn. Vị trí thuận tiện để đi lại và tham quan các điểm du lịch gần đó.
                                </p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}