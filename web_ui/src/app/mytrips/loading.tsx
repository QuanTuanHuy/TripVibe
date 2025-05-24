"use client";

import React from 'react';
import Header from '@/components/Header';

export default function TripsLoading() {
    return (
        <div className="min-h-screen bg-gray-50">
            <Header />

            <div className="container max-w-5xl mx-auto px-4 py-8">
                <div className="mb-8">
                    <div className="h-9 bg-gray-200 rounded w-64 animate-pulse mb-2"></div>
                    <div className="h-5 bg-gray-200 rounded w-96 animate-pulse"></div>
                </div>

                <div className="mb-6 bg-white rounded-lg border border-gray-200 p-1">
                    <div className="flex space-x-2">
                        <div className="h-10 bg-gray-200 rounded w-32 animate-pulse"></div>
                        <div className="h-10 bg-gray-200 rounded w-32 animate-pulse"></div>
                    </div>
                </div>

                {[1, 2].map((i) => (
                    <div key={i} className="mb-6 bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden">
                        <div className="flex flex-col md:flex-row">
                            <div className="md:w-1/3 h-48 bg-gray-200 animate-pulse"></div>

                            <div className="p-4 md:p-6 flex-1">
                                <div className="flex justify-between items-start">
                                    <div>
                                        <div className="h-7 bg-gray-200 rounded w-64 animate-pulse mb-1"></div>
                                        <div className="h-5 bg-gray-200 rounded w-48 animate-pulse mb-3"></div>
                                    </div>
                                    <div className="h-6 bg-gray-200 rounded-full w-24 animate-pulse"></div>
                                </div>

                                <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-4">
                                    <div>
                                        <div className="h-4 bg-gray-200 rounded w-24 animate-pulse mb-2"></div>
                                        <div className="h-6 bg-gray-200 rounded w-40 animate-pulse"></div>
                                    </div>

                                    <div>
                                        <div className="h-4 bg-gray-200 rounded w-24 animate-pulse mb-2"></div>
                                        <div className="h-6 bg-gray-200 rounded w-40 animate-pulse"></div>
                                    </div>
                                </div>

                                <div className="border-t border-gray-100 pt-3">
                                    <div className="flex flex-wrap justify-between items-center">
                                        <div>
                                            <div className="h-4 bg-gray-200 rounded w-40 animate-pulse mb-2"></div>
                                            <div className="h-4 bg-gray-200 rounded w-56 animate-pulse"></div>
                                        </div>

                                        <div className="text-right mt-2 md:mt-0">
                                            <div className="h-6 bg-gray-200 rounded w-28 animate-pulse mb-2"></div>
                                            <div className="h-5 bg-gray-200 rounded w-20 animate-pulse ml-auto"></div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
}
