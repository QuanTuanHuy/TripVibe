'use client';

import { Header } from '@/components/common/Header';
import { ThemeProvider } from '@/components/theme-provider';
import { AuthProvider } from '@/context/AuthContext';
import React from 'react';

interface InboxLayoutProps {
    children: React.ReactNode;
}

export default function InboxLayout({
    children,
}: InboxLayoutProps) {
    return (
        <div className="bg-gray-50 dark:bg-gray-950 text-gray-900 dark:text-gray-100 h-screen">
            <ThemeProvider attribute="class" defaultTheme="light">
                <AuthProvider>
                    <div className="h-screen flex flex-col overflow-hidden">
                        <Header />
                        <div className="pt-32 md:pt-32 flex-1">
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