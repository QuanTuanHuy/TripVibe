import {
  ChatRoom,
  Message,
  User
} from './chat';
import {
  ChatRoomEntity,
  MessageEntity,
  ParticipantEntity
} from './response';

/**
 * Chuyển đổi ChatRoomEntity từ backend thành ChatRoom cho frontend
 */
export const transformChatRoomEntity = (entity: ChatRoomEntity): ChatRoom => {
  return {
    id: entity.id,
    bookingId: entity.bookingId,
    lastMessage: entity.lastMessage ? transformMessageEntity(entity.lastMessage) : undefined,
    lastMessageTime: entity.lastMessage?.createdAt,
    unreadCount: 0, // Will be fetched separately
    participants: entity.participants?.map(transformParticipantEntity) || [],
    createdAt: entity.createdAt,
    updatedAt: entity.updatedAt,
  };
};

/**
 * Chuyển đổi MessageEntity từ backend thành Message cho frontend
 */
export const transformMessageEntity = (entity: MessageEntity): Message => {
  return {
    id: entity.id,
    chatRoomId: entity.chatRoomId,
    senderId: entity.senderId,
    senderName: undefined, // Will be resolved from participants
    content: entity.content,
    type: entity.type,
    createdAt: entity.createdAt,
    updatedAt: entity.updatedAt,
    isRead: entity.isRead,
    mediaDataId: entity.mediaDataId,
    mediaUrl: entity.mediaData?.url,
    mediaType: entity.mediaData?.mediaType,
    mimeType: entity.mediaData?.mimeType,
    mediaName: entity.mediaData?.fileName,
    mediaSize: entity.mediaData?.fileSize,
  };
};

/**
 * Chuyển đổi ParticipantEntity từ backend thành User cho frontend
 */
export const transformParticipantEntity = (entity: ParticipantEntity): User => {
  return {
    id: entity.userId,
    name: entity.userName,
    role: entity.role,
  };
};

/**
 * Chuyển đổi array ChatRoomEntity thành array ChatRoom
 */
export const transformChatRoomEntities = (entities: ChatRoomEntity[]): ChatRoom[] => {
  return entities.map(transformChatRoomEntity);
};

/**
 * Chuyển đổi array MessageEntity thành array Message
 */
export const transformMessageEntities = (entities: MessageEntity[]): Message[] => {
  return entities.map(transformMessageEntity);
};
