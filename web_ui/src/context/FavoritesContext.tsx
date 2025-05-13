"use client";

import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react';

// Types for favorite items
export interface FavoriteHotel {
    id: string;
    name: string;
    imageUrl: string;
    rating?: number;
    reviewCount?: number;
    location: string;
    distance?: string;
    price: number;
    discountPrice?: number;
    amenities?: { id: string; name: string }[];
    type?: string;
    beds?: number;
    rooms?: number;
    isFavorite: boolean;
    hasPromotion?: boolean;
    geniusLevel?: number;
    savedAt: Date;
}

export interface FavoriteLocation {
    id: string;
    name: string;
    imageUrl: string;
    description: string;
    totalHotels: number;
    averagePrice: number;
    isFavorite: boolean;
    savedAt: Date;
}

// Sort options
export type SortOption = 'recent' | 'price-high' | 'price-low' | 'rating' | 'name-asc' | 'name-desc';

// Context interface
interface FavoritesContextType {
    favoriteHotels: FavoriteHotel[];
    favoriteLocations: FavoriteLocation[];
    addHotelToFavorites: (hotel: FavoriteHotel) => void;
    removeHotelFromFavorites: (id: string) => void;
    addLocationToFavorites: (location: FavoriteLocation) => void;
    removeLocationFromFavorites: (id: string) => void;
    isHotelFavorited: (id: string) => boolean;
    isLocationFavorited: (id: string) => boolean;
    toggleHotelFavorite: (hotel: FavoriteHotel) => void;
    toggleLocationFavorite: (location: FavoriteLocation) => void;
    getSortedHotels: (sortBy: SortOption, searchTerm?: string) => FavoriteHotel[];
    getSortedLocations: (sortBy: SortOption, searchTerm?: string) => FavoriteLocation[];
    loading: boolean;
    // New bulk selection features
    selectedHotelIds: string[];
    selectedLocationIds: string[];
    toggleHotelSelection: (id: string) => void;
    toggleLocationSelection: (id: string) => void;
    selectAllHotels: () => void;
    selectAllLocations: () => void;
    clearHotelSelections: () => void;
    clearLocationSelections: () => void;
    removeSelectedHotels: () => void;
    removeSelectedLocations: () => void;
    isAllHotelsSelected: boolean;
    isAllLocationsSelected: boolean;
}

// Create context with a default empty value
const FavoritesContext = createContext<FavoritesContextType | undefined>(undefined);

// Provider props
interface FavoritesProviderProps {
    children: ReactNode;
}

// Provider component
export const FavoritesProvider: React.FC<FavoritesProviderProps> = ({ children }) => {
    const [favoriteHotels, setFavoriteHotels] = useState<FavoriteHotel[]>([]);
    const [favoriteLocations, setFavoriteLocations] = useState<FavoriteLocation[]>([]);
    const [loading, setLoading] = useState(true);
    const [selectedHotelIds, setSelectedHotelIds] = useState<string[]>([]);
    const [selectedLocationIds, setSelectedLocationIds] = useState<string[]>([]);

    const isAllHotelsSelected = favoriteHotels.length > 0 && selectedHotelIds.length === favoriteHotels.length;
    const isAllLocationsSelected = favoriteLocations.length > 0 && selectedLocationIds.length === favoriteLocations.length;

    // Load saved favorites from localStorage on mount
    useEffect(() => {
        const loadFavorites = () => {
            try {
                setLoading(true);
                // In a real app, we'd fetch from API here
                const savedHotels = localStorage.getItem('favoriteHotels');
                const savedLocations = localStorage.getItem('favoriteLocations');

                if (savedHotels) {
                    const parsedHotels = JSON.parse(savedHotels);
                    // Convert string dates back to Date objects
                    setFavoriteHotels(parsedHotels.map((hotel: any) => ({
                        ...hotel,
                        savedAt: new Date(hotel.savedAt)
                    })));
                }

                if (savedLocations) {
                    const parsedLocations = JSON.parse(savedLocations);
                    setFavoriteLocations(parsedLocations.map((location: any) => ({
                        ...location,
                        savedAt: new Date(location.savedAt)
                    })));
                }
            } catch (error) {
                console.error('Error loading favorites from localStorage:', error);
            } finally {
                setLoading(false);
            }
        };

        // Simulate API delay
        setTimeout(() => {
            loadFavorites();
        }, 500);
    }, []);

    // Save to localStorage whenever favorites change
    useEffect(() => {
        if (!loading) {
            localStorage.setItem('favoriteHotels', JSON.stringify(favoriteHotels));
        }
    }, [favoriteHotels, loading]);

    useEffect(() => {
        if (!loading) {
            localStorage.setItem('favoriteLocations', JSON.stringify(favoriteLocations));
        }
    }, [favoriteLocations, loading]);

    // Clear selections when favorites change
    useEffect(() => {
        setSelectedHotelIds([]);
    }, [favoriteHotels]);

    useEffect(() => {
        setSelectedLocationIds([]);
    }, [favoriteLocations]);

    // Add a hotel to favorites
    const addHotelToFavorites = (hotel: FavoriteHotel) => {
        setFavoriteHotels(prev => {
            if (prev.some(h => h.id === hotel.id)) {
                return prev;
            }
            return [...prev, { ...hotel, isFavorite: true, savedAt: new Date() }];
        });
    };

    // Remove a hotel from favorites
    const removeHotelFromFavorites = (id: string) => {
        setFavoriteHotels(prev => prev.filter(hotel => hotel.id !== id));
        setSelectedHotelIds(prev => prev.filter(hotelId => hotelId !== id));
    };

    // Add a location to favorites
    const addLocationToFavorites = (location: FavoriteLocation) => {
        setFavoriteLocations(prev => {
            if (prev.some(l => l.id === location.id)) {
                return prev;
            }
            return [...prev, { ...location, isFavorite: true, savedAt: new Date() }];
        });
    };

    // Remove a location from favorites
    const removeLocationFromFavorites = (id: string) => {
        setFavoriteLocations(prev => prev.filter(location => location.id !== id));
        setSelectedLocationIds(prev => prev.filter(locationId => locationId !== id));
    };

    // Check if a hotel is already favorited
    const isHotelFavorited = (id: string) => {
        return favoriteHotels.some(hotel => hotel.id === id);
    };

    // Check if a location is already favorited
    const isLocationFavorited = (id: string) => {
        return favoriteLocations.some(location => location.id === id);
    };

    // Toggle hotel favorite status
    const toggleHotelFavorite = (hotel: FavoriteHotel) => {
        if (isHotelFavorited(hotel.id)) {
            removeHotelFromFavorites(hotel.id);
        } else {
            addHotelToFavorites(hotel);
        }
    };

    // Toggle location favorite status
    const toggleLocationFavorite = (location: FavoriteLocation) => {
        if (isLocationFavorited(location.id)) {
            removeLocationFromFavorites(location.id);
        } else {
            addLocationToFavorites(location);
        }
    };

    // Get sorted and filtered hotels
    const getSortedHotels = (sortBy: SortOption, searchTerm = ''): FavoriteHotel[] => {
        let filtered = favoriteHotels;

        // Apply search filter if provided
        if (searchTerm) {
            const term = searchTerm.toLowerCase();
            filtered = filtered.filter(hotel =>
                hotel.name.toLowerCase().includes(term) ||
                hotel.location.toLowerCase().includes(term)
            );
        }

        // Apply sorting
        return [...filtered].sort((a, b) => {
            switch (sortBy) {
                case 'recent':
                    return new Date(b.savedAt).getTime() - new Date(a.savedAt).getTime();
                case 'price-high':
                    return (b.discountPrice || b.price) - (a.discountPrice || a.price);
                case 'price-low':
                    return (a.discountPrice || a.price) - (b.discountPrice || b.price);
                case 'rating':
                    return (b.rating || 0) - (a.rating || 0);
                case 'name-asc':
                    return a.name.localeCompare(b.name);
                case 'name-desc':
                    return b.name.localeCompare(a.name);
                default:
                    return 0;
            }
        });
    };

    // Get sorted and filtered locations
    const getSortedLocations = (sortBy: SortOption, searchTerm = ''): FavoriteLocation[] => {
        let filtered = favoriteLocations;

        // Apply search filter if provided
        if (searchTerm) {
            const term = searchTerm.toLowerCase();
            filtered = filtered.filter(location =>
                location.name.toLowerCase().includes(term) ||
                location.description.toLowerCase().includes(term)
            );
        }

        // Apply sorting
        return [...filtered].sort((a, b) => {
            switch (sortBy) {
                case 'recent':
                    return new Date(b.savedAt).getTime() - new Date(a.savedAt).getTime();
                case 'price-high':
                    return b.averagePrice - a.averagePrice;
                case 'price-low':
                    return a.averagePrice - b.averagePrice;
                case 'name-asc':
                    return a.name.localeCompare(b.name);
                case 'name-desc':
                    return b.name.localeCompare(a.name);
                default:
                    return 0;
            }
        });
    };

    // New bulk selection methods
    const toggleHotelSelection = (id: string) => {
        setSelectedHotelIds(prev =>
            prev.includes(id)
                ? prev.filter(hotelId => hotelId !== id)
                : [...prev, id]
        );
    };

    const toggleLocationSelection = (id: string) => {
        setSelectedLocationIds(prev =>
            prev.includes(id)
                ? prev.filter(locationId => locationId !== id)
                : [...prev, id]
        );
    };

    const selectAllHotels = () => {
        if (isAllHotelsSelected) {
            clearHotelSelections();
        } else {
            setSelectedHotelIds(favoriteHotels.map(hotel => hotel.id));
        }
    };

    const selectAllLocations = () => {
        if (isAllLocationsSelected) {
            clearLocationSelections();
        } else {
            setSelectedLocationIds(favoriteLocations.map(location => location.id));
        }
    };

    const clearHotelSelections = () => {
        setSelectedHotelIds([]);
    };

    const clearLocationSelections = () => {
        setSelectedLocationIds([]);
    };

    const removeSelectedHotels = () => {
        setFavoriteHotels(prev => prev.filter(hotel => !selectedHotelIds.includes(hotel.id)));
        clearHotelSelections();
    };

    const removeSelectedLocations = () => {
        setFavoriteLocations(prev => prev.filter(location => !selectedLocationIds.includes(location.id)));
        clearLocationSelections();
    };

    // Context value
    const value = {
        favoriteHotels,
        favoriteLocations,
        addHotelToFavorites,
        removeHotelFromFavorites,
        addLocationToFavorites,
        removeLocationFromFavorites,
        isHotelFavorited,
        isLocationFavorited,
        toggleHotelFavorite,
        toggleLocationFavorite,
        getSortedHotels,
        getSortedLocations,
        loading,
        // New bulk selection props
        selectedHotelIds,
        selectedLocationIds,
        toggleHotelSelection,
        toggleLocationSelection,
        selectAllHotels,
        selectAllLocations,
        clearHotelSelections,
        clearLocationSelections,
        removeSelectedHotels,
        removeSelectedLocations,
        isAllHotelsSelected,
        isAllLocationsSelected
    };

    return (
        <FavoritesContext.Provider value={value}>
            {children}
        </FavoritesContext.Provider>
    );
};

// Custom hook to use the favorites context
export const useFavorites = (): FavoritesContextType => {
    const context = useContext(FavoritesContext);
    if (context === undefined) {
        throw new Error('useFavorites must be used within a FavoritesProvider');
    }
    return context;
};
