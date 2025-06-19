import { ListDataResponse } from '@/types/common';
import apiClient from '../apiClient';
import {
  CreateAccommodationDto,
  Accommodation,
  AccommodationType,
  UnitName,
  Currency,
  Language, AccommodationParams,
  Unit,
  UnitNameParams,
  LanguageParams,
  UpdateAccAmenityDto,
  UpdateUnitAmenityDto,
  CreateUnitAmenityDto
} from '@/types/accommodation';

const ACCOMMODATION_PATH = '/accommodation_service/api/public/v1';

const accommodationService = {
  // Get accommodations owned by the current user
  getMyAccommodations: async (userId: number): Promise<Accommodation[]> => {
    return await accommodationService.getAccommodations(
      {
        hostId: userId
      }
    )
  },

  // Get units for an accommodation
  getUnitsByAccommodationId: async (accommodationId: number): Promise<Unit[]> => {
    const accommodation = await accommodationService.getAccommodationById(accommodationId);
    return accommodation.units || [];
  },

  // Lấy danh sách các loại chỗ nghỉ
  getAccommodationTypes: async (): Promise<AccommodationType[]> => {
    return apiClient.get<AccommodationType[]>(`${ACCOMMODATION_PATH}/accommodation_types`);
  },

  // Lấy danh sách tên đơn vị/phòng
  getUnitNames: async (params?: UnitNameParams): Promise<ListDataResponse<UnitName>> => {
    const queryParams = new URLSearchParams();

    if (params?.page) queryParams.append('page', params.page.toString());
    if (params?.pageSize) queryParams.append('pageSize', params.pageSize.toString());
    if (params?.sortBy) queryParams.append('sortBy', params.sortBy);
    if (params?.sortType) queryParams.append('sortType', params.sortType);
    if (params?.name) queryParams.append('name', params.name);

    const queryString = queryParams.toString() ? `?${queryParams.toString()}` : '';

    return apiClient.get<ListDataResponse<UnitName>>(`${ACCOMMODATION_PATH}/unit_names${queryString}`);
  },

  // Lấy danh sách các đơn vị tiền tệ
  getCurrencies: async (): Promise<Currency[]> => {
    return apiClient.get<Currency[]>(`${ACCOMMODATION_PATH}/currencies`);
  },

  getLanguages: async (params?: LanguageParams): Promise<ListDataResponse<Language>> => {
    const queryParams = new URLSearchParams();

    if (params?.page) queryParams.append('page', params.page.toString());
    if (params?.pageSize) queryParams.append('pageSize', params.pageSize.toString());
    if (params?.sortBy) queryParams.append('sortBy', params.sortBy);
    if (params?.sortType) queryParams.append('sortType', params.sortType);
    if (params?.name) queryParams.append('name', params.name);

    const queryString = queryParams.toString() ? `?${queryParams.toString()}` : '';

    return apiClient.get<ListDataResponse<Language>>(`${ACCOMMODATION_PATH}/languages${queryString}`);
  },

  // Tạo chỗ nghỉ mới
  createAccommodation: async (accommodationData: CreateAccommodationDto, images: File[]): Promise<Accommodation> => {
    const formData = new FormData();
    formData.append('accommodationJson', JSON.stringify(accommodationData));

    if (images && images.length) {
      for (const image of images) {
        formData.append('images', image);
      }
    }

    // Sử dụng một config đặc biệt với content-type multipart/form-data
    return apiClient.post<Accommodation>(
      `${ACCOMMODATION_PATH}/accommodations`,
      formData,
      {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      }
    );
  },

  // Lấy thông tin chi tiết chỗ nghỉ
  getAccommodationById: async (id: number): Promise<Accommodation> => {
    return apiClient.get<Accommodation>(`${ACCOMMODATION_PATH}/accommodations/${id}`);
  },

  // Lấy danh sách chỗ nghỉ
  getAccommodations: async (params: AccommodationParams): Promise<Accommodation[]> => {
    const queryParams = new URLSearchParams();

    if (params.hostId) queryParams.append('hostId', params.hostId.toString());
    if (params.ids && params.ids.length) {
      queryParams.append('ids', params.ids.join(','));
    }

    const queryString = queryParams.toString() ? `?${queryParams.toString()}` : '';
    return apiClient.get<Accommodation[]>(`${ACCOMMODATION_PATH}/accommodations${queryString}`);
  },

  // Cập nhật chỗ nghỉ
  updateAccommodation: async (id: number, accommodationData: Partial<CreateAccommodationDto>): Promise<Accommodation> => {
    return apiClient.put<Accommodation>(`${ACCOMMODATION_PATH}/accommodations/${id}`, accommodationData);
  },

  // Xóa chỗ nghỉ
  // deleteAccommodation: async (id: number): Promise<void> => {
  //   return apiClient.delete<void>(`${ACCOMMODATION_PATH}/accommodations/${id}`);
  // },

  // Tải lên hình ảnh cho chỗ nghỉ
  uploadImages: async (accommodationId: number, images: File[]): Promise<string[]> => {
    const formData = new FormData();

    if (images && images.length) {
      for (const image of images) {
        formData.append('images', image);
      }
    }

    return apiClient.post<string[]>(
      `${ACCOMMODATION_PATH}/accommodations/${accommodationId}/images`,
      formData,
      {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      }
    );
  },

  // Cập nhật hình ảnh cho phòng (unit)
  updateUnitImage: async (
    accommodationId: number,
    unitId: number,
    deleteImageIds?: number[],
    newImages?: File[]
  ): Promise<void> => {
    const formData = new FormData();

    // Thêm thông tin xóa ảnh nếu có
    if (deleteImageIds && deleteImageIds.length > 0) {
      const deleteImageDto = {
        imageIds: deleteImageIds
      };
      formData.append('requestBody', JSON.stringify(deleteImageDto));
    }

    // Thêm ảnh mới nếu có
    if (newImages && newImages.length > 0) {
      for (const image of newImages) {
        formData.append('images', image);
      }
    }

    return apiClient.post<void>(
      `${ACCOMMODATION_PATH}/accommodations/${accommodationId}/units/${unitId}/images`,
      formData,
      {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      }
    );
  },

  // Cập nhật tiện nghi cho accommodation
  updateAccommodationAmenities: async (accommodationId: number, amenityIds: number[]): Promise<void> => {
    const updateAccAmenityDto: UpdateAccAmenityDto = {
      amenityIds: amenityIds
    };

    return apiClient.post<void>(
      `${ACCOMMODATION_PATH}/accommodations/${accommodationId}/amenities`,
      updateAccAmenityDto,
      {
        headers: {
          'Content-Type': 'application/json',
        },
      }
    );
  },
  // Cập nhật tiện nghi cho unit/phòng
  updateUnitAmenities: async (
    accommodationId: number,
    unitId: number,
    newAmenityIds: number[],
    currentAmenityIds: number[] = []
  ): Promise<void> => {
    // Calculate differences
    const deletedAmenityIds = currentAmenityIds.filter(id => !newAmenityIds.includes(id));
    const addedAmenityIds = newAmenityIds.filter(id => !currentAmenityIds.includes(id));

    // Create new amenities array (for now without fee and needToReserve)
    const newAmenities: CreateUnitAmenityDto[] = addedAmenityIds.map(amenityId => ({
      amenityId,
      fee: undefined,
      needToReserve: undefined
    }));

    const updateUnitAmenityDto: UpdateUnitAmenityDto = {
      newAmenities,
      deletedAmenityIds
    };

    console.log('Updating unit amenities:', {
      accommodationId,
      unitId,
      currentAmenityIds,
      newAmenityIds,
      addedAmenityIds,
      deletedAmenityIds,
      updateDto: updateUnitAmenityDto
    });

    return apiClient.post<void>(
      `${ACCOMMODATION_PATH}/accommodations/${accommodationId}/units/${unitId}/amenities`,
      updateUnitAmenityDto,
      {
        headers: {
          'Content-Type': 'application/json',
        },
      }
    );
  }
};

export default accommodationService;