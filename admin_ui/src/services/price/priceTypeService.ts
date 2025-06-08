import { PriceType, PriceTypeParams } from "@/types/accommodation";
import apiClient from "../apiClient";
import { ListDataResponse } from "@/types/common";

const ACCOMMODATION_PATH = '/accommodation_service/api/public/v1';

const priceTypeService = {
    getPriceTypes: async (params?: PriceTypeParams): Promise<ListDataResponse<PriceType>> => {
        const queryParams = new URLSearchParams();

        if (params?.page) queryParams.append('page', params.page.toString());
        if (params?.pageSize) queryParams.append('pageSize', params.pageSize.toString());
        if (params?.sortBy) queryParams.append('sortBy', params.sortBy);
        if (params?.sortType) queryParams.append('sortType', params.sortType);
        if (params?.name) queryParams.append('name', params.name);

        const queryString = queryParams.toString() ? `?${queryParams.toString()}` : '';

        return apiClient.get<ListDataResponse<PriceType>>(`${ACCOMMODATION_PATH}/price_types${queryString}`);
    },
};

export default priceTypeService;
