import type { Metadata } from "next";
import { Inter } from "next/font/google";
import { ThemeProvider } from "@/components/theme-provider";
import { AuthProvider } from "@/context/AuthContext";
import "../../app/globals.css";
import { SimpleHeader } from "@/components/common/SimpleHeader";
import { Footer } from "@/components/common/Footer";

const inter = Inter({ subsets: ["latin", "vietnamese"] });

export const metadata: Metadata = {
    title: "Đăng nhập - BookingManager Admin",
    description: "Đăng nhập vào hệ thống quản lý khách sạn và chỗ nghỉ",
};

export default function AuthLayout({
    children,
}: Readonly<{
    children: React.ReactNode;
}>) {
    return (
        <div className="min-h-screen bg-gray-100 dark:bg-gray-900">
            <ThemeProvider attribute="class" defaultTheme="light">
                <AuthProvider>
                    <SimpleHeader />
                    {children}
                    <Footer />
                </AuthProvider>
            </ThemeProvider>
        </div>
    );
}