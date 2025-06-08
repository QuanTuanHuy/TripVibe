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
  distribution: Record<number, number>;
  criteriaAverages?: Record<RatingCriteriaType, number>;
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
  quantity: number | null;
}

interface UserProfile {
  userId: number;
  name: string;
  email: string;
  countryId?: number;
  countryName?: string;
  avatarUrl?: string;
}

// Rating criteria types
export enum RatingCriteriaType {
  CLEANLINESS = "CLEANLINESS",
  COMFORT = "COMFORT",
  LOCATION = "LOCATION",
  FACILITIES = "FACILITIES",
  STAFF = "STAFF",
  VALUE_FOR_MONEY = "VALUE_FOR_MONEY"
}

// Request types
export interface CreateRatingDto {
  accommodationId: number;
  unitId: number;
  bookingId: number;
  value: number;
  comment: string;
  languageId: number;
  ratingDetails?: Record<RatingCriteriaType, number>;
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