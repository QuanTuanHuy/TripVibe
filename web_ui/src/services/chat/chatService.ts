import apiClient from '../api.client';
import {
    ChatRoomQueryParams,
    MessageQueryParams,
    SendMessageParams,
    CreateChatRoomRequest,
    GetChatRoomResponse,
    GetMessagesResponse,
    MessageEntity,
    UnreadMessagesResponse,
    ChatRoomEntity,
    ChatRoom,
    Message
} from '@/types/chat';
import {
    transformChatRoomEntity,
    transformMessageEntity
} from '@/types/chat/transformers';

const CHAT_PATH = '/chat_service/api/public/v1/chats';
const CHAT_ROOMS_API = `${CHAT_PATH}/rooms`;

export class ChatService {
    /**
     * Lấy danh sách chat rooms
     */
    async getChatRooms(params?: ChatRoomQueryParams): Promise<GetChatRoomResponse> {
        const queryParams = new URLSearchParams();

        if (params?.userId) {
            queryParams.append('userId', params.userId.toString());
        }
        if (params?.chatUserId) {
            queryParams.append('chatUserId', params.chatUserId.toString());
        }
        if (params?.page) {
            queryParams.append('page', params.page.toString());
        }
        if (params?.pageSize) {
            queryParams.append('pageSize', params.pageSize.toString());
        }

        const endpoint = queryParams.toString()
            ? `${CHAT_ROOMS_API}?${queryParams.toString()}`
            : CHAT_ROOMS_API;

        return await apiClient.get<GetChatRoomResponse>(endpoint);
    }

    /**
     * Lấy tin nhắn trong room
     */
    async getMessages(roomId: number, params?: MessageQueryParams): Promise<GetMessagesResponse> {
        const queryParams = new URLSearchParams();

        if (params?.limit) {
            queryParams.append('limit', params.limit.toString());
        }
        if (params?.direction) {
            queryParams.append('direction', params.direction);
        }
        if (params?.cursor) {
            queryParams.append('cursor', params.cursor.toString());
        }

        const endpoint = queryParams.toString()
            ? `${CHAT_ROOMS_API}/${roomId}/messages?${queryParams.toString()}`
            : `${CHAT_ROOMS_API}/${roomId}/messages`;

        return await apiClient.get<GetMessagesResponse>(endpoint);
    }

    /**
     * Gửi tin nhắn text
     */
    async sendMessage(roomId: number, params: SendMessageParams): Promise<MessageEntity> {
        return await apiClient.post<MessageEntity>(
            `${CHAT_ROOMS_API}/${roomId}/send_message`,
            params
        );
    }

    /**
     * Gửi tin nhắn media
     */
    async sendMediaMessage(roomId: number, file: File): Promise<MessageEntity> {
        const formData = new FormData();
        formData.append('file', file);

        return await apiClient.post<MessageEntity>(
            `${CHAT_ROOMS_API}/${roomId}/send_media`,
            formData,
            {
                headers: {
                    'Content-Type': 'multipart/form-data',
                },
            }
        );
    }

    /**
     * Đánh dấu tin nhắn đã đọc
     */
    async markMessageAsRead(roomId: number, messageId: number): Promise<{ status: string }> {
        return await apiClient.post<{ status: string }>(
            `${CHAT_ROOMS_API}/${roomId}/messages/${messageId}/read`,
            {}
        );
    }

    /**
     * Đếm tin nhắn chưa đọc
     */
    async countUnreadMessages(roomId: number): Promise<UnreadMessagesResponse> {
        return await apiClient.get<UnreadMessagesResponse>(
            `${CHAT_ROOMS_API}/${roomId}/unread`
        );
    }

    /**
     * Tạo chat room mới (cho booking mới)
     */
    async createChatRoom(params: CreateChatRoomRequest): Promise<ChatRoomEntity> {
        return await apiClient.post<ChatRoomEntity>(CHAT_ROOMS_API, params);
    }

    /**
     * Lấy thông tin chi tiết một chat room
     */
    async getChatRoom(roomId: number): Promise<ChatRoomEntity> {
        return await apiClient.get<ChatRoomEntity>(`${CHAT_ROOMS_API}/${roomId}`);
    }

    /**
     * Tìm kiếm chat rooms theo từ khóa
     */
    async searchChatRooms(query: string, params?: ChatRoomQueryParams): Promise<GetChatRoomResponse> {
        const queryParams = new URLSearchParams();
        queryParams.append('q', query);

        if (params?.userId) {
            queryParams.append('userId', params.userId.toString());
        }
        if (params?.page) {
            queryParams.append('page', params.page.toString());
        }
        if (params?.pageSize) {
            queryParams.append('pageSize', params.pageSize.toString());
        }

        return await apiClient.get<GetChatRoomResponse>(
            `${CHAT_ROOMS_API}/search?${queryParams.toString()}`
        );
    }

    /**
     * Lấy tổng số tin nhắn chưa đọc của user
     */
    async getTotalUnreadCount(): Promise<{ totalUnreadCount: number }> {
        return await apiClient.get<{ totalUnreadCount: number }>(`${CHAT_PATH}/unread/total`);
    }

    // ============ TRANSFORMED DATA METHODS ============
    // Các methods này trả về dữ liệu đã được transform để sử dụng trong UI

    /**
     * Lấy danh sách chat rooms đã được transform cho UI
     */
    async getChatRoomsForUI(params?: ChatRoomQueryParams): Promise<{
        chatRooms: ChatRoom[];
        totalItems: number;
        totalPages: number;
        currentPage: number;
        pageSize: number;
        previousPage?: number;
        nextPage?: number;
    }> {
        const response = await this.getChatRooms(params);
        return {
            ...response,
            chatRooms: response.chatRooms.map(transformChatRoomEntity)
        };
    }

    /**
     * Lấy tin nhắn đã được transform cho UI
     */
    async getMessagesForUI(roomId: number, params?: MessageQueryParams): Promise<{
        messages: Message[];
        pagination: any;
        roomId: number;
        meta: any;
    }> {
        const response = await this.getMessages(roomId, params);
        return {
            ...response,
            messages: response.messages.map(transformMessageEntity)
        };
    }

    /**
     * Gửi tin nhắn và trả về message đã transform
     */
    async sendMessageForUI(roomId: number, params: SendMessageParams): Promise<Message> {
        const messageEntity = await this.sendMessage(roomId, params);
        return transformMessageEntity(messageEntity);
    }

    /**
     * Gửi media message và trả về message đã transform
     */
    async sendMediaMessageForUI(roomId: number, file: File): Promise<Message> {
        const messageEntity = await this.sendMediaMessage(roomId, file);
        return transformMessageEntity(messageEntity);
    }

    /**
     * Lấy thông tin chi tiết một chat room đã transform
     */
    async getChatRoomForUI(roomId: number): Promise<ChatRoom> {
        const roomEntity = await this.getChatRoom(roomId);
        return transformChatRoomEntity(roomEntity);
    }
}

// Export singleton instance
export const chatService = new ChatService();
export default chatService;
