"use client";

import { useState, useEffect, useRef } from 'react';
import { useRouter } from 'next/navigation';
import { useAuth } from '@/context/AuthContext';

const Header: React.FC = () => {
  const router = useRouter();
  const { user, isAuthenticated, logout } = useAuth();
  const [showDropdown, setShowDropdown] = useState(false);
  const dropdownRef = useRef<HTMLDivElement>(null);

  const getInitial = () => {
    if (user?.name && user.name.trim() !== '') {
      return user.name.charAt(0).toUpperCase();
    } else if (user?.email) {
      return user.email.charAt(0).toUpperCase();
    }
    return 'U';
  };

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
        setShowDropdown(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, []);

  const handleLoginClick = () => {
    router.push('/login');
  };

  const handleRegisterClick = () => {
    router.push('/register');
  };

  const handleAccountClick = () => {
    if (isAuthenticated) {
      router.push('/myaccount');
    } else {
      router.push('/login');
    }
  };

  const handleHomeClick = () => {
    router.push('/');
  };

  const handleLogout = () => {
    logout();
    setShowDropdown(false);
  };

  return (
    <header className="bg-[#003b95] text-white">
      <div className="container mx-auto px-4 py-3">
        <div className="flex flex-col md:flex-row items-center justify-between">
          <div
            className="text-2xl font-bold cursor-pointer"
            onClick={handleHomeClick}>
            Booking Admin
          </div>
          <div className="flex items-center space-x-6 mt-2 md:mt-0 flex-wrap justify-center">
            <div className="flex items-center">
              <span>VND</span>
              <div className="mx-2 h-6 w-6 rounded-full overflow-hidden">
                <div className="bg-red-500 h-full relative">
                  <div className="absolute inset-0 flex items-center justify-center text-yellow-300 text-xs">★</div>
                </div>
              </div>
            </div>
            <button className="p-2 rounded-full bg-blue-700 cursor-pointer hover:bg-blue-600 transition duration-200">
              <span className="sr-only">Help</span>
              <span className="text-xl">?</span>
            </button>

            {isAuthenticated ? (
              <>
                <div
                  className="cursor-pointer hover:underline"
                  onClick={handleAccountClick}
                >
                  Đăng chỗ nghỉ của Quý vị
                </div>

                <div className="relative" ref={dropdownRef}>
                  <div
                    className="flex items-center space-x-2 cursor-pointer"
                    onClick={() => setShowDropdown(!showDropdown)}
                  >
                    <div className="h-10 w-10 rounded-full bg-yellow-500 flex items-center justify-center font-bold border-2 border-yellow-400">
                      {getInitial()}
                    </div>
                    <span className="hidden md:inline-block font-medium">
                      {user?.name || user?.email?.split('@')[0] || 'Tài khoản'}
                    </span>
                  </div>

                  {showDropdown && (
                    <div className="absolute right-0 mt-2 w-64 bg-white rounded-lg shadow-lg z-50 text-gray-800">
                      <div className="p-3 border-b border-gray-200">
                        <div className="flex items-center">
                          <div className="h-10 w-10 rounded-full bg-yellow-500 flex items-center justify-center font-bold mr-3">
                            {getInitial()}
                          </div>
                          <div>
                            <p className="font-bold">{user?.name || 'Khách hàng'}</p>
                            <p className="text-sm text-gray-600">{user?.email}</p>
                            <p className="text-sm text-blue-600">Genius Cấp 1</p>
                          </div>
                        </div>
                      </div>

                      <div className="py-2">
                        <button
                          onClick={() => { router.push('/myaccount'); setShowDropdown(false); }}
                          className="w-full text-left px-4 py-2 hover:bg-gray-100 flex items-center"
                        >
                          <svg className="w-5 h-5 mr-3" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                          </svg>
                          Tài khoản
                        </button>
                        <button
                          onClick={() => { router.push('/myaccount/bookings'); setShowDropdown(false); }}
                          className="w-full text-left px-4 py-2 hover:bg-gray-100 flex items-center"
                        >
                          <svg className="w-5 h-5 mr-3" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                          </svg>
                          Đặt chỗ & Chuyến đi
                        </button>
                        <button
                          onClick={() => { router.push('/rewards'); setShowDropdown(false); }}
                          className="w-full text-left px-4 py-2 hover:bg-gray-100 flex items-center"
                        >
                          <svg className="w-5 h-5 mr-3" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                          </svg>
                          Thưởng & Ưu đãi
                        </button>
                        <button
                          onClick={() => { router.push('/reviews'); setShowDropdown(false); }}
                          className="w-full text-left px-4 py-2 hover:bg-gray-100 flex items-center"
                        >
                          <svg className="w-5 h-5 mr-3" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11.049 2.927c.3-.921 1.603-.921 1.902 0l1.519 4.674a1 1 0 00.95.69h4.915c.969 0 1.371 1.24.588 1.81l-3.976 2.888a1 1 0 00-.363 1.118l1.518 4.674c.3.922-.755 1.688-1.538 1.118l-3.976-2.888a1 1 0 00-1.176 0l-3.976 2.888c-.783.57-1.838-.197-1.538-1.118l1.518-4.674a1 1 0 00-.363-1.118l-3.976-2.888c-.784-.57-.38-1.81.588-1.81h4.914a1 1 0 00.951-.69l1.519-4.674z" />
                          </svg>
                          Đánh giá
                        </button>
                        <button
                          onClick={() => { router.push('/saved'); setShowDropdown(false); }}
                          className="w-full text-left px-4 py-2 hover:bg-gray-100 flex items-center"
                        >
                          <svg className="w-5 h-5 mr-3" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" />
                          </svg>
                          Đã lưu
                        </button>
                        <button
                          onClick={handleLogout}
                          className="w-full text-left px-4 py-2 hover:bg-gray-100 flex items-center"
                        >
                          <svg className="w-5 h-5 mr-3" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
                          </svg>
                          Đăng xuất
                        </button>
                      </div>
                    </div>
                  )}
                </div>
              </>
            ) : (
              <>
                <div
                  className="cursor-pointer hover:underline"
                  onClick={handleAccountClick}
                >
                  Đăng chờ nghỉ của Quý vị
                </div>
                <button
                  className="px-4 py-2 bg-white text-blue-800 rounded-md font-medium cursor-pointer"
                  onClick={handleRegisterClick}
                >
                  Đăng ký
                </button>
                <button
                  className="px-4 py-2 border border-white rounded-md font-medium cursor-pointer"
                  onClick={handleLoginClick}
                >
                  Đăng nhập
                </button>
              </>
            )}
          </div>
        </div>
      </div>
    </header>
  );
};

export default Header;