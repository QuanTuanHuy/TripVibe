
// Định nghĩa User Entity
export interface User {
  id: number;
  email: string;
  name?: string;
  roles: string[];
}

// Định nghĩa Accommodation Entity
export interface Accommodation {
  id: number;
  name: string;
  description?: string;
  typeId: number;
  hostId: number;
  locationId: number;
  thumbnailUrl?: string;
  checkInTimeFrom?: string;
  checkInTimeTo?: string;
  checkOutTimeFrom?: string;
  checkOutTimeTo?: string;
  isVerified: boolean;
  currencyId?: number;
}

// Định nghĩa Unit Entity
export interface Unit {
  id: number;
  accommodationId: number;
  description?: string;
  maxGuest: number;
  isDeleted: boolean;
}

// Định nghĩa Payment Entity
export interface Payment {
  id: number;
  bookingId: number;
  userId: number;
  amount: number;
  status: string;
  transactionId: string;
  gatewayReferenceId?: string;
  paymentMethod: string;
  paymentGateway: string;
  currency: string;
  paymentUrl?: string;
  gatewayResponse?: string;
  createdAt: number;
  updatedAt: number;
  paidAt?: number;
}

// Định nghĩa Booking Entity
export interface Booking {
  id: number;
  userId: number;
  unitId: number;
  checkInDate: string;
  checkOutDate: string;
  guestCount: number;
  status: string;
  totalPrice: number;
  currency: string;
}

// Định nghĩa Promotion Entity
export interface Promotion {
  id: number;
  name: string;
  description?: string;
  typeId: number;
  discountValue: number;
  discountType: string;
  startDate: string;
  endDate: string;
  isActive: boolean;
  code: string;
}

// Định nghĩa Rating Entity
export interface Rating {
  id: number;
  userId: number;
  accommodationId: number;
  score: number;
  comment?: string;
  createdAt: number;
}

// Định nghĩa Location Entity
export interface Location {
  id: number;
  name: string;
  address: string;
  city: string;
  state?: string;
  country: string;
  postalCode?: string;
  longitude?: number;
  latitude?: number;
}

// Định nghĩa File Resource Entity
export interface FileResource {
  id: number;
  fileName: string;
  originalName: string;
  size: number;
  contentType: string;
  url: string;
  isImage: boolean;
  width?: number;
  height?: number;
  category?: string;
  referenceId?: number;
  referenceType?: string;
  tags?: string;
  description?: string;
  isPublic: boolean;
  groupId?: number;
}