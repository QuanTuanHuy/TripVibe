"use client";

import { useState, useRef, useEffect } from "react";
import Link from "next/link";
import { useAuth } from "@/context/AuthContext";
import { 
  User, 
  Settings, 
  LogOut, 
  ChevronDown, 
  UserCog, 
  Building, 
  HelpCircle,
  Loader2 
} from "lucide-react";

export function UserMenu() {
  const [isOpen, setIsOpen] = useState(false);
  const menuRef = useRef<HTMLDivElement | null>(null);
  const auth = useAuth();
  const { user, isLoading, logout } = auth;

  const toggleMenu = () => setIsOpen(!isOpen);

  // Close menu when clicking outside
  useEffect(() => {
    const handleOutsideClick = (event: MouseEvent) => {
      if (menuRef.current && !menuRef.current.contains(event.target as Node)) {
        setIsOpen(false);
      }
    };

    document.addEventListener("mousedown", handleOutsideClick);
    return () => document.removeEventListener("mousedown", handleOutsideClick);
  }, []);

  // Show loading state while authentication is being checked
  if (isLoading) {
    return (
      <div className="flex items-center space-x-1 p-1">
        <Loader2 className="h-5 w-5 animate-spin text-gray-400" />
      </div>
    );
  }

  // If no user is logged in, show a login button
  if (!user) {
    return (
      <Link
        href="/login"
        className="flex items-center space-x-1 px-3 py-2 rounded-md bg-blue-600 text-white hover:bg-blue-700 transition-colors"
      >
        <User className="h-4 w-4" />
        <span>Đăng nhập</span>
      </Link>
    );
  }

  // Extract display properties safely with fallbacks
  const displayName = user?.name || 'Người dùng';
  const displayInitial = displayName.charAt(0);
  const email = user?.email || '';
  const role = 'Chủ sở hữu';
  
  // Handle property display safely
  const propertyName = "Mist Heaven Resort";

  return (
    <div className="relative" ref={menuRef}>
      <button
        onClick={toggleMenu}
        className="flex items-center space-x-1 p-1 rounded-full hover:bg-gray-100 dark:hover:bg-gray-800 transition-colors"
      >
        <div className="relative w-8 h-8 rounded-full bg-blue-100 dark:bg-blue-900 flex items-center justify-center text-blue-600 dark:text-blue-300 font-medium">
          {displayInitial}
        </div>
        <ChevronDown className={`h-4 w-4 text-gray-500 transition-transform ${isOpen ? "rotate-180" : ""}`} />
      </button>

      {isOpen && (
        <div className="absolute right-0 mt-2 w-72 rounded-md shadow-lg bg-white dark:bg-gray-800 ring-1 ring-black ring-opacity-5 z-50">
          <div className="py-2 px-4 border-b border-gray-100 dark:border-gray-700">
            <p className="text-sm font-medium dark:text-gray-200">{displayName}</p>
            <p className="text-xs text-gray-500 dark:text-gray-400">{email}</p>
            <div className="mt-1 flex items-center">
              <span className="inline-flex items-center rounded-full bg-green-50 dark:bg-green-900 px-2 py-1 text-xs font-medium text-green-700 dark:text-green-300">
                {role}
              </span>
            </div>
            {propertyName && (
              <p className="text-xs text-gray-500 dark:text-gray-400 mt-1">{propertyName}</p>
            )}
          </div>
          
          <div className="py-1">
            <Link
              href="/profile"
              className="flex px-4 py-2 text-sm text-gray-700 dark:text-gray-200 hover:bg-gray-100 dark:hover:bg-gray-700 items-center"
              onClick={() => setIsOpen(false)}
            >
              <User className="h-4 w-4 mr-3" />
              Hồ sơ của tôi
            </Link>
            
            <Link
              href="/hotels"
              className="flex px-4 py-2 text-sm text-gray-700 dark:text-gray-200 hover:bg-gray-100 dark:hover:bg-gray-700 items-center"
              onClick={() => setIsOpen(false)}
            >
              <Building className="h-4 w-4 mr-3" />
              Quản lý chỗ nghỉ
            </Link>
            
            <Link
              href="/settings/staff"
              className="flex px-4 py-2 text-sm text-gray-700 dark:text-gray-200 hover:bg-gray-100 dark:hover:bg-gray-700 items-center"
              onClick={() => setIsOpen(false)}
            >
              <UserCog className="h-4 w-4 mr-3" />
              Quản lý nhân viên
            </Link>
            
            <Link
              href="/settings"
              className="flex px-4 py-2 text-sm text-gray-700 dark:text-gray-200 hover:bg-gray-100 dark:hover:bg-gray-700 items-center"
              onClick={() => setIsOpen(false)}
            >
              <Settings className="h-4 w-4 mr-3" />
              Cài đặt
            </Link>
            
            <Link
              href="/help"
              className="flex px-4 py-2 text-sm text-gray-700 dark:text-gray-200 hover:bg-gray-100 dark:hover:bg-gray-700 items-center"
              onClick={() => setIsOpen(false)}
            >
              <HelpCircle className="h-4 w-4 mr-3" />
              Trợ giúp
            </Link>
          </div>
          
          <div className="py-1 border-t border-gray-100 dark:border-gray-700">
            <button
              className="flex w-full px-4 py-2 text-sm text-gray-700 dark:text-gray-200 hover:bg-gray-100 dark:hover:bg-gray-700 items-center"
              onClick={() => {
                try {
                  logout();
                  setIsOpen(false);
                } catch (error) {
                  console.error("Error during logout:", error);
                }
              }}
            >
              <LogOut className="h-4 w-4 mr-3" />
              Đăng xuất
            </button>
          </div>
        </div>
      )}
    </div>
  );
}