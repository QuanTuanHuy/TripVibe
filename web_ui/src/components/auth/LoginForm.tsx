"use client";

import { useState } from 'react';
import Link from 'next/link';
import { FcGoogle } from 'react-icons/fc';
import { FaApple, FaFacebook } from 'react-icons/fa';
import { useAuth } from '@/context/AuthContext';

export default function LoginForm() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState('');
  const { login, isLoading } = useAuth();

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    if (!email) {
      setError('Vui lòng nhập địa chỉ email');
      return;
    }

    if (!/\S+@\S+\.\S+/.test(email)) {
      setError('Vui lòng nhập địa chỉ email hợp lệ');
      return;
    }

    if (!password) {
      setError('Vui lòng nhập mật khẩu');
      return;
    }

    try {
      await login(email, password);
      // Chuyển hướng sẽ được xử lý trong AuthContext
    } catch (err: any) {
      setError(err.response?.data?.meta?.message || 'Đăng nhập thất bại. Vui lòng kiểm tra email và mật khẩu.');
    }
  };

  const handleContinueWithEmail = () => {
    if (!email) {
      setError('Vui lòng nhập địa chỉ email');
      return;
    }

    if (!/\S+@\S+\.\S+/.test(email)) {
      setError('Vui lòng nhập địa chỉ email hợp lệ');
      return;
    }

    // Hiển thị trường mật khẩu khi email hợp lệ
    setShowPassword(true);
  };

  return (
    <div className="w-full max-w-md p-6">
      <h1 className="text-2xl font-bold text-center mb-6">Đăng nhập hoặc tạo tài khoản</h1>

      <p className="mb-4 text-sm">
        Bạn có thể đăng nhập tài khoản để truy cập các dịch vụ của chúng tôi.
      </p>

      <form onSubmit={handleLogin}>
        {/* Email input */}
        <div className="mb-4">
          <label className="block text-sm mb-2">Địa chỉ email</label>
          <input
            type="email"
            className={`w-full p-2 border ${error && !showPassword ? 'border-red-500' : 'border-gray-300'} rounded`}
            placeholder="Nhập địa chỉ email của bạn"
            value={email}
            onChange={(e) => {
              setEmail(e.target.value);
              if (error) setError('');
            }}
          />
          {error && !showPassword && <p className="text-red-500 text-xs mt-1">{error}</p>}
        </div>

        {!showPassword ? (
          <button
            type="button"
            className="w-full bg-blue-500 text-white p-3 rounded font-medium mb-4"
            onClick={handleContinueWithEmail}
          >
            Tiếp tục với email
          </button>
        ) : (
          <>
            {/* Password input - hiển thị sau khi nhập email */}
            <div className="mb-4">
              <label className="block text-sm mb-2">Mật khẩu</label>
              <input
                type="password"
                className={`w-full p-2 border ${error ? 'border-red-500' : 'border-gray-300'} rounded`}
                placeholder="Nhập mật khẩu của bạn"
                value={password}
                onChange={(e) => {
                  setPassword(e.target.value);
                  if (error) setError('');
                }}
              />
              {error && <p className="text-red-500 text-xs mt-1">{error}</p>}
            </div>

            <div className="text-right mb-2">
              <Link href="/forgot-password" className="text-sm text-blue-500">
                Quên mật khẩu?
              </Link>
            </div>

            {/* Login button */}
            <button
              type="submit"
              className={`w-full bg-blue-500 text-white p-3 rounded font-medium mb-4 ${isLoading ? 'opacity-70 cursor-not-allowed' : 'cursor-pointer'}`}
              disabled={isLoading}
            >
              {isLoading ? 'Đang đăng nhập...' : 'Đăng nhập'}
            </button>

            <div className="text-center mb-4">
              <p className="text-sm">
                Chưa có tài khoản? <Link href="/register" className="text-blue-500">Đăng ký ngay</Link>
              </p>
            </div>
          </>
        )}
      </form>

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
      </div>
    </div>
  );
}