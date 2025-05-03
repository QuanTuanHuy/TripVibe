"use client";

import { useEffect, useState } from 'react';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Textarea } from '@/components/ui/textarea';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Checkbox } from '@/components/ui/checkbox';
import { CreateAccommodationDto, UnitName } from '@/types/accommodation';
import { accommodationService } from '@/services';
import { AlertCircle, MinusCircle, PlusCircle } from 'lucide-react';
import { Alert, AlertDescription } from '@/components/ui/alert';
import { Card, CardContent } from '@/components/ui/card';
import { Button } from '@/components/ui/button';

interface UnitDetailsFormProps {
  formData: CreateAccommodationDto;
  updateFormData: (data: Partial<CreateAccommodationDto>) => void;
}

export default function UnitDetailsForm({ formData, updateFormData }: UnitDetailsFormProps) {
  const [unitNames, setUnitNames] = useState<UnitName[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [adultCount, setAdultCount] = useState<number>(2);
  const [childCount, setChildCount] = useState<number>(1);

  useEffect(() => {
    if (formData.unit) {
      setAdultCount(formData.unit.maxAdults || 2);
      setChildCount(formData.unit.maxChildren || 1);
    }
  }, [formData.unit?.maxAdults, formData.unit?.maxChildren]);

  useEffect(() => {
    const fetchUnitNames = async () => {
      try {
        setLoading(true);
        setError(null);

        const response = await accommodationService.getUnitNames();

        setUnitNames(response.data || []);
      } catch (err) {
        console.error('Error fetching unit names:', err);
        setUnitNames([]);
        setError('Có lỗi xảy ra khi tải dữ liệu tên đơn vị. Vui lòng tải lại trang.');
      } finally {
        setLoading(false);
      }
    };

    fetchUnitNames();
  }, []);

  const handleUnitChange = (field: string, value: any) => {
    updateFormData({
      unit: {
        ...formData.unit,
        [field]: value
      }
    });
  };

  const decreaseAdults = () => {
    if (adultCount > 1) {
      const newValue = adultCount - 1;
      setAdultCount(newValue);
      handleUnitChange('maxAdults', newValue);
    }
  };

  const increaseAdults = () => {
    if (adultCount < 16) {
      const newValue = adultCount + 1;
      setAdultCount(newValue);
      handleUnitChange('maxAdults', newValue);
    }
  };

  const decreaseChildren = () => {
    if (childCount > 0) {
      const newValue = childCount - 1;
      setChildCount(newValue);
      handleUnitChange('maxChildren', newValue);
    }
  };

  const increaseChildren = () => {
    if (childCount < 10) {
      const newValue = childCount + 1;
      setChildCount(newValue);
      handleUnitChange('maxChildren', newValue);
    }
  };

  const unit = formData.unit || {};

  if (loading) {
    return (
      <div className="flex items-center justify-center p-8">
        <div className="w-8 h-8 border-4 border-blue-500 border-t-transparent rounded-full animate-spin"></div>
        <span className="ml-2">Đang tải dữ liệu...</span>
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
      <h2 className="text-xl font-semibold">Thông tin loại phòng/đơn vị</h2>
      <p className="text-gray-600 mb-4">
        Cung cấp thông tin chi tiết về loại phòng/đơn vị mà bạn muốn cho thuê.
        Thông tin càng chi tiết, khách hàng càng dễ dàng đưa ra quyết định đặt phòng.
      </p>

      <div className="space-y-4">
        <div className="space-y-2">
          <Label htmlFor="unitNameId">Loại phòng/đơn vị</Label>
          <Select
            value={unit.unitNameId ? unit.unitNameId.toString() : undefined}
            onValueChange={(value) => handleUnitChange('unitNameId', Number(value))}
          >
            <SelectTrigger>
              <SelectValue placeholder="Chọn loại phòng/đơn vị" />
            </SelectTrigger>
            <SelectContent>
              {Array.isArray(unitNames) && unitNames.map((unitName) => (
                <SelectItem key={unitName.id} value={unitName.id.toString()}>
                  {unitName.name}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
          <p className="text-sm text-gray-500">
            Chọn loại phòng hoặc đơn vị phù hợp với chỗ nghỉ của bạn.
          </p>
        </div>

        <div className="space-y-2">
          <Label htmlFor="description">Mô tả phòng/đơn vị</Label>
          <Textarea
            id="description"
            value={unit.description || ''}
            onChange={(e) => handleUnitChange('description', e.target.value)}
            placeholder="Mô tả chi tiết về phòng/đơn vị này"
            rows={4}
          />
          <p className="text-sm text-gray-500">
            Nêu bật các tính năng và tiện nghi đặc biệt của phòng/đơn vị này.
          </p>
        </div>

        <div className="space-y-2">
          <Label htmlFor="pricePerNight">Giá theo đêm</Label>
          <div className="flex items-center space-x-2">
            <Input
              id="pricePerNight"
              type="number"
              value={unit.pricePerNight || ''}
              onChange={(e) => handleUnitChange('pricePerNight', Number(e.target.value))}
              placeholder="Nhập giá theo đêm"
              min="0"
              step="10000"
              className="w-36"
            />
            <span className="text-gray-500">VND / đêm</span>
          </div>
          <p className="text-sm text-gray-500">
            Đây là giá cơ bản cho phòng/đơn vị này. Bạn có thể thiết lập giá theo mùa trong các bước tiếp theo.
          </p>
        </div>

        <Card className="p-4 bg-slate-50 mt-6">
          <CardContent className="p-0 space-y-4">
            <h3 className="font-medium">Sức chứa</h3>

            <div className="space-y-5">
              <div>
                <Label htmlFor="maxAdults" className="block mb-2">Số lượng người lớn tối đa:</Label>
                <div className="flex items-center">
                  <Button
                    type="button"
                    variant="outline"
                    size="icon"
                    onClick={decreaseAdults}
                    disabled={adultCount <= 1}
                    className="h-10 w-10"
                  >
                    <MinusCircle className="h-4 w-4" />
                  </Button>
                  <div className="mx-4 w-12 text-center font-medium text-lg">{adultCount}</div>
                  <Button
                    type="button"
                    variant="outline"
                    size="icon"
                    onClick={increaseAdults}
                    disabled={adultCount >= 16}
                    className="h-10 w-10"
                  >
                    <PlusCircle className="h-4 w-4" />
                  </Button>
                </div>
                <p className="text-sm text-gray-500 mt-2">
                  Số lượng người lớn tối đa có thể ở trong phòng/đơn vị này.
                </p>
              </div>

              <div>
                <Label htmlFor="maxChildren" className="block mb-2">Số lượng trẻ em tối đa:</Label>
                <div className="flex items-center">
                  <Button
                    type="button"
                    variant="outline"
                    size="icon"
                    onClick={decreaseChildren}
                    disabled={childCount <= 0}
                    className="h-10 w-10"
                  >
                    <MinusCircle className="h-4 w-4" />
                  </Button>
                  <div className="mx-4 w-12 text-center font-medium text-lg">{childCount}</div>
                  <Button
                    type="button"
                    variant="outline"
                    size="icon"
                    onClick={increaseChildren}
                    disabled={childCount >= 10}
                    className="h-10 w-10"
                  >
                    <PlusCircle className="h-4 w-4" />
                  </Button>
                </div>
                <p className="text-sm text-gray-500 mt-2">
                  Số lượng trẻ em tối đa có thể ở trong phòng/đơn vị này.
                </p>
              </div>
            </div>
          </CardContent>
        </Card>

        <div className="space-y-2">
          <Label htmlFor="quantity">Số lượng phòng/đơn vị cùng loại</Label>
          <Input
            id="quantity"
            type="number"
            value={unit.quantity || 1}
            onChange={(e) => handleUnitChange('quantity', Number(e.target.value))}
            min="1"
            step={1}
            className='w-36'
          />
          <p className="text-sm text-gray-500">
            Nhập số lượng phòng/đơn vị có cùng loại, tiện nghi và giá cả.
          </p>
        </div>

        <div className="flex items-center space-x-2 pt-2">
          <Checkbox
            id="useSharedBathroom"
            checked={unit.useSharedBathroom || false}
            onCheckedChange={(checked) => handleUnitChange('useSharedBathroom', checked === true)}
          />
          <Label htmlFor="useSharedBathroom">Sử dụng phòng tắm chung</Label>
        </div>
        <p className="text-sm text-gray-500 ml-6">
          Chọn tùy chọn này nếu phòng/đơn vị này sử dụng phòng tắm chung với các phòng/đơn vị khác.
        </p>
      </div>
    </div>
  );
}