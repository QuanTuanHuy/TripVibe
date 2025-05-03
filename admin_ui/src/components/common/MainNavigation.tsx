"use client";

import React, { useState } from "react";
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
    ChevronDown
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
        href: "/calendar",
        icon: <Calendar className="h-5 w-5" />,
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

    return (
        <nav
            className={cn(
                isMobile
                    ? "flex-col items-start space-y-1 px-4 w-full"
                    : "flex items-center justify-center flex-wrap"
            )}
        >
            {navigationItems.map((item) => {
                const isActive = pathname.startsWith(item.href);
                const isHovered = hoveredItem === item.name;

                return (
                    <Link
                        key={item.name}
                        href={item.href}
                        className={cn(
                            "relative flex items-center justify-center transition-all duration-200",
                            isMobile
                                ? "w-full px-4 py-3 mb-1 rounded-md"
                                : "flex-col h-16 min-w-[80px] px-2"
                        )}
                        onMouseEnter={() => setHoveredItem(item.name)}
                        onMouseLeave={() => setHoveredItem(null)}
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

                        {!isMobile && isActive && (
                            <span className="absolute bottom-0 left-0 right-0 h-0.5 bg-blue-600 rounded-full" />
                        )}

                        {!isMobile && isHovered && !isActive && (
                            <span className="absolute bottom-0 left-0 right-0 h-0.5 bg-gray-300 rounded-full" />
                        )}
                    </Link>
                );
            })}
        </nav>
    );
}