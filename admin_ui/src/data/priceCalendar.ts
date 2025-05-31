import { accommodationService } from '@/services';
import { UnitPriceCalendar } from '@/types/accommodation';

// Hàm tạo price calendar mock data động dựa trên unit id, ngày bắt đầu và kết thúc
export const generateMockPriceCalendar = async (unitId: number, startDate: string, endDate: string): Promise<UnitPriceCalendar[]> => {
    const start = new Date(startDate);
    const end = new Date(endDate);
    const priceCalendars: UnitPriceCalendar[] = [];

    // Find the unit to get base price
    const units = await accommodationService.getUnitsByAccommodationId(1);
    const unit = units.find(u => u.id === unitId);
    const basePrice = unit ? unit.pricePerNight : 100;

    // Generate a price for each day in the range
    const current = new Date(start);
    while (current <= end) {
        const isWeekend = [0, 6].includes(current.getDay());
        const date = current.toISOString().split('T')[0]; // YYYY-MM-DD

        priceCalendars.push({
            id: parseInt(`${unitId}${current.getTime()}`),
            unitId,
            date,
            basePrice: isWeekend ? basePrice * 1.2 : basePrice, // 20% more on weekends
            weekendMultiplier: isWeekend ? 1.2 : 1,
            holidayMultiplier: 1,
            seasonType: null,
            seasonalMultiplier: 1,
            losDiscounts: null,
            earlyBirdDays: null,
            earlyBirdDiscount: 0,
            lastMinuteDays: null,
            lastMinuteDiscount: 0,
            baseOccupancy: null,
            extraPersonFee: null,
            lastUpdated: Date.now(),
            updateSource: 'MOCK'
        });

        current.setDate(current.getDate() + 1);
    }

    return priceCalendars;
};
