"use client";

import React, { useState, useEffect } from 'react';
import { Tabs, TabsList, TabsTrigger, TabsContent } from '@/components/ui/tabs';
import { Card, CardContent, CardFooter } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Search, SlidersHorizontal, Heart, X, Trash2, CheckSquare, LayoutList, LayoutGrid } from 'lucide-react';
import Header from '@/components/Header';
import Link from 'next/link';
import Image from 'next/image';
import { useFavorites, FavoriteHotel, FavoriteLocation, SortOption } from '@/context/FavoritesContext';
import { FavoriteButton } from '@/components/ui/favorite-button';
import { HotelSkeleton, LocationSkeleton } from '@/components/ui/skeletons';
import { Checkbox } from "@/components/ui/checkbox";
import { Pagination } from "@/components/ui/pagination";

// Initialize mock data for new installations
const initHotels = [
    {
        id: '1',
        name: 'Vinpearl Resort & Spa Nha Trang Bay',
        imageUrl: 'https://images.unsplash.com/photo-1566073771259-6a8506099945?q=80&w=2070&auto=format&fit=crop&ixlib=rb-4.0.3',
        rating: 9.2,
        reviewCount: 1205,
        location: 'Nha Trang, Khánh Hòa',
        distance: '5.1 km từ trung tâm',
        price: 2500000,
        discountPrice: 2100000,
        amenities: [
            { id: '1', name: 'WiFi miễn phí' },
            { id: '2', name: 'Hồ bơi' },
            { id: '3', name: 'Bãi đậu xe' },
            { id: '4', name: 'Spa' },
            { id: '5', name: 'Phòng gym' },
        ],
        type: 'Resort',
        beds: 2,
        rooms: 1,
        isFavorite: true,
        hasPromotion: true,
        geniusLevel: 1,
        savedAt: new Date()
    },
    {
        id: '2',
        name: 'Mường Thanh Luxury Đà Nẵng',
        imageUrl: 'https://images.unsplash.com/photo-1551882547-ff40c63fe5fa?q=80&w=2070&auto=format&fit=crop&ixlib=rb-4.0.3',
        rating: 8.7,
        reviewCount: 872,
        location: 'Đà Nẵng',
        distance: '2.3 km từ trung tâm',
        price: 1800000,
        amenities: [
            { id: '1', name: 'WiFi miễn phí' },
            { id: '2', name: 'Hồ bơi' },
            { id: '3', name: 'Bãi đậu xe' },
        ],
        type: 'Khách sạn',
        beds: 1,
        rooms: 1,
        isFavorite: true,
        geniusLevel: 2,
        savedAt: new Date()
    },
    {
        id: '3',
        name: 'Intercontinental Phú Quốc',
        imageUrl: 'https://images.unsplash.com/photo-1584132967334-10e028bd69f7?q=80&w=2070&auto=format&fit=crop&ixlib=rb-4.0.3',
        rating: 9.5,
        reviewCount: 1502,
        location: 'Phú Quốc, Kiên Giang',
        distance: '7.8 km từ trung tâm',
        price: 5200000,
        discountPrice: 4680000,
        amenities: [
            { id: '1', name: 'WiFi miễn phí' },
            { id: '2', name: 'Hồ bơi' },
            { id: '3', name: 'Bãi đậu xe' },
            { id: '4', name: 'Spa' },
            { id: '5', name: 'Phòng gym' },
            { id: '6', name: 'Nhà hàng' },
        ],
        type: 'Resort',
        beds: 3,
        rooms: 2,
        isFavorite: true,
        hasPromotion: true,
        savedAt: new Date()
    }
];

const initLocations = [
    {
        id: '1',
        name: 'Phú Quốc',
        imageUrl: 'https://images.unsplash.com/photo-1583127812417-7c06e950a432?q=80&w=2069&auto=format&fit=crop&ixlib=rb-4.0.3',
        description: 'Đảo ngọc với những bãi biển đẹp nhất Việt Nam',
        totalHotels: 238,
        averagePrice: 2800000,
        isFavorite: true,
        savedAt: new Date()
    },
    {
        id: '2',
        name: 'Đà Lạt',
        imageUrl: 'https://images.unsplash.com/photo-1586959140255-7545c0e66e32?q=80&w=2070&auto=format&fit=crop&ixlib=rb-4.0.3',
        description: 'Thành phố ngàn hoa, khí hậu mát mẻ quanh năm',
        totalHotels: 412,
        averagePrice: 1200000,
        isFavorite: true,
        savedAt: new Date()
    },
    {
        id: '3',
        name: 'Hạ Long',
        imageUrl: 'https://images.unsplash.com/photo-1528127269322-539801943592?q=80&w=2070&auto=format&fit=crop&ixlib=rb-4.0.3',
        description: 'Vịnh đẹp kỳ quan thế giới với hàng nghìn hòn đảo đá vôi',
        totalHotels: 187,
        averagePrice: 2100000,
        isFavorite: true,
        savedAt: new Date()
    }
];

const SavedPage = () => {
    const [activeTab, setActiveTab] = useState('hotels');
    const [searchTerm, setSearchTerm] = useState('');
    const [sortBy, setSortBy] = useState<SortOption>('recent');
    const [showFilters, setShowFilters] = useState(false);
    const [bulkMode, setBulkMode] = useState(false);
    const [viewMode, setViewMode] = useState<'grid' | 'list'>('list');
    const [currentHotelPage, setCurrentHotelPage] = useState(1);
    const [currentLocationPage, setCurrentLocationPage] = useState(1);
    const itemsPerPage = 6;

    const {
        favoriteHotels,
        favoriteLocations,
        removeHotelFromFavorites,
        removeLocationFromFavorites,
        addHotelToFavorites,
        addLocationToFavorites,
        getSortedHotels,
        getSortedLocations,
        loading,
        // New bulk selection props
        selectedHotelIds,
        selectedLocationIds,
        toggleHotelSelection,
        toggleLocationSelection,
        selectAllHotels,
        selectAllLocations,
        clearHotelSelections,
        clearLocationSelections,
        removeSelectedHotels,
        removeSelectedLocations,
        isAllHotelsSelected,
        isAllLocationsSelected
    } = useFavorites();

    // Initialize with mock data if empty
    useEffect(() => {
        if (!loading && favoriteHotels.length === 0) {
            initHotels.forEach(hotel => addHotelToFavorites(hotel as FavoriteHotel));
        }

        if (!loading && favoriteLocations.length === 0) {
            initLocations.forEach(location => addLocationToFavorites(location as FavoriteLocation));
        }
    }, [loading, favoriteHotels.length, favoriteLocations.length, addHotelToFavorites, addLocationToFavorites]);

    // Reset bulk mode when tab changes
    useEffect(() => {
        if (bulkMode) {
            setBulkMode(false);
            clearHotelSelections();
            clearLocationSelections();
        }
    }, [activeTab, bulkMode, clearHotelSelections, clearLocationSelections]);

    // Get sorted and filtered items
    const filteredHotels = getSortedHotels(sortBy, searchTerm);
    const filteredLocations = getSortedLocations(sortBy, searchTerm);

    // Reset pagination when filters change
    useEffect(() => {
        setCurrentHotelPage(1);
    }, [sortBy, searchTerm, favoriteHotels.length]);

    useEffect(() => {
        setCurrentLocationPage(1);
    }, [sortBy, searchTerm, favoriteLocations.length]);

    // Apply pagination
    const paginatedHotels = filteredHotels.slice(
        (currentHotelPage - 1) * itemsPerPage,
        currentHotelPage * itemsPerPage
    );

    const paginatedLocations = filteredLocations.slice(
        (currentLocationPage - 1) * itemsPerPage,
        currentLocationPage * itemsPerPage
    );

    // Toggle bulk mode
    const toggleBulkMode = () => {
        setBulkMode(!bulkMode);
        if (bulkMode) {
            clearHotelSelections();
            clearLocationSelections();
        }
    };

    // Format price with Intl
    const formatPrice = (price: number) => {
        return new Intl.NumberFormat('vi-VN', {
            style: 'currency',
            currency: 'VND'
        }).format(price);
    };

    return (
        <div className="min-h-screen bg-gray-50 dark:bg-gray-900">
            <Header />
            <div className="container mx-auto px-4 max-w-7xl mt-8">
                <div className="flex flex-col md:flex-row md:items-center md:justify-between mb-6">
                    <div>
                        <h1 className="text-2xl font-bold mb-2">Danh sách đã lưu</h1>
                        <p className="text-gray-600">Quản lý các khách sạn và điểm đến bạn đã lưu</p>
                    </div>
                    <div className="mt-4 md:mt-0 flex space-x-2">
                        {activeTab === 'hotels' && selectedHotelIds.length > 0 && bulkMode && (
                            <Button
                                variant="destructive"
                                onClick={removeSelectedHotels}
                                size="sm"
                                className="flex items-center"
                            >
                                <Trash2 className="mr-1 h-4 w-4" />
                                Xóa ({selectedHotelIds.length})
                            </Button>
                        )}
                        {activeTab === 'locations' && selectedLocationIds.length > 0 && bulkMode && (
                            <Button
                                variant="destructive"
                                onClick={removeSelectedLocations}
                                size="sm"
                                className="flex items-center"
                            >
                                <Trash2 className="mr-1 h-4 w-4" />
                                Xóa ({selectedLocationIds.length})
                            </Button>
                        )}
                        <Button
                            variant={bulkMode ? "secondary" : "outline"}
                            onClick={toggleBulkMode}
                            size="sm"
                            className="flex items-center"
                        >
                            <CheckSquare className="mr-1 h-4 w-4" />
                            {bulkMode ? "Hủy chọn" : "Chọn nhiều"}
                        </Button>
                        <Link href="/myaccount">
                            <Button variant="outline" size="sm">
                                Quay lại tài khoản
                            </Button>
                        </Link>
                    </div>
                </div>

                <div className="bg-white rounded-lg shadow-sm border border-gray-100 mb-8">
                    <div className="p-6">
                        <Tabs value={activeTab} onValueChange={setActiveTab}>
                            <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between mb-6 gap-4">
                                <TabsList className="mb-4 sm:mb-0">
                                    <TabsTrigger value="hotels" className="px-4">
                                        Khách sạn đã lưu ({loading ? '...' : favoriteHotels.length})
                                    </TabsTrigger>
                                    <TabsTrigger value="locations" className="px-4">
                                        Địa điểm đã lưu ({loading ? '...' : favoriteLocations.length})
                                    </TabsTrigger>
                                </TabsList>

                                <div className="flex flex-col sm:flex-row gap-3 w-full sm:w-auto">
                                    <div className="relative flex-1">
                                        <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-4 w-4" />
                                        <Input
                                            placeholder="Tìm kiếm..."
                                            className="pl-9"
                                            value={searchTerm}
                                            onChange={(e) => setSearchTerm(e.target.value)}
                                        />
                                    </div>

                                    <div className="flex gap-2">
                                        <div className="flex border rounded-md overflow-hidden">
                                            <Button
                                                variant={viewMode === 'list' ? "secondary" : "ghost"}
                                                size="icon"
                                                className="rounded-none border-0"
                                                onClick={() => setViewMode('list')}
                                            >
                                                <LayoutList className="h-4 w-4" />
                                            </Button>
                                            <Button
                                                variant={viewMode === 'grid' ? "secondary" : "ghost"}
                                                size="icon"
                                                className="rounded-none border-0"
                                                onClick={() => setViewMode('grid')}
                                            >
                                                <LayoutGrid className="h-4 w-4" />
                                            </Button>
                                        </div>
                                        <Button
                                            variant="outline"
                                            size="icon"
                                            onClick={() => setShowFilters(!showFilters)}
                                            className={showFilters ? 'bg-blue-50 text-blue-600 border-blue-200' : ''}
                                        >
                                            <SlidersHorizontal className="h-4 w-4" />
                                        </Button>
                                        <Select value={sortBy} onValueChange={(value) => setSortBy(value as SortOption)}>
                                            <SelectTrigger className="w-full sm:w-[180px]">
                                                <SelectValue placeholder="Sắp xếp theo" />
                                            </SelectTrigger>
                                            <SelectContent>
                                                <SelectItem value="recent">Mới nhất</SelectItem>
                                                <SelectItem value="price-high">Giá cao nhất</SelectItem>
                                                <SelectItem value="price-low">Giá thấp nhất</SelectItem>
                                                <SelectItem value="rating">Đánh giá cao nhất</SelectItem>
                                                <SelectItem value="name-asc">Tên A-Z</SelectItem>
                                                <SelectItem value="name-desc">Tên Z-A</SelectItem>
                                            </SelectContent>
                                        </Select>
                                    </div>
                                </div>
                            </div>

                            {showFilters && (
                                <div className="mb-6 p-4 bg-gray-50 rounded-md border border-gray-100 animate-in fade-in-0 slide-in-from-top-5">
                                    <div className="flex justify-between items-center mb-3">
                                        <h3 className="font-semibold text-sm">Lọc thêm</h3>
                                        <Button variant="ghost" size="sm" onClick={() => setShowFilters(false)}>
                                            <X className="h-4 w-4" />
                                        </Button>
                                    </div>

                                    {activeTab === 'hotels' && (
                                        <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
                                            <div>
                                                <label className="text-xs font-medium block mb-1">Loại chỗ nghỉ</label>
                                                <Select>
                                                    <SelectTrigger className="w-full">
                                                        <SelectValue placeholder="Tất cả" />
                                                    </SelectTrigger>
                                                    <SelectContent>
                                                        <SelectItem value="all">Tất cả</SelectItem>
                                                        <SelectItem value="hotel">Khách sạn</SelectItem>
                                                        <SelectItem value="resort">Resort</SelectItem>
                                                        <SelectItem value="apartment">Căn hộ</SelectItem>
                                                    </SelectContent>
                                                </Select>
                                            </div>

                                            <div>
                                                <label className="text-xs font-medium block mb-1">Xếp hạng sao</label>
                                                <Select>
                                                    <SelectTrigger className="w-full">
                                                        <SelectValue placeholder="Tất cả" />
                                                    </SelectTrigger>
                                                    <SelectContent>
                                                        <SelectItem value="all">Tất cả</SelectItem>
                                                        <SelectItem value="5">5 sao</SelectItem>
                                                        <SelectItem value="4">4 sao</SelectItem>
                                                        <SelectItem value="3">3 sao</SelectItem>
                                                        <SelectItem value="2">2 sao</SelectItem>
                                                    </SelectContent>
                                                </Select>
                                            </div>

                                            <div>
                                                <label className="text-xs font-medium block mb-1">Điểm đánh giá</label>
                                                <Select>
                                                    <SelectTrigger className="w-full">
                                                        <SelectValue placeholder="Tất cả" />
                                                    </SelectTrigger>
                                                    <SelectContent>
                                                        <SelectItem value="all">Tất cả</SelectItem>
                                                        <SelectItem value="9+">Tuyệt hảo: 9+</SelectItem>
                                                        <SelectItem value="8+">Rất tốt: 8+</SelectItem>
                                                        <SelectItem value="7+">Tốt: 7+</SelectItem>
                                                        <SelectItem value="6+">Tạm được: 6+</SelectItem>
                                                    </SelectContent>
                                                </Select>
                                            </div>

                                            <div>
                                                <label className="text-xs font-medium block mb-1">Địa điểm</label>
                                                <Select>
                                                    <SelectTrigger className="w-full">
                                                        <SelectValue placeholder="Tất cả" />
                                                    </SelectTrigger>
                                                    <SelectContent>
                                                        <SelectItem value="all">Tất cả</SelectItem>
                                                        <SelectItem value="nha-trang">Nha Trang</SelectItem>
                                                        <SelectItem value="da-nang">Đà Nẵng</SelectItem>
                                                        <SelectItem value="phu-quoc">Phú Quốc</SelectItem>
                                                        <SelectItem value="da-lat">Đà Lạt</SelectItem>
                                                    </SelectContent>
                                                </Select>
                                            </div>
                                        </div>
                                    )}

                                    {activeTab === 'locations' && (
                                        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                                            <div>
                                                <label className="text-xs font-medium block mb-1">Khu vực</label>
                                                <Select>
                                                    <SelectTrigger className="w-full">
                                                        <SelectValue placeholder="Tất cả" />
                                                    </SelectTrigger>
                                                    <SelectContent>
                                                        <SelectItem value="all">Tất cả</SelectItem>
                                                        <SelectItem value="north">Miền Bắc</SelectItem>
                                                        <SelectItem value="central">Miền Trung</SelectItem>
                                                        <SelectItem value="south">Miền Nam</SelectItem>
                                                    </SelectContent>
                                                </Select>
                                            </div>

                                            <div>
                                                <label className="text-xs font-medium block mb-1">Loại điểm đến</label>
                                                <Select>
                                                    <SelectTrigger className="w-full">
                                                        <SelectValue placeholder="Tất cả" />
                                                    </SelectTrigger>
                                                    <SelectContent>
                                                        <SelectItem value="all">Tất cả</SelectItem>
                                                        <SelectItem value="beach">Biển</SelectItem>
                                                        <SelectItem value="mountain">Núi</SelectItem>
                                                        <SelectItem value="city">Thành phố</SelectItem>
                                                        <SelectItem value="countryside">Miền quê</SelectItem>
                                                    </SelectContent>
                                                </Select>
                                            </div>

                                            <div>
                                                <label className="text-xs font-medium block mb-1">Mức giá</label>
                                                <Select>
                                                    <SelectTrigger className="w-full">
                                                        <SelectValue placeholder="Tất cả" />
                                                    </SelectTrigger>
                                                    <SelectContent>
                                                        <SelectItem value="all">Tất cả</SelectItem>
                                                        <SelectItem value="budget">Tiết kiệm</SelectItem>
                                                        <SelectItem value="mid">Trung bình</SelectItem>
                                                        <SelectItem value="luxury">Cao cấp</SelectItem>
                                                    </SelectContent>
                                                </Select>
                                            </div>
                                        </div>
                                    )}

                                    <div className="flex justify-end mt-4 space-x-2">
                                        <Button
                                            variant="outline"
                                            size="sm"
                                            onClick={() => {
                                                setSearchTerm('');
                                                setSortBy('recent');
                                            }}
                                        >
                                            Xóa bộ lọc
                                        </Button>
                                        <Button size="sm">Áp dụng</Button>
                                    </div>
                                </div>
                            )}

                            <TabsContent value="hotels" className="mt-2">
                                {bulkMode && filteredHotels.length > 0 && (
                                    <div className="mb-4 p-3 bg-gray-50 rounded-md border border-gray-200 flex items-center justify-between">
                                        <div className="flex items-center space-x-2">
                                            <Checkbox
                                                id="select-all-hotels"
                                                checked={isAllHotelsSelected}
                                                onCheckedChange={selectAllHotels}
                                            />
                                            <label htmlFor="select-all-hotels" className="text-sm font-medium">
                                                Chọn tất cả ({filteredHotels.length})
                                            </label>
                                        </div>
                                        {selectedHotelIds.length > 0 && (
                                            <Button
                                                variant="destructive"
                                                size="sm"
                                                onClick={removeSelectedHotels}
                                                className="flex items-center"
                                            >
                                                <Trash2 className="mr-1 h-4 w-4" />
                                                Xóa đã chọn ({selectedHotelIds.length})
                                            </Button>
                                        )}
                                    </div>
                                )}

                                {loading ? (
                                    <div className="space-y-6">
                                        {[1, 2, 3].map(i => <HotelSkeleton key={i} />)}
                                    </div>
                                ) : filteredHotels.length > 0 ? (
                                    <>
                                        <div className={viewMode === 'list' ? 'space-y-6' : 'grid grid-cols-1 md:grid-cols-2 gap-6'}>
                                            {paginatedHotels.map(hotel => (
                                                <Card
                                                    key={hotel.id}
                                                    className={`overflow-hidden transition-all duration-300 hover:shadow-md group ${selectedHotelIds.includes(hotel.id) ? 'ring-2 ring-primary ring-offset-1' : ''
                                                        }`}
                                                >
                                                    <div className={`flex flex-col ${viewMode === 'list' ? 'md:flex-row' : ''}`}>
                                                        <div className={`relative ${viewMode === 'list' ? 'md:w-1/3' : ''} h-[200px] md:h-auto`}>
                                                            {bulkMode && (
                                                                <div className="absolute top-2 left-2 z-10">
                                                                    <Checkbox
                                                                        id={`hotel-${hotel.id}`}
                                                                        checked={selectedHotelIds.includes(hotel.id)}
                                                                        onCheckedChange={() => toggleHotelSelection(hotel.id)}
                                                                        className="bg-white/80 border-gray-400"
                                                                    />
                                                                </div>
                                                            )}
                                                            <Image
                                                                src={hotel.imageUrl}
                                                                alt={hotel.name}
                                                                fill
                                                                className="object-cover group-hover:scale-105 transition-transform duration-500"
                                                            />
                                                            {hotel.hasPromotion && (
                                                                <div className="absolute top-2 left-2 bg-red-500 text-white text-xs font-medium px-2 py-1 rounded">
                                                                    Giảm giá
                                                                </div>
                                                            )}
                                                            {!bulkMode && (
                                                                <div className="absolute top-2 right-2">
                                                                    <FavoriteButton
                                                                        isFavorite={true}
                                                                        onClick={() => removeHotelFromFavorites(hotel.id)}
                                                                        size="md"
                                                                    />
                                                                </div>
                                                            )}
                                                        </div>

                                                        <div className="p-6 flex-1 flex flex-col justify-between">
                                                            <div>
                                                                <div className="flex justify-between items-start mb-2">
                                                                    <h3 className="text-xl font-bold">{hotel.name}</h3>
                                                                    {hotel.rating && (
                                                                        <div className="flex items-center">
                                                                            <div className={`${hotel.rating >= 9 ? 'bg-green-600' :
                                                                                hotel.rating >= 8 ? 'bg-green-500' :
                                                                                    hotel.rating >= 7 ? 'bg-[#febb02] text-[#262626]' :
                                                                                        'bg-orange-500'
                                                                                } text-white px-2 py-1 rounded font-medium`}>
                                                                                {hotel.rating}
                                                                            </div>
                                                                            <span className="ml-2 text-sm text-gray-600">
                                                                                {hotel.reviewCount} đánh giá
                                                                            </span>
                                                                        </div>
                                                                    )}
                                                                </div>

                                                                <p className="text-gray-600 mb-4">
                                                                    <span className="font-medium">{hotel.location}</span>
                                                                    {hotel.distance && <span> • {hotel.distance}</span>}
                                                                </p>

                                                                <div className="flex flex-wrap gap-2 mb-4">
                                                                    {hotel.amenities && hotel.amenities.slice(0, 3).map(amenity => (
                                                                        <span key={amenity.id} className="inline-flex items-center bg-gray-100 px-2 py-1 rounded text-sm">
                                                                            {amenity.name}
                                                                        </span>
                                                                    ))}
                                                                    {hotel.amenities && hotel.amenities.length > 3 && (
                                                                        <span className="inline-flex items-center bg-gray-100 px-2 py-1 rounded text-sm">
                                                                            +{hotel.amenities.length - 3} tiện ích khác
                                                                        </span>
                                                                    )}
                                                                </div>

                                                                <div className="flex items-center text-gray-600 text-sm mb-4">
                                                                    {hotel.type && <span className="mr-2">{hotel.type}</span>}
                                                                    {hotel.beds && <span className="mr-2">• {hotel.beds} giường</span>}
                                                                    {hotel.rooms && <span>• {hotel.rooms} phòng</span>}
                                                                </div>

                                                                <div className="text-xs text-gray-500">
                                                                    Đã lưu {new Date(hotel.savedAt).toLocaleDateString('vi-VN')}
                                                                </div>
                                                            </div>

                                                            <div className="flex justify-between items-end mt-4">
                                                                <div>
                                                                    <div className="font-bold text-xl text-[#0071c2]">
                                                                        {formatPrice(hotel.discountPrice || hotel.price)}
                                                                    </div>
                                                                    {hotel.discountPrice && (
                                                                        <div className="text-gray-500 line-through text-sm">
                                                                            {formatPrice(hotel.price)}
                                                                        </div>
                                                                    )}
                                                                </div>

                                                                <div className="flex space-x-2">
                                                                    {!bulkMode && (
                                                                        <Button
                                                                            variant="outline"
                                                                            size="sm"
                                                                            onClick={() => removeHotelFromFavorites(hotel.id)}
                                                                        >
                                                                            Xóa
                                                                        </Button>
                                                                    )}
                                                                    <Link href={`/hotel/${hotel.id}`}>
                                                                        <Button size="sm">
                                                                            Xem chi tiết
                                                                        </Button>
                                                                    </Link>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </Card>
                                            ))}
                                        </div>

                                        {filteredHotels.length > itemsPerPage && (
                                            <div className="mt-6">
                                                <Pagination
                                                    total={filteredHotels.length}
                                                    perPage={itemsPerPage}
                                                    currentPage={currentHotelPage}
                                                    onPageChange={setCurrentHotelPage}
                                                />
                                            </div>
                                        )}
                                    </>
                                ) : (
                                    <div className="text-center py-16">
                                        <div className="h-20 w-20 bg-gray-100 rounded-full flex items-center justify-center mx-auto mb-4">
                                            <Heart className="h-8 w-8 text-gray-400 stroke-[1.5]" />
                                        </div>
                                        <h3 className="text-lg font-medium mb-2">Không tìm thấy khách sạn đã lưu</h3>
                                        <p className="text-gray-500 mb-6">Bạn chưa lưu khách sạn nào hoặc không có kết quả phù hợp với tìm kiếm của bạn</p>
                                        <Link href="/search">
                                            <Button>
                                                Tìm kiếm khách sạn
                                            </Button>
                                        </Link>
                                    </div>
                                )}
                            </TabsContent>

                            <TabsContent value="locations" className="mt-2">
                                {bulkMode && filteredLocations.length > 0 && (
                                    <div className="mb-4 p-3 bg-gray-50 rounded-md border border-gray-200 flex items-center justify-between">
                                        <div className="flex items-center space-x-2">
                                            <Checkbox
                                                id="select-all-locations"
                                                checked={isAllLocationsSelected}
                                                onCheckedChange={selectAllLocations}
                                            />
                                            <label htmlFor="select-all-locations" className="text-sm font-medium">
                                                Chọn tất cả ({filteredLocations.length})
                                            </label>
                                        </div>
                                        {selectedLocationIds.length > 0 && (
                                            <Button
                                                variant="destructive"
                                                size="sm"
                                                onClick={removeSelectedLocations}
                                                className="flex items-center"
                                            >
                                                <Trash2 className="mr-1 h-4 w-4" />
                                                Xóa đã chọn ({selectedLocationIds.length})
                                            </Button>
                                        )}
                                    </div>
                                )}

                                {loading ? (
                                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                                        {[1, 2, 3].map(i => <LocationSkeleton key={i} />)}
                                    </div>
                                ) : filteredLocations.length > 0 ? (
                                    <>
                                        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                                            {paginatedLocations.map(location => (
                                                <Card
                                                    key={location.id}
                                                    className={`overflow-hidden transition-all duration-300 hover:shadow-md group ${selectedLocationIds.includes(location.id) ? 'ring-2 ring-primary ring-offset-1' : ''
                                                        }`}
                                                >
                                                    <div className="relative h-[200px]">
                                                        {bulkMode && (
                                                            <div className="absolute top-2 left-2 z-10">
                                                                <Checkbox
                                                                    id={`location-${location.id}`}
                                                                    checked={selectedLocationIds.includes(location.id)}
                                                                    onCheckedChange={() => toggleLocationSelection(location.id)}
                                                                    className="bg-white/80 border-gray-400"
                                                                />
                                                            </div>
                                                        )}
                                                        <Image
                                                            src={location.imageUrl}
                                                            alt={location.name}
                                                            fill
                                                            className="object-cover group-hover:scale-105 transition-transform duration-500"
                                                        />
                                                        {!bulkMode && (
                                                            <div className="absolute top-2 right-2">
                                                                <FavoriteButton
                                                                    isFavorite={true}
                                                                    onClick={() => removeLocationFromFavorites(location.id)}
                                                                    size="md"
                                                                />
                                                            </div>
                                                        )}
                                                    </div>

                                                    <CardContent className="p-6">
                                                        <h3 className="text-xl font-bold mb-2">{location.name}</h3>
                                                        <p className="text-gray-600 mb-4 h-12 line-clamp-2">{location.description}</p>

                                                        <div className="flex justify-between text-sm text-gray-600 mb-2">
                                                            <span>{location.totalHotels} khách sạn</span>
                                                            <span>Trung bình {formatPrice(location.averagePrice)}/đêm</span>
                                                        </div>

                                                        <div className="text-xs text-gray-500">
                                                            Đã lưu {new Date(location.savedAt).toLocaleDateString('vi-VN')}
                                                        </div>
                                                    </CardContent>

                                                    <CardFooter className="px-6 pb-6 pt-0 flex justify-between">
                                                        {!bulkMode && (
                                                            <Button
                                                                variant="outline"
                                                                size="sm"
                                                                onClick={() => removeLocationFromFavorites(location.id)}
                                                            >
                                                                Xóa
                                                            </Button>
                                                        )}
                                                        <Link href={`/search?location=${encodeURIComponent(location.name)}`}>
                                                            <Button size="sm">
                                                                Xem khách sạn
                                                            </Button>
                                                        </Link>
                                                    </CardFooter>
                                                </Card>
                                            ))}
                                        </div>

                                        {filteredLocations.length > itemsPerPage && (
                                            <div className="mt-6">
                                                <Pagination
                                                    total={filteredLocations.length}
                                                    perPage={itemsPerPage}
                                                    currentPage={currentLocationPage}
                                                    onPageChange={setCurrentLocationPage}
                                                />
                                            </div>
                                        )}
                                    </>
                                ) : (
                                    <div className="text-center py-16">
                                        <div className="h-20 w-20 bg-gray-100 rounded-full flex items-center justify-center mx-auto mb-4">
                                            <Heart className="h-8 w-8 text-gray-400 stroke-[1.5]" />
                                        </div>
                                        <h3 className="text-lg font-medium mb-2">Không tìm thấy địa điểm đã lưu</h3>
                                        <p className="text-gray-500 mb-6">Bạn chưa lưu địa điểm nào hoặc không có kết quả phù hợp với tìm kiếm của bạn</p>
                                        <Link href="/">
                                            <Button>
                                                Khám phá điểm đến
                                            </Button>
                                        </Link>
                                    </div>
                                )}
                            </TabsContent>
                        </Tabs>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default SavedPage;
