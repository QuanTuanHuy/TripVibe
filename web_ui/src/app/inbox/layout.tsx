'use client';

import React from 'react';
import Header from '@/components/Header/Header';

interface InboxLayoutProps {
    children: React.ReactNode;
}

export default function InboxLayout({ children }: InboxLayoutProps) {
    return (
        <div className="h-screen flex flex-col overflow-hidden">
            <Header />

            <div className="flex-1 overflow-hidden">
                {children}
            </div>
        </div>
    );
}
