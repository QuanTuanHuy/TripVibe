import { Image } from "@/types/common";
import { Bedroom, CreateBedroomDto } from "../bed";
import { CreatePriceGroupDto, CreatePriceTypeDto, PriceType, UnitPriceGroup, UnitPriceType } from "../price";
import { Location } from "@/types/location";

export interface AccommodationType {
    id: number;
    name: string;
    description?: string;
}

export interface Currency {
    id: number;
    name: string;
    code: string;
    symbol: string;
}

export interface Language {
    id: number;
    name: string;
    code: string;
    nativeName: string;
}

export interface Amenity {
    id: number;
    name: string;
    description?: string;
    icon?: string;
    groupId?: number;
    isPaid?: boolean;
    price?: number;
    availableTime?: string;
    isHighlighted?: boolean;
    isFilterable?: boolean;
    needToReserve?: boolean;
}

export interface AccommodationAmenity {
    id: number;
    accommodationId: number;
    amenityId: number;
    fee?: number;
    needToReserve?: boolean;
    amenity?: Amenity;
}

export interface AmenityGroup {
    id: number;
    name: string;
    type: string;
    description?: string;
    icon?: string;
    displayOrder?: number;
    isPopular?: boolean;
    amenities: Amenity[];
}

export interface AmenityGroupParams {
    page?: number;
    pageSize?: number;
    sortBy?: string;
    sortType?: string;
    type?: string;
    isPopular?: boolean;
}

export interface UnitName {
    id: number;
    name: string;
    description?: string;
}

export interface CreateLocationDto {
    countryId: number;
    provinceId: number;
    detailAddress: string;
    latitude: number;
    longitude: number;
}

export interface UnitAmenity {
    id: number;
    unitId: number;
    amenityId: number;
    fee?: number;
    needToReserve?: boolean;
    amenity?: Amenity;
}


export interface Unit {
    id?: number;
    accommodationId: number;
    unitNameId: number;
    description: string;
    pricePerNight: number;
    maxAdults: number;
    maxChildren: number;
    useSharedBathroom: boolean;
    isDeleted?: boolean;
    quantity: number;

    unitName?: UnitName;
    images?: Image[];
    bedrooms?: Bedroom[];
    amenities?: UnitAmenity[];
    priceTypes?: UnitPriceType[];
    priceGroups?: UnitPriceGroup[];
}

export interface CreateUnitDto {
    unitNameId: number;
    description: string;
    pricePerNight: number;
    maxAdults: number;
    maxChildren: number;
    useSharedBathroom: boolean;
    quantity: number;
    amenityIds: number[];
    priceTypes: CreatePriceTypeDto[];
    priceGroups: CreatePriceGroupDto[];
    bedrooms: CreateBedroomDto[];
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
