import { Location } from "@/types/location";
import { Amenity } from "../amenity";
import { CreateLocationDto, Language } from "./common.types";
import { CreateUnitDto, Unit } from "./unit.types";

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
    hostId?: number;
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

export interface CreateAccommodationDto {
    typeId: number;
    currencyId: number;
    name: string;
    description: string;
    checkInTimeFrom: number; // Thời gian dưới dạng hours (0-23)
    checkInTimeTo: number;
    checkOutTimeFrom: number;
    checkOutTimeTo: number;
    unit: CreateUnitDto;
    amenityIds: number[];
    languageIds: number[];
    location: CreateLocationDto;
}