"use client";

import { useState } from 'react';
import Header from "@/components/Header";
import SearchBar from "@/components/SearchBar";
import SearchFilterSidebar from "@/components/SearchFilterSidebar";
import SearchResultsList from "@/components/SearchResultsList";
import { Map, X } from 'lucide-react';
import Link from "next/link";

export default function SearchPage() {
    const [showMobileFilters, setShowMobileFilters] = useState(false);
    const [showMap, setShowMap] = useState(false);
    
    return (
        <div className="min-h-screen bg-gray-50">
            {/* Header */}
            <Header />
            
            {/* Search bar */}
            <SearchBar />
            
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
                    <span className="mx-2">/</span>
                    <Link href="/search?city=HaNoi" className="hover:text-blue-500">Hà Nội</Link>
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
                        
                        {/* Map (desktop) - đặt phía trên bộ lọc */}
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
                            />
                        </div>
                    </div>
                    
                    {/* Search results (right side) */}
                    <div className={`flex-1 ${showMap ? 'hidden lg:block' : ''}`}>
                        <SearchResultsList 
                            onFilterClick={() => setShowMobileFilters(true)}
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