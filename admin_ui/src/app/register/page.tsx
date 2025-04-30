"use client";

import { useEffect } from 'react';
import { useRouter } from 'next/navigation';
import RegisterForm from '@/components/auth/RegisterForm';
import { useAuth } from '@/context/AuthContext';

export default function Register() {
  const { user, isAuthenticated } = useAuth();
  const router = useRouter();

  // Nếu đã đăng nhập thì chuyển hướng về trang chủ
  useEffect(() => {
    if (user && isAuthenticated) {
      router.push('/');
    }
  }, [user, isAuthenticated, router]);

  return (
    <div className="min-h-screen bg-white flex flex-col text-gray-800">
      {/* Register Form */}
      <main className="flex-grow flex justify-center items-start py-8">
        <RegisterForm />
      </main>
    </div>
  );
}