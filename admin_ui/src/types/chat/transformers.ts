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


export const transformParticipantEntity = (entity: ParticipantEntity): User => {
  return {
    id: entity.userId,
    name: entity.userName,
    role: entity.role,
  };
};


export const transformChatRoomEntities = (entities: ChatRoomEntity[]): ChatRoom[] => {
  return entities.map(transformChatRoomEntity);
};


export const transformMessageEntities = (entities: MessageEntity[]): Message[] => {
  return entities.map(transformMessageEntity);
};
