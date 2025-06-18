import { CreateRatingHelpfulnessDto, CreateRatingResponseDto, Rating, RatingHelpfulness, RatingParams, RatingResponse, RatingSummary, RatingStatistic } from "@/types/rating";
import apiClient from "../apiClient";
import { ListDataResponseV2 } from "@/types/common";

const RATING_PATH = '/rating_service/api/public/v1';
const RATING_API = `${RATING_PATH}/ratings`;
const RATING_RESPONSE_API = `${RATING_PATH}/rating_responses`;
const RATING_SUMMARY_API = `${RATING_PATH}/rating_summaries`;
const RATING_STATISTIC_API = `${RATING_PATH}/statistics`;

export const ratingService = {
    getAllRatings: async (params: RatingParams): Promise<ListDataResponseV2<Rating>> => {
        return apiClient.get<ListDataResponseV2<Rating>>(RATING_API, { params });
    },

    getRatingById: async (id: number): Promise<Rating> => {
        return apiClient.get<Rating>(`${RATING_API}/${id}`);
    },

    createRatingHelpfulness: async (ratingId: number, helpfulnessData: CreateRatingHelpfulnessDto): Promise<RatingHelpfulness> => {
        return apiClient.post<RatingHelpfulness>(`${RATING_API}/${ratingId}/helpfulness`, helpfulnessData);
    },

    // Create rating response (for accommodation owners)
    createRatingResponse: async (responseData: CreateRatingResponseDto): Promise<RatingResponse> => {
        return apiClient.post<RatingResponse>(RATING_RESPONSE_API, responseData);
    },

    // Get rating responses for specific ratings
    getRatingResponses: async (ratingIds: number[]): Promise<RatingResponse[]> => {
        return apiClient.get<RatingResponse[]>(RATING_RESPONSE_API, {
            params: { ratingIds: ratingIds.join(',') }
        });
    },

    // Get rating summaries for multiple accommodations
    getRatingSummaries: async (accommodationIds: number[]): Promise<RatingSummary[]> => {
        return apiClient.get<RatingSummary[]>(RATING_SUMMARY_API, {
            params: { accommodationIds: accommodationIds.join(',') }
        });
    },

    // Get rating statistic for a specific accommodation
    getRatingStatistic: async (accommodationId: number): Promise<RatingStatistic> => {
        return apiClient.get<RatingStatistic>(RATING_STATISTIC_API, {
            params: { accommodationId }
        });
    }
};

export default ratingService;