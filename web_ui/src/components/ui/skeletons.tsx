"use client";

import React from 'react';
import { Card } from '@/components/ui/card';

export const HotelSkeleton: React.FC = () => {
    return (
        <Card className="overflow-hidden animate-pulse">
            <div className="flex flex-col md:flex-row">
                <div className="relative md:w-1/3 h-[200px] md:h-auto bg-gray-200"></div>
                <div className="p-6 flex-1 flex flex-col justify-between">
                    <div>
                        <div className="flex justify-between items-start mb-2">
                            <div className="h-6 bg-gray-200 w-3/4 rounded"></div>
                            <div className="flex items-center">
                                <div className="h-8 w-12 bg-gray-200 rounded"></div>
                                <div className="ml-2 h-4 w-24 bg-gray-200 rounded"></div>
                            </div>
                        </div>

                        <div className="h-4 bg-gray-200 w-2/3 rounded mb-4"></div>

                        <div className="flex flex-wrap gap-2 mb-4">
                            {[1, 2, 3].map(i => (
                                <div key={i} className="h-6 w-24 bg-gray-200 rounded"></div>
                            ))}
                        </div>

                        <div className="h-4 bg-gray-200 w-1/2 rounded mb-4"></div>
                    </div>

                    <div className="flex justify-between items-end mt-4">
                        <div>
                            <div className="h-7 w-32 bg-gray-200 rounded mb-1"></div>
                            <div className="h-4 w-20 bg-gray-200 rounded"></div>
                        </div>

                        <div className="flex space-x-2">
                            <div className="h-9 w-16 bg-gray-200 rounded"></div>
                            <div className="h-9 w-28 bg-gray-200 rounded"></div>
                        </div>
                    </div>
                </div>
            </div>
        </Card>
    );
};

export const LocationSkeleton: React.FC = () => {
    return (
        <Card className="overflow-hidden animate-pulse">
            <div className="relative h-[200px] bg-gray-200"></div>

            <div className="p-6">
                <div className="h-6 bg-gray-200 w-1/2 rounded mb-2"></div>
                <div className="h-12 bg-gray-200 rounded mb-4"></div>

                <div className="flex justify-between text-sm mb-4">
                    <div className="h-4 bg-gray-200 w-24 rounded"></div>
                    <div className="h-4 bg-gray-200 w-32 rounded"></div>
                </div>
            </div>

            <div className="px-6 pb-6 pt-0 flex justify-between">
                <div className="h-9 w-16 bg-gray-200 rounded"></div>
                <div className="h-9 w-28 bg-gray-200 rounded"></div>
            </div>
        </Card>
    );
};
