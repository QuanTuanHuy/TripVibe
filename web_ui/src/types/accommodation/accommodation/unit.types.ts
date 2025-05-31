import { Image } from "@/types/common";
import { Bedroom } from "../bed";
import { UnitPriceGroup, UnitPriceType } from "../price";
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

export interface UnitName {
    id: number;
    name: string;
    description?: string;
}