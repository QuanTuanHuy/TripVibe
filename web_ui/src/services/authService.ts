import { User } from '@/types';
import apiClient from './apiClient';

// Path đến authentication service thông qua API Gateway
const AUTH_PATH = '/authentication_service/api/public/v1';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  user: User;
}

export interface RegisterRequest {
  email: string;
  password: string;
  name?: string;
  phone?: string;
}

const authService = {
  login: async (credentials: LoginRequest): Promise<LoginResponse> => {
    return apiClient.post<LoginResponse>(`${AUTH_PATH}/auth`, credentials);
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

  logout: (): void => {
    localStorage.removeItem('token');
  }
};

export default authService;