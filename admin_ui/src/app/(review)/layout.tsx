import type { Metadata } from "next";
import { Inter } from "next/font/google";
import "../../app/globals.css";
import { Header } from "@/components/common/Header";
import { ThemeProvider } from "@/components/theme-provider";
import { AuthProvider } from "@/context/AuthContext";

const inter = Inter({ subsets: ["latin", "vietnamese"] });

export const metadata: Metadata = {
    title: "BookingManager Admin - Quản lý khách sạn và chỗ nghỉ",
    description: "Hệ thống quản lý dành cho chủ khách sạn và chỗ nghỉ",
};

export default function ReviewLayout({
    children,
}: Readonly<{
    children: React.ReactNode;
}>) {
    return (
        <div className="bg-gray-50 dark:bg-gray-950 text-gray-900 dark:text-gray-100 min-h-screen">
            <ThemeProvider attribute="class" defaultTheme="light">
                <AuthProvider>
                    <div className="min-h-full flex flex-col">
                        <Header />
                        <div className="pt-32 md:pt-32 flex-1"> {/* Padding for the header height */}
                            <main className="max-w-[1920px] mx-auto p-4 md:p-6 h-full">
                                {children}
                            </main>
                        </div>
                    </div>
                </AuthProvider>
            </ThemeProvider>
        </div>
    );
}