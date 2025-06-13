import {CreateWishlistItemRequest, CreateWishlistRequest, Wishlist, WishlistWithDetails} from '@/types/wishlist';
import {AccommodationThumbnail} from '@/types/accommodation';
import apiClient from '../api.client';
import accommodationService from '../accommodation/accommodationService';

const PROFILE_SERVICE_PATH = '/profile_service/api/public/v1';

const wishlistService = {
    getWishlists: async (): Promise<Wishlist[]> => {
        const response = await apiClient.get<Wishlist[]>(`${PROFILE_SERVICE_PATH}/wishlists`);
        return response || [];
    },


    getWishlistById: async (id: number): Promise<Wishlist> => {
        return await apiClient.get<Wishlist>(`${PROFILE_SERVICE_PATH}/wishlists/${id}`);
    },

    getWishlistWithDetails: async (id: number): Promise<WishlistWithDetails> => {
        const wishlist = await wishlistService.getWishlistById(id);

        if (!wishlist.items || wishlist.items.length === 0) {
            return { ...wishlist, items: [] };
        }

        try {
            // Get accommodation details for all items
            const accommodationIds = wishlist.items.map(item => item.accommodationId);
            const accommodationThumbnails = await accommodationService.getAccommodationThumbnails(accommodationIds);

            // Create a map for quick lookup
            const accommodationMap = new Map<number, AccommodationThumbnail>();
            accommodationThumbnails.forEach(acc => {
                accommodationMap.set(acc.id, acc);
            });
            // Merge wishlist items with accommodation details
            const itemsWithDetails = wishlist.items.map(item => {
                const accommodation = accommodationMap.get(item.accommodationId);
                return {
                    ...item,
                    accommodation: accommodation ? {
                        id: accommodation.id,
                        name: accommodation.name,
                        description: accommodation.description,
                        thumbnailUrl: accommodation.thumbnailUrl,
                        location: accommodation.location,
                        units: accommodation.units,
                        ratingSummary: accommodation.ratingSummary,
                        priceInfo: accommodation.priceInfo || undefined
                    } : undefined
                };
            });

            return {
                ...wishlist,
                items: itemsWithDetails
            };
        } catch (error) {
            console.error('Error fetching accommodation details:', error);
            // Return wishlist without accommodation details if there's an error
            return {
                ...wishlist,
                items: wishlist.items.map(item => ({ ...item, accommodation: undefined }))
            };
        }
    },

    createWishlist: async (request: CreateWishlistRequest): Promise<Wishlist> => {
        return await apiClient.post<Wishlist>(`${PROFILE_SERVICE_PATH}/wishlists`, request);
    },

    addWishlistItem: async (wishlistId: number, request: CreateWishlistItemRequest): Promise<void> => {
        await apiClient.post(`${PROFILE_SERVICE_PATH}/wishlists/${wishlistId}/items`, request);
    },

    removeWishlistItem: async (wishlistId: number, itemId: number): Promise<void> => {
        await apiClient.delete(`${PROFILE_SERVICE_PATH}/wishlists/${wishlistId}/items/${itemId}`);
    },

    updateWishlist: async (id: number, request: { name: string }): Promise<Wishlist> => {
        return await apiClient.put<Wishlist>(`${PROFILE_SERVICE_PATH}/wishlists/${id}`, request);
    },

    deleteWishlist: async (id: number): Promise<void> => {
        await apiClient.delete(`${PROFILE_SERVICE_PATH}/wishlists/${id}`);
    },

    isAccommodationInWishlist: async (accommodationId: number): Promise<boolean> => {
        try {
            const wishlists = await wishlistService.getWishlists();
            return wishlists.some(wishlist =>
                wishlist.items?.some(item => item.accommodationId === accommodationId)
            );
        } catch (error) {
            console.error('Error checking accommodation in wishlist:', error);
            return false;
        }
    }
};

export default wishlistService;
