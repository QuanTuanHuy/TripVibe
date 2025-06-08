import { useState, useEffect, useCallback } from 'react';
import { chatService } from '@/services/chat';
import { Message, MessageQueryParams } from '@/types/chat';

export const useMessages = (roomId: number | null, params?: MessageQueryParams) => {
    const [messages, setMessages] = useState<Message[]>([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [hasMore, setHasMore] = useState(true);
    const [pagination, setPagination] = useState<any>(null);
    const [loadingMore, setLoadingMore] = useState(false);

    const fetchMessages = useCallback(async (loadMore = false) => {
        if (!roomId) return;

        try {
            if (loadMore) {
                setLoadingMore(true);
            } else {
                setLoading(true);
                setMessages([]); // Clear existing messages when fetching fresh
            }
            setError(null);

            const queryParams = loadMore && pagination?.cursor
                ? { ...params, cursor: pagination.cursor, direction: 'newer' as const }
                : { ...params, direction: 'newer' as const };

            const response = await chatService.getMessagesForUI(roomId, queryParams);

            if (loadMore) {
                setMessages(prev => [...response.messages, ...prev]);
            } else {
                setMessages(response.messages);
            }

            setPagination(response.pagination);
            setHasMore(response.meta?.hasMore || false);
        } catch (err) {
            setError(err instanceof Error ? err.message : 'Failed to fetch messages');
            console.error('Failed to fetch messages:', err);
        } finally {
            setLoading(false);
            setLoadingMore(false);
        }
    }, [roomId, params, pagination?.cursor]);

    useEffect(() => {
        if (roomId) {
            // Reset state when roomId changes
            setMessages([]);
            setPagination(null);
            setHasMore(true);
            fetchMessages();
        } else {
            setMessages([]);
            setPagination(null);
            setHasMore(true);
        }
    }, [fetchMessages, roomId]); 

    const addMessage = useCallback((newMessage: Message) => {
        setMessages(prev => {
            // Check if message already exists
            const exists = prev.some(msg => msg.id === newMessage.id);
            if (!exists) {
                return [...prev, newMessage];
            }
            return prev;
        });
    }, []);

    const updateMessage = useCallback((messageId: number, updates: Partial<Message>) => {
        setMessages(prev =>
            prev.map(msg =>
                msg.id === messageId ? { ...msg, ...updates } : msg
            )
        );
    }, []);

    const markMessageAsRead = useCallback(async (messageId: number) => {
        if (!roomId) return;

        try {
            await chatService.markMessageAsRead(roomId, messageId);
            updateMessage(messageId, { isRead: true });
        } catch (err) {
            console.error('Failed to mark message as read:', err);
        }
    }, [roomId, updateMessage]);

    const loadMoreMessages = useCallback(() => {
        if (hasMore && !loading && !loadingMore && pagination) {
            fetchMessages(true);
        }
    }, [hasMore, loading, loadingMore, pagination, fetchMessages]);

    const sendMessage = useCallback(async (content: string, messageType = 'text') => {
        if (!roomId) return null;

        try {
            const newMessage = await chatService.sendMessageForUI(roomId, {
                content,
                messageType: messageType as any
            });

            addMessage(newMessage);
            return newMessage;
        } catch (err) {
            console.error('Failed to send message:', err);
            throw err;
        }
    }, [roomId, addMessage]);

    const sendMediaMessage = useCallback(async (file: File) => {
        if (!roomId) return null;

        try {
            const newMessage = await chatService.sendMediaMessageForUI(roomId, file);
            addMessage(newMessage);
            return newMessage;
        } catch (err) {
            console.error('Failed to send media message:', err);
            throw err;
        }
    }, [roomId, addMessage]);

    const refetch = useCallback(() => {
        setMessages([]);
        setPagination(null);
        setHasMore(true);
        return fetchMessages(false);
    }, [fetchMessages]);

    // Get messages by sender
    const getMessagesBySender = useCallback((senderId: number) => {
        return messages.filter(msg => msg.senderId === senderId);
    }, [messages]);

    // Get unread messages
    const getUnreadMessages = useCallback(() => {
        return messages.filter(msg => !msg.isRead);
    }, [messages]);

    // Mark all messages as read
    const markAllAsRead = useCallback(async () => {
        if (!roomId) return;

        const unreadMessages = getUnreadMessages();

        try {
            await Promise.all(
                unreadMessages.map(msg =>
                    chatService.markMessageAsRead(roomId, msg.id)
                )
            );

            // Update local state
            unreadMessages.forEach(msg => {
                updateMessage(msg.id, { isRead: true });
            });
        } catch (err) {
            console.error('Failed to mark all messages as read:', err);
        }
    }, [roomId, getUnreadMessages, updateMessage]);

    return {
        messages,
        loading,
        loadingMore,
        error,
        hasMore,
        addMessage,
        updateMessage,
        markMessageAsRead,
        markAllAsRead,
        loadMoreMessages,
        sendMessage,
        sendMediaMessage,
        refetch,
        getMessagesBySender,
        getUnreadMessages
    };
};
