import { Image } from "@/types/common";
import { Bedroom, CreateBedroomDto } from "../bed";
import { CreatePriceGroupDto, CreatePriceTypeDto, UnitPriceGroup, UnitPriceType } from "../price";
import { Amenity } from "../amenity";

export interface UnitAmenity {
    id: number;
    unitId: number;
    amenityId: number;
    fee?: number;
    needToReserve?: boolean;
    amenity?: Amenity;
}


export interface Unit {
    id: number;
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

export interface UnitName {
    id: number;
    name: string;
    description?: string;
}

export interface UnitNameParams {
    page?: number;
    pageSize?: number;
    sortBy?: string;
    sortType?: 'asc' | 'desc';
    name?: string;
}