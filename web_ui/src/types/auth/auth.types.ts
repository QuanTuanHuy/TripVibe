export interface User {
    id: number;
    email: string;
    name?: string;
    avatarUrl?: string;
    roles: string[];
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

export interface DecodedToken {
    sub: string;
    scope: string;
    iss: string;
    exp: number;
    type: string;
    iat: number;
    jti: string;
    email: string;
}