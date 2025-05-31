import { PriceType } from "@/types/accommodation";
import apiClient from "../apiClient";

const ACCOMMODATION_PATH = '/accommodation_service/api/public/v1';

const priceTypeService = {
    getPriceTypes: async (): Promise<PriceType[]> => {
        return apiClient.get<PriceType[]>(`${ACCOMMODATION_PATH}/price_types`);
    },
};

export default priceTypeService;
