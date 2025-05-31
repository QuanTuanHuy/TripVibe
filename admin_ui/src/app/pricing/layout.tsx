import type { Metadata } from "next";
import "../../app/globals.css";
import { Header } from "@/components/common/Header";
import { ThemeProvider } from "@/components/theme-provider";
import { AuthProvider } from "@/context/AuthContext";

export const metadata: Metadata = {
    title: "Quản lý Giá Phòng",
    description: "Quản lý giá phòng theo ngày cho khách sạn và chỗ nghỉ của bạn",
};

export default function PricingLayout({
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