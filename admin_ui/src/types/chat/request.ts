import { MessageType } from './chat';

export interface ChatRoomQueryParams {
  userId?: number;
  chatUserId?: number;
  page?: number;
  pageSize?: number;
}

export interface MessageQueryParams {
  limit?: number;
  direction?: 'newer' | 'older';
  cursor?: number;
}

export interface SendMessageParams {
  content: string;
  messageType?: MessageType;
}

export interface SendMediaMessageParams {
  file: File;
}

export interface CreateChatRoomRequest {
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
}
