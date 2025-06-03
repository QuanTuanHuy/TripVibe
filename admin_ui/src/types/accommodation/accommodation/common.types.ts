
export interface Currency {
    id: number;
    name: string;
    code: string;
    symbol: string;
}

export interface Language {
    id: number;
    name: string;
    code: string;
    nativeName: string;
}

export interface LanguageParams {
    page?: number;
    pageSize?: number;
    sortBy?: string;
    sortType?: 'asc' | 'desc';
    name?: string;
}


export interface CreateLocationDto {
    countryId: number;
    provinceId: number;
    detailAddress: string;
    latitude: number;
    longitude: number;
}
