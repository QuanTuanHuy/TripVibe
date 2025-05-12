import { useState } from 'react';
import {
    CreateRatingDto,
    CreateRatingHelpfulnessDto,
    CreateRatingResponseDto,
    Rating,
    RatingParams,
    RatingResponse,
    RatingSummary
} from '../types/rating/rating.types';
import RatingService from '../services/rating/ratingService';
import { PageInfo } from '@/types/common';

interface RatingState {
    ratings: Rating[];
    ratingSummaries: RatingSummary[];
    ratingResponses: RatingResponse[];
    pageInfo: PageInfo | null;
    isLoading: boolean;
    error: Error | null;
}

export const useRating = () => {
    const [state, setState] = useState<RatingState>({
        ratings: [],
        ratingSummaries: [],
        ratingResponses: [],
        pageInfo: null,
        isLoading: false,
        error: null
    });

    const resetError = () => {
        setState(prevState => ({ ...prevState, error: null }));
    };

    // Fetch ratings with filters and pagination
    const fetchRatings = async (params: RatingParams) => {
        setState(prevState => ({ ...prevState, isLoading: true, error: null }));
        try {
            const response = await RatingService.getAllRatings(params);
            setState(prevState => ({
                ...prevState,
                ratings: response.data,
                pageInfo: response.pageInfo,
                isLoading: false
            }));
            return response;
        } catch (error) {
            setState(prevState => ({
                ...prevState,
                isLoading: false,
                error: error instanceof Error ? error : new Error('An unknown error occurred')
            }));
            throw error;
        }
    };

    // Submit a new rating
    const submitRating = async (ratingData: CreateRatingDto) => {
        setState(prevState => ({ ...prevState, isLoading: true, error: null }));
        try {
            const response = await RatingService.createRating(ratingData);
            setState(prevState => ({
                ...prevState,
                isLoading: false
            }));
            return response;
        } catch (error) {
            setState(prevState => ({
                ...prevState,
                isLoading: false,
                error: error instanceof Error ? error : new Error('Failed to submit rating')
            }));
            throw error;
        }
    };

    // Mark a rating as helpful or not helpful
    const markRatingHelpfulness = async (ratingId: number, isHelpful: boolean) => {
        setState(prevState => ({ ...prevState, isLoading: true, error: null }));
        try {
            const helpfulnessData: CreateRatingHelpfulnessDto = {
                ratingId,
                isHelpful
            };
            const response = await RatingService.createRatingHelpfulness(ratingId, helpfulnessData);
            setState(prevState => ({ ...prevState, isLoading: false }));
            return response;
        } catch (error) {
            setState(prevState => ({
                ...prevState,
                isLoading: false,
                error: error instanceof Error ? error : new Error('Failed to update rating helpfulness')
            }));
            throw error;
        }
    };

    // Fetch rating responses for a set of ratings
    const fetchRatingResponses = async (ratingIds: number[]) => {
        setState(prevState => ({ ...prevState, isLoading: true, error: null }));
        try {
            const responses = await RatingService.getRatingResponses(ratingIds);
            setState(prevState => ({
                ...prevState,
                ratingResponses: responses,
                isLoading: false
            }));
            return responses;
        } catch (error) {
            setState(prevState => ({
                ...prevState,
                isLoading: false,
                error: error instanceof Error ? error : new Error('Failed to fetch rating responses')
            }));
            throw error;
        }
    };

    // Submit a response to a rating (for accommodation owners)
    const submitRatingResponse = async (responseData: CreateRatingResponseDto) => {
        setState(prevState => ({ ...prevState, isLoading: true, error: null }));
        try {
            const response = await RatingService.createRatingResponse(responseData);
            setState(prevState => ({ ...prevState, isLoading: false }));
            return response;
        } catch (error) {
            setState(prevState => ({
                ...prevState,
                isLoading: false,
                error: error instanceof Error ? error : new Error('Failed to submit rating response')
            }));
            throw error;
        }
    };

    // Fetch rating summaries for multiple accommodations
    const fetchRatingSummaries = async (accommodationIds: number[]) => {
        setState(prevState => ({ ...prevState, isLoading: true, error: null }));
        try {
            const summaries = await RatingService.getRatingSummaries(accommodationIds);
            setState(prevState => ({
                ...prevState,
                ratingSummaries: summaries,
                isLoading: false
            }));
            return summaries;
        } catch (error) {
            setState(prevState => ({
                ...prevState,
                isLoading: false,
                error: error instanceof Error ? error : new Error('Failed to fetch rating summaries')
            }));
            throw error;
        }
    };

    return {
        ...state,
        fetchRatings,
        submitRating,
        markRatingHelpfulness,
        fetchRatingResponses,
        submitRatingResponse,
        fetchRatingSummaries,
        resetError
    };
};

export default useRating;