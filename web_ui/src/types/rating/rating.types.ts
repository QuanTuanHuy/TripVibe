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
}

export interface RatingSummary {
  id: number;
  accommodationId: number;
  totalRating: number;
  numberOfRatings: number;
  isSyncedWithSearchService: boolean;
}

export interface RatingResponse {
  id: number;
  ratingId: number;
  ownerId: number;
  content: string;
  createdAt: number;
}

export interface RatingHelpfulness {
  id: number;
  ratingId: number;
  userId: number;
  isHelpful: boolean;
}

interface Unit {
  id: number;
  name: string;
  accommodationId: number;
}

interface UserProfile {
  userId: number;
  firstName: string;
  lastName: string;
  avatarUrl?: string;
}

// Request types
export interface CreateRatingDto {
  accommodationId: number;
  unitId: number;
  value: number;
  comment: string;
  languageId: number;
}

export interface CreateRatingResponseDto {
  ratingId: number;
  content: string;
}

export interface CreateRatingHelpfulnessDto {
  ratingId: number;
  isHelpful: boolean;
}

export interface RatingParams {
  page?: number;
  pageSize?: number;
  sortBy?: string;
  sortType?: string;
  unitId?: number;
  accommodationId: number;
  minValue?: number;
  maxValue?: number;
  languageId?: number;
  createdFrom?: number;
  createdTo?: number;
}