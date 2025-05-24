import { format, isWeekend } from 'date-fns';

/**
 * Formats a number as currency
 */
export function formatCurrency(amount: number, currency: string = 'VND', locale: string = 'vi-VN'): string {
    return new Intl.NumberFormat(locale, {
        style: 'currency',
        currency: currency,
        minimumFractionDigits: 0
    }).format(amount);
}

/**
 * Checks if a date is a weekend
 */
export function isDateWeekend(date: Date): boolean {
    return isWeekend(date);
}

/**
 * Generates dates between start and end date (inclusive)
 */
export function generateDateRange(startDate: Date, endDate: Date): Date[] {
    const dates: Date[] = [];
    let currentDate = new Date(startDate);

    while (currentDate <= endDate) {
        dates.push(new Date(currentDate));
        currentDate.setDate(currentDate.getDate() + 1);
    }

    return dates;
}

/**
 * Formats a date for API requests
 */
export function formatDateForApi(date: Date): string {
    return format(date, 'yyyy-MM-dd');
}

/**
 * Formats a price value to avoid decimal places when possible
 */
export function formatPrice(price: number): string {
    return price % 1 === 0
        ? price.toString()
        : price.toFixed(2);
}
