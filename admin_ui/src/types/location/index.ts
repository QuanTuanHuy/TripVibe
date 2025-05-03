export interface CountryParams {
  page?: number;
  pageSize?: number;
  name?: string;
  sortBy?: string;
  sortOrder?: string;
}

export interface ProvinceParams {
  page?: number;
  pageSize?: number;
  name?: string;
  code?: string;
  countryId?: number;
  sortBy?: string;
  sortOrder?: string;
}

export interface ApiResponse<T> {
  data: T;
  message: string;
  status: string;
  timestamp: string;
}

export interface Country {
  id: number;
  name: string;
  code: string;
  currency?: string;
  timezone?: string;
  language?: string;
  region?: string;
  subRegion?: string;
  flagUrl?: string;
}

export interface Province {
  id: number;
  name: string;
  code: string;
  countryId: number;
  thumbnailUrl?: string;
}

export interface Location {
  id: number;
  countryId: number;
  provinceId: number;
  detailAddress: string;
  latitude: number;
  longitude: number;
}
