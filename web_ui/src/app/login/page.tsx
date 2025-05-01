"use client";

import { useEffect } from 'react';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import LoginForm from '@/components/auth/LoginForm';
import { useAuth } from '@/context/AuthContext';

export default function Login() {
  const { user, isAuthenticated } = useAuth();
  const router = useRouter();
  
  // Kiểm tra nếu đã đăng nhập thì chuyển hướng về trang chủ
  useEffect(() => {
    if (user && isAuthenticated) {
      router.push('/');
    }
  }, [user, isAuthenticated, router]);
  
  return (
    <div className="min-h-screen bg-white flex flex-col text-gray-800">
      {/* Header */}
      <header className="bg-blue-800 p-4">
        <div className="container mx-auto">
          <Link href="/" className="text-white text-2xl font-bold">
            Booking System
          </Link>
        </div>
      </header>
      
      {/* Login Form */}
      <main className="flex-grow flex justify-center items-start py-8">
        <LoginForm />
      </main>
    
    </div>
  );
}