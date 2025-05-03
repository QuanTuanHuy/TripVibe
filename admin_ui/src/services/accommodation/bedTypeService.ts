import { BedType, BedTypeParams } from "@/types/accommodation";
import { ListDataResponse } from "@/types/common";
import apiClient from "../apiClient";

const ACCOMMODATION_PATH = '/accommodation_service/api/public/v1';

const bedTypeService = {
    // Lấy danh sách các loại giường
    getBedTypes: async (params?: BedTypeParams): Promise<ListDataResponse<BedType>> => {
        const queryParams = new URLSearchParams();

        if (params?.page) queryParams.append('page', params.page.toString());
        if (params?.pageSize) queryParams.append('pageSize', params.pageSize.toString());
        if (params?.name) queryParams.append('name', params.name);
        if (params?.sortBy) queryParams.append('sortBy', params.sortBy);
        if (params?.sortType) queryParams.append('sortType', params.sortType);

        const queryString = queryParams.toString() ? `?${queryParams.toString()}` : '';
        return apiClient.get<ListDataResponse<BedType>>(`${ACCOMMODATION_PATH}/bed_types${queryString}`);
    },
}

export default bedTypeService;