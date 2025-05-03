import { Bedroom, CreateBedroomDto } from "../bed";
import { CreatePriceGroupDto, CreatePriceTypeDto, PriceType, UnitPriceGroup, UnitPriceType } from "../price";

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
    id?: number;
    amenityId: number;
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
    images?: string[];
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
    typeId: number;
    typeName?: string;
    name: string;
    description: string;
    ownerId: number;
    ownerName?: string;
    checkInTimeFrom: number;
    checkInTimeTo: number;
    checkOutTimeFrom: number;
    checkOutTimeTo: number;
    rating?: number;
    reviewCount?: number;
    location?: Location;
    units?: Unit[];
    amenities?: Amenity[];
    images?: string[];
    createdAt?: number;
    updatedAt?: number;
}