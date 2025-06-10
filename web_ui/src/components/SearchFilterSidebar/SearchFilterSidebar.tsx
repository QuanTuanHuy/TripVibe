"use client";

import { useState, useEffect } from 'react';
import { X, Plus, Minus } from 'lucide-react';
import { accommodationService } from '@/services';
import amenityService from '@/services/accommodation/amenityService';
import { AccommodationType, AmenityGroup } from '@/types/accommodation';

interface FilterProps {
  minPrice?: number;
  maxPrice?: number;
  showMobileFilters: boolean;
  closeMobileFilters: () => void;
  onFiltersChange?: (filters: any) => void;
}

const SearchFilterSidebar: React.FC<FilterProps> = ({
  minPrice = 100000,
  maxPrice = 3000000,
  showMobileFilters,
  closeMobileFilters,
  onFiltersChange
}) => {
  const [priceRange, setPriceRange] = useState([minPrice, maxPrice]);
  const [selectedRatings, setSelectedRatings] = useState<number[]>([]);
  const [roomsCount, setRoomsCount] = useState(0);
  const [bathroomsCount, setBathroomsCount] = useState(0);
  const [searchName, setSearchName] = useState('');
  const [selectedAccommodationTypes, setSelectedAccommodationTypes] = useState<string[]>([]);
  const [selectedAmenities, setSelectedAmenities] = useState<string[]>([]);
  const [selectedDistricts, setSelectedDistricts] = useState<string[]>([]);

  const [accommodationTypes, setAccommodationTypes] = useState<AccommodationType[]>([]);
  const [amenityGroups, setAmenityGroups] = useState<AmenityGroup[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const loadFilterData = async () => {
      try {
        setLoading(true);

        const typesResponse = await accommodationService.getAccommodationTypes();
        setAccommodationTypes(typesResponse);

        const amenitiesResponse = await amenityService.getAmenityGroups({ isPopular: true });
        setAmenityGroups(amenitiesResponse.data);

      } catch (error) {
        console.error('Error loading filter data:', error);
      } finally {
        setLoading(false);
      }
    };

    loadFilterData();
  }, []);

  useEffect(() => {
    if (!loading) {
      sendFiltersToParent();
    }
  }, [
    searchName,
    priceRange,
    selectedRatings,
    roomsCount,
    bathroomsCount,
    selectedAccommodationTypes,
    selectedAmenities,
    selectedDistricts,
    loading
  ]);

  const sendFiltersToParent = () => {
    if (onFiltersChange) {
      const filters = {
        name: searchName,
        minBudget: minPrice,
        maxBudget: priceRange[1],
        minRatingStar: selectedRatings.length > 0 ? Math.min(...selectedRatings) : undefined,
        accTypeId: selectedAccommodationTypes.length > 0 ? parseInt(selectedAccommodationTypes[0]) : undefined,
        accAmenityIds: selectedAmenities.map(id => parseInt(id)),
        districts: selectedDistricts,
        roomsCount,
        bathroomsCount
      };
      onFiltersChange(filters);
    }
  };

  const handlePriceChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const newPriceRange = [minPrice, parseInt(event.target.value)];
    setPriceRange(newPriceRange);
  };

  const toggleRating = (rating: number) => {
    let newSelectedRatings;
    if (selectedRatings.includes(rating)) {
      newSelectedRatings = selectedRatings.filter(r => r !== rating);
    } else {
      newSelectedRatings = [...selectedRatings, rating];
    }
    setSelectedRatings(newSelectedRatings);
  };

  const toggleAccommodationType = (typeId: string) => {
    let newSelectedTypes;
    if (selectedAccommodationTypes.includes(typeId)) {
      newSelectedTypes = selectedAccommodationTypes.filter(t => t !== typeId);
    } else {
      newSelectedTypes = [...selectedAccommodationTypes, typeId];
    }
    setSelectedAccommodationTypes(newSelectedTypes);
  };

  const toggleAmenity = (amenityId: string) => {
    let newSelectedAmenities;
    if (selectedAmenities.includes(amenityId)) {
      newSelectedAmenities = selectedAmenities.filter(a => a !== amenityId);
    } else {
      newSelectedAmenities = [...selectedAmenities, amenityId];
    }
    setSelectedAmenities(newSelectedAmenities);
  };

  const toggleDistrict = (districtId: string) => {
    let newSelectedDistricts;
    if (selectedDistricts.includes(districtId)) {
      newSelectedDistricts = selectedDistricts.filter(d => d !== districtId);
    } else {
      newSelectedDistricts = [...selectedDistricts, districtId];
    }
    setSelectedDistricts(newSelectedDistricts);
  };

  const handleSearch = () => {
    sendFiltersToParent();
  };

  const formatCurrency = (value: number) => {
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value)
      .replace('₫', 'VND');
  };

  return (
    <div className={`md:block ${showMobileFilters ? 'fixed inset-0 z-50 bg-white overflow-auto px-4 py-6' : 'hidden'} md:static md:z-auto md:bg-transparent md:overflow-visible md:p-0`}>
      {showMobileFilters && (
        <div className="flex items-center justify-between mb-4 md:hidden">
          <h2 className="text-xl font-bold">Bộ lọc</h2>
          <button onClick={closeMobileFilters} className="p-2">
            <X size={24} />
          </button>
        </div>
      )}

      <div className="bg-white rounded-lg shadow p-4 mb-4">
        <h2 className="font-bold text-lg mb-4 text-gray-800">Tìm kiếm</h2>
        <div className="mb-4">
          <input
            type="text"
            placeholder="Tên chỗ nghỉ"
            value={searchName}
            onChange={(e) => setSearchName(e.target.value)}
            className="w-full text-gray-600 px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-1 focus:ring-blue-500"
          />
        </div>
        <button
          onClick={handleSearch}
          className="w-full bg-blue-600 hover:bg-blue-700 text-white font-medium py-2 rounded-md"
        >
          Tìm kiếm
        </button>
      </div>

      <div className="bg-white rounded-lg shadow p-4 mb-4">
        <h2 className="font-bold text-lg mb-4 text-gray-800">Ngân sách của bạn (mỗi đêm)</h2>
        <div className="mb-2">
          <input
            type="range"
            min={minPrice}
            max={maxPrice}
            value={priceRange[1]}
            onChange={handlePriceChange}
            className="w-full h-2 bg-gray-200 rounded-lg appearance-none cursor-pointer"
          />
        </div>
        <div className="flex justify-between text-sm text-gray-700 font-medium">
          <span>{formatCurrency(minPrice)}</span>
          <span>{formatCurrency(priceRange[1])}</span>
        </div>
      </div>

      <div className="bg-white rounded-lg shadow p-4 mb-4">
        <h2 className="font-bold text-lg mb-4 text-gray-800">Loại chỗ ở</h2>
        {loading ? (
          <div className="text-center py-4">
            <div className="animate-spin rounded-full h-6 w-6 border-t-2 border-b-2 border-blue-500 mx-auto"></div>
          </div>
        ) : (
          <div className="space-y-2">
            {accommodationTypes.map((type) => (
              <div key={type.id} className="flex items-center justify-between">
                <div className="flex items-center">
                  <input
                    type="checkbox"
                    id={`type-${type.id}`}
                    checked={selectedAccommodationTypes.includes(type.id.toString())}
                    onChange={() => toggleAccommodationType(type.id.toString())}
                    className="mr-2 h-4 w-4"
                  />
                  <label htmlFor={`type-${type.id}`} className="text-gray-700">{type.name}</label>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      <div className="bg-white rounded-lg shadow p-4 mb-4">
        <h2 className="font-bold text-lg mb-4 text-gray-800">Điểm đánh giá của khách</h2>
        <div className="space-y-2">
          {[
            { score: 9, label: 'Tuyệt hảo: 9 điểm trở lên', count: 935 },
            { score: 8, label: 'Rất tốt: 8 điểm trở lên', count: 1809 },
            { score: 7, label: 'Tốt: 7 điểm trở lên', count: 2239 },
            { score: 6, label: 'Dễ chịu: 6 điểm trở lên', count: 2381 }
          ].map((rating) => (
            <div key={rating.score} className="flex items-center justify-between">
              <div className="flex items-center">
                <input
                  type="checkbox"
                  id={`rating-${rating.score}`}
                  checked={selectedRatings.includes(rating.score)}
                  onChange={() => toggleRating(rating.score)}
                  className="mr-2 h-4 w-4"
                />
                <label htmlFor={`rating-${rating.score}`} className="text-gray-700">{rating.label}</label>
              </div>
              <span className="text-gray-500 text-sm">{rating.count}</span>
            </div>
          ))}
        </div>
      </div>

      <div className="bg-white rounded-lg shadow p-4 mb-4">
        <h2 className="font-bold text-lg mb-4 text-gray-800">Phòng ngủ và phòng tắm</h2>
        <div className="mb-4">
          <div className="flex items-center justify-between mb-2">
            <span className="text-gray-700">Phòng ngủ</span>
            <div className="flex items-center">
              <button
                onClick={() => setRoomsCount(Math.max(0, roomsCount - 1))}
                className="h-8 w-8 rounded-full border border-gray-300 flex items-center justify-center text-gray-600"
                disabled={roomsCount === 0}
              >
                <Minus size={16} />
              </button>
              <span className="mx-3 w-6 text-center text-gray-700">{roomsCount}</span>
              <button
                onClick={() => setRoomsCount(roomsCount + 1)}
                className="h-8 w-8 rounded-full border border-gray-300 flex items-center justify-center text-gray-600"
              >
                <Plus size={16} />
              </button>
            </div>
          </div>
          <div className="flex items-center justify-between">
            <span className="text-gray-700">Phòng tắm</span>
            <div className="flex items-center">
              <button
                onClick={() => setBathroomsCount(Math.max(0, bathroomsCount - 1))}
                className="h-8 w-8 rounded-full border border-gray-300 flex items-center justify-center text-gray-600"
                disabled={bathroomsCount === 0}
              >
                <Minus size={16} />
              </button>
              <span className="mx-3 w-6 text-center text-gray-700">{bathroomsCount}</span>
              <button
                onClick={() => setBathroomsCount(bathroomsCount + 1)}
                className="h-8 w-8 rounded-full border border-gray-300 flex items-center justify-center text-gray-600"
              >
                <Plus size={16} />
              </button>
            </div>
          </div>
        </div>
      </div>

      <div className="bg-white rounded-lg shadow p-4 mb-4">
        <h2 className="font-bold text-lg mb-4 text-gray-800">Các bộ lọc phổ biến</h2>
        {loading ? (
          <div className="text-center py-4">
            <div className="animate-spin rounded-full h-6 w-6 border-t-2 border-b-2 border-blue-500 mx-auto"></div>
          </div>
        ) : (
          <div className="space-y-2">
            {amenityGroups.flatMap(group =>
              group.amenities.filter(amenity => amenity.isFilterable).map(amenity => (
                <div key={amenity.id} className="flex items-center">
                  <input
                    type="checkbox"
                    id={`amenity-${amenity.id}`}
                    checked={selectedAmenities.includes(amenity.id.toString())}
                    onChange={() => toggleAmenity(amenity.id.toString())}
                    className="mr-2 h-4 w-4"
                  />
                  <label htmlFor={`amenity-${amenity.id}`} className="text-gray-700">{amenity.name}</label>
                </div>
              ))
            )}
          </div>
        )}
      </div>

      <div className="bg-white rounded-lg shadow p-4 mb-4">
        <h2 className="font-bold text-lg mb-4 text-gray-800">Khu vực</h2>
        <div className="space-y-2">
          {[
            { id: 'hoan-kiem', label: 'Quận Hoàn Kiếm', count: 1140 },
            { id: 'ba-dinh', label: 'Quận Ba Đình', count: 798 },
            { id: 'dong-da', label: 'Quận Đống Đa', count: 1166 },
            { id: 'tay-ho', label: 'Quận Tây Hồ', count: 935 },
            { id: 'cau-giay', label: 'Quận Cầu Giấy', count: 1080 }
          ].map((district) => (
            <div key={district.id} className="flex items-center justify-between">
              <div className="flex items-center">
                <input
                  type="checkbox"
                  id={district.id}
                  checked={selectedDistricts.includes(district.id)}
                  onChange={() => toggleDistrict(district.id)}
                  className="mr-2 h-4 w-4"
                />
                <label htmlFor={district.id} className="text-gray-700">{district.label}</label>
              </div>
              <span className="text-gray-500 text-sm">{district.count}</span>
            </div>
          ))}
        </div>
      </div>

      {showMobileFilters && (
        <div className="fixed bottom-0 left-0 right-0 p-4 bg-white border-t md:hidden">
          <button
            onClick={() => {
              sendFiltersToParent();
              closeMobileFilters();
            }}
            className="w-full bg-blue-600 text-white font-semibold py-3 rounded-md"
          >
            Xem kết quả
          </button>
        </div>
      )}
    </div>
  );
};

export default SearchFilterSidebar;