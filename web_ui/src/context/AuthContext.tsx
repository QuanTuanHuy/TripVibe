"use client";

import { createContext, useContext, useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { authService } from '../services';
import { User } from '../types/entities';

type AuthContextType = {
    user: User | null;
    token: string | null;
    isAuthenticated: boolean;
    isLoading: boolean;
    login: (email: string, password: string) => Promise<void>;
    logout: () => void;
    register: (userData: any) => Promise<void>;
    verifyOtp: (email: string, otp: string) => Promise<void>;
};

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider = ({ children }: { children: React.ReactNode }) => {
    const [user, setUser] = useState<User | null>(null);
    const [token, setToken] = useState<string | null>(null);
    const [isLoading, setIsLoading] = useState(true);
    const router = useRouter();

    // Tính toán trạng thái isAuthenticated dựa trên token và user
    const isAuthenticated = !!token && !!user;

    useEffect(() => {
        // Kiểm tra token trong localStorage
        const storedToken = localStorage.getItem('token');
        if (storedToken) {
            // Giải mã token để lấy thông tin user hoặc fetch thông tin từ API
            setToken(storedToken);
            // Fetch thông tin người dùng
            fetchUserProfile();
        } else {
            setIsLoading(false);
        }
    }, []);

    const fetchUserProfile = async () => {
        try {
            // Sử dụng authService để lấy thông tin user
            const userData = await authService.getCurrentUser();
            setUser(userData);
        } catch (error) {
            console.error('Error fetching user profile', error);
            logout();
        } finally {
            setIsLoading(false);
        }
    };

    const login = async (email: string, password: string) => {
        setIsLoading(true);
        try {
            // Sử dụng authService để đăng nhập
            const response = await authService.login({ email, password });
            localStorage.setItem('token', response.token);
            setToken(response.token);
            setUser(response.user);
            router.push('/');
        } catch (error) {
            console.error('Login error:', error);
            throw error;
        } finally {
            setIsLoading(false);
        }
    };

    const register = async (userData: any) => {
        setIsLoading(true);
        try {
            // Sử dụng authService để đăng ký
            await authService.register(userData);
            // Lưu email để dùng cho việc xác thực OTP
            localStorage.setItem('userEmail', userData.email);
            router.push('/verify-otp');
        } catch (error) {
            console.error('Registration error:', error);
            throw error;
        } finally {
            setIsLoading(false);
        }
    };

    const verifyOtp = async (email: string, otp: string) => {
        setIsLoading(true);
        try {
            // Sử dụng authService để xác thực OTP
            await authService.verifyOtp(email, otp);
            router.push('/login');
        } catch (error) {
            console.error('OTP verification error:', error);
            throw error;
        } finally {
            setIsLoading(false);
        }
    };

    const logout = () => {
        authService.logout();
        setUser(null);
        setToken(null);
        router.push('/login');
    };

    return (
        <AuthContext.Provider value={{ 
            user, 
            token, 
            isAuthenticated,  // Thêm thuộc tính isAuthenticated vào Provider
            isLoading, 
            login, 
            logout, 
            register, 
            verifyOtp 
        }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error('useAuth must be used within an AuthProvider');
    }
    return context;
};