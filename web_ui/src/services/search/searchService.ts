import { AccommodationThumbnail } from '@/types/accommodation';
import { SearchParams, SearchResult, PageInfo } from '@/types/search';
import apiClient from '../api.client';

const SEARCH_PATH = '/search_service/api/public/v1';

const formatDateToString = (date: Date): string => {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
};

const searchService = {
    searchAccommodations: async (params: SearchParams): Promise<SearchResult<AccommodationThumbnail>> => {
        console.log("Search params sent to backend:", params);

        const cleanParams = Object.fromEntries(
            Object.entries(params).filter(([_, value]) => value !== undefined && value !== null && value !== '')
        );

        if (cleanParams.startDate instanceof Date) {
            cleanParams.startDate = formatDateToString(cleanParams.startDate);
        }
        if (cleanParams.endDate instanceof Date) {
            cleanParams.endDate = formatDateToString(cleanParams.endDate);
        }

        console.log("Cleaned params for backend:", cleanParams);

        const response = await apiClient.post<SearchResult<AccommodationThumbnail>>(
            `${SEARCH_PATH}/accommodations/search/thumbnail`,
            cleanParams
        );
        console.log("Response from backend:", response);
        return response;
    }
};

export default searchService;
