/**
 * Utility functions for transforming dates between frontend and backend formats
 */

/**
 * Convert a Date object to Unix timestamp (epoch seconds)
 */
export function dateToEpochSeconds(date: Date): number {
    return Math.floor(date.getTime() / 1000);
}

/**
 * Convert Unix timestamp (epoch seconds) to Date object
 */
export function epochSecondsToDate(epochSeconds: number): Date {
    return new Date(epochSeconds * 1000);
}

/**
 * Convert ISO date string to Unix timestamp (epoch seconds)
 */
export function isoStringToEpochSeconds(isoString: string): number {
    return dateToEpochSeconds(new Date(isoString));
}

/**
 * Convert Unix timestamp (epoch seconds) to ISO date string
 */
export function epochSecondsToIsoString(epochSeconds: number): string {
    return epochSecondsToDate(epochSeconds).toISOString();
}

/**
 * Convert date range object to epoch seconds
 */
export function dateRangeToEpochRange(dateRange: { from: Date; to: Date }): { stayFrom: number; stayTo: number } {
    return {
        stayFrom: dateToEpochSeconds(dateRange.from),
        stayTo: dateToEpochSeconds(dateRange.to)
    };
}

/**
 * Convert epoch range to date range object
 */
export function epochRangeToDateRange(epochRange: { stayFrom: number; stayTo: number }): { from: Date; to: Date } {
    return {
        from: epochSecondsToDate(epochRange.stayFrom),
        to: epochSecondsToDate(epochRange.stayTo)
    };
}

/**
 * Format date for display (e.g., "Dec 15, 2024")
 */
export function formatDateForDisplay(date: Date): string {
    return date.toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
    });
}

/**
 * Format epoch seconds for display
 */
export function formatEpochSecondsForDisplay(epochSeconds: number): string {
    return formatDateForDisplay(epochSecondsToDate(epochSeconds));
}

/**
 * Calculate number of nights between two dates
 */
export function calculateNights(checkIn: Date, checkOut: Date): number {
    const timeDiff = checkOut.getTime() - checkIn.getTime();
    return Math.ceil(timeDiff / (1000 * 3600 * 24));
}

/**
 * Calculate number of nights from epoch timestamps
 */
export function calculateNightsFromEpoch(stayFrom: number, stayTo: number): number {
    return calculateNights(epochSecondsToDate(stayFrom), epochSecondsToDate(stayTo));
}
