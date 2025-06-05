import { ListDataResponse } from '@/types/common';
import { Country, Province, CountryParams, ProvinceParams } from '@/types/location';
import apiClient from '../api.client';

const LOCATION_PATH = '/location_service/api/public/v1';

const locationService = {
    getCountries: async (params?: CountryParams): Promise<ListDataResponse<Country>> => {
        const queryParams = new URLSearchParams();

        if (params?.page !== undefined) queryParams.append('page', params.page.toString());
        if (params?.pageSize !== undefined) queryParams.append('pageSize', params.pageSize.toString());
        if (params?.name) queryParams.append('name', params.name);
        if (params?.sortBy) queryParams.append('sortBy', params.sortBy);
        if (params?.sortOrder) queryParams.append('sortOrder', params.sortOrder);

        const queryString = queryParams.toString() ? `?${queryParams.toString()}` : '';
        return apiClient.get<ListDataResponse<Country>>(`${LOCATION_PATH}/countries${queryString}`);
    },

    getCountryById: async (id: number): Promise<Country> => {
        return apiClient.get<Country>(`${LOCATION_PATH}/countries/${id}`);
    },

    getProvinces: async (params?: ProvinceParams): Promise<ListDataResponse<Province>> => {
        const queryParams = new URLSearchParams();

        if (params?.page !== undefined) queryParams.append('page', params.page.toString());
        if (params?.pageSize !== undefined) queryParams.append('pageSize', params.pageSize.toString());
        if (params?.name) queryParams.append('name', params.name);
        if (params?.code) queryParams.append('code', params.code);
        if (params?.countryId !== undefined) queryParams.append('countryId', params.countryId.toString());
        if (params?.sortBy) queryParams.append('sortBy', params.sortBy);
        if (params?.sortOrder) queryParams.append('sortOrder', params.sortOrder);

        const queryString = queryParams.toString() ? `?${queryParams.toString()}` : '';
        return apiClient.get<ListDataResponse<Province>>(`${LOCATION_PATH}/provinces${queryString}`);
    },

    getProvinceById: async (id: number): Promise<Province> => {
        return apiClient.get<Province>(`${LOCATION_PATH}/provinces/${id}`);
    },

    getProvincesByCountryId: async (countryId: number, params?: Omit<ProvinceParams, 'countryId'>): Promise<ListDataResponse<Province>> => {
        const queryParams = new URLSearchParams();

        queryParams.append('countryId', countryId.toString());
        if (params?.page !== undefined) queryParams.append('page', params.page.toString());
        if (params?.pageSize !== undefined) queryParams.append('pageSize', params.pageSize.toString());
        if (params?.name) queryParams.append('name', params.name);
        if (params?.code) queryParams.append('code', params.code);
        if (params?.sortBy) queryParams.append('sortBy', params.sortBy);
        if (params?.sortOrder) queryParams.append('sortOrder', params.sortOrder);

        const queryString = queryParams.toString() ? `?${queryParams.toString()}` : '';
        return apiClient.get<ListDataResponse<Province>>(`${LOCATION_PATH}/provinces${queryString}`);
    }
};

export default locationService;