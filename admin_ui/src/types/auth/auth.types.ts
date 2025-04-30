export interface User {
    id: number;
    email: string;
    name?: string;
    avatarUrl?: string;
    roleIds: number[];
    createdAt?: string;
    updatedAt?: string;
}

export interface Role {
    id: number;
    name: string;
    description?: string;
}

export interface UserCredentials {
    email: string;
    password: string;
}

export interface RegisterRequest {
    email: string;
    password: string;
    name?: string;
    roleIds?: number[];
}

export interface AuthResponse {
    user: User;
    token: string;
    expiresAt?: string;
}