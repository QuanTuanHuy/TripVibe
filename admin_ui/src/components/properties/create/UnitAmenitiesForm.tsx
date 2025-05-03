"use client";

import { useEffect, useState } from 'react';
import { Checkbox } from '@/components/ui/checkbox';
import { Label } from '@/components/ui/label';
import { AmenityGroup, CreateAccommodationDto } from '@/types/accommodation';
import { accommodationService } from '@/services';
import { AlertCircle } from 'lucide-react';
import { Alert, AlertDescription } from '@/components/ui/alert';
import { Skeleton } from '@/components/ui/skeleton';

interface UnitAmenitiesFormProps {
  formData: CreateAccommodationDto;
  updateFormData: (data: Partial<CreateAccommodationDto>) => void;
}


export default function UnitAmenitiesForm({ formData, updateFormData }: UnitAmenitiesFormProps) {
  const [amenityGroups, setAmenityGroups] = useState<AmenityGroup[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchAmenityGroups = async () => {
      try {
        setLoading(true);
        setError(null);

        // Fetch amenity groups from the API
        const groups = await accommodationService.getAmenityGroups(
          {
            page: 0,
            pageSize: 10,
            type: 'Unit',
          }
        );

        // If no amenity groups are available, show an empty state
        if (!groups || groups.data.length === 0) {
          setAmenityGroups([]);
          return;
        }

        const finalAmenityGroups = groups.data.filter(group => group.amenities && group.amenities.length > 0);


        setAmenityGroups(finalAmenityGroups);
      } catch (err) {
        console.error('Error fetching amenity groups:', err);
        setError('Có lỗi xảy ra khi tải dữ liệu tiện nghi. Vui lòng tải lại trang.');
      } finally {
        setLoading(false);
      }
    };

    fetchAmenityGroups();
  }, []);

  const handleAmenityChange = (amenityId: number, checked: boolean) => {
    // Ensure unit.amenities is initialized as an array
    const currentAmenities = Array.isArray(formData.unit?.amenityIds) ? [...formData.unit.amenityIds] : [];

    let updatedAmenities;
    if (checked) {
      // Add the amenity if it's not already in the list
      if (!currentAmenities.includes(amenityId)) {
        updatedAmenities = [...currentAmenities, amenityId];
      } else {
        updatedAmenities = currentAmenities;
      }
    } else {
      // Remove the amenity if it exists
      updatedAmenities = currentAmenities.filter(id => id !== amenityId);
    }

    // Update the form data with the new amenities array
    updateFormData({
      unit: {
        ...formData.unit,
        amenityIds: updatedAmenities
      }
    });
  };

  if (loading) {
    return (
      <div className="space-y-6">
        <h2 className="text-xl font-semibold">Tiện nghi phòng</h2>
        <div className="space-y-4">
          <Skeleton className="h-4 w-3/4" />
          <Skeleton className="h-10 w-full" />
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {[1, 2, 3, 4, 5, 6].map(i => (
              <Skeleton key={i} className="h-10 w-full" />
            ))}
          </div>
        </div>
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

  // Make sure unit.amenities is initialized as an array
  const unitAmenities = Array.isArray(formData.unit?.amenityIds) ? formData.unit.amenityIds : [];

  return (
    <div className="space-y-6">
      <h2 className="text-xl font-semibold">Tiện nghi phòng</h2>
      <p className="text-gray-600 mb-4">
        Chọn các tiện nghi có sẵn trong phòng/đơn vị của bạn. Càng nhiều tiện nghi, phòng/đơn vị càng hấp dẫn với khách hàng.
      </p>

      {amenityGroups.length === 0 ? (
        <div className="text-center p-8 border rounded-md">
          <p>Không có tiện nghi phòng nào để hiển thị</p>
        </div>
      ) : (
        <div className="space-y-6">
          {amenityGroups.map((group) => (
            <div key={group.id} className="space-y-3">
              <h3 className="font-medium text-lg">{group.name}</h3>
              {group.description && (
                <p className="text-sm text-gray-500 mb-2">{group.description}</p>
              )}
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                {group.amenities.map((amenity) => (
                  <div key={amenity.id} className="flex items-start space-x-2">
                    <Checkbox
                      id={`unit-amenity-${amenity.id}`}
                      checked={unitAmenities.includes(amenity.id)}
                      onCheckedChange={(checked) =>
                        handleAmenityChange(amenity.id, checked === true)
                      }
                    />
                    <div className="grid gap-1.5 leading-none">
                      <Label
                        htmlFor={`unit-amenity-${amenity.id}`}
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
      )}

      <div className="p-4 bg-blue-50 rounded-md border border-blue-200 mt-6">
        <p className="text-sm text-blue-700">
          <strong>Lời khuyên:</strong> Các tiện nghi như Wi-Fi miễn phí, điều hòa, TV và phòng tắm riêng
          là những tiện nghi cơ bản mà khách hàng thường mong đợi. Cung cấp những tiện nghi bổ sung như
          máy sấy tóc, minibar sẽ giúp tăng sự hài lòng của khách.
        </p>
      </div>
    </div>
  );
}