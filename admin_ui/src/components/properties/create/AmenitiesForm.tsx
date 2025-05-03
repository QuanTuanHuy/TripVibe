"use client";

import { useEffect, useState } from 'react';
import { Checkbox } from '@/components/ui/checkbox';
import { Label } from '@/components/ui/label';
import { CreateAccommodationDto, AmenityGroup } from '@/types/accommodation';
import { accommodationService } from '@/services';
import { AlertCircle } from 'lucide-react';
import { Alert, AlertDescription } from '@/components/ui/alert';

interface AmenitiesFormProps {
  formData: CreateAccommodationDto;
  updateFormData: (data: Partial<CreateAccommodationDto>) => void;
}

export default function AmenitiesForm({ formData, updateFormData }: AmenitiesFormProps) {
  const [amenityGroups, setAmenityGroups] = useState<AmenityGroup[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchAmenities = async () => {
      try {
        setLoading(true);
        setError(null);

        // Group tiện nghi theo groupId
        const amenityGroups = await accommodationService.getAmenityGroups();
        const finalAmenityGroups = amenityGroups.data.filter(group => group.amenities && group.amenities.length > 0);

        setAmenityGroups(finalAmenityGroups);
      } catch (err) {
        console.error('Error fetching amenities:', err);
        setError('Có lỗi xảy ra khi tải dữ liệu tiện nghi. Vui lòng tải lại trang.');
      } finally {
        setLoading(false);
      }
    };

    fetchAmenities();
  }, []);


  const handleAmenityChange = (amenityId: number, checked: boolean) => {
    let newAmenities = [...formData.amenityIds];

    if (checked) {
      newAmenities.push(amenityId);
    } else {
      newAmenities = newAmenities.filter(id => id !== amenityId);
    }

    updateFormData({ amenityIds: newAmenities });
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center p-8">
        <div className="w-8 h-8 border-4 border-blue-500 border-t-transparent rounded-full animate-spin"></div>
        <span className="ml-2">Đang tải dữ liệu tiện nghi...</span>
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
      <h2 className="text-xl font-semibold">Tiện nghi chỗ nghỉ</h2>
      <p className="text-gray-600 mb-4">
        Chọn các tiện nghi có sẵn tại chỗ nghỉ của bạn. Càng nhiều tiện nghi, chỗ nghỉ càng hấp dẫn với khách hàng.
      </p>

      <div className="space-y-6">
        {amenityGroups.map((group) => (
          <div key={group.id} className="space-y-3">
            <h3 className="font-medium text-lg">{group.name}</h3>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              {group.amenities.map((amenity) => (
                <div key={amenity.id} className="flex items-start space-x-2">
                  <Checkbox
                    id={`amenity-${amenity.id}`}
                    checked={formData.amenityIds.includes(amenity.id)}
                    onCheckedChange={(checked) =>
                      handleAmenityChange(amenity.id, checked === true)
                    }
                  />
                  <div className="grid gap-1.5 leading-none">
                    <Label
                      htmlFor={`amenity-${amenity.id}`}
                      className="text-sm font-medium leading-none"
                    >
                      {amenity.name}
                      {amenity.needToReserve && (
                        <span className="text-xs text-amber-600 ml-1">(Cần đặt trước)</span>
                      )}
                    </Label>
                    {amenity.description && (
                      <p className="text-xs text-muted-foreground">
                        {amenity.description}
                      </p>
                    )}
                  </div>
                </div>
              ))}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}