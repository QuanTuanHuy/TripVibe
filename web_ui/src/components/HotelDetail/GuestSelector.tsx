"use client";

import { useRef, useEffect } from 'react';
import { Users, ChevronDown, Plus, Minus } from 'lucide-react';

interface GuestSelectorProps {
    adults: number;
    childrenCount: number;
    rooms: number;
    onAdultsChange: (count: number) => void;
    onChildrenChange: (count: number) => void;
    onRoomsChange: (count: number) => void;
    showGuestSelector: boolean;
    onShowGuestSelectorChange: (show: boolean) => void;
}

export default function GuestSelector({
    adults,
    childrenCount,
    rooms,
    onAdultsChange,
    onChildrenChange,
    onRoomsChange,
    showGuestSelector,
    onShowGuestSelectorChange
}: GuestSelectorProps) {
    const guestSelectorRef = useRef<HTMLDivElement>(null);

    // Handle outside click
    useEffect(() => {
        const handleClickOutside = (event: MouseEvent) => {
            if (guestSelectorRef.current && !guestSelectorRef.current.contains(event.target as Node)) {
                onShowGuestSelectorChange(false);
            }
        };

        document.addEventListener('mousedown', handleClickOutside);
        return () => {
            document.removeEventListener('mousedown', handleClickOutside);
        };
    }, [onShowGuestSelectorChange]);

    // Format display text
    const formatGuestCount = () => {
        const roomsText = rooms === 1 ? '1 phòng' : `${rooms} phòng`;
        const adultsText = adults === 1 ? '1 người lớn' : `${adults} người lớn`;
        const childrenText = childrenCount === 0 ? '0 trẻ em' : childrenCount === 1 ? '1 trẻ em' : `${childrenCount} trẻ em`;
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
                onClick={() => onShowGuestSelectorChange(!showGuestSelector)}
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
                                        onClick={() => decrement(adults, onAdultsChange, 1)}
                                        disabled={adults <= 1}
                                        className={`w-8 h-8 rounded-full flex items-center justify-center border ${adults <= 1 ? 'border-gray-200 text-gray-300' : 'border-blue-500 text-blue-500 hover:bg-blue-50'}`}
                                    >
                                        <Minus size={16} />
                                    </button>
                                    <span className="w-6 text-center">{adults}</span>
                                    <button
                                        onClick={() => increment(adults, onAdultsChange, 30)}
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
                                </div>                                <div className="flex items-center gap-3">
                                    <button
                                        onClick={() => decrement(childrenCount, onChildrenChange)}
                                        disabled={childrenCount <= 0}
                                        className={`w-8 h-8 rounded-full flex items-center justify-center border ${childrenCount <= 0 ? 'border-gray-200 text-gray-300' : 'border-blue-500 text-blue-500 hover:bg-blue-50'}`}
                                    >
                                        <Minus size={16} />
                                    </button>
                                    <span className="w-6 text-center">{childrenCount}</span>
                                    <button
                                        onClick={() => increment(childrenCount, onChildrenChange, 10)}
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
                                        onClick={() => decrement(rooms, onRoomsChange, 1)}
                                        disabled={rooms <= 1}
                                        className={`w-8 h-8 rounded-full flex items-center justify-center border ${rooms <= 1 ? 'border-gray-200 text-gray-300' : 'border-blue-500 text-blue-500 hover:bg-blue-50'}`}
                                    >
                                        <Minus size={16} />
                                    </button>
                                    <span className="w-6 text-center">{rooms}</span>
                                    <button
                                        onClick={() => increment(rooms, onRoomsChange, 30)}
                                        className="w-8 h-8 rounded-full flex items-center justify-center border border-blue-500 text-blue-500 hover:bg-blue-50"
                                    >
                                        <Plus size={16} />
                                    </button>
                                </div>
                            </div>

                            <div className="pt-4 flex justify-end">
                                <button
                                    onClick={() => onShowGuestSelectorChange(false)}
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
}
