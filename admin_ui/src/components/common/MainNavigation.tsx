"use client";

import React, { useState, useEffect, useRef } from "react";
import Link from "next/link";
import { usePathname } from "next/navigation";
import { cn } from "@/lib/utils";
import {
    Home,
    Calendar,
    Percent,
    BookOpen,
    Hotel,
    TrendingUp,
    MessageCircle,
    Star,
    CreditCard,
    BarChart2,
    ChevronDown,
    PlusCircle,
    Settings,
    List,
    Calendar1
} from "lucide-react";

interface MainNavigationProps {
    isMobile?: boolean;
}

// Define navigation items based on Booking.com's admin header structure
const navigationItems = [
    {
        name: "Trang chủ",
        href: "/dashboard",
        icon: <Home className="h-5 w-5" />,
    },
    {
        name: "Lịch & Giá",
        href: "/pricing",
        icon: <Calendar className="h-5 w-5" />,
        subMenu: [
            {
                name: "Lịch",
                href: "/pricing/calendar",
                icon: <Calendar1 className="h-4 w-4" />
            },
        ]
    },
    {
        name: "Chương trình KM",
        href: "/promotions",
        icon: <Percent className="h-5 w-5" />,
    },
    {
        name: "Đặt phòng",
        href: "/bookings",
        icon: <BookOpen className="h-5 w-5" />,
    },
    {
        name: "Chỗ nghỉ",
        href: "/hotels",
        icon: <Hotel className="h-5 w-5" />,
        subMenu: [
            {
                name: "Tổng quan",
                href: "/hotels",
                icon: <List className="h-4 w-4" />
            },
            {
                name: "Quản lý chỗ nghỉ",
                href: "/hotels/manage",
                icon: <Settings className="h-4 w-4" />
            },
            {
                name: "Thêm chỗ nghỉ mới",
                href: "/hotels/new",
                icon: <PlusCircle className="h-4 w-4" />
            }
        ]
    },
    {
        name: "Hiệu suất",
        href: "/performance",
        icon: <TrendingUp className="h-5 w-5" />,
    },
    {
        name: "Hộp thư",
        href: "/inbox",
        icon: <MessageCircle className="h-5 w-5" />,
    },
    {
        name: "Đánh giá",
        href: "/reviews",
        icon: <Star className="h-5 w-5" />,
    },
    {
        name: "Tài chính",
        href: "/payments",
        icon: <CreditCard className="h-5 w-5" />,
    },
    {
        name: "Phân tích",
        href: "/reports",
        icon: <BarChart2 className="h-5 w-5" />,
    },
];

export function MainNavigation({ isMobile = false }: MainNavigationProps) {
    const pathname = usePathname();
    const [hoveredItem, setHoveredItem] = useState<string | null>(null);
    const [openSubMenu, setOpenSubMenu] = useState<string | null>(null);
    const menuRef = useRef<HTMLDivElement>(null);

    const toggleSubMenu = (itemName: string) => {
        if (openSubMenu === itemName) {
            setOpenSubMenu(null);
        } else {
            setOpenSubMenu(itemName);
        }
    };

    // Handler to close submenu when clicking outside
    useEffect(() => {
        const handleClickOutside = (event: MouseEvent) => {
            if (menuRef.current && !menuRef.current.contains(event.target as Node)) {
                setOpenSubMenu(null);
            }
        };

        // Add event listener when component mounts
        document.addEventListener("mousedown", handleClickOutside);

        // Clean up event listener when component unmounts
        return () => {
            document.removeEventListener("mousedown", handleClickOutside);
        };
    }, []);

    // Close submenu when pathname changes (navigation occurs)
    useEffect(() => {
        setOpenSubMenu(null);
    }, [pathname]);

    return (
        <nav
            ref={menuRef}
            className={cn(
                isMobile
                    ? "flex-col items-start space-y-1 px-4 w-full"
                    : "flex items-center justify-center flex-wrap"
            )}
        >
            {navigationItems.map((item) => {
                const isActive = pathname.startsWith(item.href);
                const isHovered = hoveredItem === item.name;
                const hasSubMenu = item.subMenu && item.subMenu.length > 0;
                const isSubMenuOpen = openSubMenu === item.name;

                return (
                    <div key={item.name} className="relative">
                        <div
                            className={cn(
                                "relative flex items-center transition-all duration-200",
                                isMobile
                                    ? "w-full px-4 py-3 mb-1 rounded-md"
                                    : "flex-col h-16 min-w-[80px] px-2"
                            )}
                            onMouseEnter={() => setHoveredItem(item.name)}
                            onMouseLeave={() => setHoveredItem(null)}
                        >
                            {hasSubMenu ? (
                                <button
                                    className="w-full flex items-center justify-center"
                                    onClick={() => toggleSubMenu(item.name)}
                                >
                                    <div
                                        className={cn(
                                            "flex items-center justify-center transition-all",
                                            isMobile ? "flex-row gap-3 w-full justify-between" : "flex-col gap-1"
                                        )}
                                    >
                                        <div className={cn(
                                            "flex items-center",
                                            isMobile ? "gap-3" : "flex-col gap-1"
                                        )}>
                                            <div className={cn(
                                                "relative flex items-center justify-center",
                                                isMobile ? "" : "p-1.5 rounded-full",
                                                isActive ?
                                                    "text-blue-600" :
                                                    "text-gray-500 group-hover:text-blue-500"
                                            )}>
                                                {item.icon}
                                            </div>

                                            <span className={cn(
                                                "text-sm font-medium whitespace-nowrap",
                                                isActive ?
                                                    "text-blue-600" :
                                                    "text-gray-600"
                                            )}>
                                                {item.name}
                                                <ChevronDown
                                                    className={cn(
                                                        "ml-1 inline h-3 w-3 transition-transform",
                                                        isSubMenuOpen ? "rotate-180" : "",
                                                        isActive ? "text-blue-600" : "text-gray-500"
                                                    )}
                                                />
                                            </span>
                                        </div>
                                    </div>
                                </button>
                            ) : (
                                <Link
                                    href={item.href}
                                    className="w-full flex items-center justify-center"
                                >
                                    <div
                                        className={cn(
                                            "flex items-center justify-center transition-all",
                                            isMobile ? "flex-row gap-3" : "flex-col gap-1"
                                        )}
                                    >
                                        <div className={cn(
                                            "relative flex items-center justify-center",
                                            isMobile ? "" : "p-1.5 rounded-full",
                                            isActive ?
                                                "text-blue-600" :
                                                "text-gray-500 group-hover:text-blue-500"
                                        )}>
                                            {item.icon}
                                        </div>

                                        <span className={cn(
                                            "text-sm font-medium whitespace-nowrap",
                                            isActive ?
                                                "text-blue-600" :
                                                "text-gray-600"
                                        )}>
                                            {item.name}
                                        </span>
                                    </div>
                                </Link>
                            )}

                            {!isMobile && isActive && (
                                <span className="absolute bottom-0 left-0 right-0 h-0.5 bg-blue-600 rounded-full" />
                            )}

                            {!isMobile && isHovered && !isActive && (
                                <span className="absolute bottom-0 left-0 right-0 h-0.5 bg-gray-300 rounded-full" />
                            )}
                        </div>

                        {/* SubMenu */}
                        {hasSubMenu && isSubMenuOpen && (
                            <div className={cn(
                                "absolute z-10 bg-white shadow-lg rounded-md py-2",
                                isMobile ? "w-full left-0 mt-1" : "w-48 top-16 left-1/2 -translate-x-1/2"
                            )}>
                                {item.subMenu?.map((subItem) => {
                                    const isSubActive = pathname === subItem.href;

                                    return (
                                        <Link
                                            key={subItem.name}
                                            href={subItem.href}
                                            className={cn(
                                                "flex items-center gap-3 px-4 py-2 text-sm hover:bg-gray-50",
                                                isSubActive ? "text-blue-600 bg-blue-50" : "text-gray-700"
                                            )}
                                            onClick={() => setOpenSubMenu(null)}
                                        >
                                            {subItem.icon}
                                            {subItem.name}
                                        </Link>
                                    );
                                })}
                            </div>
                        )}
                    </div>
                );
            })}
        </nav>
    );
}