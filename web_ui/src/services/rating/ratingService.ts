import apiClient from '../api.client';
import { CreateRatingDto, CreateRatingHelpfulnessDto, CreateRatingResponseDto, Rating, RatingHelpfulness, RatingParams, RatingResponse, RatingSummary } from '../../types/rating/rating.types';
import { ListDataResponseV2 } from '@/types/common';
import { bookingService } from '../booking';
import accommodationService from '../accommodation/accommodationService';
import { epochToDate } from '@/lib/datetime.utils';

// Path đến rating service thông qua API Gateway
const RATING_PATH = '/rating_service/api/public/v1';
const RATING_API = `${RATING_PATH}/ratings`;
const RATING_RESPONSE_API = `${RATING_PATH}/rating_responses`;
const RATING_SUMMARY_API = `${RATING_PATH}/rating_summaries`;
const USER_RATINGS_API = `${RATING_PATH}/ratings/me`;

export interface ListRatingResponse<T> {
    totalPage: number;
    totalRecord: number;
    pageSize: number;
    nextPage: number | null;
    previousPage: number | null;
    data: T[];
}

export interface PendingReview {
    accommodationId: number;
    accommodationName: string;
    unitId: number;
    unitName: string;
    bookingId: number;
    checkIn: string;
    checkOut: string;
    accommodationImageUrl: string;
    location: string;
}

export const ratingService = {
    getAllRatings: async (params: RatingParams): Promise<ListDataResponseV2<Rating>> => {
        return apiClient.get<ListDataResponseV2<Rating>>(RATING_API, { params });
    },

    // Get ratings for the current user with pagination
    getUserRatings: async (params: { page?: number; pageSize?: number }): Promise<ListDataResponseV2<Rating>> => {
        return apiClient.get<ListDataResponseV2<Rating>>(USER_RATINGS_API, { params });
    },

    getRatingById: async (id: number): Promise<Rating> => {
        return apiClient.get<Rating>(`${RATING_API}/${id}`);
    },

    createRating: async (ratingData: CreateRatingDto): Promise<Rating> => {
        return apiClient.post<Rating>(RATING_API, ratingData);
    },

    updateRating: async (id: number, ratingData: Partial<CreateRatingDto>): Promise<Rating> => {
        return apiClient.put<Rating>(`${RATING_API}/${id}`, ratingData);
    },

    deleteRating: async (id: number): Promise<void> => {
        return apiClient.delete(`${RATING_API}/${id}`);
    },

    createRatingHelpfulness: async (ratingId: number, helpfulnessData: CreateRatingHelpfulnessDto): Promise<RatingHelpfulness> => {
        return apiClient.post<RatingHelpfulness>(`${RATING_API}/${ratingId}/helpfulness`, helpfulnessData);
    },

    // Get pending reviews (stays that haven't been reviewed)
    getPendingReviews: async (params: { page?: number; pageSize?: number }): Promise<ListRatingResponse<PendingReview>> => {
        const completedBookings = await bookingService.getBookings({ status: 'COMPLETED', ...params });
        if (!completedBookings || !completedBookings.data) {
            return {
                totalPage: 0,
                totalRecord: 0,
                pageSize: params.pageSize || 10,
                nextPage: null,
                previousPage: null,
                data: []
            };
        }

        const accommodationIds = completedBookings.data.map(booking => booking.accommodationId);
        const uniqueAccommodationIds = Array.from(new Set(accommodationIds));
        const accommodations = await accommodationService.getAccommodationThumbnails(uniqueAccommodationIds);
        const accommodationMap = new Map(accommodations.map(acc => [acc.id, acc]));

        const pendingReviews: PendingReview[] = completedBookings.data.map(booking => {
            const accommodation = accommodationMap.get(booking.accommodationId);
            return {
                accommodationId: booking.accommodationId,
                accommodationName: accommodation?.name || 'Unknown Accommodation',
                unitId: booking.units?.[0]?.id || 0,
                unitName: accommodation?.units?.find(unit => unit.id === booking.units?.[0]?.id)?.name || 'Unknown Unit',
                bookingId: booking.id,
                checkIn: epochToDate(booking.stayFrom).toString(),
                checkOut: epochToDate(booking.stayTo).toString(),
                accommodationImageUrl: accommodation?.thumbnailUrl || '',
                location: accommodation?.location.address || 'Unknown Location'
            };
        });

        console.log('Pending Reviews:', pendingReviews);

        return {
            totalPage: completedBookings.totalPage,
            totalRecord: completedBookings.totalRecord,
            pageSize: completedBookings.pageSize,
            nextPage: completedBookings.nextPage,
            previousPage: completedBookings.previousPage,
            data: pendingReviews
        }
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
    }
};

export default ratingService;