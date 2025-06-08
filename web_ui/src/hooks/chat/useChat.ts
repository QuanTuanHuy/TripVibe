import { useEffect, useCallback } from 'react';
import { useWebSocket } from './useWebSocket';
import { useMessages } from './useMessages';
import { useTyping } from './useTyping';
import { WSIncomingMessage, MessageType } from '@/types/chat';
import { userStore } from '@/stores/userStore';

interface UseChatProps {
    roomId: number | null;
    currentUserId: number | null;
    autoConnect?: boolean;
}

export const useChat = ({ roomId, currentUserId, autoConnect = true }: UseChatProps) => {
    // Initialize hooks
    const webSocket = useWebSocket(autoConnect ? roomId : null);
    const messages = useMessages(roomId);
    const typing = useTyping(roomId, currentUserId);  // Set typing sender from WebSocket - only run when roomId changes

    useEffect(() => {
        console.log('Setting typing sender for roomId:', roomId);
        typing.setTypingSender(webSocket.sendTyping);
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [roomId]); // Only depend on roomId to avoid infinite loops

    // Handle incoming WebSocket messages
    const handleWebSocketMessage = useCallback((message: WSIncomingMessage) => {
        switch (message.type) {
            case 'new_message':
                if (message.data && message.roomId === roomId) {
                    // Store user information for future use
                    if (message.data.senderId && message.data.senderName) {
                        userStore.setUser(message.data.senderId, message.data.senderName);
                    }

                    // Add new message to local state
                    const newMessage = {
                        id: message.data.id,
                        chatRoomId: message.data.chatRoomId || roomId || 0,
                        senderId: message.data.senderId,
                        senderName: message.data.senderName,
                        content: message.data.content,
                        type: message.data.type || MessageType.TEXT,
                        createdAt: message.data.createdAt || Date.now(),
                        updatedAt: message.data.updatedAt || Date.now(),
                        isRead: message.data.isRead || false,
                        mediaDataId: message.data.mediaDataId,
                        mediaUrl: message.data.mediaUrl,
                        mediaType: message.data.mediaType
                    };

                    messages.addMessage(newMessage);

                    // Auto-mark as read if sent by current user
                    if (message.data.senderId === currentUserId) {
                        messages.updateMessage(newMessage.id, { isRead: true });
                    }
                }
                break;
                
            case 'typing_indicator':
                console.log('Received typing_indicator:', message);
                if (message.data && message.roomId === roomId) {
                    const userId = message.data.userId;
                    const userName = message.data.userName || userStore.getUser(userId);

                    console.log('Processing typing indicator:', {
                        userId,
                        userName,
                        isTyping: message.data.isTyping,
                        currentUserId
                    });

                    if (message.data.isTyping) {
                        typing.addTypingUser({
                            userId,
                            userName,
                            isTyping: true
                        });
                    } else {
                        typing.removeTypingUser(userId);
                    }
                } else {
                    console.log('Ignoring typing indicator - wrong room or no data');
                }
                break;

            case 'message_read':
                if (message.data && message.roomId === roomId) {
                    messages.updateMessage(message.data.messageId, { isRead: true });
                }
                break;

            case 'user_connected':
            case 'user_disconnected':
                // Handle user presence if needed
                console.log(`User ${message.data?.userId} ${message.type.replace('user_', '')}`);
                break;

            default: console.log('Unhandled WebSocket message:', message);
        }
    }, [roomId, currentUserId, messages, typing]); // Include the entire hook objects  // Subscribe to WebSocket messages - only when connection status or roomId changes

    useEffect(() => {
        if (webSocket.isConnected) {
            webSocket.addMessageHandler(handleWebSocketMessage);
            return () => {
                webSocket.removeMessageHandler(handleWebSocketMessage);
            };
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [webSocket.isConnected, roomId]); // Only depend on connection status and roomId

    const startTyping = useCallback(() => {
        if (!roomId || !currentUserId) return;

        try {
            webSocket.sendTyping(true);
        } catch (error) {
            console.error('Failed to send typing start:', error);
        }
        typing.startTyping();
    }, [roomId, currentUserId, webSocket, typing]);

    const stopTyping = useCallback(() => {
        if (!roomId || !currentUserId) return;

        try {
            webSocket.sendTyping(false);
        } catch (error) {
            console.error('Failed to send typing stop:', error);
        }
        typing.handleStopTyping();
    }, [roomId, currentUserId, webSocket, typing]);

    // Enhanced send message with WebSocket and HTTP fallback
    const sendMessage = useCallback(async (content: string, messageType: MessageType = MessageType.TEXT) => {
        if (!roomId || !content.trim()) return null;

        try {
            // First try WebSocket for instant delivery
            if (webSocket.isConnected) {
                webSocket.sendMessage(content, messageType);
            } else {
                return await messages.sendMessage(content, messageType)
            }
        } catch (error) {
            console.error('Failed to send message:', error);
            throw error;
        }
    }, [roomId, webSocket, messages]);

    // Enhanced send media message
    const sendMediaMessage = useCallback(async (file: File) => {
        if (!roomId) return null;

        try {
            // Media messages always go through HTTP API
            const sentMessage = await messages.sendMediaMessage(file);

            return sentMessage;
        } catch (error) {
            console.error('Failed to send media message:', error);
            throw error;
        }
    }, [roomId, messages]);

    // Mark message as read with WebSocket notification
    const markMessageAsRead = useCallback(async (messageId: number) => {
        if (!roomId) return;

        try {
            // Send read receipt via WebSocket
            if (webSocket.isConnected) {
                webSocket.sendReadReceipt(messageId);
            } else {
                await messages.markMessageAsRead(messageId);
            }
        } catch (error) {
            console.error('Failed to mark message as read:', error);
        }
    }, [roomId, webSocket, messages]);

    // Connect to a specific room
    const connectToRoom = useCallback(async (newRoomId: number) => {
        if (webSocket.isConnected && webSocket.getCurrentRoomId() === newRoomId) {
            return; // Already connected to this room
        }

        try {
            await webSocket.connect(newRoomId);
        } catch (error) {
            console.error('Failed to connect to room:', error);
            throw error;
        }
    }, [webSocket]);

    // Disconnect from current room
    const disconnect = useCallback(() => {
        webSocket.disconnect();
        typing.cleanup();
    }, [webSocket, typing]);  // Cleanup on unmount - use ref to avoid infinite loops

    useEffect(() => {
        return () => {
            // Direct cleanup without dependency on disconnect function
            webSocket.disconnect();
            typing.cleanup();
        };
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []); // Empty dependency array - only run on mount/unmount

    return {
        // WebSocket state
        isConnected: webSocket.isConnected,
        connectionError: webSocket.error,

        // Messages
        messages: messages.messages,
        messagesLoading: messages.loading,
        messagesError: messages.error,
        hasMoreMessages: messages.hasMore,
        loadingMoreMessages: messages.loadingMore,

        // Typing
        typingUsers: typing.typingUsers,
        isTyping: typing.isTyping,
        typingMessage: typing.getTypingMessage(),
        hasTypingUsers: typing.hasTypingUsers,

        // Actions
        sendMessage,
        sendMediaMessage,
        markMessageAsRead,
        markAllAsRead: messages.markAllAsRead,
        loadMoreMessages: messages.loadMoreMessages,
        connectToRoom,
        disconnect,

        // Typing actions
        startTyping: startTyping,
        stopTyping: stopTyping,

        // Utility
        refetchMessages: messages.refetch,
        reconnect: webSocket.reconnect,
        getUnreadMessages: messages.getUnreadMessages,
        getMessagesBySender: messages.getMessagesBySender
    };
};
