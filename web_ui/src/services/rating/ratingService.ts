import apiClient from '../api.client';
import { CreateRatingDto, CreateRatingHelpfulnessDto, CreateRatingResponseDto, Rating, RatingHelpfulness, RatingParams, RatingResponse, RatingSummary } from '../../types/rating/rating.types';
import { ListDataResponse } from '@/types/common';

// Path đến rating service thông qua API Gateway
const RATING_PATH = '/rating_service/api/public/v1';
const RATING_API = `${RATING_PATH}/ratings`;
const RATING_RESPONSE_API = `${RATING_PATH}/rating_responses`;
const RATING_SUMMARY_API = `${RATING_PATH}/rating_summaries`;
const USER_RATINGS_API = `${RATING_PATH}/user/ratings`;

export const ratingService = {
    // Get ratings for an accommodation with pagination and filters
    getAllRatings: async (params: RatingParams): Promise<ListDataResponse<Rating>> => {
        return apiClient.get<ListDataResponse<Rating>>(RATING_API, { params });
    },

    // Get ratings for the current user with pagination
    getUserRatings: async (params: { page?: number; pageSize?: number }): Promise<ListDataResponse<Rating>> => {
        return apiClient.get<ListDataResponse<Rating>>(USER_RATINGS_API, { params });
    },

    // Get a single rating by ID
    getRatingById: async (id: number): Promise<Rating> => {
        return apiClient.get<Rating>(`${RATING_API}/${id}`);
    },

    // Create a new rating
    createRating: async (ratingData: CreateRatingDto): Promise<Rating> => {
        return apiClient.post<Rating>(RATING_API, ratingData);
    },

    // Update an existing rating
    updateRating: async (id: number, ratingData: Partial<CreateRatingDto>): Promise<Rating> => {
        return apiClient.put<Rating>(`${RATING_API}/${id}`, ratingData);
    },

    // Delete a rating
    deleteRating: async (id: number): Promise<void> => {
        return apiClient.delete(`${RATING_API}/${id}`);
    },

    // Mark a rating as helpful or unhelpful
    createRatingHelpfulness: async (ratingId: number, helpfulnessData: CreateRatingHelpfulnessDto): Promise<RatingHelpfulness> => {
        return apiClient.post<RatingHelpfulness>(`${RATING_API}/${ratingId}/helpfulness`, helpfulnessData);
    },

    // Get pending reviews (stays that haven't been reviewed)
    getPendingReviews: async (params: { page?: number; pageSize?: number }): Promise<ListDataResponse<any>> => {
        return apiClient.get<ListDataResponse<any>>(`${RATING_PATH}/user/pending-reviews`, { params });
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

export default ratingService;