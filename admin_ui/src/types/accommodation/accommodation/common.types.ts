
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


export interface CreateLocationDto {
    countryId: number;
    provinceId: number;
    detailAddress: string;
    latitude: number;
    longitude: number;
}
