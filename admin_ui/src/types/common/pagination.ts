export interface PageInfo {
    pageSize: number;
    totalPage: number;
    totalRecord: number;
    nextPage: number | null;
    previousPage: number | null;
}

export interface ListDataResponse<T> {
    data: T[];
    pageInfo: PageInfo;
}

export interface PaginationParams {
    page?: number;
    pageSize?: number;
    sortBy?: string;
    sortDirection?: 'ASC' | 'DESC';
}