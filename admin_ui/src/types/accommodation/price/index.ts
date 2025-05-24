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

export * from './priceCalendar';

export interface UnitPriceCalendar {
    id: number;
    unitId: number;
    date: string;
    basePrice: number;
    weekendMultiplier: number;
    holidayMultiplier: number;
    seasonType: string | null;
    seasonalMultiplier: number;
    losDiscounts: string | null;
    earlyBirdDays: number | null;
    earlyBirdDiscount: number;
    lastMinuteDays: number | null;
    lastMinuteDiscount: number;
    baseOccupancy: number | null;
    extraPersonFee: number | null;
    lastUpdated: number;
    updateSource: string;
}