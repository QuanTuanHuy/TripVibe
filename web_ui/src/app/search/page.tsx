"use client";

import { useState, useEffect, useRef } from 'react';
import { useSearchParams } from 'next/navigation';
import Header from "@/components/Header";
import SearchBar from "@/components/SearchBar";
import SearchFilterSidebar from "@/components/SearchFilterSidebar";
import SearchResultsList from "@/components/SearchResultsList";
import { Map, X } from 'lucide-react';
import Link from "next/link";
import { AccommodationThumbnail } from '@/types/accommodation';
import { SearchResult } from '@/types/search';
import { searchService } from '@/services';

export default function SearchPage() {
    const urlParams = useSearchParams();
    const [showMobileFilters, setShowMobileFilters] = useState(false);
    const [showMap, setShowMap] = useState(false);
    const [accommodations, setAccommodations] = useState<AccommodationThumbnail[]>([]);
    const [loading, setLoading] = useState(false);
    const [searchParams, setSearchParams] = useState<any>({
        page: 0,
        pageSize: 10,
        name: undefined,
        provinceId: undefined,
        startDate: undefined,
        endDate: undefined,
        numAdults: 2,
        numChildren: 0,
        sortBy: 'recommended'
    });
    const [totalResults, setTotalResults] = useState(0);
    const [destination, setDestination] = useState('');

    // Use ref to track if initial load has been done
    const hasInitialLoadRef = useRef(false);

    // Single useEffect to handle both URL parsing and subsequent searches
    useEffect(() => {
        const handleSearch = async () => {
            // Check if this is URL change (initial load)
            const isUrlChange = !hasInitialLoadRef.current;

            if (isUrlChange) {
                // Parse URL parameters for initial load
                const destination = urlParams.get('destination') || '';
                const locationId = urlParams.get('locationId');
                const checkin = urlParams.get('checkin');
                const checkout = urlParams.get('checkout');
                const adults = urlParams.get('adults');
                const children = urlParams.get('children');

                console.log('Initial URL params:', { destination, locationId, checkin, checkout, adults, children });

                setDestination(destination);

                const updatedParams: any = {
                    page: 0,
                    pageSize: 10,
                    name: undefined,
                    provinceId: locationId ? parseInt(locationId) : undefined,
                    startDate: checkin ? new Date(checkin) : undefined,
                    endDate: checkout ? new Date(checkout) : undefined,
                    numAdults: adults ? parseInt(adults) : 2,
                    numChildren: children ? parseInt(children) : 0,
                    sortBy: 'recommended'
                };

                setSearchParams(updatedParams);
                hasInitialLoadRef.current = true;

                // Only search if we have meaningful parameters
                if (updatedParams.provinceId || updatedParams.startDate || updatedParams.endDate) {
                    console.log('Making initial API call with params:', updatedParams);
                    try {
                        setLoading(true);
                        const result: SearchResult<AccommodationThumbnail> = await searchService.searchAccommodations(updatedParams);
                        setAccommodations(result.second);
                        setTotalResults(result.first.totalRecord);
                        console.log('Initial search results loaded:', result.second.length, 'items');
                    } catch (error) {
                        console.error('Error loading initial search results:', error);
                        setAccommodations([]);
                        setTotalResults(0);
                    } finally {
                        setLoading(false);
                    }
                } else {
                    console.log('No meaningful search parameters, skipping initial search');
                }
            } else {
                // This is a filter/sort change, use current searchParams
                console.log('Making filter/sort API call with params:', searchParams);
                try {
                    setLoading(true);
                    const result: SearchResult<AccommodationThumbnail> = await searchService.searchAccommodations(searchParams);
                    setAccommodations(result.second);
                    setTotalResults(result.first.totalRecord);
                    console.log('Filter/sort search results loaded:', result.second.length, 'items');
                } catch (error) {
                    console.error('Error loading search results:', error);
                    setAccommodations([]);
                    setTotalResults(0);
                } finally {
                    setLoading(false);
                }
            }
        };

        handleSearch();
    }, [urlParams, searchParams]);

    const handleSearchParamsChange = (newFilters: any) => {
        console.log('Filter change:', newFilters);
        const updatedParams: Partial<any> = {
            page: 0, // Reset to first page when filters change
            name: newFilters.name || undefined,
            minBudget: newFilters.minBudget || undefined,
            maxBudget: newFilters.maxBudget || undefined,
            minRatingStar: newFilters.minRatingStar || undefined,
            accTypeId: newFilters.accTypeId || undefined,
            accAmenityIds: newFilters.accAmenityIds && newFilters.accAmenityIds.length > 0 ? newFilters.accAmenityIds : undefined,
        };

        setSearchParams((prev: any) => ({
            ...prev,
            ...updatedParams
        }));
    };

    const handleSortChange = (sortBy: string) => {
        console.log('Sort change:', sortBy);
        setSearchParams((prev: any) => ({
            ...prev,
            sortBy,
            page: 0
        }));
    };

    const handlePageChange = (page: number) => {
        console.log('Page change:', page);
        setSearchParams((prev: any) => ({
            ...prev,
            page
        }));
    };

    return (
        <div className="min-h-screen bg-gray-50">
            {/* Header */}
            <Header />

            {/* Search bar */}
            <SearchBar
                initialDestination={destination}
                initialCheckIn={searchParams.startDate}
                initialCheckOut={searchParams.endDate}
                initialAdults={searchParams.numAdults}
                initialChildren={searchParams.numChildren}
                initialRooms={1}
            />

            {/* Map toggle button (mobile) */}
            <div className="fixed bottom-5 left-1/2 transform -translate-x-1/2 z-20 md:hidden">
                <button
                    onClick={() => setShowMap(!showMap)}
                    className="flex items-center gap-2 bg-white border border-gray-300 shadow-lg rounded-full px-4 py-2"
                >
                    <Map size={18} />
                    <span>{showMap ? "Hiển thị danh sách" : "Hiển thị bản đồ"}</span>
                </button>
            </div>

            {/* Breadcrumbs */}
            <div className="container mx-auto px-4 py-2 text-sm">
                <div className="flex items-center text-gray-500">
                    <Link href="/" className="hover:text-blue-500">Trang chủ</Link>
                    <span className="mx-2">/</span>
                    <Link href="/search?country=Vietnam" className="hover:text-blue-500">Việt Nam</Link>
                    {destination && (
                        <>
                            <span className="mx-2">/</span>
                            <span className="text-gray-700">{destination}</span>
                        </>
                    )}
                    <span className="mx-2">/</span>
                    <span className="text-gray-700">Kết quả tìm kiếm</span>
                </div>
            </div>

            {/* Main content */}
            <div className="container mx-auto px-4 py-4">
                <div className="flex flex-col lg:flex-row gap-8">
                    {/* Left sidebar with map and filters */}
                    <div className={`${showMobileFilters ? 'fixed inset-0 z-50 bg-white overflow-auto' : ''} lg:flex lg:flex-col lg:w-1/3 lg:sticky lg:top-4 lg:self-start gap-6`}>
                        {showMobileFilters && (
                            <div className="flex items-center justify-between p-4 border-b lg:hidden">
                                <h2 className="text-xl font-bold">Bộ lọc</h2>
                                <button onClick={() => setShowMobileFilters(false)} className="p-2">
                                    <X size={24} />
                                </button>
                            </div>
                        )}

                        {/* Map (desktop) */}
                        <div className="hidden lg:block h-[400px] bg-gray-200 rounded-lg mb-6">
                            <div className="w-full h-full flex items-center justify-center">
                                <p className="text-gray-500">Bản đồ sẽ hiển thị ở đây</p>
                            </div>
                        </div>

                        {/* Filter sidebar */}
                        <div className={`${showMobileFilters ? 'block' : 'hidden'} lg:block`}>
                            <SearchFilterSidebar
                                showMobileFilters={showMobileFilters}
                                closeMobileFilters={() => setShowMobileFilters(false)}
                                onFiltersChange={handleSearchParamsChange}
                            />
                        </div>
                    </div>

                    {/* Search results (right side) */}
                    <div className={`flex-1 ${showMap ? 'hidden lg:block' : ''}`}>
                        <SearchResultsList
                            onFilterClick={() => setShowMobileFilters(true)}
                            accommodations={accommodations}
                            loading={loading}
                            totalResults={totalResults}
                            onSortChange={handleSortChange}
                            onPageChange={handlePageChange}
                            currentPage={searchParams.page || 0}
                        />
                    </div>

                    {/* Map (mobile only - shown when toggled) */}
                    <div className={`${!showMap ? 'hidden' : 'block'} lg:hidden w-full h-screen fixed inset-0 z-40`}>
                        <div className="relative w-full h-full bg-gray-200 flex items-center justify-center">
                            <p className="text-gray-500">Bản đồ sẽ hiển thị ở đây (mobile)</p>

                            {/* Close map button */}
                            <button
                                onClick={() => setShowMap(false)}
                                className="absolute top-4 right-4 bg-white rounded-full p-2 shadow-md"
                            >
                                <X size={24} />
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}