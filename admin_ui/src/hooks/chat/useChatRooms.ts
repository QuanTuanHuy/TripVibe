import { useState, useEffect, useCallback } from 'react';
import { chatService } from '@/services/chat';
import { ChatRoom, ChatRoomQueryParams } from '@/types/chat';

export const useChatRooms = (params?: ChatRoomQueryParams) => {
    const [chatRooms, setChatRooms] = useState<ChatRoom[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [totalItems, setTotalItems] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [currentPage, setCurrentPage] = useState(1);

    const fetchChatRooms = useCallback(async (refresh = false) => {
        try {
            if (!refresh) {
                setLoading(true);
            }
            setError(null);

            const response = await chatService.getChatRoomsForUI(params);

            setChatRooms(response.chatRooms);
            setTotalItems(response.totalItems);
            setTotalPages(response.totalPages);
            setCurrentPage(response.currentPage);
        } catch (err) {
            setError(err instanceof Error ? err.message : 'Failed to fetch chat rooms');
            console.error('Failed to fetch chat rooms:', err);
        } finally {
            setLoading(false);
        }
    }, [params]);

    useEffect(() => {
        fetchChatRooms();
    }, [fetchChatRooms]);

    const updateChatRoom = useCallback((roomId: number, updates: Partial<ChatRoom>) => {
        setChatRooms(prev =>
            prev.map(room =>
                room.id === roomId ? { ...room, ...updates } : room
            )
        );
    }, []);

    const addChatRoom = useCallback((newRoom: ChatRoom) => {
        setChatRooms(prev => {
            // Check if room already exists
            const exists = prev.some(room => room.id === newRoom.id);
            if (exists) {
                return prev.map(room => room.id === newRoom.id ? newRoom : room);
            }
            return [newRoom, ...prev];
        });
    }, []);

    const removeChatRoom = useCallback((roomId: number) => {
        setChatRooms(prev => prev.filter(room => room.id !== roomId));
    }, []);

    const updateUnreadCount = useCallback((roomId: number, count: number) => {
        updateChatRoom(roomId, { unreadCount: count });
    }, [updateChatRoom]);

    const updateLastMessage = useCallback((roomId: number, message: any) => {
        updateChatRoom(roomId, {
            lastMessage: message,
            lastMessageTime: message.createdAt
        });
    }, [updateChatRoom]);

    const refetch = useCallback(() => {
        return fetchChatRooms(true);
    }, [fetchChatRooms]);

    // Search functionality
    const searchChatRooms = useCallback(async (query: string) => {
        try {
            setLoading(true);
            setError(null);

            const response = await chatService.searchChatRooms(query, params);
            setChatRooms(response.chatRooms.map(room => ({
                id: room.id,
                bookingId: room.bookingId,
                lastMessage: room.lastMessage ? {
                    id: room.lastMessage.id,
                    chatRoomId: room.lastMessage.chatRoomId,
                    senderId: room.lastMessage.senderId,
                    content: room.lastMessage.content,
                    type: room.lastMessage.type,
                    createdAt: room.lastMessage.createdAt,
                    updatedAt: room.lastMessage.updatedAt,
                    isRead: room.lastMessage.isRead,
                    mediaDataId: room.lastMessage.mediaDataId
                } : undefined,
                lastMessageTime: room.lastMessage?.createdAt,
                unreadCount: 0,
                participants: room.participants?.map(p => ({
                    id: p.userId,
                    name: p.userName,
                    role: p.role
                })) || [],
                createdAt: room.createdAt,
                updatedAt: room.updatedAt
            })));
        } catch (err) {
            setError(err instanceof Error ? err.message : 'Search failed');
            console.error('Search failed:', err);
        } finally {
            setLoading(false);
        }
    }, [params]);

    return {
        chatRooms,
        loading,
        error,
        totalItems,
        totalPages,
        currentPage,
        refetch,
        updateChatRoom,
        addChatRoom,
        removeChatRoom,
        updateUnreadCount,
        updateLastMessage,
        searchChatRooms
    };
};
