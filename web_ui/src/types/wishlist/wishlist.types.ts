export interface Wishlist {
    id: number;
    touristId: number;
    name: string;
    createdAt: string;
    items?: WishlistItem[];
}

export interface WishlistItem {
    id: number;
    wishlistId: number;
    accommodationId: number;
    accommodationName: string;
    accommodationImageUrl: string;
    createdAt: string;
}

export interface CreateWishlistRequest {
    name: string;
    items?: CreateWishlistItemRequest[];
}

export interface CreateWishlistItemRequest {
    accommodationId: number;
}

export interface WishlistResponse {
    success: boolean;
    data: Wishlist;
    message?: string;
}

export interface WishlistItemResponse {
    success: boolean;
    data?: any;
    message?: string;
}

// Extended types for UI with accommodation details
export interface WishlistItemWithDetails extends WishlistItem {
    accommodation?: {
        id: number;
        name: string;
        description: string;
        thumbnailUrl: string;
        location: {
            address: string;
            countryId: number;
            provinceId: number;
        };
        units: Array<{
            id: number;
            name: string;
            description: string;
        }>;
        ratingSummary: {
            rating: number;
            numberOfRatings: number;
        };
        priceInfo?: {
            lengthOfStay: number;
            guestCount: number;
            initialPrice: number;
            currentPrice: number;
        } | null;
    };
}

export interface WishlistWithDetails extends Wishlist {
    items?: WishlistItemWithDetails[];
}
