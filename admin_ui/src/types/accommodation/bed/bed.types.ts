export interface CreateBedDto {
    bedTypeId: number;
    quantity: number;
}

export interface CreateBedroomDto {
    quantity: number;
    beds: CreateBedDto[];
}


export interface Bedroom {
    id: number;
    unitId: number;
    quantity: number;
    beds?: Bed[];
}

export interface Bed {
    id: number;
    bedroomId: number;
    bedTypeId: number;
    quantity: number;
    type?: BedType;
}

export interface BedType {
    id: number;
    name: string;
    size: number;
}

export interface BedTypeParams {
    page?: number;
    pageSize?: number;
    name?: string;
    sortBy?: string;
    sortType?: string;
}