export interface Amenity {
  id: number;
  name: string;
  description?: string;
  icon?: string;
  groupId?: number;
  isPaid?: boolean;
  price?: number;
  availableTime?: string;
  isHighlighted?: boolean;
  isFilterable?: boolean;
  needToReserve?: boolean;
}

export interface AmenityGroup {
  id: number;
  name: string;
  type: string; // PROPERTY, ACCOMMODATION, UNIT, BATHROOM, etc.
  description?: string;
  icon?: string;
  displayOrder?: number;
  isPopular?: boolean;
  amenities: Amenity[];
}

export interface AmenityGroupParams {
  page?: number;
  pageSize?: number;
  sortBy?: string;
  sortType?: string;
  type?: string;
  isPopular?: boolean | null;
}

export interface UpdateUnitAmenityRequest {
  amenityIds: number[];
}
