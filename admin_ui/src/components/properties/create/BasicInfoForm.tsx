"use client";

import { useEffect, useState } from 'react';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Textarea } from '@/components/ui/textarea';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { CreateAccommodationDto, AccommodationType, Currency } from '@/types/accommodation';
import { accommodationService } from '@/services';
import { AlertCircle } from 'lucide-react';
import { Alert, AlertDescription } from '@/components/ui/alert';

interface BasicInfoFormProps {
  formData: CreateAccommodationDto;
  updateFormData: (data: Partial<CreateAccommodationDto>) => void;
}

export default function BasicInfoForm({ formData, updateFormData }: BasicInfoFormProps) {
  const [accommodationTypes, setAccommodationTypes] = useState<AccommodationType[]>([]);
  const [currencies, setCurrencies] = useState<Currency[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        setError(null);

        // Fetch accommodation types
        const typesData = await accommodationService.getAccommodationTypes();
        setAccommodationTypes(typesData || []);

        // Fetch currencies
        const currenciesData = await accommodationService.getCurrencies();
        setCurrencies(currenciesData || []);
      } catch (err) {
        console.error('Error fetching form data:', err);
        setError('Có lỗi xảy ra khi tải dữ liệu. Vui lòng tải lại trang.');
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

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
      <h2 className="text-xl font-semibold">Thông tin cơ bản</h2>
      <p className="text-gray-600 mb-4">
        Hãy bắt đầu bằng việc cung cấp những thông tin cơ bản về chỗ nghỉ của bạn.
      </p>

      <div className="space-y-4">
        <div className="space-y-2">
          <Label htmlFor="name">Tên chỗ nghỉ</Label>
          <Input
            id="name"
            value={formData.name}
            onChange={(e) => updateFormData({ name: e.target.value })}
            placeholder="Nhập tên chỗ nghỉ của bạn"
            required
          />
          <p className="text-sm text-gray-500">
            Ví dụ: Khách sạn Majestic, Căn hộ Sunrise City, Biệt thự Ocean View...
          </p>
        </div>

        <div className="space-y-2">
          <Label htmlFor="typeId">Loại chỗ nghỉ</Label>
          <Select
            value={formData.typeId ? formData.typeId.toString() : undefined}
            onValueChange={(value) => updateFormData({ typeId: Number(value) })}
          >
            <SelectTrigger>
              <SelectValue placeholder="Chọn loại chỗ nghỉ" />
            </SelectTrigger>
            <SelectContent>
              {accommodationTypes.map((type) => (
                <SelectItem key={type.id} value={type.id.toString()}>
                  {type.name}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
          <p className="text-sm text-gray-500">
            Chọn loại chỗ nghỉ phù hợp sẽ giúp khách hàng dễ dàng tìm thấy dịch vụ của bạn.
          </p>
        </div>

        <div className="space-y-2">
          <Label htmlFor="description">Mô tả</Label>
          <Textarea
            id="description"
            value={formData.description}
            onChange={(e) => updateFormData({ description: e.target.value })}
            placeholder="Mô tả về chỗ nghỉ của bạn"
            rows={5}
          />
          <p className="text-sm text-gray-500">
            Mô tả chi tiết sẽ giúp khách hàng hiểu rõ hơn về chỗ nghỉ của bạn. Hãy nêu bật những đặc điểm nổi bật
            và các tiện ích xung quanh.
          </p>
        </div>

        <div className="space-y-2">
          <Label htmlFor="currencyId">Đơn vị tiền tệ</Label>
          <Select
            value={formData.currencyId ? formData.currencyId.toString() : undefined}
            onValueChange={(value) => updateFormData({ currencyId: Number(value) })}
          >
            <SelectTrigger>
              <SelectValue placeholder="Chọn đơn vị tiền tệ" />
            </SelectTrigger>
            <SelectContent>
              {currencies.map((currency) => (
                <SelectItem key={currency.id} value={currency.id.toString()}>
                  {currency.code} ({currency.symbol})
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
          <p className="text-sm text-gray-500">
            Đơn vị tiền tệ sẽ được sử dụng cho tất cả các giao dịch liên quan đến chỗ nghỉ của bạn.
          </p>
        </div>
      </div>
    </div>
  );
}