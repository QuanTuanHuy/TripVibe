import { useState, useCallback, useRef } from 'react';
import { TypingUser } from '@/types/chat';

export const useTyping = (roomId: number | null, currentUserId: number | null) => {
    const [typingUsers, setTypingUsers] = useState<TypingUser[]>([]);
    const [isTyping, setIsTyping] = useState(false);
    const typingTimeoutRef = useRef<NodeJS.Timeout | undefined>(undefined);
    const sendTypingRef = useRef<((isTyping: boolean) => void) | null>(null);
    // Create stable cleanup function reference
    const cleanupRef = useRef<() => void>(() => { });

    // Set the typing sender function (from useWebSocket)
    const setTypingSender = useCallback((sendTypingFn: (isTyping: boolean) => void) => {
        sendTypingRef.current = sendTypingFn;
    }, []);

    const addTypingUser = useCallback((user: TypingUser) => {
        if (!currentUserId || user.userId === currentUserId) {
            return; // Don't show current user as typing
        }

        setTypingUsers(prev => {
            const existingIndex = prev.findIndex(u => u.userId === user.userId);
            if (existingIndex >= 0) {
                return prev.map((u, index) =>
                    index === existingIndex ? user : u
                );
            }
            return [...prev, user];
        });
    }, [currentUserId]);

    const removeTypingUser = useCallback((userId: number) => {
        setTypingUsers(prev => prev.filter(user => user.userId !== userId));
    }, []);

    // Create stable wrapper functions with empty dependency arrays
    const handleTyping = useCallback(() => {
        console.log('handleTyping called, sendTypingRef.current:', !!sendTypingRef.current);
        if (!sendTypingRef.current) return;

        setIsTyping(prev => {
            console.log('handleTyping - current isTyping state:', prev);
            if (prev) return prev; // Already typing, don't send again

            console.log('Sending typing start event');
            sendTypingRef.current!(true);

            // Clear any existing timeout
            if (typingTimeoutRef.current) {
                clearTimeout(typingTimeoutRef.current);
            }

            // Set timeout to stop typing indicator
            typingTimeoutRef.current = setTimeout(() => {
                if (sendTypingRef.current) {
                    console.log('Auto-stopping typing after timeout');
                    setIsTyping(false);
                    sendTypingRef.current(false);
                    typingTimeoutRef.current = undefined;
                }
            }, 3000); // Stop typing after 3 seconds of inactivity

            return true;
        });
    }, []); // Empty deps to avoid infinite loops

    const handleStopTyping = useCallback(() => {
        console.log('handleStopTyping called, sendTypingRef.current:', !!sendTypingRef.current);
        if (!sendTypingRef.current) return;

        setIsTyping(prev => {
            console.log('handleStopTyping - current isTyping state:', prev);
            if (!prev) return prev; // Don't send if not already typing

            console.log('Sending typing stop event');
            sendTypingRef.current!(false);

            if (typingTimeoutRef.current) {
                clearTimeout(typingTimeoutRef.current);
                typingTimeoutRef.current = undefined;
            }

            return false;
        });
    }, []); // Empty deps to avoid infinite loops// Cleanup function - create once and reuse the same reference

    if (!cleanupRef.current) {
        cleanupRef.current = () => {
            setTypingUsers([]);
            setIsTyping(false);
            if (typingTimeoutRef.current) {
                clearTimeout(typingTimeoutRef.current);
                typingTimeoutRef.current = undefined;
            }
        };
    }

    // Get formatted typing message
    const getTypingMessage = useCallback(() => {
        if (typingUsers.length === 0) return '';

        if (typingUsers.length === 1) {
            return `${typingUsers[0].userName} đang gõ...`;
        } else if (typingUsers.length === 2) {
            return `${typingUsers[0].userName} và ${typingUsers[1].userName} đang gõ...`;
        } else {
            return `${typingUsers[0].userName} và ${typingUsers.length - 1} người khác đang gõ...`;
        }
    }, [typingUsers]);

    return {
        typingUsers,
        isTyping,
        addTypingUser,
        removeTypingUser,
        startTyping: handleTyping,
        stopTyping: handleStopTyping,
        handleStopTyping,
        setTypingSender,
        cleanup: cleanupRef.current,
        getTypingMessage,
        hasTypingUsers: typingUsers.length > 0
    };
};
