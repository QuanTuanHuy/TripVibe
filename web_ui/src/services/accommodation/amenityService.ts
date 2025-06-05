import { ListDataResponse } from '@/types/common';
import { AmenityGroup, AmenityGroupParams } from '@/types/accommodation';
import apiClient from '../api.client';
import accommodationService from './accommodationService';


const ACCOMMODATION_PATH = '/accommodation_service/api/public/v1';

const amenityService = {
    getAmenityGroups: async (params?: AmenityGroupParams): Promise<ListDataResponse<AmenityGroup>> => {
        const queryParams = new URLSearchParams();

        if (params?.page !== undefined) queryParams.append('page', params.page.toString());
        if (params?.pageSize !== undefined) queryParams.append('pageSize', params.pageSize.toString());
        if (params?.sortBy) queryParams.append('sortBy', params.sortBy);
        if (params?.sortType) queryParams.append('sortType', params.sortType);
        if (params?.type) queryParams.append('type', params.type);
        if (params?.isPopular !== undefined) queryParams.append('isPopular', params.isPopular.toString());
        if (params?.ids && params.ids.length > 0) {
            queryParams.append('ids', params.ids.join(','));
        }

        const queryString = queryParams.toString() ? `?${queryParams.toString()}` : '';
        return apiClient.get<ListDataResponse<AmenityGroup>>(`${ACCOMMODATION_PATH}/amenity_groups${queryString}`);
    },

    getAmenityGroupsByType: async (type: string): Promise<ListDataResponse<AmenityGroup>> => {
        return amenityService.getAmenityGroups({ type });
    },

    getAmenityGroupById: async (id: number): Promise<AmenityGroup> => {
        return apiClient.get<AmenityGroup>(`${ACCOMMODATION_PATH}/amenity_groups/${id}`);
    },


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
