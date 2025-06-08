export interface User {
  id: number;
  name: string;
  avatar?: string;
  email?: string;
  role?: UserRole;
}

export enum UserRole {
  TOURIST = 'TOURIST',
  OWNER = 'OWNER',
  ADMIN = 'ADMIN'
}

export interface ChatRoom {
  id: number;
  bookingId: number;
  lastMessage?: Message;
  lastMessageTime?: number;
  unreadCount: number;
  participants: User[];
  createdAt: number;
  updatedAt: number;
}

export interface Message {
  id: number;
  chatRoomId: number;
  senderId?: number;
  senderName?: string;
  content: string;
  type: MessageType;
  createdAt: number;
  updatedAt: number;
  isRead: boolean;
  mediaDataId?: number;
  mediaUrl?: string;
  mediaType?: string;
}

export enum MessageType {
  TEXT = 'text',
  MEDIA = 'media',
  SYSTEM = 'system'
}

export interface TypingUser {
  userId: number;
  userName: string;
  isTyping: boolean;
}
