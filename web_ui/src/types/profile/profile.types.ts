// User Profile Types
export interface UserProfile {
    id: number;
    name: string;
    gender?: string;
    phone?: string;
    email: string;
    avatarUrl?: string;
    dateOfBirth?: string;
    memberLevel?: number;
    isActive: boolean;

    location?: Location;
    passport?: Passport;
    creditCard?: CreditCard;
    userSetting?: UserSetting;
}

export interface Location {
    id?: number;
    countryId?: number;
    provinceId?: number;
    longitude?: number;
    latitude?: number;
    address?: string;
    countryName?: string;
    provinceName?: string;
}

export interface Passport {
    id?: number;
    passportNumber?: string;
    firstName?: string;
    lastName?: string;
    expirationDate?: number; // timestamp
    nationalityId?: number;
    nationalityName?: string;
}

export interface CreditCard {
    id?: number;
    cardNumber?: string;
    cardholderName?: string;
    expirationDate?: string;
    cvv?: string;
}

export interface UserSetting {
    id?: number;
    preferredCurrency?: string;
    preferredLanguage?: string;
    receiveNotifications?: boolean;
    receiveNewsletters?: boolean;
}

// Update Profile DTOs
export interface UpdateProfileDto {
    name?: string;
    phone?: string;
    avatarUrl?: string;
    dateOfBirth?: string;
    gender?: string;
    location?: UpdateLocationDto;
    passport?: UpdatePassportDto;
    userSetting?: UpdateUserSettingDto;
}

export interface UpdateLocationDto {
    countryId?: number;
    provinceId?: number;
    address?: string;
    longitude?: number;
    latitude?: number;
}

export interface UpdatePassportDto {
    passportNumber?: string;
    firstName?: string;
    lastName?: string;
    expirationDate?: number;
    nationalityId?: number;
}

export interface UpdateUserSettingDto {
    preferredCurrency?: string;
    preferredLanguage?: string;
    receiveNotifications?: boolean;
    receiveNewsletters?: boolean;
}

// Friend / Travel Companion Types
export interface Friend {
    id: number;
    touristId: number;
    firstName: string;
    lastName: string;
    dateOfBirth?: string;
    gender?: string;
}

export interface CreateFriendDto {
    firstName: string;
    lastName: string;
    dateOfBirth?: string;
    gender?: string;
}
