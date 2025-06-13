export interface SearchParams {
    page?: number;
    pageSize?: number;
    name?: string;
    provinceId?: number;
    startDate?: string;
    endDate?: string;
    numAdults?: number;
    numChildren?: number;
    minBudget?: number;
    maxBudget?: number;
    accTypeId?: number;
    accAmenityIds?: number[];
    unitAmenityIds?: number[];
    bookingPolicyIds?: number[];
    minRatingStar?: number;
    longitude?: number;
    latitude?: number;
    radius?: number;
    sortBy?: string;
}

export interface SearchResult<T> {
    second: T[];
    first: PageInfo;
}

export interface PageInfo {
    pageSize: number;
    totalRecord: number;
    totalPage: number;
    nextPage: number | null;
    previousPage: number | null;
}
