"use client";

import React, { useState, useEffect, useCallback } from 'react';
import { Card, CardContent, CardFooter } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Search, Heart, Trash2, CheckSquare, LayoutList, LayoutGrid, Plus, Star, MapPin } from 'lucide-react';
import { Checkbox } from "@/components/ui/checkbox";
import { Pagination } from "@/components/ui/pagination";
import { toast } from "sonner";
import Header from '@/components/Header';
import Link from 'next/link';
import Image from 'next/image';

// Import wishlist types and service
import {
    Wishlist,
    WishlistWithDetails,
    WishlistItemWithDetails,
} from '@/types/wishlist';
import wishlistService from '@/services/wishlist/wishlistService';

type SortOption = 'recent' | 'name-asc' | 'name-desc' | 'price-low' | 'price-high' | 'rating';

const MyWishlistsPage = () => {
    // States
    const [wishlists, setWishlists] = useState<Wishlist[]>([]);
    const [selectedWishlist, setSelectedWishlist] = useState<WishlistWithDetails | null>(null);
    const [wishlistItems, setWishlistItems] = useState<WishlistItemWithDetails[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    // UI States
    const [searchTerm, setSearchTerm] = useState('');
    const [sortBy, setSortBy] = useState<SortOption>('recent');
    const [bulkMode, setBulkMode] = useState(false);
    const [viewMode, setViewMode] = useState<'grid' | 'list'>('list');
    const [currentPage, setCurrentPage] = useState(1);
    const [selectedItems, setSelectedItems] = useState<Set<number>>(new Set());

    // Create wishlist states
    const [isCreatingWishlist, setIsCreatingWishlist] = useState(false);
    const [newWishlistName, setNewWishlistName] = useState('');
    const [showCreateForm, setShowCreateForm] = useState(false);

    const itemsPerPage = 6;

    // Load wishlists data
    const loadWishlists = useCallback(async () => {
        try {
            setLoading(true);
            setError(null);
            const wishlistsData = await wishlistService.getWishlists();
            setWishlists(wishlistsData);

            // Auto-select first wishlist if available
            if (wishlistsData.length > 0 && !selectedWishlist) {
                await loadWishlistDetails(wishlistsData[0].id);
            }
        } catch (err) {
            console.error('Failed to load wishlists:', err);
            setError('Không thể tải danh sách yêu thích. Vui lòng thử lại.');
            toast.error('Không thể tải danh sách yêu thích');
        } finally {
            setLoading(false);
        }
    }, [selectedWishlist]);

    // Load wishlist details with accommodation info
    const loadWishlistDetails = useCallback(async (wishlistId: number) => {
        try {
            const wishlistWithDetails = await wishlistService.getWishlistWithDetails(wishlistId);
            setSelectedWishlist(wishlistWithDetails);
            setWishlistItems(wishlistWithDetails.items || []);
        } catch (err) {
            console.error('Failed to load wishlist details:', err);
            toast.error('Không thể tải chi tiết danh sách yêu thích');
        }
    }, []);

    // Create new wishlist
    const handleCreateWishlist = useCallback(async () => {
        if (!newWishlistName.trim()) {
            toast.error('Vui lòng nhập tên danh sách');
            return;
        }

        try {
            setIsCreatingWishlist(true);
            const newWishlist = await wishlistService.createWishlist({
                name: newWishlistName.trim(),
                items: []
            });

            setWishlists(prev => [...prev, newWishlist]);
            setNewWishlistName('');
            setShowCreateForm(false);
            await loadWishlistDetails(newWishlist.id);
            toast.success('Tạo danh sách yêu thích thành công');
        } catch (err) {
            console.error('Failed to create wishlist:', err);
            toast.error('Không thể tạo danh sách yêu thích');
        } finally {
            setIsCreatingWishlist(false);
        }
    }, [newWishlistName, loadWishlistDetails]);

    // Remove items from wishlist
    const handleRemoveItems = useCallback(async () => {
        if (!selectedWishlist || selectedItems.size === 0) return;

        try {
            // Remove selected items
            for (const itemId of selectedItems) {
                await wishlistService.removeWishlistItem(selectedWishlist.id, itemId);
            }

            // Reload wishlist details
            await loadWishlistDetails(selectedWishlist.id);
            setSelectedItems(new Set());
            setBulkMode(false);
            toast.success(`Đã xóa ${selectedItems.size} khách sạn khỏi danh sách`);
        } catch (err) {
            console.error('Failed to remove items:', err);
            toast.error('Không thể xóa khách sạn khỏi danh sách');
        }
    }, [selectedWishlist, selectedItems, loadWishlistDetails]);

    // Toggle item selection
    const toggleItemSelection = useCallback((itemId: number) => {
        setSelectedItems(prev => {
            const newSet = new Set(prev);
            if (newSet.has(itemId)) {
                newSet.delete(itemId);
            } else {
                newSet.add(itemId);
            }
            return newSet;
        });
    }, []);

    // Select all items
    const selectAllItems = useCallback(() => {
        setSelectedItems(new Set(wishlistItems.map(item => item.id)));
    }, [wishlistItems]);

    // Clear selections
    const clearSelections = useCallback(() => {
        setSelectedItems(new Set());
    }, []);

    // Load data on mount
    useEffect(() => {
        loadWishlists();
    }, [loadWishlists]);

    // Filter and sort wishlist items
    const filteredItems = wishlistItems.filter(item => {
        if (!searchTerm) return true;
        return item.accommodationName.toLowerCase().includes(searchTerm.toLowerCase()) ||
            item.accommodation?.location.address.toLowerCase().includes(searchTerm.toLowerCase());
    });

    // Sort items based on sortBy
    const sortedItems = [...filteredItems].sort((a, b) => {
        switch (sortBy) {
            case 'recent':
                return new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime();
            case 'name-asc':
                return a.accommodationName.localeCompare(b.accommodationName);
            case 'name-desc':
                return b.accommodationName.localeCompare(a.accommodationName);
            case 'price-low':
                const priceA = a.accommodation?.priceInfo?.currentPrice || 0;
                const priceB = b.accommodation?.priceInfo?.currentPrice || 0;
                return priceA - priceB;
            case 'price-high':
                const priceA2 = a.accommodation?.priceInfo?.currentPrice || 0;
                const priceB2 = b.accommodation?.priceInfo?.currentPrice || 0;
                return priceB2 - priceA2;
            case 'rating':
                const ratingA = a.accommodation?.ratingSummary?.rating || 0;
                const ratingB = b.accommodation?.ratingSummary?.rating || 0;
                return ratingB - ratingA;
            default:
                return 0;
        }
    });

    // Apply pagination to sorted items
    const paginatedItems = sortedItems.slice(
        (currentPage - 1) * itemsPerPage,
        currentPage * itemsPerPage
    );

    // Reset pagination when filters change
    useEffect(() => {
        setCurrentPage(1);
    }, [sortBy, searchTerm, wishlistItems.length]);

    // Toggle bulk mode
    const toggleBulkMode = () => {
        setBulkMode(!bulkMode);
        if (bulkMode) {
            setSelectedItems(new Set());
        }
    };

    // Format price with Intl
    const formatPrice = (price: number) => {
        return new Intl.NumberFormat('vi-VN', {
            style: 'currency',
            currency: 'VND'
        }).format(price);
    };

    if (error) {
        return (
            <div className="min-h-screen bg-gray-50 dark:bg-gray-900">
                <Header />
                <div className="container mx-auto px-4 max-w-7xl mt-8">
                    <div className="text-center py-16">
                        <div className="h-20 w-20 bg-red-100 rounded-full flex items-center justify-center mx-auto mb-4">
                            <Heart className="h-8 w-8 text-red-500" />
                        </div>
                        <h3 className="text-lg font-medium mb-2">Có lỗi xảy ra</h3>
                        <p className="text-gray-500 mb-6">{error}</p>
                        <Button onClick={loadWishlists}>
                            Thử lại
                        </Button>
                    </div>
                </div>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-gray-50 dark:bg-gray-900">
            <Header />
            <div className="container mx-auto px-4 max-w-7xl mt-8">
                {/* Page Header */}
                <div className="flex flex-col md:flex-row md:items-center md:justify-between mb-6">
                    <div>
                        <h1 className="text-2xl font-bold mb-2">Danh sách yêu thích</h1>
                        <p className="text-gray-600">Quản lý các khách sạn bạn đã lưu</p>
                    </div>
                    <div className="mt-4 md:mt-0 flex space-x-2">
                        {selectedItems.size > 0 && bulkMode && (
                            <Button
                                variant="destructive"
                                onClick={handleRemoveItems}
                                size="sm"
                                className="flex items-center"
                            >
                                <Trash2 className="mr-1 h-4 w-4" />
                                Xóa ({selectedItems.size})
                            </Button>
                        )}
                        <Button
                            variant={bulkMode ? "secondary" : "outline"}
                            onClick={toggleBulkMode}
                            size="sm"
                            className="flex items-center"
                            disabled={wishlistItems.length === 0}
                        >
                            <CheckSquare className="mr-1 h-4 w-4" />
                            {bulkMode ? "Hủy chọn" : "Chọn nhiều"}
                        </Button>
                        <Button
                            onClick={() => setShowCreateForm(true)}
                            size="sm"
                            className="flex items-center"
                        >
                            <Plus className="mr-1 h-4 w-4" />
                            Tạo danh sách mới
                        </Button>
                    </div>
                </div>

                {/* Main Content */}
                <div className="bg-white rounded-lg shadow-sm border border-gray-100 mb-8">
                    <div className="p-6">
                        {/* Wishlist Selector and Controls */}
                        <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between mb-6 gap-4">
                            {/* Wishlist Selector */}
                            <div className="flex items-center gap-4">
                                <Select
                                    value={selectedWishlist?.id.toString() || ""}
                                    onValueChange={(value) => loadWishlistDetails(Number(value))}
                                >
                                    <SelectTrigger className="w-[200px]">
                                        <SelectValue placeholder="Chọn danh sách" />
                                    </SelectTrigger>
                                    <SelectContent>
                                        {wishlists.map((wishlist) => (
                                            <SelectItem key={wishlist.id} value={wishlist.id.toString()}>
                                                {wishlist.name} ({wishlist.items?.length || 0})
                                            </SelectItem>
                                        ))}
                                    </SelectContent>
                                </Select>
                            </div>

                            {/* Search and Controls */}
                            <div className="flex flex-col sm:flex-row gap-3 w-full sm:w-auto">
                                <div className="relative flex-1">
                                    <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-4 w-4" />
                                    <Input
                                        placeholder="Tìm kiếm khách sạn..."
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
                                            onClick={() => setViewMode('list')}
                                            className="rounded-none"
                                        >
                                            <LayoutList className="h-4 w-4" />
                                        </Button>
                                        <Button
                                            variant={viewMode === 'grid' ? "secondary" : "ghost"}
                                            size="icon"
                                            onClick={() => setViewMode('grid')}
                                            className="rounded-none"
                                        >
                                            <LayoutGrid className="h-4 w-4" />
                                        </Button>
                                    </div>

                                    <Select value={sortBy} onValueChange={(value: SortOption) => setSortBy(value)}>
                                        <SelectTrigger className="w-[140px]">
                                            <SelectValue />
                                        </SelectTrigger>
                                        <SelectContent>
                                            <SelectItem value="recent">Mới nhất</SelectItem>
                                            <SelectItem value="name-asc">Tên A-Z</SelectItem>
                                            <SelectItem value="name-desc">Tên Z-A</SelectItem>
                                            <SelectItem value="price-low">Giá thấp</SelectItem>
                                            <SelectItem value="price-high">Giá cao</SelectItem>
                                            <SelectItem value="rating">Đánh giá</SelectItem>
                                        </SelectContent>
                                    </Select>
                                </div>
                            </div>
                        </div>

                        {/* Bulk Selection Controls */}
                        {bulkMode && wishlistItems.length > 0 && (
                            <div className="flex items-center gap-4 mb-4 p-3 bg-blue-50 rounded-lg">
                                <Checkbox
                                    id="select-all"
                                    checked={selectedItems.size === wishlistItems.length && wishlistItems.length > 0}
                                    onCheckedChange={(checked) => {
                                        if (checked) {
                                            selectAllItems();
                                        } else {
                                            clearSelections();
                                        }
                                    }}
                                />
                                <label htmlFor="select-all" className="text-sm font-medium">
                                    Chọn tất cả ({wishlistItems.length} khách sạn)
                                </label>
                            </div>
                        )}

                        {/* Content */}
                        {loading ? (
                            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                                {[...Array(6)].map((_, i) => (
                                    <div key={i} className="animate-pulse">
                                        <div className="bg-gray-200 h-48 rounded-lg mb-4"></div>
                                        <div className="h-4 bg-gray-200 rounded mb-2"></div>
                                        <div className="h-4 bg-gray-200 rounded w-2/3"></div>
                                    </div>
                                ))}
                            </div>
                        ) : !selectedWishlist ? (
                            <div className="text-center py-16">
                                <div className="h-20 w-20 bg-gray-100 rounded-full flex items-center justify-center mx-auto mb-4">
                                    <Heart className="h-8 w-8 text-gray-400" />
                                </div>
                                <h3 className="text-lg font-medium mb-2">Chưa có danh sách yêu thích</h3>
                                <p className="text-gray-500 mb-6">Tạo danh sách đầu tiên để bắt đầu lưu các khách sạn yêu thích</p>
                                <Button onClick={() => setShowCreateForm(true)}>
                                    <Plus className="mr-2 h-4 w-4" />
                                    Tạo danh sách mới
                                </Button>
                            </div>
                        ) : sortedItems.length > 0 ? (
                            <>
                                {viewMode === 'grid' ? (
                                    // Grid Layout
                                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                                        {paginatedItems.map((item) => (
                                            <Card key={item.id} className="group overflow-hidden hover:shadow-xl transition-all duration-300 border border-gray-200 rounded-xl bg-white">
                                                {bulkMode && (
                                                    <div className="absolute top-4 left-4 z-20">
                                                        <Checkbox
                                                            checked={selectedItems.has(item.id)}
                                                            onCheckedChange={() => toggleItemSelection(item.id)}
                                                            className="bg-white shadow-md border-2"
                                                        />
                                                    </div>
                                                )}

                                                <div className="relative overflow-hidden">
                                                    <Link href={`/hotel/${item.accommodationId}`}>
                                                        <div className="relative h-48 cursor-pointer">
                                                            <Image
                                                                src={item.accommodationImageUrl || '/placeholder-hotel.jpg'}
                                                                alt={item.accommodationName}
                                                                fill
                                                                className="object-cover group-hover:scale-105 transition-transform duration-300"
                                                            />
                                                            <div className="absolute inset-0 bg-gradient-to-t from-black/20 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-300"></div>
                                                        </div>
                                                    </Link>

                                                    {/* Heart Button */}
                                                    <button
                                                        onClick={async () => {
                                                            try {
                                                                await wishlistService.removeWishlistItem(selectedWishlist!.id, item.id);
                                                                await loadWishlistDetails(selectedWishlist!.id);
                                                                toast.success('Đã xóa khỏi danh sách yêu thích');
                                                            } catch (error) {
                                                                toast.error('Không thể xóa khỏi danh sách');
                                                            }
                                                        }}
                                                        className="absolute top-4 right-4 z-10 p-2 rounded-full bg-white/90 backdrop-blur-sm border border-white/20 shadow-lg hover:bg-white hover:scale-110 transition-all duration-200 group/heart"
                                                    >
                                                        <Heart className="h-4 w-4 text-red-500 fill-red-500 group-hover/heart:scale-110 transition-transform duration-200" />
                                                    </button>

                                                    {/* Price Badge */}
                                                    {item.accommodation?.priceInfo && (
                                                        <div className="absolute bottom-4 right-4 bg-white/95 backdrop-blur-sm px-3 py-1 rounded-full shadow-lg border border-white/20">
                                                            <span className="text-sm font-semibold text-gray-900">
                                                                {formatPrice(item.accommodation.priceInfo.currentPrice)}
                                                            </span>
                                                            <span className="text-xs text-gray-500 ml-1">/đêm</span>
                                                        </div>
                                                    )}
                                                </div>

                                                <CardContent className="p-4 space-y-3">
                                                    <div className="space-y-2">
                                                        <Link href={`/hotel/${item.accommodationId}`}>
                                                            <h3 className="font-semibold text-lg text-gray-900 hover:text-blue-600 transition-colors duration-200 overflow-hidden text-ellipsis whitespace-nowrap cursor-pointer">
                                                                {item.accommodationName}
                                                            </h3>
                                                        </Link>

                                                        {item.accommodation && (
                                                            <>
                                                                <div className="flex items-center gap-1.5 text-gray-600">
                                                                    <MapPin className="h-4 w-4 text-gray-400 flex-shrink-0" />
                                                                    <span className="text-sm overflow-hidden text-ellipsis whitespace-nowrap">
                                                                        {item.accommodation.location.address}
                                                                    </span>
                                                                </div>

                                                                {item.accommodation.ratingSummary.numberOfRatings > 0 && (
                                                                    <div className="flex items-center gap-2">
                                                                        <div className="flex items-center gap-1">
                                                                            <Star className="h-4 w-4 text-yellow-400 fill-yellow-400" />
                                                                            <span className="text-sm font-medium text-gray-900">
                                                                                {item.accommodation.ratingSummary.rating.toFixed(1)}
                                                                            </span>
                                                                        </div>
                                                                        <span className="text-sm text-gray-500">
                                                                            ({item.accommodation.ratingSummary.numberOfRatings} đánh giá)
                                                                        </span>
                                                                    </div>
                                                                )}

                                                                <p className="text-sm text-gray-600 leading-relaxed overflow-hidden" style={{
                                                                    display: '-webkit-box',
                                                                    WebkitLineClamp: 2,
                                                                    WebkitBoxOrient: 'vertical'
                                                                }}>
                                                                    {item.accommodation.description}
                                                                </p>
                                                            </>
                                                        )}
                                                    </div>

                                                    <div className="flex items-center justify-between pt-2 border-t border-gray-100">
                                                        <div className="text-xs text-gray-500">
                                                            Đã lưu: {new Date(item.createdAt).toLocaleDateString('vi-VN')}
                                                        </div>
                                                    </div>
                                                </CardContent>

                                                <CardFooter className="p-4 pt-0 flex gap-2">
                                                    <Link href={`/hotel/${item.accommodationId}`} className="flex-1">
                                                        <Button size="sm" className="w-full">
                                                            Xem chi tiết
                                                        </Button>
                                                    </Link>
                                                    {!bulkMode && (
                                                        <Button
                                                            variant="outline"
                                                            size="sm"
                                                            onClick={async () => {
                                                                try {
                                                                    await wishlistService.removeWishlistItem(selectedWishlist!.id, item.id);
                                                                    await loadWishlistDetails(selectedWishlist!.id);
                                                                    toast.success('Đã xóa khỏi danh sách');
                                                                } catch (error) {
                                                                    toast.error('Không thể xóa khỏi danh sách');
                                                                }
                                                            }}
                                                            className="h-8 w-8 p-0"
                                                        >
                                                            <Trash2 className="h-4 w-4" />
                                                        </Button>
                                                    )}
                                                </CardFooter>
                                            </Card>
                                        ))}
                                    </div>
                                ) : (
                                    // List Layout - Horizontal Cards
                                    <div className="space-y-4">
                                        {paginatedItems.map((item) => (
                                            <Card key={item.id} className="group overflow-hidden hover:shadow-lg transition-all duration-300 border border-gray-200 rounded-xl bg-white">
                                                <div className="flex">
                                                    {bulkMode && (
                                                        <div className="absolute top-4 left-4 z-20">
                                                            <Checkbox
                                                                checked={selectedItems.has(item.id)}
                                                                onCheckedChange={() => toggleItemSelection(item.id)}
                                                                className="bg-white shadow-md border-2"
                                                            />
                                                        </div>
                                                    )}

                                                    {/* Image Section */}
                                                    <div className="relative md:w-1/3 h-[200px] md:h-auto bg-gray-200">
                                                        <Link href={`/hotel/${item.accommodationId}`}>
                                                            <div className="relative w-full h-full cursor-pointer overflow-hidden rounded-l-xl">
                                                                <Image
                                                                    src={item.accommodationImageUrl || '/placeholder-hotel.jpg'}
                                                                    alt={item.accommodationName}
                                                                    fill
                                                                    className="object-cover group-hover:scale-105 transition-transform duration-300"
                                                                />
                                                                <div className="absolute inset-0 bg-gradient-to-r from-transparent to-black/10 opacity-0 group-hover:opacity-100 transition-opacity duration-300"></div>
                                                            </div>
                                                        </Link>

                                                        {/* Heart Button */}
                                                        <button
                                                            onClick={async () => {
                                                                try {
                                                                    await wishlistService.removeWishlistItem(selectedWishlist!.id, item.id);
                                                                    await loadWishlistDetails(selectedWishlist!.id);
                                                                    toast.success('Đã xóa khỏi danh sách yêu thích');
                                                                } catch (error) {
                                                                    toast.error('Không thể xóa khỏi danh sách');
                                                                }
                                                            }}
                                                            className="absolute top-3 right-3 z-10 p-1.5 rounded-full bg-white/90 backdrop-blur-sm border border-white/20 shadow-md hover:bg-white hover:scale-110 transition-all duration-200 group/heart"
                                                        >
                                                            <Heart className="h-3.5 w-3.5 text-red-500 fill-red-500 group-hover/heart:scale-110 transition-transform duration-200" />
                                                        </button>
                                                    </div>

                                                    {/* Content Section */}
                                                    <div className="flex-1 p-4 flex flex-col justify-between">
                                                        <div className="space-y-2">
                                                            <div className="flex items-start justify-between">
                                                                <div className="flex-1">
                                                                    <Link href={`/hotel/${item.accommodationId}`}>
                                                                        <h3 className="font-semibold text-lg text-gray-900 hover:text-blue-600 transition-colors duration-200 overflow-hidden text-ellipsis whitespace-nowrap cursor-pointer">
                                                                            {item.accommodationName}
                                                                        </h3>
                                                                    </Link>

                                                                    {item.accommodation && (
                                                                        <div className="flex items-center gap-1.5 text-gray-600 mt-1">
                                                                            <MapPin className="h-4 w-4 text-gray-400 flex-shrink-0" />
                                                                            <span className="text-sm overflow-hidden text-ellipsis whitespace-nowrap">
                                                                                {item.accommodation.location.address}
                                                                            </span>
                                                                        </div>
                                                                    )}
                                                                </div>

                                                                {/* Price Section */}
                                                                {item.accommodation?.priceInfo && (
                                                                    <div className="text-right ml-4">
                                                                        <div className="text-lg font-semibold text-gray-900">
                                                                            {formatPrice(item.accommodation.priceInfo.currentPrice)}
                                                                        </div>
                                                                        <div className="text-xs text-gray-500">
                                                                            /đêm
                                                                        </div>
                                                                    </div>
                                                                )}
                                                            </div>

                                                            {item.accommodation && (
                                                                <>
                                                                    {item.accommodation.ratingSummary.numberOfRatings > 0 && (
                                                                        <div className="flex items-center gap-2">
                                                                            <div className="flex items-center gap-1">
                                                                                <Star className="h-4 w-4 text-yellow-400 fill-yellow-400" />
                                                                                <span className="text-sm font-medium text-gray-900">
                                                                                    {item.accommodation.ratingSummary.rating.toFixed(1)}
                                                                                </span>
                                                                            </div>
                                                                            <span className="text-sm text-gray-500">
                                                                                ({item.accommodation.ratingSummary.numberOfRatings} đánh giá)
                                                                            </span>
                                                                        </div>
                                                                    )}

                                                                    <p className="text-sm text-gray-600 leading-relaxed overflow-hidden" style={{
                                                                        display: '-webkit-box',
                                                                        WebkitLineClamp: 2,
                                                                        WebkitBoxOrient: 'vertical'
                                                                    }}>
                                                                        {item.accommodation.description}
                                                                    </p>
                                                                </>
                                                            )}
                                                        </div>

                                                        {/* Bottom Section */}
                                                        <div className="flex items-center justify-between pt-3 mt-3 border-t border-gray-100">
                                                            <div className="text-xs text-gray-500">
                                                                Đã lưu: {new Date(item.createdAt).toLocaleDateString('vi-VN')}
                                                            </div>

                                                            <div className="flex gap-2">
                                                                {!bulkMode && (
                                                                    <Button
                                                                        variant="outline"
                                                                        size="sm"
                                                                        onClick={async () => {
                                                                            try {
                                                                                await wishlistService.removeWishlistItem(selectedWishlist!.id, item.id);
                                                                                await loadWishlistDetails(selectedWishlist!.id);
                                                                                toast.success('Đã xóa khỏi danh sách');
                                                                            } catch (error) {
                                                                                toast.error('Không thể xóa khỏi danh sách');
                                                                            }
                                                                        }}
                                                                        className="h-8 w-8 p-0"
                                                                    >
                                                                        <Trash2 className="h-4 w-4" />
                                                                    </Button>
                                                                )}
                                                                <Link href={`/hotel/${item.accommodationId}`}>
                                                                    <Button size="sm" className="h-8 px-4">
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
                                )}

                                {/* Pagination */}
                                {sortedItems.length > itemsPerPage && (
                                    <div className="mt-6">
                                        <Pagination
                                            total={sortedItems.length}
                                            perPage={itemsPerPage}
                                            currentPage={currentPage}
                                            onPageChange={setCurrentPage}
                                        />
                                    </div>
                                )}
                            </>
                        ) : (
                            <div className="text-center py-16">
                                <div className="h-20 w-20 bg-gray-100 rounded-full flex items-center justify-center mx-auto mb-4">
                                    <Heart className="h-8 w-8 text-gray-400 stroke-[1.5]" />
                                </div>
                                <h3 className="text-lg font-medium mb-2">Không tìm thấy khách sạn</h3>
                                <p className="text-gray-500 mb-6">
                                    {searchTerm
                                        ? 'Không có kết quả phù hợp với tìm kiếm của bạn'
                                        : 'Danh sách này chưa có khách sạn nào'
                                    }
                                </p>
                                <Link href="/search">
                                    <Button>
                                        Khám phá khách sạn
                                    </Button>
                                </Link>
                            </div>
                        )}
                    </div>
                </div>
            </div>

            {/* Create Wishlist Modal */}
            {showCreateForm && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
                    <div className="bg-white rounded-lg p-6 w-full max-w-md mx-4">
                        <h3 className="text-lg font-semibold mb-4">Tạo danh sách yêu thích mới</h3>
                        <Input
                            placeholder="Tên danh sách..."
                            value={newWishlistName}
                            onChange={(e) => setNewWishlistName(e.target.value)}
                            onKeyPress={(e) => e.key === 'Enter' && handleCreateWishlist()}
                            className="mb-4"
                        />
                        <div className="flex gap-2">
                            <Button
                                variant="outline"
                                onClick={() => {
                                    setShowCreateForm(false);
                                    setNewWishlistName('');
                                }}
                                className="flex-1"
                            >
                                Hủy
                            </Button>
                            <Button
                                onClick={handleCreateWishlist}
                                disabled={isCreatingWishlist || !newWishlistName.trim()}
                                className="flex-1"
                            >
                                {isCreatingWishlist ? 'Đang tạo...' : 'Tạo'}
                            </Button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default MyWishlistsPage;