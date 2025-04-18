"use client";

import { useState } from 'react';
import Image from 'next/image';
import { ChevronRight, MapPin, Heart, Share, Star, X, Check, Search, Coffee, Wifi, Car, Users, Clock, Utensils, GlassWater, Sunrise } from 'lucide-react';
import { BsGrid3X3Gap } from 'react-icons/bs';
import Header from '@/components/Header';

export default function HotelDetailPage() {
    const [activeTab, setActiveTab] = useState('overview');
    const [selectedRoom, setSelectedRoom] = useState(null);
    const [showAllPhotos, setShowAllPhotos] = useState(false);

    const hotelImages = [
        '/images/hotel-1.jpg',
        '/images/hotel-2.jpg',
        '/images/hotel-3.jpg',
        '/images/hotel-4.jpg',
        '/images/hotel-5.jpg',
        '/images/hotel-6.jpg',
        '/images/hotel-7.jpg',
        '/images/hotel-8.jpg',
        '/images/hotel-9.jpg',
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
                        onClick={() => setActiveTab('overview')}
                        className={`px-6 py-4 font-medium ${activeTab === 'overview' ? 'text-blue-600 border-b-2 border-blue-600' : 'text-gray-600'}`}
                    >
                        Tổng quan
                    </button>
                    <button
                        onClick={() => setActiveTab('info')}
                        className={`px-6 py-4 font-medium ${activeTab === 'info' ? 'text-blue-600 border-b-2 border-blue-600' : 'text-gray-600'}`}
                    >
                        Thông tin & giá
                    </button>
                    <button
                        onClick={() => setActiveTab('amenities')}
                        className={`px-6 py-4 font-medium ${activeTab === 'amenities' ? 'text-blue-600 border-b-2 border-blue-600' : 'text-gray-600'}`}
                    >
                        Tiện nghi
                    </button>
                    <button
                        onClick={() => setActiveTab('rules')}
                        className={`px-6 py-4 font-medium ${activeTab === 'rules' ? 'text-blue-600 border-b-2 border-blue-600' : 'text-gray-600'}`}
                    >
                        Quy tắc chung
                    </button>
                    <button
                        onClick={() => setActiveTab('reviews')}
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
                        <div className="bg-yellow-50 border border-yellow-200 p-4 rounded mb-6">
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
                                    "Khách sạn vị trí thuận lợi, bữa sáng đầy đủ nhân viên nhiệt tình Linh"
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
                <div className="mt-12">
                    <h2 className="text-2xl font-bold mb-6">Phòng trống</h2>

                    <div className="mb-4 flex items-center text-red-600 gap-2">
                        <X size={20} />
                        <p>Chọn ngày để xem phòng trống và giá tại chỗ nghỉ này</p>
                    </div>

                    <div className="flex mb-6">
                        <div className="border border-gray-300 rounded-l p-3 flex-grow">
                            <div className="flex items-center">
                                <button className="px-3 py-2 border-r border-gray-300">Ngày nhận phòng</button>
                                <span className="px-3">—</span>
                                <button className="px-3 py-2">Ngày trả phòng</button>
                            </div>
                        </div>
                        <div className="border-t border-b border-r border-gray-300 p-3">
                            <button className="flex items-center gap-2">
                                <Users size={18} />
                                <span>2 người lớn · 0 trẻ em · 1 phòng</span>
                            </button>
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
                <div className="mt-16 mb-12">
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

                        <div className="grid grid-cols-1 md:grid-cols-2 gap-x-8 gap-y-4 mb-8"></div>
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
                                <div className="text-sm">Thoại mái</div>
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
            {/* Footer */}
            <div className="mt-12 py-6 bg-gray-100 border-t border-gray-200">
                <div className="max-w-7xl mx-auto px-4 text-center text-sm text-gray-600">
                    <p>© 2025 Hotel Booking System. Tất cả các quyền được bảo lưu.</p>
                </div>
            </div>
        </div >

        </div>
    );
}