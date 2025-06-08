import { MessageType, UserRole } from './chat';

// API Response types - these match the backend structure exactly
export interface GetChatRoomResponse {
  chatRooms: ChatRoomEntity[];
  totalItems: number;
  totalPages: number;
  currentPage: number;
  pageSize: number;
  previousPage?: number;
  nextPage?: number;
}

// Backend Entity - raw response from API
export interface ChatRoomEntity {
  id: number;
  bookingId: number;
  lastMessageId: number;
  createdAt: number;
  updatedAt: number;
  participants?: ParticipantEntity[];
  lastMessage?: MessageEntity;
}

// Backend Entity - raw message from API
export interface MessageEntity {
  id: number;
  chatRoomId: number;
  senderId?: number;
  content: string;
  type: MessageType;
  createdAt: number;
  updatedAt: number;
  isRead: boolean;
  mediaDataId?: number;
  mediaData?: MediaData
}

export interface MediaData {
  id: number;
  createdAt: number;
  updatedAt: number;
  url: string;
  fileName: string;
  fileSize: number;
  mimeType: string;
  mediaType: string;
}

// Backend Entity - raw participant from API
export interface ParticipantEntity {
  userId: number;
  userName: string;
  role: UserRole;
}

export interface GetMessagesResponse {
  messages: MessageEntity[];
  pagination: PaginationResult;
  roomId: number;
  meta: {
    direction: string;
    hasMore: boolean;
  };
}

export interface PaginationResult {
  cursor?: number;
  hasNext: boolean;
  hasPrevious: boolean;
  hasMore: boolean;
  limit: number;
}

export interface UnreadMessagesResponse {
  unreadCount: number;
}
