"use client";

import { createContext, useContext, useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { jwtDecode } from 'jwt-decode';
import { authService } from '../services';
import { User, DecodedToken } from '@/types/auth';

type AuthContextType = {
    user: User | null;
    token: string | null;
    refreshToken: string | null;
    isAuthenticated: boolean;
    isLoading: boolean;
    login: (email: string, password: string) => Promise<void>;
    logout: () => Promise<void>;
    logoutAll: () => Promise<void>;
    register: (userData: any) => Promise<void>;
    verifyOtp: (email: string, otp: string) => Promise<void>;
    refreshTokenManually: () => Promise<void>;
    hasRole: (role: string) => boolean;
    getTokenExpiration: () => Date | null;
};

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider = ({ children }: { children: React.ReactNode }) => {
    const [user, setUser] = useState<User | null>(null);
    const [token, setToken] = useState<string | null>(null);
    const [refreshToken, setRefreshToken] = useState<string | null>(null);
    const [isLoading, setIsLoading] = useState(true);
    const router = useRouter();

    // Tính toán trạng thái isAuthenticated dựa trên token và user
    const isAuthenticated = !!token && !!user;

    useEffect(() => {
        // Kiểm tra token trong localStorage
        const storedToken = authService.getToken();
        const storedRefreshToken = authService.getRefreshToken();

        if (storedToken) {
            // Lưu các giá trị token vào state
            setToken(storedToken);
            if (storedRefreshToken) {
                setRefreshToken(storedRefreshToken);
            }
            // Fetch thông tin người dùng
            fetchUserProfile();
        } else {
            setIsLoading(false);
        }
    }, []);

    const fetchUserProfile = async () => {
        try {
            const storedToken = authService.getToken();
            if (storedToken) {
                // Parse thông tin từ token thay vì gọi API
                const decodedToken = jwtDecode<DecodedToken>(storedToken);

                // Tạo đối tượng user từ thông tin trong token
                const userData: User = {
                    id: parseInt(decodedToken.sub),
                    email: decodedToken.email,
                    roles: decodedToken.scope.split(',')
                };

                setUser(userData);
            } else {
                throw new Error('Token not found');
            }
        } catch (error) {
            console.error('Error fetching user profile', error);
            // Không gọi logout ở đây để tránh lặp vô hạn nếu logout cũng thất bại
            // Thay vào đó chỉ xóa token và state
            authService.removeToken();
            authService.removeRefreshToken();
            setUser(null);
            setToken(null);
            setRefreshToken(null);
            router.push('/login');
        } finally {
            setIsLoading(false);
        }
    };

    const login = async (email: string, password: string) => {
        setIsLoading(true);
        try {
            // Sử dụng authService để đăng nhập
            const response = await authService.login({ email, password });
            authService.setToken(response.accessToken);
            authService.setRefreshToken(response.refreshToken);
            setToken(response.accessToken);
            setRefreshToken(response.refreshToken);

            // Parse thông tin user từ token
            const decodedToken = jwtDecode<DecodedToken>(response.accessToken);
            const userData: User = {
                id: parseInt(decodedToken.sub),
                email: decodedToken.email,
                roles: decodedToken.scope.split(',')
            };

            setUser(userData);
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

    const logout = async () => {
        const currentRefreshToken = authService.getRefreshToken();
        if (currentRefreshToken) {
            await authService.logout(currentRefreshToken);
        }
        // Xóa các token khỏi localStorage
        authService.removeToken();
        authService.removeRefreshToken();
        // Cập nhật state
        setUser(null);
        setToken(null);
        setRefreshToken(null);
        router.push('/login');
    };

    const logoutAll = async () => {
        await authService.logoutAll();
        // Xóa các token khỏi localStorage
        authService.removeToken();
        authService.removeRefreshToken();
        // Cập nhật state
        setUser(null);
        setToken(null);
        setRefreshToken(null);
        router.push('/login');
    };

    const refreshTokenManually = async () => {
        if (!refreshToken) return;

        setIsLoading(true);
        try {
            const response = await authService.refreshToken(refreshToken);
            // Lưu token mới vào localStorage
            authService.setToken(response.accessToken);
            authService.setRefreshToken(response.refreshToken);
            // Cập nhật state
            setToken(response.accessToken);
            setRefreshToken(response.refreshToken);

            // Parse thông tin user từ token mới
            const decodedToken = jwtDecode<DecodedToken>(response.accessToken);
            const userData: User = {
                id: parseInt(decodedToken.sub),
                email: decodedToken.email,
                roles: decodedToken.scope.split(',')
            };

            setUser(userData);
        } catch (error) {
            console.error('Token refresh error:', error);
            // Đăng xuất nếu refresh thất bại
            await logout();
        } finally {
            setIsLoading(false);
        }
    };

    /**
     * Kiểm tra xem người dùng có quyền (role) cụ thể không
     */
    const hasRole = (role: string): boolean => {
        if (!user || !user.roles) return false;
        return user.roles.includes(role);
    };

    /**
     * Lấy thời gian hết hạn của token
     */
    const getTokenExpiration = (): Date | null => {
        if (!token) return null;
        try {
            const decodedToken = jwtDecode<DecodedToken>(token);
            return new Date(decodedToken.exp * 1000); // Convert UNIX timestamp to Date
        } catch (error) {
            console.error('Error decoding token expiration:', error);
            return null;
        }
    };

    return (
        <AuthContext.Provider value={{
            user,
            token,
            refreshToken,
            isAuthenticated,
            isLoading,
            login,
            logout,
            logoutAll,
            register,
            verifyOtp,
            refreshTokenManually,
            hasRole,
            getTokenExpiration
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