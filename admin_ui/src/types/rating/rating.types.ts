export interface RatingParams {
    page?: number;
    pageSize?: number;
    sortBy?: string;
    sortType?: 'asc' | 'desc';
    unitId?: number;
    accommodationId: number;
    minValue?: number;
    maxValue?: number;
    languageId?: number;
    createdFrom?: number;
    createdTo?: number;
}

export interface Rating {
    id: number;
    value: number;
    comment: string;
    languageId: number;
    createdAt: number;
    unit?: Unit;
    user?: UserProfile;
    numberOfHelpful?: number;
    numberOfUnhelpful?: number;
    ratingDetails?: Record<string, number>;
    ratingResponse?: RatingResponse;
}

export interface RatingResponse {
    id: number;
    ratingId: number;
    content: string;
    createdAt: number;
}

interface Unit {
    id: number;
    name: string;
    accommodationId: number;
    quantity: number | null;
}

interface UserProfile {
    userId: number;
    name: string | null;
    email: string;
    countryId?: number;
    countryName?: string;
    avatarUrl?: string;
}

export interface RatingSummary {
    accommodationId: number;
    numberOfRatings: number;
    totalRating: number;
}

export interface RatingHelpfulness {
    id: number;
    ratingId: number;
    userId: number;
    isHelpful: boolean;
}

export interface CreateRatingHelpfulnessDto {
    ratingId: number;
    isHelpful: boolean;
}

// DTO for creating rating response
export interface CreateRatingResponseDto {
    ratingId: number;
    content: string;
}

// Extended interfaces for frontend use
export interface RatingWithResponse extends Rating {
    response?: RatingResponse; // For backward compatibility with frontend components
}

// Rating summary with distribution for charts
export interface RatingSummaryWithDistribution extends RatingSummary {
    averageRating: number;
    ratingDistribution: Record<string, number>;
}

// Rating statistic from backend API
export interface RatingStatistic {
    accommodationId: number;
    overallAverage: number;
    totalRatings: number;
    ratingDistribution: Record<number, number>; 
    criteriaAverages: Record<string, number>; 
}
