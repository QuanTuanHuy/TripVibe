"use client";

import { useState, useEffect, useRef } from 'react';
import { useRouter } from 'next/navigation';
import { Search, Calendar, Users, MapPin, X } from 'lucide-react';
import DatePickerInput from '../DatePicker';
import { locationService } from '@/services';
import { Province } from '@/types/location';

interface SearchBarProps {
  initialDestination?: string;
  initialCheckIn?: Date | null;
  initialCheckOut?: Date | null;
  initialAdults?: number;
  initialChildren?: number;
  initialRooms?: number;
}

interface LocationSuggestion {
  id: number;
  name: string;
  country: string;
  type: string;
  provinceId?: number;
}

const SearchBar: React.FC<SearchBarProps> = ({
  initialDestination = '',
  initialCheckIn = null,
  initialCheckOut = null,
  initialAdults = 2,
  initialChildren = 0,
  initialRooms = 1
}) => {
  const router = useRouter();
  const [destination, setDestination] = useState(initialDestination);
  const [selectedLocation, setSelectedLocation] = useState<LocationSuggestion | null>(null);
  const [checkInDate, setCheckInDate] = useState<Date | null>(initialCheckIn);
  const [checkOutDate, setCheckOutDate] = useState<Date | null>(initialCheckOut);
  const [showDatePicker, setShowDatePicker] = useState(false);
  const [showLocationPicker, setShowLocationPicker] = useState(false);
  const [showGuestPicker, setShowGuestPicker] = useState(false);
  const [locationSuggestions, setLocationSuggestions] = useState<LocationSuggestion[]>([]);
  const [filteredLocations, setFilteredLocations] = useState<LocationSuggestion[]>([]);
  const [loading, setLoading] = useState(false);

  const [adultsCount, setAdultsCount] = useState(initialAdults);
  const [childrenCount, setChildrenCount] = useState(initialChildren);
  const [roomCount, setRoomCount] = useState(initialRooms);

  // Refs cho các dropdown để xử lý click bên ngoài
  const locationPickerRef = useRef<HTMLDivElement>(null);
  const datePickerRef = useRef<HTMLDivElement>(null);
  const guestPickerRef = useRef<HTMLDivElement>(null);

  // Load dữ liệu các tỉnh/thành phố của Việt Nam khi component mount
  useEffect(() => {
    const loadVietnamProvinces = async () => {
      try {
        setLoading(true);
        const response = await locationService.getProvincesByCountryId(1, { pageSize: 100 }); // Vietnam countryId = 1

        const suggestions: LocationSuggestion[] = response.data.map((province: Province) => ({
          id: province.id,
          name: province.name,
          country: 'Việt Nam',
          type: 'province',
          provinceId: province.id
        }));

        setLocationSuggestions(suggestions);
        setFilteredLocations(suggestions);
      } catch (error) {
        console.error('Error loading Vietnam provinces:', error);
        // Fallback với data mặc định nếu API lỗi
        const fallbackSuggestions: LocationSuggestion[] = [
          { id: 1, name: 'Hà Nội', country: 'Việt Nam', type: 'province' },
          { id: 2, name: 'Hồ Chí Minh', country: 'Việt Nam', type: 'province' },
          { id: 3, name: 'Đà Nẵng', country: 'Việt Nam', type: 'province' },
          { id: 4, name: 'Quảng Nam', country: 'Việt Nam', type: 'province' },
          { id: 5, name: 'Lâm Đồng', country: 'Việt Nam', type: 'province' },
          { id: 6, name: 'Khánh Hòa', country: 'Việt Nam', type: 'province' },
          { id: 7, name: 'Kiên Giang', country: 'Việt Nam', type: 'province' },
          { id: 8, name: 'Quảng Ninh', country: 'Việt Nam', type: 'province' }
        ];
        setLocationSuggestions(fallbackSuggestions);
        setFilteredLocations(fallbackSuggestions);
      } finally {
        setLoading(false);
      }
    };

    loadVietnamProvinces();
  }, []);

  // Xử lý click bên ngoài để đóng dropdown
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (locationPickerRef.current && !locationPickerRef.current.contains(event.target as Node)) {
        setShowLocationPicker(false);
      }
      if (datePickerRef.current && !datePickerRef.current.contains(event.target as Node)) {
        setShowDatePicker(false);
      }
      if (guestPickerRef.current && !guestPickerRef.current.contains(event.target as Node)) {
        setShowGuestPicker(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, []);

  // Lọc địa điểm khi người dùng nhập
  useEffect(() => {
    if (destination.trim() === '') {
      setFilteredLocations(locationSuggestions);
    } else {
      const filtered = locationSuggestions.filter(location =>
        location.name.toLowerCase().includes(destination.toLowerCase()) ||
        location.country.toLowerCase().includes(destination.toLowerCase())
      );
      setFilteredLocations(filtered);
    }
  }, [destination]);

  const formatDateShort = (date: Date | null) => {
    if (!date) return '';
    return date.toLocaleDateString('vi-VN', { day: 'numeric', month: 'numeric', year: 'numeric' });
  };

  const formatGuestsInfo = () => {
    return `${adultsCount} người lớn · ${childrenCount} trẻ em · ${roomCount} phòng`;
  };

  const handleLocationSelect = (location: LocationSuggestion) => {
    setDestination(location.name);
    setSelectedLocation(location);
    setShowLocationPicker(false);
  };

  const handleSearch = () => {
    if (!destination) {
      alert('Vui lòng nhập điểm đến');
      return;
    }
    if (!checkInDate || !checkOutDate) {
      alert('Vui lòng chọn ngày đến và ngày đi');
      return;
    }

    const searchParams = new URLSearchParams();
    searchParams.set('destination', destination);
    if (selectedLocation?.provinceId) {
      searchParams.set('locationId', selectedLocation.provinceId.toString());
    }
    searchParams.set('checkin', checkInDate.toISOString().split('T')[0]);
    searchParams.set('checkout', checkOutDate.toISOString().split('T')[0]);
    searchParams.set('adults', adultsCount.toString());
    searchParams.set('children', childrenCount.toString());
    searchParams.set('rooms', roomCount.toString());

    router.push(`/search?${searchParams.toString()}`);
  };

  return (
    <div className="bg-[#003b95] p-6">
      <div className="container mx-auto">
        <div className="mb-6">
          <h1 className="text-3xl font-bold text-white mb-1">Tìm chỗ nghỉ tiếp theo</h1>
          <p className="text-white">Tìm ưu đãi khách sạn, chỗ nghỉ và nhiều hơn nữa...</p>
        </div>

        <div className="bg-[#ffb700] rounded-md p-1 flex flex-col lg:flex-row">
          {/* Ô tìm kiếm điểm đến */}
          <div className="flex-1 relative" ref={locationPickerRef}>
            <div
              className={`flex items-center h-full px-3 py-3 bg-white rounded-md border-2 ${showLocationPicker ? 'border-[#ffb700]' : 'border-transparent'}`}
              onClick={() => {
                setShowLocationPicker(true);
                setShowDatePicker(false);
                setShowGuestPicker(false);
              }}
            >
              <MapPin className="text-gray-500 mr-2 min-w-5" size={20} />
              <input
                type="text"
                className="w-full outline-none text-gray-700 bg-transparent"
                placeholder="Bạn muốn đến đâu?"
                value={destination}
                onChange={(e) => {
                  setDestination(e.target.value);
                  setShowLocationPicker(true);
                }}
              />
              {destination && (
                <button
                  onClick={(e) => {
                    e.stopPropagation();
                    setDestination('');
                  }}
                  className="text-gray-400 hover:text-gray-600"
                >
                  <X size={18} />
                </button>
              )}
            </div>

            {/* Gợi ý địa điểm */}
            {showLocationPicker && (
              <div className="absolute left-0 right-0 z-50 bg-white shadow-lg rounded-md mt-1 max-h-80 overflow-y-auto">
                <div className="p-4">
                  <h3 className="font-semibold text-gray-700 mb-2">Các điểm đến phổ biến</h3>
                  <div className="space-y-3">
                    {loading ? (
                      <div className="text-gray-500 py-2">Đang tải dữ liệu...</div>
                    ) : filteredLocations.length > 0 ? (
                      filteredLocations.map((location) => (
                        <div
                          key={location.id}
                          className="flex items-start p-2 hover:bg-gray-50 cursor-pointer rounded-md"
                          onClick={() => handleLocationSelect(location)}
                        >
                          <MapPin className="text-gray-500 mr-3 mt-0.5 min-w-5" size={20} />
                          <div>
                            <div className="font-medium text-gray-800">{location.name}</div>
                            <div className="text-sm text-gray-500">
                              {location.type === 'province' ? 'Tỉnh/Thành phố' : 'Khu vực'} ở {location.country}
                            </div>
                          </div>
                        </div>
                      ))
                    ) : (
                      <div className="text-gray-500 py-2">Không tìm thấy địa điểm phù hợp</div>
                    )}
                  </div>
                </div>
              </div>
            )}
          </div>

          {/* Ô chọn ngày */}
          <div className="flex-1 relative mt-1 lg:mt-0 lg:ml-1" ref={datePickerRef}>
            <div
              className={`flex items-center h-full px-3 py-3 bg-white rounded-md border-2 ${showDatePicker ? 'border-[#ffb700]' : 'border-transparent'}`}
              onClick={() => {
                setShowDatePicker(true);
                setShowLocationPicker(false);
                setShowGuestPicker(false);
              }}
            >
              <Calendar className="text-gray-500 mr-2 min-w-5" size={20} />
              <div className="text-gray-700">
                {checkInDate && checkOutDate ? (
                  <div className="flex flex-col">
                    <span>{formatDateShort(checkInDate)} — {formatDateShort(checkOutDate)}</span>
                  </div>
                ) : (
                  <span className="text-gray-400">Nhập ngày nhận phòng — trả phòng</span>
                )}
              </div>
            </div>

            {showDatePicker && (
              <div className="absolute left-0 right-0 z-50 bg-white shadow-lg rounded-md mt-1 p-4 w-[700px]">
                <DatePickerInput
                  checkInDate={checkInDate}
                  setCheckInDate={setCheckInDate}
                  checkOutDate={checkOutDate}
                  setCheckOutDate={setCheckOutDate}
                />
                <div className="flex justify-end mt-4">
                  <button
                    onClick={() => setShowDatePicker(false)}
                    className="bg-blue-600 hover:bg-blue-700 text-white py-2 px-4 rounded font-medium"
                  >
                    Xong
                  </button>
                </div>
              </div>
            )}
          </div>

          {/* Ô chọn số lượng khách */}
          <div className="flex-1 relative mt-1 lg:mt-0 lg:ml-1" ref={guestPickerRef}>
            <div
              className={`flex items-center h-full px-3 py-3 bg-white rounded-md border-2 ${showGuestPicker ? 'border-[#ffb700]' : 'border-transparent'}`}
              onClick={() => {
                setShowGuestPicker(true);
                setShowDatePicker(false);
                setShowLocationPicker(false);
              }}
            >
              <Users className="text-gray-500 mr-2 min-w-5" size={20} />
              <div className="text-gray-700">
                {formatGuestsInfo()}
              </div>
            </div>

            {showGuestPicker && (
              <div className="absolute left-0 right-0 z-50 bg-white shadow-lg rounded-md mt-1">
                <div className="p-4">
                  <div className="flex items-center justify-between py-3 border-b">
                    <div>
                      <div className="text-gray-800 font-medium">Người lớn</div>
                      <div className="text-gray-500 text-sm">Từ 18 tuổi trở lên</div>
                    </div>
                    <div className="flex items-center space-x-3">
                      <button
                        className="w-8 h-8 rounded-full border border-gray-300 flex items-center justify-center text-gray-600 hover:border-gray-500"
                        onClick={() => setAdultsCount(Math.max(1, adultsCount - 1))}
                        disabled={adultsCount <= 1}
                      >
                        <span className="text-xl font-medium">−</span>
                      </button>
                      <span className="w-8 text-center text-gray-700 font-medium">{adultsCount}</span>
                      <button
                        className="w-8 h-8 rounded-full border border-gray-300 flex items-center justify-center text-gray-600 hover:border-gray-500"
                        onClick={() => setAdultsCount(adultsCount + 1)}
                      >
                        <span className="text-xl font-medium">+</span>
                      </button>
                    </div>
                  </div>
                  <div className="flex items-center justify-between py-3 border-b">
                    <div>
                      <div className="text-gray-800 font-medium">Trẻ em</div>
                      <div className="text-gray-500 text-sm">0 - 17 tuổi</div>
                    </div>
                    <div className="flex items-center space-x-3">
                      <button
                        className="w-8 h-8 rounded-full border border-gray-300 flex items-center justify-center text-gray-600 hover:border-gray-500"
                        onClick={() => setChildrenCount(Math.max(0, childrenCount - 1))}
                        disabled={childrenCount <= 0}
                      >
                        <span className="text-xl font-medium">−</span>
                      </button>
                      <span className="w-8 text-center text-gray-700 font-medium">{childrenCount}</span>
                      <button
                        className="w-8 h-8 rounded-full border border-gray-300 flex items-center justify-center text-gray-600 hover:border-gray-500"
                        onClick={() => setChildrenCount(childrenCount + 1)}
                      >
                        <span className="text-xl font-medium">+</span>
                      </button>
                    </div>
                  </div>
                  <div className="flex items-center justify-between py-3">
                    <div>
                      <div className="text-gray-800 font-medium">Phòng</div>
                    </div>
                    <div className="flex items-center space-x-3">
                      <button
                        className="w-8 h-8 rounded-full border border-gray-300 flex items-center justify-center text-gray-600 hover:border-gray-500"
                        onClick={() => setRoomCount(Math.max(1, roomCount - 1))}
                        disabled={roomCount <= 1}
                      >
                        <span className="text-xl font-medium">−</span>
                      </button>
                      <span className="w-8 text-center text-gray-700 font-medium">{roomCount}</span>
                      <button
                        className="w-8 h-8 rounded-full border border-gray-300 flex items-center justify-center text-gray-600 hover:border-gray-500"
                        onClick={() => setRoomCount(roomCount + 1)}
                      >
                        <span className="text-xl font-medium">+</span>
                      </button>
                    </div>
                  </div>
                  <div className="flex justify-end mt-4">
                    <button
                      onClick={() => setShowGuestPicker(false)}
                      className="bg-blue-600 hover:bg-blue-700 text-white py-2 px-4 rounded font-medium"
                    >
                      Xong
                    </button>
                  </div>
                </div>
              </div>
            )}
          </div>

          {/* Nút tìm kiếm */}
          <div className="mt-1 lg:mt-0 lg:ml-1">
            <button
              onClick={handleSearch}
              className="w-full bg-[#0071c2] hover:bg-[#00487a] text-white font-bold py-3 px-8 rounded-md transition duration-200"
            >
              <div className="flex items-center justify-center">
                <Search size={20} className="mr-2" />
                <span>Tìm</span>
              </div>
            </button>
          </div>
        </div>

        {/* Checkbox "Đi công tác" */}
        <div className="mt-4 flex items-center">
          <input
            type="checkbox"
            id="business-trip"
            className="h-4 w-4 text-blue-600 rounded border-gray-300"
          />
          <label htmlFor="business-trip" className="ml-2 text-white">
            Tôi đi công tác
          </label>
        </div>
      </div>
    </div>
  );
};

export default SearchBar;