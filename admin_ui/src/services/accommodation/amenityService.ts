import { ListDataResponse, ListDataResponseV2 } from '@/types/common';
import apiClient from '../apiClient';
import { AmenityGroup, AmenityGroupParams } from '@/types/accommodation';
import accommodationService from './accommodationService';

// Path to accommodation service through API Gateway
const ACCOMMODATION_PATH = '/accommodation_service/api/public/v1';

const amenityService = {
  // Get all amenity groups with optional filtering
  getAmenityGroups: async (params?: AmenityGroupParams): Promise<ListDataResponseV2<AmenityGroup>> => {
    const queryParams = new URLSearchParams();

    if (params?.page !== undefined) queryParams.append('page', params.page.toString());
    if (params?.pageSize !== undefined) queryParams.append('pageSize', params.pageSize.toString());
    if (params?.sortBy) queryParams.append('sortBy', params.sortBy);
    if (params?.sortType) queryParams.append('sortType', params.sortType);
    if (params?.type) queryParams.append('type', params.type);
    if (params?.isPopular !== undefined && params.isPopular !== null) queryParams.append('isPopular', params.isPopular.toString());

    const queryString = queryParams.toString() ? `?${queryParams.toString()}` : '';
    return apiClient.get<ListDataResponseV2<AmenityGroup>>(`${ACCOMMODATION_PATH}/amenity_groups${queryString}`);
  },

  // Get amenity groups filtered by type (e.g., UNIT, BATHROOM)
  getAmenityGroupsByType: async (type: string): Promise<ListDataResponseV2<AmenityGroup>> => {
    return amenityService.getAmenityGroups({ type });
  },
  // Get a specific amenity group by ID
  getAmenityGroupById: async (id: number): Promise<AmenityGroup> => {
    return apiClient.get<AmenityGroup>(`${ACCOMMODATION_PATH}/amenity_groups/${id}`);
  },

  // Get unit amenities (if needed)
  getUnitAmenities: async (accommodationId: number, unitId: number): Promise<number[]> => {
    const accommodation = await accommodationService.getAccommodationById(accommodationId);
    const unit = accommodation.units?.find(u => u.id === unitId);
    if (!unit) {
      throw new Error(`Unit with ID ${unitId} not found in accommodation ${accommodationId}`);
    }
    return unit.amenities?.map(a => a.amenity?.id || 0) || [];
  }
};

export default amenityService;
