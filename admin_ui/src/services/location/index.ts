import apiClient from '../apiClient';
import { Country, CountryParams, Province, ProvinceParams } from '@/types/location';
import { ListDataResponse } from '@/types/common/pagination';

/**
 * Service to interact with the location_service APIs
 * Provides methods to fetch country and province data
 */
class LocationService {
    private readonly BASE_URL = '/location_service/api/public/v1';

    /**
     * Get list of countries with pagination and filtering
     * @param params Query parameters for filtering and pagination
     * @returns Promise with paged list of countries
     */
    async getCountries(params?: CountryParams): Promise<ListDataResponse<Country>> {
        const queryParams = new URLSearchParams();

        if (params?.page) queryParams.append('page', params.page.toString());
        if (params?.pageSize) queryParams.append('pageSize', params.pageSize.toString());
        if (params?.name) queryParams.append('name', params.name);
        if (params?.sortBy) queryParams.append('sortBy', params.sortBy);
        if (params?.sortOrder) queryParams.append('sortOrder', params.sortOrder);

        const queryString = queryParams.toString() ? `?${queryParams.toString()}` : '';
        return apiClient.get<ListDataResponse<Country>>(`${this.BASE_URL}/countries${queryString}`);
    }

    /**
     * Get a country by ID
     * @param id Country ID
     * @returns Promise with country data
     */
    async getCountryById(id: number): Promise<Country> {
        return apiClient.get<Country>(`${this.BASE_URL}/countries/${id}`);
    }

    /**
     * Get list of provinces with pagination and filtering
     * @param params Query parameters for filtering and pagination
     * @returns Promise with paged list of provinces
     */
    async getProvinces(params?: ProvinceParams): Promise<ListDataResponse<Province>> {
        const queryParams = new URLSearchParams();

        if (params?.page) queryParams.append('page', params.page.toString());
        if (params?.pageSize) queryParams.append('pageSize', params.pageSize.toString());
        if (params?.name) queryParams.append('name', params.name);
        if (params?.code) queryParams.append('code', params.code);
        if (params?.countryId) queryParams.append('countryId', params.countryId.toString());
        if (params?.sortBy) queryParams.append('sortBy', params.sortBy);
        if (params?.sortOrder) queryParams.append('sortOrder', params.sortOrder);

        const queryString = queryParams.toString() ? `?${queryParams.toString()}` : '';
        return apiClient.get<ListDataResponse<Province>>(`${this.BASE_URL}/provinces${queryString}`);
    }

    /**
     * Get a province by ID
     * @param id Province ID
     * @returns Promise with province data
     */
    async getProvinceById(id: number): Promise<Province> {
        return apiClient.get<Province>(`${this.BASE_URL}/provinces/${id}`);
    }

    /**
     * Get list of provinces by country ID
     * @param countryId Country ID
     * @param params Query parameters for filtering and pagination
     * @returns Promise with paged list of provinces
     */
    async getProvincesByCountryId(countryId: number, params?: Omit<ProvinceParams, 'countryId'>): Promise<ListDataResponse<Province>> {
        const queryParams = new URLSearchParams();

        if (countryId) queryParams.append('countryId', countryId.toString());
        if (params?.page) queryParams.append('page', params.page.toString());
        if (params?.pageSize) queryParams.append('pageSize', params.pageSize.toString());
        if (params?.name) queryParams.append('name', params.name);
        if (params?.code) queryParams.append('code', params.code);
        if (params?.sortBy) queryParams.append('sortBy', params.sortBy);
        if (params?.sortOrder) queryParams.append('sortOrder', params.sortOrder);

        const queryString = queryParams.toString() ? `?${queryParams.toString()}` : '';
        return apiClient.get<ListDataResponse<Province>>(`${this.BASE_URL}/provinces${queryString}`);
    }
}

// Export a singleton instance
export const locationService = new LocationService();
export default locationService;