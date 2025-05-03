"use client";

import { useState, useEffect } from 'react';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { CreateAccommodationDto } from '@/types/accommodation';
import { AlertCircle } from 'lucide-react';
import { Alert, AlertDescription } from '@/components/ui/alert';
import MapPicker from './MapPicker';
import { locationService } from '@/services';
import { Country, Province } from '@/types/location';

interface LocationFormProps {
  formData: CreateAccommodationDto;
  updateFormData: (data: Partial<CreateAccommodationDto>) => void;
}

interface District {
  id: number;
  name: string;
  provinceId: number;
}

export default function LocationForm({ formData, updateFormData }: LocationFormProps) {
  const [countries, setCountries] = useState<Country[]>([]);
  const [provinces, setProvinces] = useState<Province[]>([]);
  const [districts, setDistricts] = useState<District[]>([]);
  const [filteredDistricts, setFilteredDistricts] = useState<District[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Fetch countries on component mount
  useEffect(() => {
    const fetchCountries = async () => {
      try {
        setLoading(true);
        setError(null);

        const response = await locationService.getCountries({
          page: 0,
          pageSize: 100,
          sortBy: 'name',
          sortOrder: 'asc'
        });

        setCountries(response.data || []);

        // Mock data cho districts (vẫn giữ nguyên như cũ)
        setDistricts([
          { id: 1, name: 'Quận 1', provinceId: 2 },
          { id: 2, name: 'Quận 2', provinceId: 2 },
          { id: 3, name: 'Quận 3', provinceId: 2 },
          { id: 4, name: 'Quận Ba Đình', provinceId: 1 },
          { id: 5, name: 'Quận Hoàn Kiếm', provinceId: 1 },
          { id: 6, name: 'Quận Hải Châu', provinceId: 3 },
          { id: 7, name: 'Sukhumvit', provinceId: 4 },
        ]);
      } catch (err) {
        console.error('Error fetching countries:', err);
        setError('Có lỗi xảy ra khi tải dữ liệu quốc gia. Vui lòng tải lại trang.');
      } finally {
        setLoading(false);
      }
    };

    fetchCountries();
  }, []);

  // Fetch provinces when a country is selected
  useEffect(() => {
    const fetchProvinces = async () => {
      if (!formData.location.countryId) {
        setProvinces([]);
        return;
      }

      try {
        setLoading(true);

        const response = await locationService.getProvincesByCountryId(
          formData.location.countryId,
          {
            page: 0,
            pageSize: 100,
            sortBy: 'name',
            sortOrder: 'asc'
          }
        );

        setProvinces(response.data || []);

        // Reset provinceId if it's not in the fetched provinces
        if (response.data.length > 0 && formData.location.provinceId) {
          const provinceExists = response.data.some(p => p.id === formData.location.provinceId);
          if (!provinceExists) {
            handleLocationChange('provinceId', 0);
          }
        }
      } catch (err) {
        console.error('Error fetching provinces:', err);
        setError('Có lỗi xảy ra khi tải dữ liệu tỉnh/thành phố. Vui lòng thử lại.');
      } finally {
        setLoading(false);
      }
    };

    if (formData.location.countryId) {
      fetchProvinces();
    } else {
      setProvinces([]);
      handleLocationChange('provinceId', 0);
    }
  }, [formData.location.countryId]);

  useEffect(() => {
    // Filter districts based on selected province
    if (formData.location.provinceId) {
      const filtered = districts.filter(
        district => district.provinceId === formData.location.provinceId
      );
      setFilteredDistricts(filtered);

      // Reset districtId if it's not in the filtered list
      // if (filtered.length > 0 && formData.location.districtId) {
      //   const districtExists = filtered.some(d => d.id === formData.location.districtId);
      //   if (!districtExists) {
      //     handleLocationChange('districtId', 0);
      //   }
      // }
    } else {
      setFilteredDistricts([]);
    }
  }, [formData.location.provinceId, districts]);

  const handleLocationChange = (field: string, value: any) => {
    updateFormData({
      location: {
        ...formData.location,
        [field]: value
      }
    });
  };

  const handleMapCoordinatesChange = (latitude: number, longitude: number) => {
    updateFormData({
      location: {
        ...formData.location,
        latitude,
        longitude
      }
    });
  };

  if (loading && countries.length === 0) {
    return (
      <div className="flex items-center justify-center p-8">
        <div className="w-8 h-8 border-4 border-blue-500 border-t-transparent rounded-full animate-spin"></div>
        <span className="ml-2">Đang tải dữ liệu vị trí...</span>
      </div>
    );
  }

  if (error) {
    return (
      <Alert variant="destructive">
        <AlertCircle className="h-4 w-4" />
        <AlertDescription>{error}</AlertDescription>
      </Alert>
    );
  }

  return (
    <div className="space-y-6">
      <div className="mb-6">
        <h2 className="text-xl font-semibold">Vị trí chỗ nghỉ</h2>
        <p className="text-gray-600">
          Cung cấp địa chỉ chính xác sẽ giúp khách hàng dễ dàng tìm thấy chỗ nghỉ của bạn.
        </p>
      </div>

      {/* Thiết kế mới với 2 cột - bản đồ bên phải và form bên trái */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
        {/* Form nhập địa chỉ bên trái */}
        <div className="lg:col-span-5 space-y-4">
          <div className="p-4 bg-blue-50 rounded-md mb-4 border border-blue-100">
            <h3 className="text-md font-medium text-blue-800 mb-1">Vị trí hiện tại</h3>
            {formData.location.latitude && formData.location.longitude ? (
              <p className="text-sm text-blue-700">
                Vĩ độ: {formData.location.latitude.toFixed(6)}, Kinh độ: {formData.location.longitude.toFixed(6)}
              </p>
            ) : (
              <p className="text-sm text-blue-700">Chưa chọn vị trí trên bản đồ</p>
            )}
          </div>

          <div className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="countryId">Quốc gia</Label>
              <Select
                value={formData.location.countryId?.toString() || undefined}
                onValueChange={(value) => handleLocationChange('countryId', Number(value))}
              >
                <SelectTrigger className='w-full'>
                  <SelectValue placeholder="Chọn quốc gia" />
                </SelectTrigger>
                <SelectContent>
                  {countries.map((country) => (
                    <SelectItem key={country.id} value={country.id.toString()}>
                      {country.name}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>

            <div className="space-y-2">
              <Label htmlFor="provinceId">Tỉnh/Thành phố</Label>
              <Select
                value={formData.location.provinceId?.toString() || undefined}
                onValueChange={(value) => handleLocationChange('provinceId', Number(value))}
                disabled={!formData.location.countryId || provinces.length === 0}
              >
                <SelectTrigger className='w-full'>
                  <SelectValue placeholder="Chọn tỉnh/thành phố" />
                </SelectTrigger>
                <SelectContent>
                  {provinces.map((province) => (
                    <SelectItem key={province.id} value={province.id.toString()}>
                      {province.name}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>

            {/* <div className="space-y-2">
              <Label htmlFor="districtId">Quận/Huyện</Label>
              <Select 
                value={formData.location.districtId?.toString() || undefined}
                onValueChange={(value) => handleLocationChange('districtId', Number(value))}
                disabled={!formData.location.provinceId || filteredDistricts.length === 0}
              >
                <SelectTrigger>
                  <SelectValue placeholder="Chọn quận/huyện" />
                </SelectTrigger>
                <SelectContent>
                  {filteredDistricts.map((district) => (
                    <SelectItem key={district.id} value={district.id.toString()}>
                      {district.name}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div> */}

            <div className="space-y-2">
              <Label htmlFor="detailAddress">Địa chỉ chi tiết</Label>
              <Input
                id="detailAddress"
                value={formData.location.detailAddress || ''}
                onChange={(e) => handleLocationChange('detailAddress', e.target.value)}
                placeholder="Nhập địa chỉ chi tiết (số nhà, đường, phường/xã)"
              />
              <p className="text-sm text-gray-500">
                Ví dụ: 123 Đường Nguyễn Văn Linh, Phường Tân Phong
              </p>
            </div>

            {/* <div className="space-y-2">
              <Label htmlFor="postalCode">Mã bưu chính</Label>
              <Input 
                id="postalCode"
                value={formData.location.postalCode || ''}
                onChange={(e) => handleLocationChange('postalCode', e.target.value)}
                placeholder="Nhập mã bưu chính"
              />
            </div> */}
          </div>
        </div>

        {/* Bản đồ bên phải - chiếm phần lớn không gian */}
        <div className="lg:col-span-7">
          <div className="space-y-3">
            <div className="flex justify-between items-center">
              <Label className="font-medium">Chọn vị trí chính xác trên bản đồ</Label>
            </div>
            <div
              className="border border-gray-200 rounded-md overflow-hidden shadow-sm"
              style={{ height: '500px' }}
            >
              <MapPicker
                initialLatitude={formData.location.latitude || 21.008854}
                initialLongitude={formData.location.longitude || 105.850782}
                onCoordinatesChange={handleMapCoordinatesChange}
              />
            </div>
            <p className="text-sm text-gray-500 italic">
              Kéo điểm đánh dấu hoặc click vào vị trí trên bản đồ để chọn vị trí chính xác của chỗ nghỉ.
              Vị trí này sẽ được hiển thị cho khách hàng trên trang thông tin chi tiết.
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}