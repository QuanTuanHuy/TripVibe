import { User } from '@/types/auth';
import apiClient from './api.client';

// Path đến authentication service thông qua API Gateway
const AUTH_PATH = '/authentication_service/api/public/v1';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  accessToken: string;
  refreshToken: string;
  expiresIn: number;
  tokenType: string;
  user: User;
}

export interface TokenRefreshRequest {
  refreshToken: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  name?: string;
  phone?: string;
}

const authService = {
  login: async (credentials: LoginRequest): Promise<LoginResponse> => {
    console.log('Login request:', credentials);
    // Không lưu token vào localStorage ở đây, việc này sẽ được xử lý trong AuthContext
    return apiClient.post<LoginResponse>(`${AUTH_PATH}/auth/login`, credentials);
  },

  refreshToken: async (refreshToken: string): Promise<LoginResponse> => {
    // Không lưu token vào localStorage ở đây, việc này sẽ được xử lý trong AuthContext
    return apiClient.post<LoginResponse>(`${AUTH_PATH}/auth/refresh`, { refreshToken });
  },

  register: async (userData: RegisterRequest): Promise<void> => {
    return apiClient.post<void>(`${AUTH_PATH}/users`, userData);
  },

  verifyOtp: async (email: string, otp: string): Promise<void> => {
    return apiClient.post<void>(`${AUTH_PATH}/users/otp/verify?email=${email}&otp=${otp}`, {});
  },

  getCurrentUser: async (): Promise<User> => {
    return apiClient.get<User>(`${AUTH_PATH}/users/me`);
  },

  logout: async (refreshToken: string): Promise<void> => {
    if (refreshToken) {
      try {
        await apiClient.post<void>(`${AUTH_PATH}/auth/logout`, { refreshToken });
      } catch (error) {
        console.error('Error during logout:', error);
      }
    }
    // Không xóa localStorage ở đây, việc này sẽ được xử lý trong AuthContext
  },

  logoutAll: async (): Promise<void> => {
    try {
      await apiClient.post<void>(`${AUTH_PATH}/auth/logout/all`, {});
    } catch (error) {
      console.error('Error during logout all sessions:', error);
    }
    // Không xóa localStorage ở đây, việc này sẽ được xử lý trong AuthContext
  },

  isAuthenticated: (): boolean => {
    return !!localStorage.getItem('token');
  },

  setToken: (token: string): void => {
    localStorage.setItem('token', token);
  },

  setRefreshToken: (refreshToken: string): void => {
    localStorage.setItem("refreshToken", refreshToken)
  },

  getToken: (): string | null => {
    return localStorage.getItem('token');
  },

  getRefreshToken: (): string | null => {
    return localStorage.getItem('refreshToken');
  },

  removeToken: (): void => {
    localStorage.removeItem('token');
  },

  removeRefreshToken: (): void => {
    localStorage.removeItem('refreshToken');
  },
};

export default authService;