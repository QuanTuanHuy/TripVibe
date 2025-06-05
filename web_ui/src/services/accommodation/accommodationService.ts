import { ListDataResponse } from '@/types/common';
import {
    Accommodation,
    AccommodationType,
    UnitName,
    Currency,
    Language,
    Unit,
    AccommodationThumbnail
} from '@/types/accommodation';
import apiClient from '../api.client';

const ACCOMMODATION_PATH = '/accommodation_service/api/public/v1';

const accommodationService = {

    getUnitsByAccommodationId: async (accommodationId: number): Promise<Unit[]> => {
        const accommodation = await accommodationService.getAccommodationById(accommodationId);
        return accommodation.units || [];
    },

    getAccommodationTypes: async (): Promise<AccommodationType[]> => {
        return apiClient.get<AccommodationType[]>(`${ACCOMMODATION_PATH}/accommodation_types`);
    },

    getUnitNames: async (): Promise<ListDataResponse<UnitName>> => {
        return apiClient.get<ListDataResponse<UnitName>>(`${ACCOMMODATION_PATH}/unit_names`);
    },

    getCurrencies: async (): Promise<Currency[]> => {
        return apiClient.get<Currency[]>(`${ACCOMMODATION_PATH}/currencies`);
    },

    getLanguages: async (): Promise<ListDataResponse<Language>> => {
        return apiClient.get<ListDataResponse<Language>>(`${ACCOMMODATION_PATH}/languages`);
    },

    getAccommodationById: async (id: number): Promise<Accommodation> => {
        return apiClient.get<Accommodation>(`${ACCOMMODATION_PATH}/accommodations/${id}`);
    },

    getAccommodationThumbnails: async (ids: number[]): Promise<AccommodationThumbnail[]> => {
        const queryString = ids.map(id => `ids=${id}`).join('&');
        return apiClient.get<AccommodationThumbnail[]>(`${ACCOMMODATION_PATH}/accommodations/thumbnails?${queryString}`);
    }
};

export default accommodationService;