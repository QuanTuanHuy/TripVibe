"use client";

import { useEffect, useState } from 'react';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Button } from '@/components/ui/button';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { BedType, CreateAccommodationDto, CreateBedDto } from '@/types/accommodation';
import { PlusCircle, MinusCircle, Trash2, AlertCircle } from 'lucide-react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { bedTypeService } from '@/services';
import { Alert, AlertDescription } from '@/components/ui/alert';

interface BedroomsFormProps {
  formData: CreateAccommodationDto;
  updateFormData: (data: Partial<CreateAccommodationDto>) => void;
}


export default function BedroomsForm({ formData, updateFormData }: BedroomsFormProps) {
  const [bedTypes, setBedTypes] = useState<BedType[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchBedTypes = async () => {
      try {
        setLoading(true);
        setError(null);

        const response = await bedTypeService.getBedTypes({ page: 0, pageSize: 100 });
        setBedTypes(response.data || []);
      } catch (error) {
        console.error('Error fetching bed types:', error);
        setError('Không thể tải loại giường. Vui lòng thử lại sau.');
      } finally {
        setLoading(false);
      }
    }

    fetchBedTypes();
  }, []);

  const addNewBedroom = () => {
    // Tạo danh sách phòng ngủ mới nếu chưa có
    const currentBedrooms = formData.unit.bedrooms || [];

    // Thêm phòng ngủ mới với một giường mặc định
    const updatedBedrooms = [
      ...currentBedrooms,
      {
        quantity: 1,  // Số lượng phòng ngủ cùng loại
        beds: [
          // Thêm một giường đơn mặc định cho phòng mới
          { bedTypeId: bedTypes[0].id, quantity: 1 }
        ]
      }
    ];

    updateFormData({
      unit: {
        ...formData.unit,
        bedrooms: updatedBedrooms
      }
    });
  };

  const removeBedroom = (bedroomIndex: number) => {
    const updatedBedrooms = [...(formData.unit.bedrooms || [])];
    updatedBedrooms.splice(bedroomIndex, 1);

    updateFormData({
      unit: {
        ...formData.unit,
        bedrooms: updatedBedrooms
      }
    });
  };

  const updateBedroomQuantity = (bedroomIndex: number, value: number) => {
    const updatedBedrooms = [...(formData.unit.bedrooms || [])];

    updatedBedrooms[bedroomIndex] = {
      ...updatedBedrooms[bedroomIndex],
      quantity: value
    };

    updateFormData({
      unit: {
        ...formData.unit,
        bedrooms: updatedBedrooms
      }
    });
  };

  // Thêm giường mới vào phòng
  const addBed = (bedroomIndex: number) => {
    const updatedBedrooms = [...(formData.unit.bedrooms || [])];
    const currentBeds = updatedBedrooms[bedroomIndex].beds || [];

    // Tìm loại giường chưa được sử dụng trong phòng này
    const currentBedTypeIds = currentBeds.map(bed => bed.bedTypeId);
    const availableBedType = bedTypes.find(type => !currentBedTypeIds.includes(type.id))?.id || bedTypes[0].id;

    // Thêm giường mới
    updatedBedrooms[bedroomIndex] = {
      ...updatedBedrooms[bedroomIndex],
      beds: [
        ...currentBeds,
        { bedTypeId: availableBedType, quantity: 1 }
      ]
    };

    updateFormData({
      unit: {
        ...formData.unit,
        bedrooms: updatedBedrooms
      }
    });
  };

  const removeBed = (bedroomIndex: number, bedIndex: number) => {
    const updatedBedrooms = [...(formData.unit.bedrooms || [])];
    const currentBeds = [...(updatedBedrooms[bedroomIndex].beds || [])];

    // Xóa giường
    currentBeds.splice(bedIndex, 1);

    updatedBedrooms[bedroomIndex] = {
      ...updatedBedrooms[bedroomIndex],
      beds: currentBeds
    };

    updateFormData({
      unit: {
        ...formData.unit,
        bedrooms: updatedBedrooms
      }
    });
  };

  const updateBed = (bedroomIndex: number, bedIndex: number, field: keyof CreateBedDto, value: number) => {
    const updatedBedrooms = [...(formData.unit.bedrooms || [])];
    const currentBeds = [...(updatedBedrooms[bedroomIndex].beds || [])];

    // Cập nhật thông tin giường
    currentBeds[bedIndex] = {
      ...currentBeds[bedIndex],
      [field]: value
    };

    updatedBedrooms[bedroomIndex] = {
      ...updatedBedrooms[bedroomIndex],
      beds: currentBeds
    };

    updateFormData({
      unit: {
        ...formData.unit,
        bedrooms: updatedBedrooms
      }
    });
  };

  // Tính tổng số người có thể ngủ dựa trên số lượng và loại giường
  const calculateTotalCapacity = () => {
    let totalCapacity = 0;

    if (!formData.unit.bedrooms?.length) return 0;

    formData.unit.bedrooms.forEach(bedroom => {
      let bedroomCapacity = 0;

      // Tính số lượng người cho mỗi loại giường trong phòng ngủ
      bedroom.beds?.forEach(bed => {
        const bedType = bedTypes.find(type => type.id === bed.bedTypeId);
        if (bedType) {
          bedroomCapacity += bedType.size * bed.quantity;
        }
      });

      totalCapacity += bedroomCapacity * bedroom.quantity;
    });

    return totalCapacity;
  };

  const getBedTypeName = (bedTypeId: number): string => {
    return bedTypes.find(type => type.id === bedTypeId)?.name || 'Không xác định';
  };

  const totalCapacity = calculateTotalCapacity();

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
      <h2 className="text-xl font-semibold">Thông tin phòng ngủ và giường</h2>
      <p className="text-gray-600 mb-4">
        Thêm thông tin chi tiết về các giường trong phòng/đơn vị của bạn. Thông tin này sẽ giúp khách hàng biết họ có thể ngủ ở đâu.
      </p>

      <div className="flex items-center justify-between">
        <div>
          <h3 className="font-medium">Danh sách phòng ngủ</h3>
          <p className="text-sm text-gray-500">
            Tổng số người có thể ngủ: <Badge variant="outline">{totalCapacity} người</Badge>
          </p>
        </div>
        <Button
          variant="outline"
          size="sm"
          onClick={addNewBedroom}
          className="flex items-center gap-1"
        >
          <PlusCircle className="h-4 w-4" />
          <span>Thêm phòng ngủ</span>
        </Button>
      </div>

      {(!formData.unit.bedrooms || formData.unit.bedrooms.length === 0) && (
        <div className="flex flex-col items-center justify-center p-12 border-2 border-dashed rounded-lg">
          <p className="mb-4 text-gray-500">Chưa có thông tin phòng ngủ</p>
          <Button variant="default" onClick={addNewBedroom} className="flex items-center gap-1">
            <PlusCircle className="h-4 w-4" />
            <span>Thêm phòng ngủ mới</span>
          </Button>
        </div>
      )}

      <div className="space-y-4 mt-4">
        {formData.unit.bedrooms?.map((bedroom, bedroomIndex) => (
          <Card key={bedroomIndex}>
            <CardHeader className="flex flex-row items-center justify-between pb-2">
              <CardTitle className="text-md">
                Phòng ngủ {bedroomIndex + 1}
              </CardTitle>
              <Button
                variant="ghost"
                size="sm"
                onClick={() => removeBedroom(bedroomIndex)}
                className="h-8 w-8 p-0 text-red-500"
              >
                <Trash2 className="h-4 w-4" />
              </Button>
            </CardHeader>
            <CardContent>
              <div className="space-y-4">
                {/* Số lượng phòng ngủ cùng loại */}
                <div className="space-y-2">
                  <Label htmlFor={`bedroom-${bedroomIndex}-quantity`}>Số lượng phòng ngủ cùng loại</Label>
                  <div className="flex items-center space-x-2">
                    <Button
                      type="button"
                      variant="outline"
                      size="sm"
                      className="h-8 w-8 p-0"
                      onClick={() =>
                        bedroom.quantity > 1 && updateBedroomQuantity(bedroomIndex, bedroom.quantity - 1)
                      }
                      disabled={bedroom.quantity <= 1}
                    >
                      <MinusCircle className="h-4 w-4" />
                    </Button>
                    <Input
                      id={`bedroom-${bedroomIndex}-quantity`}
                      type="number"
                      value={bedroom.quantity}
                      onChange={(e) => updateBedroomQuantity(bedroomIndex, Number(e.target.value))}
                      min="1"
                      max="10"
                      className="w-16 text-center"
                    />
                    <Button
                      type="button"
                      variant="outline"
                      size="sm"
                      className="h-8 w-8 p-0"
                      onClick={() => updateBedroomQuantity(bedroomIndex, bedroom.quantity + 1)}
                      disabled={bedroom.quantity >= 10}
                    >
                      <PlusCircle className="h-4 w-4" />
                    </Button>
                  </div>
                </div>

                {/* Tiêu đề danh sách giường */}
                <div className="flex items-center justify-between mt-4 pt-4 border-t">
                  <h4 className="font-medium">Danh sách giường trong phòng</h4>
                  <Button
                    variant="outline"
                    size="sm"
                    onClick={() => addBed(bedroomIndex)}
                    className="flex items-center gap-1"
                    disabled={bedroom.beds?.length === bedTypes.length}
                  >
                    <PlusCircle className="h-4 w-4" />
                    <span>Thêm giường</span>
                  </Button>
                </div>

                {/* Danh sách giường */}
                {bedroom.beds?.map((bed, bedIndex) => (
                  <div key={bedIndex} className="p-3 border rounded-md bg-gray-50">
                    <div className="flex items-center justify-between mb-2">
                      <h5 className="font-medium">Giường {bedIndex + 1}</h5>
                      {bedroom.beds.length > 1 && (
                        <Button
                          variant="ghost"
                          size="sm"
                          onClick={() => removeBed(bedroomIndex, bedIndex)}
                          className="h-7 w-7 p-0 text-red-500"
                        >
                          <Trash2 className="h-4 w-4" />
                        </Button>
                      )}
                    </div>
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                      <div className="space-y-2">
                        <Label htmlFor={`bed-${bedroomIndex}-${bedIndex}-type`}>Loại giường</Label>
                        <Select
                          value={bed.bedTypeId?.toString()}
                          onValueChange={(value) => updateBed(bedroomIndex, bedIndex, 'bedTypeId', Number(value))}
                        >
                          <SelectTrigger>
                            <SelectValue placeholder="Chọn loại giường" />
                          </SelectTrigger>
                          <SelectContent>
                            {bedTypes.map((type) => (
                              <SelectItem
                                key={type.id}
                                value={type.id.toString()}
                                disabled={bedroom.beds.some(
                                  (b, i) => i !== bedIndex && b.bedTypeId === type.id
                                )}
                              >
                                {type.name} ({type.size} người)
                              </SelectItem>
                            ))}
                          </SelectContent>
                        </Select>
                      </div>
                      <div className="space-y-2">
                        <Label htmlFor={`bed-${bedroomIndex}-${bedIndex}-quantity`}>Số lượng</Label>
                        <div className="flex items-center space-x-2">
                          <Button
                            type="button"
                            variant="outline"
                            size="sm"
                            className="h-8 w-8 p-0"
                            onClick={() =>
                              bed.quantity > 1 &&
                              updateBed(bedroomIndex, bedIndex, 'quantity', bed.quantity - 1)
                            }
                            disabled={bed.quantity <= 1}
                          >
                            <MinusCircle className="h-4 w-4" />
                          </Button>
                          <Input
                            id={`bed-${bedroomIndex}-${bedIndex}-quantity`}
                            type="number"
                            value={bed.quantity}
                            onChange={(e) => updateBed(bedroomIndex, bedIndex, 'quantity', Number(e.target.value))}
                            min="1"
                            max="10"
                            className="w-16 text-center"
                          />
                          <Button
                            type="button"
                            variant="outline"
                            size="sm"
                            className="h-8 w-8 p-0"
                            onClick={() => updateBed(bedroomIndex, bedIndex, 'quantity', bed.quantity + 1)}
                            disabled={bed.quantity >= 10}
                          >
                            <PlusCircle className="h-4 w-4" />
                          </Button>
                        </div>
                      </div>
                    </div>
                    <div className="text-sm text-gray-500 mt-2">
                      <p>
                        {bed.quantity} {getBedTypeName(bed.bedTypeId)} có thể chứa{' '}
                        {bed.quantity *
                          (bedTypes.find(type => type.id === bed.bedTypeId)?.size || 1)
                        } người
                      </p>
                    </div>
                  </div>
                ))}

                {/* Tổng người cho phòng này */}
                <div className="text-right text-sm font-medium mt-2">
                  Tổng số người cho {bedroom.quantity} phòng ngủ này:{' '}
                  {bedroom.beds?.reduce((acc, bed) => {
                    const bedType = bedTypes.find(type => type.id === bed.bedTypeId);
                    return acc + (bedType?.size || 1) * bed.quantity * bedroom.quantity;
                  }, 0) || 0}{' '}
                  người
                </div>
              </div>
            </CardContent>
          </Card>
        ))}
      </div>

      <div className="p-4 bg-blue-50 rounded-md border border-blue-200 mt-4">
        <p className="text-sm text-blue-700">
          <strong>Lời khuyên:</strong> Cung cấp thông tin chính xác về số lượng và loại giường sẽ giúp khách hàng
          hiểu rõ hơn về không gian ngủ nghỉ và đưa ra quyết định đặt phòng phù hợp.
        </p>
      </div>
    </div>
  );
}