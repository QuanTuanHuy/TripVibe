"use client";

import { useState } from 'react';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { FcGoogle } from 'react-icons/fc';
import { FaApple, FaFacebook } from 'react-icons/fa';

export default function Login() {
  const [email, setEmail] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');
  const router = useRouter();
  
  const handleContinue = () => {
    if (!email) {
      setError('Vui lòng nhập địa chỉ email');
      return;
    }
    
    if (!/\S+@\S+\.\S+/.test(email)) {
      setError('Vui lòng nhập địa chỉ email hợp lệ');
      return;
    }
    
    setIsLoading(true);
    setError('');
    
    // Giả lập gửi yêu cầu đăng nhập/đăng ký
    setTimeout(() => {
      // Lưu email vào localStorage để có thể sử dụng ở trang OTP
      localStorage.setItem('userEmail', email);
      
      // Điều hướng đến trang xác thực OTP
      router.push('/verify-otp');
      
      setIsLoading(false);
    }, 1000);
  };
  
  return (
    <div className="min-h-screen bg-white flex flex-col text-gray-800">
      {/* Header */}
      <header className="bg-blue-800 p-4">
        <div className="container mx-auto">
          <Link href="/" className="text-white text-2xl font-bold">
            Booking.com
          </Link>
        </div>
      </header>
      
      {/* Login Form */}
      <main className="flex-grow flex justify-center items-start py-8">
        <div className="w-full max-w-md p-6">
          <h1 className="text-2xl font-bold text-center mb-6">Đăng nhập hoặc tạo tài khoản</h1>
          
          <p className="mb-4 text-sm">
            Bạn có thể đăng nhập tài khoản Booking.com của mình để truy cập các dịch vụ của chúng tôi.
          </p>
          
          {/* Email input */}
          <div className="mb-4">
            <label className="block text-sm mb-2">Địa chỉ email</label>
            <input
              type="email"
              className={`w-full p-2 border ${error ? 'border-red-500' : 'border-gray-300'} rounded`}
              placeholder="Nhập địa chỉ email của bạn"
              value={email}
              onChange={(e) => {
                setEmail(e.target.value);
                if (error) setError('');
              }}
            />
            {error && <p className="text-red-500 text-xs mt-1">{error}</p>}
          </div>
          
          {/* Continue button */}
          <button 
            className={`w-full bg-blue-500 text-white p-3 rounded font-medium mb-4 ${isLoading ? 'opacity-70 cursor-not-allowed' : ''}`}
            onClick={handleContinue}
            disabled={isLoading}
          >
            {isLoading ? 'Đang xử lý...' : 'Tiếp tục với email'}
          </button>
          
          {/* Divider */}
          <div className="flex items-center my-4">
            <div className="flex-grow border-t border-gray-300"></div>
            <span className="px-4 text-sm text-gray-500">hoặc sử dụng một trong các lựa chọn này</span>
            <div className="flex-grow border-t border-gray-300"></div>
          </div>
          
          {/* Social login buttons */}
          <div className="grid grid-cols-1 gap-2 mb-6">
            <button className="flex items-center justify-center border border-gray-300 rounded p-2">
              <FcGoogle className="text-2xl mr-2" />
              <span>Google</span>
            </button>
            
            <button className="flex items-center justify-center border border-gray-300 rounded p-2">
              <FaApple className="text-2xl mr-2" />
              <span>Apple</span>
            </button>
            
            <button className="flex items-center justify-center border border-gray-300 rounded p-2">
              <FaFacebook className="text-2xl mr-2 text-blue-600" />
              <span>Facebook</span>
            </button>
          </div>
          
          {/* Terms */}
          <div className="text-xs text-center text-gray-500">
            <p>Qua việc đăng nhập hoặc tạo tài khoản, bạn đồng ý với các</p>
            <p className="mb-1">
              <Link href="/terms" className="text-blue-500">Điều khoản</Link> và 
              <Link href="/privacy" className="text-blue-500"> Điều kiện</Link> cũng như 
              <Link href="/privacy-policy" className="text-blue-500"> Chính sách bảo mật</Link> và 
              <Link href="/cookies" className="text-blue-500"> Bảo mật của chúng tôi</Link>
            </p>
            <p className="mt-4">Bản quyền (2006 - 2025) - Booking.com™</p>
          </div>
        </div>
      </main>
    </div>
  );
}