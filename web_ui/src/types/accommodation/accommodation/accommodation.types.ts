import { Location } from "@/types/location";
import { Amenity } from "../amenity";
import { Language } from "./common.types";
import { Unit } from "./unit.types";

export interface Accommodation {
    id: number;
    hostId: number;
    locationId: number;
    typeId: number;
    currencyId: number;
    name: string;
    description: string;
    thumbnailUrl?: string;
    checkInTimeFrom: number; // Thời gian dưới dạng hours (0-23)
    checkInTimeTo: number;
    checkOutTimeFrom: number;
    checkOutTimeTo: number;
    isVerified?: boolean;
    location?: Location;
    languages?: Language[];
    units?: Unit[];
    type?: AccommodationType;
    amenities?: AccommodationAmenity[];
}

export interface AccommodationParams {
    ids?: number[];
}

export interface AccommodationType {
    id: number;
    name: string;
    description?: string;
}

export interface AccommodationAmenity {
    id: number;
    accommodationId: number;
    amenityId: number;
    fee?: number;
    needToReserve?: boolean;
    amenity?: Amenity;
}

export interface AccommodationThumbnail {
    id: number;
    name: string;
    description: string;
    thumbnailUrl: string;
    location: AccommodationThumbnailLocation;
    units: AccommodationThumbnailUnit[];
    ratingSummary: AccommodationThumbnailRatingSummary;
    priceInfo: AccommodationThumbnailPriceInfo | null;
}

export interface AccommodationThumbnailLocation {
    address: string;
    countryId: number;
    provinceId: number;
}

export interface AccommodationThumbnailUnit {
    id: number;
    name: string;
    description: string;
}

export interface AccommodationThumbnailRatingSummary {
    rating: number;
    numberOfRatings: number;
}

export interface AccommodationThumbnailPriceInfo {
    lengthOfStay: number;
    guestCount: number;
    initialPrice: number;
    currentPrice: number;
}
