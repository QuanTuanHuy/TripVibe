"use client";

import { useEffect, useState } from 'react';
import { Checkbox } from '@/components/ui/checkbox';
import { Label } from '@/components/ui/label';
import { CreateAccommodationDto } from '@/types/accommodation';
import { Card } from '@/components/ui/card';
import { accommodationService } from '@/services';
import { Alert, AlertDescription } from '@/components/ui/alert';
import { AlertCircle } from 'lucide-react';

interface LanguagesFormProps {
  formData: CreateAccommodationDto;
  updateFormData: (data: Partial<CreateAccommodationDto>) => void;
}

interface Language {
  id: number;
  name: string;
  code: string;
  nativeName: string;
}

export default function LanguagesForm({ formData, updateFormData }: LanguagesFormProps) {
  // Danh sách các ngôn ngữ - có thể thay thế bằng API call trong thực tế
  const [languages, setLanguages] = useState<Language[]>([])
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchLanguages = async () => {
      try {
        setLoading(true);
        setError(null);

        const languagesResponse = await accommodationService.getLanguages({
          page: 0,
          pageSize: 100,
          sortType: 'asc',
        });
        setLanguages(languagesResponse.data || []);

      } catch (err) {
        console.error('Error fetching languages:', err);
        setLanguages([]);
        setError('Có lỗi xảy ra khi tải dữ liệu ngôn ngữ. Vui lòng tải lại trang.');
      } finally {
        setLoading(false);
      }
    }
    fetchLanguages();
  }, []);

  const handleLanguageChange = (languageId: number, checked: boolean) => {
    let newLanguageIds = [...(formData.languageIds || [])];

    if (checked) {
      newLanguageIds.push(languageId);
    } else {
      newLanguageIds = newLanguageIds.filter(id => id !== languageId);
    }

    updateFormData({ languageIds: newLanguageIds });
  };

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
      <h2 className="text-xl font-semibold">Ngôn ngữ phục vụ</h2>
      <p className="text-gray-600 mb-4">
        Chọn các ngôn ngữ mà bạn và nhân viên của bạn có thể sử dụng để giao tiếp với khách hàng.
        Điều này sẽ giúp khách hàng biết họ có thể sử dụng ngôn ngữ nào khi liên hệ với bạn.
      </p>

      <Card className="p-6">
        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-4">
          {languages.map((language) => (
            <div key={language.id} className="flex items-center space-x-2">
              <Checkbox
                id={`language-${language.id}`}
                checked={(formData.languageIds || []).includes(language.id)}
                onCheckedChange={(checked) =>
                  handleLanguageChange(language.id, checked === true)
                }
              />
              <Label
                htmlFor={`language-${language.id}`}
                className="text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70 cursor-pointer"
              >
                {language.name}
                <span className="text-gray-500">({language.nativeName})</span>
              </Label>
            </div>
          ))}
        </div>
      </Card>

      {(formData.languageIds || []).length === 0 && (
        <p className="text-amber-600 text-sm mt-2">
          Vui lòng chọn ít nhất một ngôn ngữ phục vụ.
        </p>
      )}

      <div className="p-4 bg-blue-50 rounded-md border border-blue-200 mt-6">
        <p className="text-sm text-blue-700">
          <strong>Lời khuyên:</strong> Cung cấp dịch vụ bằng nhiều ngôn ngữ sẽ giúp bạn tiếp cận nhiều khách hàng quốc tế hơn.
          Nếu bạn chỉ chọn một ngôn ngữ, chúng tôi khuyến nghị nên chọn tiếng Anh ngoài tiếng Việt để phục vụ du khách nước ngoài.
        </p>
      </div>
    </div>
  );
}