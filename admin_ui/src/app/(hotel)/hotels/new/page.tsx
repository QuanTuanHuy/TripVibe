"use client";

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { Card } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Progress } from '@/components/ui/progress';
import BasicInfoForm from '@/components/properties/create/BasicInfoForm';
import LocationForm from '@/components/properties/create/LocationForm';
import AmenitiesForm from '@/components/properties/create/AmenitiesForm';
import CheckInOutForm from '@/components/properties/create/CheckInOutForm';
import UnitDetailsForm from '@/components/properties/create/UnitDetailsForm';
import BedroomsForm from '@/components/properties/create/BedroomsForm';
import UnitAmenitiesForm from '@/components/properties/create/UnitAmenitiesForm';
import PricingForm from '@/components/properties/create/PricingForm';
import ImagesUploadForm from '@/components/properties/create/ImagesUploadForm';
import LanguagesForm from '@/components/properties/create/LanguagesForm';
import ReviewForm from '@/components/properties/create/ReviewForm';
import { CreateAccommodationDto } from '@/types/accommodation';
import { accommodationService } from '@/services';
import { toast } from "sonner"
// import { useAuth } from '@/context/AuthContext';

const steps = [
    { id: 0, name: 'Thông tin cơ bản' },
    { id: 1, name: 'Vị trí' },
    { id: 2, name: 'Tiện nghi chỗ nghỉ' },
    { id: 3, name: 'Thời gian check-in/out' },
    { id: 4, name: 'Thông tin phòng' },
    { id: 5, name: 'Chi tiết phòng ngủ' },
    { id: 6, name: 'Tiện nghi phòng' },
    { id: 7, name: 'Giá cả' },
    { id: 8, name: 'Hình ảnh' },
    { id: 9, name: 'Ngôn ngữ phục vụ' },
    { id: 10, name: 'Xác nhận' }
];

export default function NewHotelPage() {
    // const { isAuthenticated } = useAuth();
    const [currentStep, setCurrentStep] = useState(0);
    const [formData, setFormData] = useState<CreateAccommodationDto>({
        typeId: 0,
        currencyId: 0,
        name: '',
        description: '',
        checkInTimeFrom: 14, // 2PM default
        checkInTimeTo: 22, // 10PM default
        checkOutTimeFrom: 0, // 12AM default
        checkOutTimeTo: 12, // 12PM default
        unit: {
            unitNameId: 0,
            description: '',
            pricePerNight: 0,
            maxAdults: 1,
            maxChildren: 0,
            useSharedBathroom: false,
            quantity: 1,
            amenityIds: [],
            priceTypes: [],
            priceGroups: [],
            bedrooms: []
        },
        amenityIds: [],
        languageIds: [],
        location: {
            countryId: 0,
            provinceId: 0,
            detailAddress: '',
            latitude: 0,
            longitude: 0
        }
    });
    const [images, setImages] = useState<File[]>([]);
    const [isSubmitting, setIsSubmitting] = useState(false);

    const router = useRouter();

    // useEffect(() => {
    //     if (!isAuthenticated) {
    //         router.push('/login');
    //     }
    // }, [isAuthenticated, router]);

    const updateFormData = (data: Partial<CreateAccommodationDto>) => {
        setFormData(prev => ({ ...prev, ...data }));
    };

    const handleNext = () => {
        // Thêm validation ở đây nếu cần
        setCurrentStep(prev => Math.min(prev + 1, steps.length - 1));
        window.scrollTo(0, 0);
    };

    const handlePrevious = () => {
        setCurrentStep(prev => Math.max(prev - 1, 0));
        window.scrollTo(0, 0);
    };

    const handleSubmit = async () => {
        try {
            setIsSubmitting(true);

            // Gọi API để tạo chỗ nghỉ mới
            const result = await accommodationService.createAccommodation(formData, images);

            toast("Chỗ nghỉ đã được tạo thành công");

            router.push(`/hotels/manage/${result.id}`);
        } catch (error) {
            console.error('Error creating accommodation:', error);
            toast("Đã có lỗi xảy ra khi tạo chỗ nghỉ. Vui lòng thử lại.");
        } finally {
            setIsSubmitting(false);
        }
    };

    const renderStep = () => {
        switch (currentStep) {
            case 0:
                return <BasicInfoForm formData={formData} updateFormData={updateFormData} />;
            case 1:
                return <LocationForm formData={formData} updateFormData={updateFormData} />;
            case 2:
                return <AmenitiesForm formData={formData} updateFormData={updateFormData} />;
            case 3:
                return <CheckInOutForm formData={formData} updateFormData={updateFormData} />;
            case 4:
                return <UnitDetailsForm formData={formData} updateFormData={updateFormData} />;
            case 5:
                return <BedroomsForm formData={formData} updateFormData={updateFormData} />;
            case 6:
                return <UnitAmenitiesForm formData={formData} updateFormData={updateFormData} />;
            case 7:
                return <PricingForm formData={formData} updateFormData={updateFormData} />;
            case 8:
                return <ImagesUploadForm images={images} setImages={setImages} />;
            case 9:
                return <LanguagesForm formData={formData} updateFormData={updateFormData} />;
            case 10:
                return <ReviewForm formData={formData} images={images} />;
            default:
                return null;
        }
    };

    return (
        <div className="container max-w-5xl mx-auto py-8 px-4">
            <h1 className="text-2xl font-bold mb-6">Đăng ký chỗ nghỉ mới</h1>

            <div className="mb-8">
                <Progress value={((currentStep + 1) / steps.length) * 100} className="h-2" />
                <div className="flex justify-between mt-2 text-sm text-gray-500">
                    <span>Bước {currentStep + 1}/{steps.length}</span>
                    <span>{steps[currentStep].name}</span>
                </div>
            </div>

            <Card className="p-6 mb-6">
                {renderStep()}
            </Card>

            <div className="flex justify-between mt-6">
                <Button
                    variant="outline"
                    onClick={handlePrevious}
                    disabled={currentStep === 0 || isSubmitting}
                >
                    Quay lại
                </Button>

                {currentStep < steps.length - 1 ? (
                    <Button onClick={handleNext} disabled={isSubmitting}>Tiếp theo</Button>
                ) : (
                    <Button
                        onClick={handleSubmit}
                        className="bg-green-600 hover:bg-green-700"
                        disabled={isSubmitting}
                    >
                        {isSubmitting ? 'Đang xử lý...' : 'Tạo chỗ nghỉ'}
                    </Button>
                )}
            </div>
        </div>
    );
}