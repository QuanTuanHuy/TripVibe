"use client";

import React, { useState } from 'react';
import { Users } from 'lucide-react';

interface GuestSelectorProps {
  adultsCount: number;
  setAdultsCount: (count: number) => void;
  childrenCount: number;
  setChildrenCount: (count: number) => void;
  roomCount: number;
  setRoomCount: (count: number) => void;
}

const GuestSelector: React.FC<GuestSelectorProps> = ({
  adultsCount,
  setAdultsCount,
  childrenCount,
  setChildrenCount,
  roomCount,
  setRoomCount
}) => {
  const [isOpen, setIsOpen] = useState(false);

  // Format guests information
  const formatGuestsInfo = () => {
    return `${adultsCount} người lớn · ${childrenCount} trẻ em · ${roomCount} phòng`;
  };

  return (
    <div className="flex-1 border-b md:border-b-0 md:border-r border-yellow-500 relative">
      <div className="flex items-center h-full px-4 py-3">
        <Users className="w-6 h-6 text-gray-500 mr-2" />
        <div 
          className="w-full h-full bg-white border-none focus:outline-none text-gray-800 placeholder-gray-500 rounded-md px-2 py-1 cursor-pointer"
          onClick={() => setIsOpen(!isOpen)}
        >
          {formatGuestsInfo()}
        </div>
      </div>
      
      {/* Absolute position popup */}
      {isOpen && (
        <div className="absolute right-0 mt-1 z-50">
          <div className="bg-white p-4 shadow-lg rounded-md w-80">
            <div className="flex items-center justify-between py-3 border-b">
              <span className="text-gray-800">Người lớn</span>
              <div className="flex items-center space-x-4">
                <button 
                  className="w-8 h-8 rounded-full bg-gray-100 flex items-center justify-center text-gray-600 hover:bg-gray-200"
                  onClick={(e) => {
                    e.stopPropagation();
                    setAdultsCount(Math.max(1, adultsCount - 1));
                  }}
                  type="button"
                >
                  -
                </button>
                <span className="w-6 text-center text-gray-600">{adultsCount}</span>
                <button 
                  className="w-8 h-8 rounded-full bg-gray-100 flex items-center justify-center text-gray-600 hover:bg-gray-200"
                  onClick={(e) => {
                    e.stopPropagation();
                    setAdultsCount(adultsCount + 1);
                  }}
                  type="button"
                >
                  +
                </button>
              </div>
            </div>
            <div className="flex items-center justify-between py-3 border-b">
              <span className="text-gray-800">Trẻ em</span>
              <div className="flex items-center space-x-4">
                <button 
                  className="w-8 h-8 rounded-full bg-gray-100 flex items-center justify-center text-gray-600 hover:bg-gray-200"
                  onClick={(e) => {
                    e.stopPropagation();
                    setChildrenCount(Math.max(0, childrenCount - 1));
                  }}
                  type="button"
                >
                  -
                </button>
                <span className="w-6 text-center text-gray-600">{childrenCount}</span>
                <button 
                  className="w-8 h-8 rounded-full bg-gray-100 flex items-center justify-center text-gray-600 hover:bg-gray-200"
                  onClick={(e) => {
                    e.stopPropagation();
                    setChildrenCount(childrenCount + 1);
                  }}
                  type="button"
                >
                  +
                </button>
              </div>
            </div>
            <div className="flex items-center justify-between py-3">
              <span className="text-gray-800">Phòng</span>
              <div className="flex items-center space-x-4">
                <button 
                  className="w-8 h-8 rounded-full bg-gray-100 flex items-center justify-center text-gray-600 hover:bg-gray-200"
                  onClick={(e) => {
                    e.stopPropagation();
                    setRoomCount(Math.max(1, roomCount - 1));
                  }}
                  type="button"
                >
                  -
                </button>
                <span className="w-6 text-center text-gray-600">{roomCount}</span>
                <button 
                  className="w-8 h-8 rounded-full bg-gray-100 flex items-center justify-center text-gray-600 hover:bg-gray-200"
                  onClick={(e) => {
                    e.stopPropagation();
                    setRoomCount(roomCount + 1);
                  }}
                  type="button"
                >
                  +
                </button>
              </div>
            </div>
            <div className="mt-4 flex justify-end">
              <button 
                className="px-4 py-2 bg-blue-500 text-white rounded-md"
                onClick={() => setIsOpen(false)}
                type="button"
              >
                Xong
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default GuestSelector;