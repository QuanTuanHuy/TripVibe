'use client';

import { InboxPage } from '@/components/chat';
import { Suspense } from 'react';
import { LoadingSpinner } from '@/components/ui/loading-spinner';
import { useAuth } from '@/context/AuthContext';

export default function Inbox() {
    const { user } = useAuth();

    const currentUserId = user?.id;

    return (
        <div className="h-screen">
            <Suspense fallback={
                <div className="h-screen flex items-center justify-center">
                    <LoadingSpinner />
                </div>
            }>
                <InboxPage currentUserId={currentUserId} />
            </Suspense>
        </div>
    );
}
