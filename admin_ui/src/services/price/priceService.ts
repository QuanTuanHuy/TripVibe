import { UnitPriceCalendar } from '@/types/accommodation/price';
import { generateMockPriceCalendar } from '@/data/priceCalendar';

const PRICE_PATH = '/accommodation_service/api/public/v1';

const priceService = {  // Get price calendar for a unit
    getUnitPriceCalendar: async (unitId: number, startDate: string, endDate: string): Promise<UnitPriceCalendar[]> => {
        // Uncomment to use real API
        /*
        const response = await apiClient.get(
          `${PRICE_PATH}/units/${unitId}/price-calendar`, 
          { params: { startDate, endDate } }
        );
        return response.data;
        */

        // Use the mock data generator function
        const priceCalendars = generateMockPriceCalendar(unitId, startDate, endDate);
        return Promise.resolve(priceCalendars);
    },

    // Update base price for a unit for a date range
    updateBasePrice: async (
        unitId: number,
        startDate: string,
        endDate: string,
        basePrice: number
    ): Promise<void> => {
        // Uncomment to use real API
        /*
        await apiClient.put(`${PRICE_PATH}/units/${unitId}/price-calendar/base-price`, {
          startDate,
          endDate,
          basePrice,
          updateSource: 'ADMIN_UI'
        });
        */

        // Just log for mock
        console.log(`Updated base price for unit ${unitId} from ${startDate} to ${endDate} to $${basePrice}`);
        return Promise.resolve();
    },
};

export default priceService;