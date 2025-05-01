"use client";

import { useState } from 'react';
import Link from 'next/link';
import { FcGoogle } from 'react-icons/fc';
import { FaApple, FaFacebook } from 'react-icons/fa';
import { useAuth } from '@/context/AuthContext';
import { useTheme } from '@/components/theme-provider';
import { Moon, Sun } from 'lucide-react';

export default function LoginForm() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState('');
  const { login, isLoading } = useAuth();
  const { theme, setTheme } = useTheme();

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

  const toggleTheme = () => {
    setTheme(theme === 'light' ? 'dark' : 'light');
  };

  return (
    <div className="w-full max-w-md p-6 bg-white dark:bg-gray-800 rounded-lg shadow-md">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-bold text-gray-900 dark:text-white">Đăng nhập hoặc tạo tài khoản</h1>
        <button
          onClick={toggleTheme}
          className="p-2 rounded-full hover:bg-gray-100 dark:hover:bg-gray-700"
          aria-label="Toggle theme"
        >
          {theme === 'light' ? (
            <Moon className="h-5 w-5 text-gray-600 dark:text-gray-400" />
          ) : (
            <Sun className="h-5 w-5 text-gray-600 dark:text-gray-400" />
          )}
        </button>
      </div>

      <p className="mb-4 text-sm text-gray-600 dark:text-gray-300">
        Bạn có thể đăng nhập tài khoản để truy cập các dịch vụ của chúng tôi.
      </p>

      <form onSubmit={handleLogin}>
        {/* Email input */}
        <div className="mb-4">
          <label className="block text-sm mb-2 text-gray-700 dark:text-gray-300">Địa chỉ email</label>
          <input
            type="email"
            className={`w-full p-2 border ${
              error && !showPassword ? 'border-red-500' : 'border-gray-300 dark:border-gray-600'
            } rounded bg-white dark:bg-gray-700 text-gray-900 dark:text-white focus:ring-blue-500 focus:border-blue-500`}
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
            className="w-full bg-blue-600 hover:bg-blue-700 text-white p-3 rounded font-medium mb-4"
            onClick={handleContinueWithEmail}
          >
            Tiếp tục với email
          </button>
        ) : (
          <>
            {/* Password input - hiển thị sau khi nhập email */}
            <div className="mb-4">
              <label className="block text-sm mb-2 text-gray-700 dark:text-gray-300">Mật khẩu</label>
              <input
                type="password"
                className={`w-full p-2 border ${
                  error ? 'border-red-500' : 'border-gray-300 dark:border-gray-600'
                } rounded bg-white dark:bg-gray-700 text-gray-900 dark:text-white focus:ring-blue-500 focus:border-blue-500`}
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
              <Link href="/forgot-password" className="text-sm text-blue-600 dark:text-blue-400 hover:underline">
                Quên mật khẩu?
              </Link>
            </div>

            {/* Login button */}
            <button
              type="submit"
              className={`w-full bg-blue-600 hover:bg-blue-700 text-white p-3 rounded font-medium mb-4 ${
                isLoading ? 'opacity-70 cursor-not-allowed' : 'cursor-pointer'
              }`}
              disabled={isLoading}
            >
              {isLoading ? 'Đang đăng nhập...' : 'Đăng nhập'}
            </button>

            <div className="text-center mb-4">
              <p className="text-sm text-gray-700 dark:text-gray-300">
                Chưa có tài khoản?{' '}
                <Link href="/register" className="text-blue-600 dark:text-blue-400 hover:underline">
                  Đăng ký ngay
                </Link>
              </p>
            </div>
          </>
        )}
      </form>

      {/* Divider */}
      <div className="flex items-center my-4">
        <div className="flex-grow border-t border-gray-300 dark:border-gray-600"></div>
        <span className="px-4 text-sm text-gray-500 dark:text-gray-400">hoặc sử dụng một trong các lựa chọn này</span>
        <div className="flex-grow border-t border-gray-300 dark:border-gray-600"></div>
      </div>

      {/* Social login buttons */}
      <div className="grid grid-cols-1 gap-2 mb-6">
        <button className="flex items-center justify-center border border-gray-300 dark:border-gray-600 rounded p-2 bg-white dark:bg-gray-700 text-gray-800 dark:text-white hover:bg-gray-50 dark:hover:bg-gray-600">
          <FcGoogle className="text-2xl mr-2" />
          <span>Google</span>
        </button>

        <button className="flex items-center justify-center border border-gray-300 dark:border-gray-600 rounded p-2 bg-white dark:bg-gray-700 text-gray-800 dark:text-white hover:bg-gray-50 dark:hover:bg-gray-600">
          <FaApple className="text-2xl mr-2 dark:text-white" />
          <span>Apple</span>
        </button>

        <button className="flex items-center justify-center border border-gray-300 dark:border-gray-600 rounded p-2 bg-white dark:bg-gray-700 text-gray-800 dark:text-white hover:bg-gray-50 dark:hover:bg-gray-600">
          <FaFacebook className="text-2xl mr-2 text-blue-600" />
          <span>Facebook</span>
        </button>
      </div>

      {/* Terms */}
      <div className="text-xs text-center text-gray-500 dark:text-gray-400">
        <p>Qua việc đăng nhập hoặc tạo tài khoản, bạn đồng ý với các</p>
        <p className="mb-1">
          <Link href="/terms" className="text-blue-600 dark:text-blue-400 hover:underline">Điều khoản</Link> và
          <Link href="/privacy" className="text-blue-600 dark:text-blue-400 hover:underline"> Điều kiện</Link> cũng như
          <Link href="/privacy-policy" className="text-blue-600 dark:text-blue-400 hover:underline"> Chính sách bảo mật</Link> và
          <Link href="/cookies" className="text-blue-600 dark:text-blue-400 hover:underline"> Bảo mật của chúng tôi</Link>
        </p>
      </div>
    </div>
  );
}