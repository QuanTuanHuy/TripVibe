"use client";

import { useState, useEffect } from 'react';
import { CreateAccommodationDto, Amenity, UnitName, AccommodationType, Currency, PriceType, BedType, Language } from '@/types/accommodation';
import { accommodationService, bedTypeService, priceTypeService } from '@/services';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Separator } from '@/components/ui/separator';
import { Clock, MapPin, Star, BedDouble, Wifi, Languages } from 'lucide-react';
import Image from 'next/image';
import MapPicker from './MapPicker';

interface ReviewFormProps {
  formData: CreateAccommodationDto;
  images: File[];
}

interface LookupData {
  accommodationTypes: AccommodationType[];
  unitNames: UnitName[];
  amenities: Amenity[];
  currencies: Currency[];
  bedTypes: BedType[];
  priceTypes: PriceType[];
  languages: Language[];
  loading: boolean;
}

export default function ReviewForm({ formData, images }: ReviewFormProps) {
  const [lookupData, setLookupData] = useState<LookupData>({
    accommodationTypes: [],
    unitNames: [],
    amenities: [],
    currencies: [],
    bedTypes: [],
    priceTypes: [],
    languages: [],
    loading: true
  });

  useEffect(() => {
    const fetchLookupData = async () => {
      try {
        // Gọi các API để lấy dữ liệu tham chiếu
        const [types, unitNames, amenitiGroups, currencies, bedTypes, priceTypes, languages] = await Promise.all([
          accommodationService.getAccommodationTypes(),
          accommodationService.getUnitNames({ page: 0, pageSize: 100, sortType: 'asc' }),
          accommodationService.getAmenityGroups({ page: 0, pageSize: 100 }),
          accommodationService.getCurrencies(),
          bedTypeService.getBedTypes({ page: 0, pageSize: 100, sortType: 'asc' }),
          priceTypeService.getPriceTypes({ page: 0, pageSize: 2, sortType: 'asc' }),
          accommodationService.getLanguages({ page: 0, pageSize: 100, sortType: 'asc' })
        ]);

        setLookupData({
          accommodationTypes: types || [],
          unitNames: unitNames.data || [],
          amenities: amenitiGroups.data.flatMap(group => group.amenities) || [],
          currencies: currencies || [],
          bedTypes: bedTypes.data || [],
          priceTypes: priceTypes.data || [],
          languages: languages.data || [],
          loading: false
        });
      } catch (error) {
        console.error('Error fetching lookup data:', error);
        setLookupData(prev => ({ ...prev, loading: false }));
      }
    };

    fetchLookupData();
  }, []);

  const getAccommodationTypeName = (id: number): string => {
    return lookupData.accommodationTypes.find(t => t.id === id)?.name || 'Không xác định';
  };

  const getUnitName = (id: number): string => {
    return lookupData.unitNames.find(t => t.id === id)?.name || 'Không xác định';
  };

  const getCurrencySymbol = (id: number): string => {
    return lookupData.currencies.find(c => c.id === id)?.symbol || 'VND';
  };

  const getLanguageName = (id: number): string => {
    return lookupData.languages.find(l => l.id === id)?.name || 'Không xác định';
  }

  const getAmenityNames = (ids: number[]): string[] => {
    return ids.map(id => {
      const amenity = lookupData.amenities.find(a => a.id === id);
      return amenity ? amenity.name : 'Không xác định';
    });
  };

  const formatTime = (hour: number): string => {
    if (hour === 0 || hour === 24) return '00:00';
    return `${String(hour).padStart(2, '0')}:00`;
  };

  const formatPrice = (price: number): string => {
    return price.toLocaleString('vi-VN');
  };

  const getPriceTypeName = (typeId: number): string => {
    return lookupData.priceTypes.find(type => type.id === typeId)?.name || 'Không xác định';
  };

  // Tính giá từ phần trăm
  const calculatePriceFromPercentage = (percentage: number): number => {
    return Math.round((percentage / 100) * (formData.unit.pricePerNight || 0));
  };

  if (lookupData.loading) {
    return (
      <div className="flex items-center justify-center p-8">
        <div className="w-8 h-8 border-4 border-blue-500 border-t-transparent rounded-full animate-spin"></div>
        <span className="ml-2">Đang tải dữ liệu...</span>
      </div>
    );
  }

  return (
    <div className="space-y-8">
      <div>
        <h2 className="text-xl font-semibold">Xác nhận thông tin</h2>
        <p className="text-gray-600 mb-4">
          Vui lòng kiểm tra kỹ thông tin chỗ nghỉ của bạn trước khi gửi. Bạn có thể quay lại các bước trước để chỉnh sửa nếu cần.
        </p>
      </div>

      {/* Thông tin cơ bản */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center">
            <Star className="mr-2 h-5 w-5 text-amber-500" />
            Thông tin cơ bản
          </CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <p className="text-sm text-gray-500">Tên chỗ nghỉ</p>
              <p className="font-medium">{formData.name || 'Chưa có thông tin'}</p>
            </div>
            <div>
              <p className="text-sm text-gray-500">Loại chỗ nghỉ</p>
              <p className="font-medium">{getAccommodationTypeName(formData.typeId)}</p>
            </div>
          </div>

          <div>
            <p className="text-sm text-gray-500">Mô tả</p>
            <p className="text-sm">{formData.description || 'Chưa có mô tả'}</p>
          </div>
        </CardContent>
      </Card>

      {/* Vị trí */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center">
            <MapPin className="mr-2 h-5 w-5 text-red-500" />
            Vị trí
          </CardTitle>
        </CardHeader>
        <CardContent className="space-y-2">
          <p className="font-medium">{formData.location.detailAddress || 'Chưa có địa chỉ chi tiết'}</p>
          <div className="h-64 rounded-md overflow-hidden border">
            {/* Sử dụng MapPicker trong chế độ readonly */}
            <div className="w-full h-full">
              {formData.location.latitude && formData.location.longitude ? (
                <MapPicker
                  initialLatitude={formData.location.latitude}
                  initialLongitude={formData.location.longitude}
                  onCoordinatesChange={() => { }} // Readonly mode, không cho phép thay đổi tọa độ
                />
              ) : (
                <div className="flex items-center justify-center h-full bg-gray-100">
                  <MapPin className="h-8 w-8 text-red-500" />
                  <span className="ml-2">Chưa có tọa độ vị trí</span>
                </div>
              )}
            </div>
          </div>
          <p className="text-sm text-gray-500 mt-2">
            Tọa độ: {formData.location.latitude.toFixed(6)}, {formData.location.longitude.toFixed(6)}
          </p>
        </CardContent>
      </Card>

      {/* Thời gian check-in/check-out */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center">
            <Clock className="mr-2 h-5 w-5 text-blue-500" />
            Thời gian check-in/check-out
          </CardTitle>
        </CardHeader>
        <CardContent>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <h3 className="font-medium">Check-in</h3>
              <p className="text-sm mt-1">
                Từ {formatTime(formData.checkInTimeFrom)} đến {formatTime(formData.checkInTimeTo)}
              </p>
            </div>
            <div>
              <h3 className="font-medium">Check-out</h3>
              <p className="text-sm mt-1">
                Từ {formatTime(formData.checkOutTimeFrom)} đến {formatTime(formData.checkOutTimeTo)}
              </p>
            </div>
          </div>
        </CardContent>
      </Card>

      {/* Chi tiết phòng/đơn vị */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center">
            <BedDouble className="mr-2 h-5 w-5 text-indigo-500" />
            Chi tiết phòng/đơn vị
          </CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="space-y-2">
            <div>
              <span className="text-sm text-gray-500">Loại phòng/đơn vị:</span>
              <span className="ml-2 font-medium">{getUnitName(formData.unit.unitNameId)}</span>
            </div>

            <div>
              <span className="text-sm text-gray-500">Số lượng:</span>
              <span className="ml-2">{formData.unit.quantity} phòng/đơn vị</span>
            </div>

            <div>
              <span className="text-sm text-gray-500">Giá cơ bản:</span>
              <span className="ml-2 font-medium">
                {formatPrice(formData.unit.pricePerNight)} {getCurrencySymbol(formData.currencyId)}/đêm
              </span>
            </div>

            <div>
              <span className="text-sm text-gray-500">Sức chứa:</span>
              <span className="ml-2">
                Tối đa {formData.unit.maxAdults} người lớn, {formData.unit.maxChildren} trẻ em
              </span>
            </div>

            <div>
              <span className="text-sm text-gray-500">Phòng tắm:</span>
              <span className="ml-2">
                {formData.unit.useSharedBathroom ? 'Phòng tắm chung' : 'Phòng tắm riêng'}
              </span>
            </div>
          </div>

          <Separator />

          <div>
            <h3 className="font-medium mb-2">Phòng ngủ</h3>
            {formData.unit.bedrooms && formData.unit.bedrooms.length > 0 ? (
              <div className="space-y-4">
                {formData.unit.bedrooms.map((bedroom, index) => (
                  <div key={index} className="border rounded-md p-3">
                    <div className="flex justify-between text-sm font-medium mb-2">
                      <span>Phòng ngủ {index + 1}</span>
                      <span className="text-blue-600">{bedroom.quantity} phòng</span>
                    </div>

                    {bedroom.beds && bedroom.beds.length > 0 ? (
                      <div className="space-y-1 ml-4">
                        {bedroom.beds.map((bed, bedIndex) => (
                          <div key={bedIndex} className="flex justify-between text-sm">
                            <span className="text-gray-600">
                              {/* Ideally we'd use a service to get bed type name */}
                              Giường loại {bed.bedTypeId}:
                            </span>
                            <span>{bed.quantity} giường</span>
                          </div>
                        ))}
                      </div>
                    ) : (
                      <p className="text-sm text-gray-500 ml-4">Không có thông tin giường</p>
                    )}
                  </div>
                ))}
              </div>
            ) : (
              <p className="text-sm text-gray-500">Chưa có thông tin phòng ngủ</p>
            )}
          </div>

          <Separator />

          <div>
            <h3 className="font-medium mb-2">Loại giá</h3>
            {formData.unit.priceTypes && formData.unit.priceTypes.length > 0 ? (
              <div className="space-y-3">
                {formData.unit.priceTypes.map((priceType, index) => (
                  <div key={index} className="space-y-1">
                    <div className="flex justify-between text-sm font-medium">
                      <span>{getPriceTypeName(priceType.priceTypeId)}</span>
                    </div>
                    <div className="flex justify-between text-sm">
                      <span className="text-gray-500">Phần trăm giá cơ bản:</span>
                      <span>{priceType.percentage}% ({formatPrice(calculatePriceFromPercentage(priceType.percentage))} {getCurrencySymbol(formData.currencyId)})</span>
                    </div>
                    {priceType.priceTypeId === 2 && (
                      <p className="text-xs text-blue-600">Giá thấp hơn nhưng không thể hủy hoặc hoàn tiền</p>
                    )}
                  </div>
                ))}
              </div>
            ) : (
              <p className="text-sm text-gray-500">Chưa có thông tin loại giá</p>
            )}
          </div>

          <Separator />

          <div>
            <h3 className="font-medium mb-2">Giá theo số lượng khách</h3>
            {formData.unit.priceGroups && formData.unit.priceGroups.length > 0 ? (
              <div className="space-y-3">
                {formData.unit.priceGroups.map((priceGroup, index) => (
                  <div key={index} className="space-y-1">
                    <div className="flex justify-between text-sm font-medium">
                      <span>{priceGroup.numberOfGuests} khách</span>
                    </div>
                    <div className="flex justify-between text-sm">
                      <span className="text-gray-500">Phần trăm giá cơ bản:</span>
                      <span>{priceGroup.percentage}% ({formatPrice(calculatePriceFromPercentage(priceGroup.percentage))} {getCurrencySymbol(formData.currencyId)})</span>
                    </div>
                    {formData.unit.maxAdults === priceGroup.numberOfGuests && (
                      <p className="text-xs text-blue-600">Số lượng khách tối đa: 100% giá cơ bản</p>
                    )}
                    {formData.unit.maxAdults > priceGroup.numberOfGuests && (
                      <p className="text-xs text-blue-600">Giảm {formData.unit.maxAdults - priceGroup.numberOfGuests} người: giảm {(formData.unit.maxAdults - priceGroup.numberOfGuests) * 10}% giá cơ bản</p>
                    )}
                  </div>
                ))}
              </div>
            ) : (
              <p className="text-sm text-gray-500">Chưa có thông tin giá theo số lượng khách</p>
            )}
          </div>
        </CardContent>
      </Card>

      {/* Tiện nghi */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center">
            <Wifi className="mr-2 h-5 w-5 text-green-500" />
            Tiện nghi
          </CardTitle>
        </CardHeader>
        <CardContent>
          <h3 className="font-medium mb-2">Tiện nghi chỗ nghỉ</h3>
          <div className="flex flex-wrap gap-2 mb-4">
            {formData.amenityIds && formData.amenityIds.length > 0 ? (
              getAmenityNames(formData.amenityIds).map((name, index) => (
                <Badge key={index} variant="outline" className="bg-gray-50">
                  {name}
                </Badge>
              ))
            ) : (
              <p className="text-sm text-gray-500">Chưa có tiện nghi chỗ nghỉ nào được chọn</p>
            )}
          </div>

          <Separator className="my-4" />

          <h3 className="font-medium mb-2">Tiện nghi phòng/đơn vị</h3>
          <div className="flex flex-wrap gap-2">
            {formData.unit.amenityIds && formData.unit.amenityIds.length > 0 ? (
              getAmenityNames(formData.unit.amenityIds).map((name, index) => (
                <Badge key={index} variant="outline" className="bg-gray-50">
                  {name}
                </Badge>
              ))
            ) : (
              <p className="text-sm text-gray-500">Chưa có tiện nghi phòng/đơn vị nào được chọn</p>
            )}
          </div>
        </CardContent>
      </Card>

      {/* Ngôn ngữ */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center">
            <Languages className="mr-2 h-5 w-5 text-purple-500" />
            Ngôn ngữ phục vụ
          </CardTitle>
        </CardHeader>
        <CardContent>
          <div className="flex flex-wrap gap-2">
            {formData.languageIds && formData.languageIds.length > 0 ? (
              formData.languageIds.map((id) => (
                <Badge key={id} variant="outline" className="bg-gray-50">
                  {getLanguageName(id)}
                </Badge>
              ))
            ) : (
              <p className="text-sm text-gray-500">Chưa có ngôn ngữ phục vụ nào được chọn</p>
            )}
          </div>
        </CardContent>
      </Card>

      {/* Hình ảnh */}
      <Card>
        <CardHeader>
          <CardTitle>Hình ảnh</CardTitle>
        </CardHeader>
        <CardContent>
          {images.length > 0 ? (
            <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
              {images.map((image, index) => (
                <div key={index} className="relative aspect-[4/3] border rounded-md overflow-hidden">
                  <Image
                    src={URL.createObjectURL(image)}
                    alt={`Hình ảnh ${index + 1}`}
                    fill
                    className="object-cover"
                  />
                  {index === 0 && (
                    <Badge className="absolute top-2 left-2 bg-blue-500 text-white">
                      Ảnh đại diện
                    </Badge>
                  )}
                </div>
              ))}
            </div>
          ) : (
            <p className="text-sm text-gray-500">Chưa có hình ảnh nào được tải lên</p>
          )}
        </CardContent>
      </Card>

      <div className="p-4 bg-amber-50 rounded-md border border-amber-200">
        <p className="text-amber-700">
          <strong>Lưu ý:</strong> Sau khi gửi, thông tin chỗ nghỉ của bạn sẽ được quản trị viên xem xét trước khi được xuất bản.
          Thời gian xem xét thông thường là 1-3 ngày làm việc.
        </p>
      </div>
    </div>
  );
}