"use client";

import { useState, useRef } from 'react';
import { ChevronRight, X } from 'lucide-react';
import Header from '@/components/Header';
import { sampleRooms } from '@/data/sampleRooms'; // Use our test data
import ReviewSection from '@/components/HotelDetail/ReviewSection';
import DateRangePicker from '@/components/HotelDetail/DateRangePicker';
import GuestSelector from '@/components/HotelDetail/GuestSelector';
import HotelGallery from '@/components/HotelDetail/HotelGallery';
import HotelHeader from '@/components/HotelDetail/HotelHeader';
import AvailableRooms from '@/components/HotelDetail/AvailableRooms';
import DetailedAmenities from '@/components/HotelDetail/DetailedAmenities';
import HotelDescription from '@/components/HotelDetail/HotelDescription';
import HotelRules from '@/components/HotelDetail/HotelRules';
import HotelSidebar from '@/components/HotelDetail/HotelSidebar';

export default function HotelDetailPage() {
    const [activeTab, setActiveTab] = useState('overview');

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
    const [showDatePicker, setShowDatePicker] = useState(false); const [adults, setAdults] = useState(2);
    const [childrenCount, setChildrenCount] = useState(0);
    const [rooms, setRooms] = useState(1); const [showGuestSelector, setShowGuestSelector] = useState(false);

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

    const hotelImages = [
        'https://cf.bstatic.com/xdata/images/hotel/max1024x768/587368962.jpg?k=d0c0ad579ea42f87f719ce513507df22c228ce8b6619d3691ff13a6936f17487&o=&hp=1',
        "https://cf.bstatic.com/xdata/images/hotel/max1024x768/414199546.jpg?k=7b9b9e04139210f5ae231c67ecdbac1c6b0fe784a0527e1d0867b36ab813c960&o=",
        "https://cf.bstatic.com/xdata/images/hotel/max1024x768/414203667.jpg?k=c068ca50ec803e86d211a0c10c3601cf8933749ad818bbe4814ad414d7d40fd6&o=",
        "https://cf.bstatic.com/xdata/images/hotel/max1024x768/414203665.jpg?k=9849b5702ce408d1b1075fb84f49e1dc2a1de30a191c8e5ce64fb051e87711bc&o=",
        "https://cf.bstatic.com/xdata/images/hotel/max1024x768/414203669.jpg?k=6e54fafedcfd46d60e23c2522e3a83a8e24831f48de325faa988345ae89f7684&o=",
        "https://cf.bstatic.com/xdata/images/hotel/max1024x768/414203664.jpg?k=6442947d520e09eb6a9cd8394e4f676334fb3396555970fe6ff3b652e0162620&o=",
        "https://cf.bstatic.com/xdata/images/hotel/max1024x768/303034839.jpg?k=24c08beed8f2e8f6b37e9ef4267635564424db28f25aa6d0b8b32abb7c13a185&o=",
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
                    <HotelHeader
                        name="La Passion Classic Hotel"
                        address="No 1 Phan Dinh Phung street, Hoan Kiem district, Quận Hoàn Kiếm, Hà Nội, Việt Nam"
                        rating={4}
                        reviewScore={8.8}
                        hasAirportShuttle={true}
                    />

                    {/* Gallery */}
                    <HotelGallery
                        hotelName="La Passion Classic Hotel"
                        images={hotelImages}
                    />

                    {/* Hotel Description */}
                    <div className="grid grid-cols-3 gap-6 mt-10">
                        <HotelDescription
                            ref={overviewRef}
                            hotelName="La Passion Classic Hotel"
                            geniusEligible={true}
                            onShowMore={() => console.log('Show more clicked')}
                        />                        <HotelSidebar
                            onShowMap={() => console.log('Show map clicked')}
                        />
                    </div>

                    {/* Available Rooms - Enhanced Booking.com Style */}
                    <div ref={infoRef} className="mt-12">
                        <h2 className="text-2xl font-bold mb-6">Phòng trống</h2>

                        <div className="mb-4 flex items-center text-red-600 gap-2">
                            <X size={20} />
                            <p>Chọn ngày để xem phòng trống và giá tại chỗ nghỉ này</p>
                        </div>
                        <div className="flex mb-6">
                            <div className="border border-gray-300 rounded-l p-3 flex-grow">
                                <DateRangePicker
                                    dateRange={dateRange}
                                    onDateRangeChange={setDateRange}
                                    showDatePicker={showDatePicker}
                                    onShowDatePickerChange={setShowDatePicker}
                                />
                            </div>
                            <div className="border-t border-b border-r border-gray-300 p-3">                                <GuestSelector
                                adults={adults}
                                childrenCount={childrenCount}
                                rooms={rooms}
                                onAdultsChange={setAdults}
                                onChildrenChange={setChildrenCount}
                                onRoomsChange={setRooms}
                                showGuestSelector={showGuestSelector}
                                onShowGuestSelectorChange={setShowGuestSelector}
                            />
                            </div>
                            <button className="bg-blue-600 text-white font-medium px-6 rounded-r flex items-center">
                                Tìm
                            </button>
                        </div>                        {/* Available Rooms Component */}
                        <AvailableRooms
                            rooms={sampleRooms}
                        />
                    </div>

                    {/* Reviews Section - Updated to match Booking.com */}
                    <div ref={reviewsRef} className="mt-16 pb-8">
                        <ReviewSection />
                    </div>

                    {/* Detailed Amenities Section */}
                    <DetailedAmenities
                        ref={amenitiesRef}
                        hotelName="La Passion Hanoi Hotel & Spa"
                        rating={9.2}
                    />

                    {/* Hotel Rules Section */}
                    <HotelRules ref={rulesRef} />

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
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}