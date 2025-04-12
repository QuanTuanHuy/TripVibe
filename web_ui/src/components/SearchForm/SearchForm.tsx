"use client";

import { useState } from 'react';
import { Search } from 'lucide-react';
import DatePickerInput from '../DatePicker';
import GuestSelector from '../GuestSelector';

const SearchForm: React.FC = () => {
  const [destination, setDestination] = useState('');
  const [checkInDate, setCheckInDate] = useState<Date | null>(null);
  const [checkOutDate, setCheckOutDate] = useState<Date | null>(null);
  const [adultsCount, setAdultsCount] = useState(2);
  const [childrenCount, setChildrenCount] = useState(0);
  const [roomCount, setRoomCount] = useState(1);
  const [searchingFlights, setSearchingFlights] = useState(false);

  const handleSearch = () => {
    // Xử lý tìm kiếm
    console.log({
      destination,
      checkInDate,
      checkOutDate,
      adultsCount,
      childrenCount,
      roomCount,
      searchingFlights
    });
  };

  return (
    <section className="bg-blue-800 text-white pb-6">
      <div className="container mx-auto px-4">
        <div className="py-6 md:py-8">
          <h1 className="text-3xl md:text-4xl font-bold mb-2">Tìm chỗ nghỉ tiếp theo</h1>
          <p className="text-lg md:text-xl">Tìm ưu đãi khách sạn, chỗ nghỉ dạng nhà và nhiều hơn nữa...</p>
        </div>

        {/* Search box */}
        <div className="relative z-10">
          <div className="flex flex-col md:flex-row bg-yellow-400 rounded-md">
            <div className="flex-1 border-b md:border-b-0 md:border-r border-yellow-500">
              <div className="flex items-center h-full px-4 py-3">
                <Search className="w-6 h-6 text-gray-500 mr-2" />
                <input
                  type="text"
                  className="w-full bg-white border-none focus:outline-none text-gray-800 placeholder-gray-500 rounded-md px-2 py-1"
                  placeholder="Bạn muốn đến đâu?"
                  value={destination}
                  onChange={(e) => setDestination(e.target.value)}
                />
              </div>
            </div>
            
            <DatePickerInput 
              checkInDate={checkInDate}
              setCheckInDate={setCheckInDate}
              checkOutDate={checkOutDate}
              setCheckOutDate={setCheckOutDate}
            />
            
            <GuestSelector
              adultsCount={adultsCount}
              setAdultsCount={setAdultsCount}
              childrenCount={childrenCount}
              setChildrenCount={setChildrenCount}
              roomCount={roomCount}
              setRoomCount={setRoomCount}
            />
            
            <div className="p-2">
              <button 
                onClick={handleSearch}
                className="w-full h-full bg-blue-500 hover:bg-blue-600 text-white font-bold py-2 px-6 md:px-8 rounded-md transition duration-200"
              >
                Tìm
              </button>
            </div>
          </div>
          <div className="mt-3 flex items-center text-sm">
            <input
              type="checkbox"
              id="flight-search"
              className="mr-2"
              checked={searchingFlights}
              onChange={() => setSearchingFlights(!searchingFlights)}
            />
            <label htmlFor="flight-search">Tôi đang tìm vé máy bay</label>
          </div>
        </div>
      </div>
    </section>
  );
};

export default SearchForm;