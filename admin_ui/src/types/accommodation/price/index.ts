export interface CreatePriceGroupDto {
    numberOfGuests: number;
    percentage: number;
}

export interface CreatePriceTypeDto {
    priceTypeId: number;
    percentage: number;
}

export interface PriceType {
    id: number;
    name: string;
    description?: string;
}

export interface UnitPriceType {
    id: number;
    unitId: number;
    priceTypeId: number;
    percentage: number;
    priceType?: PriceType;
}

export interface UnitPriceGroup {
    id: number;
    unitId: number;
    numberOfGuests: number;
    percentage: number;
}