"use client";

import { useState, useRef, useEffect } from 'react';
import { useParams } from 'next/navigation';
import { ChevronRight, X } from 'lucide-react';
import Header from '@/components/Header';

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
import HotelLocationSection from '@/components/HotelDetail/HotelLocationSection';
import accommodationService from '@/services/accommodation/accommodationService';
import { locationService } from '@/services';
import ratingService from '@/services/rating/ratingService';
import { Accommodation } from '@/types/accommodation';
import { Country, Province } from '@/types/location';
import { RatingSummary } from '@/types/rating';

export default function HotelDetailPage() {
    const params = useParams();
    const hotelId = params.id as string; const [activeTab, setActiveTab] = useState('overview');
    const [accommodation, setAccommodation] = useState<Accommodation | null>(null);
    const [country, setCountry] = useState<Country | null>(null);
    const [province, setProvince] = useState<Province | null>(null);
    const [ratingSummary, setRatingSummary] = useState<RatingSummary | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    // Refs cho các phần tương ứng với tab
    const overviewRef = useRef<HTMLDivElement>(null);
    const infoRef = useRef<HTMLDivElement>(null);
    const amenitiesRef = useRef<HTMLDivElement>(null);
    const rulesRef = useRef<HTMLDivElement>(null);
    const reviewsRef = useRef<HTMLDivElement>(null);

    // State cho DatePicker và GuestSelector
    const [dateRange, setDateRange] = useState<{
        from: Date | undefined;
        to: Date | undefined;
    }>({ from: undefined, to: undefined });
    const [showDatePicker, setShowDatePicker] = useState(false);
    const [adults, setAdults] = useState(2);
    const [childrenCount, setChildrenCount] = useState(0);
    const [rooms, setRooms] = useState(1);
    const [showGuestSelector, setShowGuestSelector] = useState(false);

    useEffect(() => {
        const fetchAccommodation = async () => {
            if (!hotelId) return;

            try {
                setLoading(true);
                setError(null);
                const data = await accommodationService.getAccommodationById(parseInt(hotelId));
                setAccommodation(data);

                if (data.location) {
                    try {
                        if (data.location.countryId) {
                            const countryData = await locationService.getCountryById(data.location.countryId);
                            setCountry(countryData);
                        }
                        if (data.location.provinceId) {
                            const provinceData = await locationService.getProvinceById(data.location.provinceId);
                            setProvince(provinceData);
                        }
                    } catch (locationError) {
                        console.error('Error fetching location details:', locationError);
                        // Don't break the whole page if location fetch fails
                    }
                }

                try {
                    const ratingSummaries = await ratingService.getRatingSummaries([data.id]);
                    if (ratingSummaries && ratingSummaries.length > 0) {
                        setRatingSummary(ratingSummaries[0]);
                    }
                } catch (ratingError) {
                    console.error('Error fetching rating summary:', ratingError);
                }
            } catch (err) {
                console.error('Error fetching accommodation:', err);
                setError('Không thể tải thông tin khách sạn. Vui lòng thử lại sau.');
            } finally {
                setLoading(false);
            }
        };

        fetchAccommodation();
    }, [hotelId]);

    const handleTabClick = (tab: string) => {
        setActiveTab(tab);
        switch (tab) {
            case 'overview':
                overviewRef.current?.scrollIntoView({ behavior: 'smooth' });
                break;
            case 'info':
                infoRef.current?.scrollIntoView({ behavior: 'smooth' });
                break; case 'amenities':
                amenitiesRef.current?.scrollIntoView({ behavior: 'smooth' });
                break;
            case 'rules':
                rulesRef.current?.scrollIntoView({ behavior: 'smooth' });
                break;
            case 'reviews':
                reviewsRef.current?.scrollIntoView({ behavior: 'smooth' });
                break;
        }
    };

    const hotelImages: string[] = [];

    const calculateHotelImages = () => {
        if (!accommodation) return hotelImages;

        const allImages: string[] = [];

        if (accommodation.thumbnailUrl) {
            allImages.push(accommodation.thumbnailUrl);
        }

        if (accommodation.units) {
            accommodation.units.forEach(unit => {
                if (unit.images) {
                    unit.images.forEach(image => {
                        allImages.push(image.url);
                    });
                }
            });
        }

        return allImages.length > 0 ? allImages : hotelImages;
    };

    const finalHotelImages = calculateHotelImages();

    return (
        <div>
            <Header />
            <div className="min-h-screen bg-gray-50 text-gray-800">
                {loading && (
                    <div className="flex justify-center items-center min-h-screen">
                        <div className="text-lg">Đang tải thông tin khách sạn...</div>
                    </div>
                )}

                {error && (
                    <div className="flex justify-center items-center min-h-screen">
                        <div className="text-red-600">{error}</div>
                    </div>
                )}

                {/* Main content */}
                {!loading && !error && accommodation && (
                    <>
                        {/* Navigation Breadcrumb */}
                        <div className="bg-white py-3 px-4 border-b border-gray-200">
                            <div className="max-w-7xl mx-auto flex items-center text-sm gap-1">
                                <a href="#" className="text-blue-600 hover:underline">Trang chủ</a>
                                <ChevronRight size={14} className="text-gray-400" />
                                <a href="#" className="text-blue-600 hover:underline">Khách sạn</a>
                                <ChevronRight size={14} className="text-gray-400" />
                                <a href="#" className="text-blue-600 hover:underline">{country?.name || 'Việt Nam'}</a>
                                <ChevronRight size={14} className="text-gray-400" />
                                <a href="#" className="text-blue-600 hover:underline">{province?.name || 'Hà Nội'}</a>
                                <ChevronRight size={14} className="text-gray-400" />
                                <span className="text-gray-500">{accommodation.name}</span>
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
                                    Đánh giá của khách ({ratingSummary?.numberOfRatings || 0})
                                </button>
                            </div>

                            {/* Hotel Title & Rating */}
                            <HotelHeader
                                name={accommodation.name}
                                address={accommodation.location?.detailAddress || 'Địa chỉ không có sẵn'}
                                rating={ratingSummary?.numberOfRatings && ratingSummary.numberOfRatings > 0 ? Math.floor(ratingSummary.totalRating / ratingSummary.numberOfRatings) : 0}
                                reviewScore={ratingSummary?.numberOfRatings && ratingSummary.numberOfRatings > 0 ? Math.round((ratingSummary.totalRating / ratingSummary.numberOfRatings) * 10) / 10 : 0}
                                hasAirportShuttle={accommodation.amenities?.some(a =>
                                    a.amenity?.name?.toLowerCase().includes('shuttle') ||
                                    a.amenity?.name?.toLowerCase().includes('airport') ||
                                    a.amenity?.name?.toLowerCase().includes('đưa đón')
                                ) || false}
                            />

                            {/* Gallery */}
                            <HotelGallery
                                hotelName={accommodation.name}
                                images={finalHotelImages}
                            />

                            {/* Hotel Description */}
                            <div className="grid grid-cols-3 gap-6 mt-10">
                                <HotelDescription
                                    ref={overviewRef}
                                    accommodation={accommodation}
                                    geniusEligible={true}
                                    onShowMore={() => console.log('Show more clicked')}
                                />
                                <HotelSidebar
                                    location={accommodation.location}
                                    hotelName={accommodation.name}
                                    onShowMap={() => console.log('Show map clicked')}
                                />
                            </div>

                            {/* Available Rooms */}
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
                                    <div className="border-t border-b border-r border-gray-300 p-3">
                                        <GuestSelector
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
                                </div>

                                <AvailableRooms
                                    units={accommodation.units || []}
                                    hotelId={Array.isArray(params.id) ? params.id[0] : params.id}
                                    hotelName={accommodation.name}
                                />
                            </div>

                            {/* Reviews Section */}
                            <div ref={reviewsRef} className="mt-16 pb-8">
                                <ReviewSection />
                            </div>

                            {/* Detailed Amenities Section */}
                            <DetailedAmenities
                                ref={amenitiesRef}
                                hotelName={accommodation.name}
                                rating={ratingSummary?.numberOfRatings && ratingSummary.numberOfRatings > 0 ? Math.round((ratingSummary.totalRating / ratingSummary.numberOfRatings) * 10) / 10 : 0}
                                accommodationAmenities={accommodation.amenities}
                                units={accommodation.units}
                            />
                            {/* Hotel Rules Section */}
                            <HotelRules ref={rulesRef} />

                            {/* Hotel Location Section */}
                            <HotelLocationSection
                                ref={null}
                                location={accommodation.location}
                                hotelName={accommodation.name}
                                address={accommodation.location?.detailAddress || 'Địa chỉ không có sẵn'}
                            />

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
                    </>
                )}
            </div>
        </div>
    );
}
