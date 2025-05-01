import apiClient from '../apiClient';
import { CreateRatingDto, CreateRatingHelpfulnessDto, CreateRatingResponseDto, Rating, RatingHelpfulness, RatingParams, RatingResponse, RatingSummary } from '../../types/rating/rating.types';
import { ListDataResponse } from '@/types/common';

// Path đến rating service thông qua API Gateway
const RATING_PATH = '/rating_service/api/public/v1';
const RATING_API = `${RATING_PATH}/ratings`;
const RATING_RESPONSE_API = `${RATING_PATH}/rating_responses`;
const RATING_SUMMARY_API = `${RATING_PATH}/rating_summaries`;

export const RatingService = {
    // Get ratings for an accommodation with pagination and filters
    getAllRatings: async (params: RatingParams): Promise<ListDataResponse<Rating>> => {
        return apiClient.get<ListDataResponse<Rating>>(RATING_API, { params });
    },

    // Create a new rating
    createRating: async (ratingData: CreateRatingDto): Promise<Rating> => {
        return apiClient.post<Rating>(RATING_API, ratingData);
    },

    // Mark a rating as helpful or unhelpful
    createRatingHelpfulness: async (ratingId: number, helpfulnessData: CreateRatingHelpfulnessDto): Promise<RatingHelpfulness> => {
        return apiClient.post<RatingHelpfulness>(`${RATING_API}/${ratingId}/helpfulness`, helpfulnessData);
    },

    // Get rating responses for specific ratings
    getRatingResponses: async (ratingIds: number[]): Promise<RatingResponse[]> => {
        return apiClient.get<RatingResponse[]>(RATING_RESPONSE_API, {
            params: { ratingIds: ratingIds.join(',') }
        });
    },

    // Create a response to a rating (typically by accommodation owner)
    createRatingResponse: async (responseData: CreateRatingResponseDto): Promise<RatingResponse> => {
        return apiClient.post<RatingResponse>(RATING_RESPONSE_API, responseData);
    },

    // Get rating summaries for multiple accommodations
    getRatingSummaries: async (accommodationIds: number[]): Promise<RatingSummary[]> => {
        return apiClient.get<RatingSummary[]>(RATING_SUMMARY_API, {
            params: { accommodationIds: accommodationIds.join(',') }
        });
    }
};

export default RatingService;