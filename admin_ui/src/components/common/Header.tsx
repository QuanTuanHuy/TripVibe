"use client";

import { useState, useEffect } from "react";
import Link from "next/link";
import {
    Menu,
    X,
    MessageCircle,
    Moon,
    Sun,
    Search
} from "lucide-react";
import { MainNavigation } from "./MainNavigation";
import { UserMenu } from "./UserMenu";
import { NotificationMenu } from "./NotificationMenu";
import { useTheme } from "@/components/theme-provider";

export function Header() {
    const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);
    const [scrolled, setScrolled] = useState(false);
    const { theme, setTheme } = useTheme();

    // Effect to handle scroll for sticky header with shadow
    useEffect(() => {
        const handleScroll = () => {
            if (window.scrollY > 10) {
                setScrolled(true);
            } else {
                setScrolled(false);
            }
        };

        window.addEventListener("scroll", handleScroll);
        return () => window.removeEventListener("scroll", handleScroll);
    }, []);

    const toggleTheme = () => {
        setTheme(theme === "light" ? "dark" : "light");
    };

    return (
        <header
            className={`fixed top-0 left-0 w-full bg-white dark:bg-gray-900 z-50 transition-all duration-200 ${scrolled ? "shadow-md" : ""
                }`}
        >
            {/* Top Bar - Logo, Search, User Actions */}
            <div className="border-b border-gray-200 dark:border-gray-800">
                <div className="max-w-[1920px] mx-auto px-4 md:px-6">
                    <div className="flex h-16 items-center justify-between">
                        {/* Logo and Mobile Menu Button */}
                        <div className="flex items-center">
                            <button
                                className="inline-flex items-center justify-center mr-3 rounded-md md:hidden"
                                onClick={() => setIsMobileMenuOpen(!isMobileMenuOpen)}
                            >
                                {isMobileMenuOpen ? (
                                    <X className="h-6 w-6" />
                                ) : (
                                    <Menu className="h-6 w-6" />
                                )}
                                <span className="sr-only">
                                    {isMobileMenuOpen ? "Close menu" : "Open menu"}
                                </span>
                            </button>

                            {/* Logo */}
                            <Link href="/dashboard" className="flex items-center">
                                <div className="relative h-10 w-10 mr-2 rounded-lg overflow-hidden bg-gradient-to-br from-blue-500 to-blue-700 flex items-center justify-center shadow-sm">
                                    <span className="text-white font-bold text-xl">T</span>
                                </div>
                                <span className="hidden md:inline-block font-semibold text-lg text-gray-800 dark:text-gray-100">TripVibe Manager</span>
                            </Link>
                        </div>

                        {/* Search Bar - Show on medium screens and up */}
                        <div className="hidden md:flex flex-1 max-w-md mx-6 lg:mx-12">
                            <div className="relative w-full">
                                <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
                                    <Search className="w-4 h-4 text-gray-400" />
                                </div>
                                <input
                                    type="search"
                                    className="block w-full pl-10 pr-3 py-2 rounded-lg text-sm bg-gray-100 dark:bg-gray-800 border-0 focus:outline-none focus:ring-2 focus:ring-blue-500 placeholder-gray-400 dark:placeholder-gray-500"
                                    placeholder="Tìm kiếm đặt phòng, khách sạn, phòng..."
                                    aria-label="Tìm kiếm"
                                />
                            </div>
                        </div>

                        {/* Right side icons */}
                        <div className="flex items-center space-x-1 md:space-x-3">
                            {/* Theme Toggle */}
                            <button
                                onClick={toggleTheme}
                                className="p-2 rounded-full hover:bg-gray-100 dark:hover:bg-gray-800 transition-colors"
                                title={theme === "light" ? "Chuyển sang chế độ tối" : "Chuyển sang chế độ sáng"}
                            >
                                {theme === "light" ? (
                                    <Moon className="h-5 w-5 text-gray-600" />
                                ) : (
                                    <Sun className="h-5 w-5 text-gray-200" />
                                )}
                            </button>

                            {/* Notification Icon */}
                            <NotificationMenu />

                            {/* Messages Icon with badge */}
                            <Link
                                href="/inbox"
                                className="relative p-2 rounded-full hover:bg-gray-100 dark:hover:bg-gray-800 transition-colors"
                                title="Tin nhắn"
                            >
                                <MessageCircle className="h-5 w-5 text-gray-600 dark:text-gray-300" />
                                <span className="absolute top-1.5 right-1.5 flex h-2 w-2 items-center justify-center rounded-full bg-red-500 ring-2 ring-white dark:ring-gray-900"></span>
                            </Link>

                            {/* User Menu */}
                            <UserMenu />
                        </div>
                    </div>
                </div>
            </div>

            {/* Bottom Bar - Main Navigation */}
            <div className="hidden md:block border-b border-gray-100 dark:border-gray-800 bg-white dark:bg-gray-900">
                <div className="max-w-[1920px] mx-auto">
                    <MainNavigation />
                </div>
            </div>

            {/* Mobile Navigation */}
            {isMobileMenuOpen && (
                <div className="fixed inset-0 top-16 z-50 bg-white dark:bg-gray-900 md:hidden overflow-y-auto">
                    <div className="flex flex-col h-full pb-20 pt-4">
                        <MainNavigation isMobile={true} />
                    </div>
                </div>
            )}
        </header>
    );
}