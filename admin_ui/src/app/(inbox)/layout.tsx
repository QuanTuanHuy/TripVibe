"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { MessageSquare, ChevronLeft } from "lucide-react";
import { cn } from "@/lib/utils";
import { AuthProvider } from "@/context/AuthContext";
import { ThemeProvider } from "@/components/theme-provider";

interface InboxLayoutProps {
    children: React.ReactNode;
}

export default function InboxLayout({ children }: InboxLayoutProps) {
    const pathname = usePathname();

    return (
        <div className="flex flex-col h-screen">
            <ThemeProvider attribute="class" defaultTheme="light">
                <AuthProvider>
                    {/* Header */}
                    <header className="h-16 border-b flex items-center justify-between px-4">
                        <div className="flex items-center gap-2">
                            <Link href="/dashboard" className="flex items-center text-muted-foreground hover:text-foreground transition-colors">
                                <ChevronLeft className="h-5 w-5 mr-1" />
                                <span>Quay lại Dashboard</span>
                            </Link>
                        </div>
                        <div className="flex items-center gap-4">
                            <Link
                                href="/inbox"
                                className={cn(
                                    "flex items-center gap-1 px-3 py-2 rounded-md transition-colors",
                                    pathname === "/inbox" ? "bg-primary text-primary-foreground" : "hover:bg-muted"
                                )}
                            >
                                <MessageSquare className="h-4 w-4" />
                                <span>Tin nhắn</span>
                            </Link>
                        </div>
                    </header>

                    {/* Main content */}
                    <main className="flex-1 overflow-hidden">
                        {children}
                    </main>
                </AuthProvider>
            </ThemeProvider>
        </div>
    );
}