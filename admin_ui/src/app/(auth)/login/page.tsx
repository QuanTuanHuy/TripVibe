"use client";

import { useEffect } from 'react';
import { useRouter } from 'next/navigation';
import LoginForm from '@/components/auth/LoginForm';
import { useAuth } from '@/context/AuthContext';

export default function Login() {
    const { user, isAuthenticated } = useAuth();
    const router = useRouter();

    // Kiểm tra nếu đã đăng nhập thì chuyển hướng về trang chủ
    useEffect(() => {
        if (user && isAuthenticated) {
            router.push('/dashboard');
        }
    }, [user, isAuthenticated, router]);

    return (
        <div className="min-h-screen text-gray-800 dark:text-gray-200 bg-gray-100 dark:bg-gray-900 transition-colors duration-200">
            {/* Main content*/}
            <main className="flex-grow flex items-center justify-center p-6">
                <div className="w-full max-w-md">
                    <LoginForm />
                </div>
            </main>
        </div>
    );
}