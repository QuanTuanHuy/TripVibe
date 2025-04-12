"use client";

import { Building, Plane, Car, Hotel, MapPin, Bus } from 'lucide-react';

const Header: React.FC = () => {
  return (
    <header className="bg-blue-800 text-white">
      <div className="container mx-auto px-4 py-3">
        <div className="flex flex-col md:flex-row items-center justify-between">
          <div className="text-2xl font-bold">Booking</div>
          <div className="flex items-center space-x-4 mt-2 md:mt-0 flex-wrap justify-center">
            <div className="flex items-center">
              <span>VND</span>
              <div className="mx-2 h-6 w-6 rounded-full overflow-hidden">
                <div className="bg-red-500 h-full relative">
                  <div className="absolute inset-0 flex items-center justify-center text-yellow-300 text-xs">★</div>
                </div>
              </div>
            </div>
            <button className="p-2 rounded-full bg-blue-700">
              <span className="sr-only">Help</span>
              <span className="text-xl">?</span>
            </button>
            <div>Đăng chờ nghỉ của Quý vị</div>
            <button className="px-4 py-2 bg-white text-blue-800 rounded-md font-medium">Đăng ký</button>
            <button className="px-4 py-2 border border-white rounded-md font-medium">Đăng nhập</button>
          </div>
        </div>

        {/* Navigation */}
        <nav className="flex mt-4 space-x-2 md:space-x-4 overflow-x-auto pb-2 scrollbar-hide">
          <button className="flex items-center px-3 py-2 bg-blue-900 rounded-full whitespace-nowrap">
            <Building className="w-4 h-4 mr-1 md:w-5 md:h-5 md:mr-2" />
            <span>Lưu trú</span>
          </button>
          <button className="flex items-center px-3 py-2 rounded-full whitespace-nowrap">
            <Plane className="w-4 h-4 mr-1 md:w-5 md:h-5 md:mr-2" />
            <span>Chuyến bay</span>
          </button>
          <button className="flex items-center px-3 py-2 rounded-full whitespace-nowrap">
            <Hotel className="w-4 h-4 mr-1 md:w-5 md:h-5 md:mr-2" />
            <span>Chuyến bay + Khách sạn</span>
          </button>
          <button className="flex items-center px-3 py-2 rounded-full whitespace-nowrap">
            <Car className="w-4 h-4 mr-1 md:w-5 md:h-5 md:mr-2" />
            <span>Thuê xe</span>
          </button>
          <button className="flex items-center px-3 py-2 rounded-full whitespace-nowrap">
            <MapPin className="w-4 h-4 mr-1 md:w-5 md:h-5 md:mr-2" />
            <span>Hoạt động</span>
          </button>
          <button className="flex items-center px-3 py-2 rounded-full whitespace-nowrap">
            <Bus className="w-4 h-4 mr-1 md:w-5 md:h-5 md:mr-2" />
            <span>Taxi sân bay</span>
          </button>
        </nav>
      </div>
    </header>
  );
};

export default Header;