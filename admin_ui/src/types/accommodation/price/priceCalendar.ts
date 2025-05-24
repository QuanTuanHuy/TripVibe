import { UnitPriceType, UnitPriceGroup } from './index';

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
