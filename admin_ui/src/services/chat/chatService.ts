import apiClient from '../apiClient';

// Path đến chat service thông qua API Gateway
const CHAT_PATH = '/chat_service/api/public/v1';
const CHAT_API = `${CHAT_PATH}/chats`;

export type ChatRoomParams = {
    userId?: number;
    chatUserId?: number;
    page?: number;
    pageSize?: number;
};

export type MessageParams = {
    page?: number;
    pageSize?: number;
};

export type SendMessageParams = {
    content: string;
    senderId: number;
};

export type CreateChatRoomParams = {
    bookingId: number;
    tourist: {
        userId: number;
        name: string;
        avatar?: string;
    };
    owner: {
        userId: number;
        name: string;
        avatar?: string;
    };
};

export const chatService = {
    // Get all chat rooms for a user
    getChatRooms: async (params: ChatRoomParams) => {
        return apiClient.get(`${CHAT_API}/rooms`, { params });
    },

    // Get messages for a chat room
    getChatMessages: async (roomId: number, params?: MessageParams) => {
        return apiClient.get(`${CHAT_API}/rooms/${roomId}/messages`, { params });
    },

    // Send a message in a chat room
    sendMessage: async (roomId: number, data: SendMessageParams) => {
        return apiClient.post(`${CHAT_API}/rooms/${roomId}/send_message`, data);
    },

    // Mark a message as read
    markMessageAsRead: async (roomId: number, messageId: number) => {
        return apiClient.post(`${CHAT_API}/rooms/${roomId}/messages/${messageId}/read`, null);
    },

    // Count unread messages in a chat room
    countUnreadMessages: async (roomId: number) => {
        return apiClient.get(`${CHAT_API}/rooms/${roomId}/unread`);
    },

    // Create a new chat room
    createChatRoom: async (data: CreateChatRoomParams) => {
        return apiClient.post(`${CHAT_API}/rooms`, data);
    }
};