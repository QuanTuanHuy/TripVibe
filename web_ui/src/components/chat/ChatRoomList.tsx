'use client';

import React from 'react';
import { format } from 'date-fns';
import { vi } from 'date-fns/locale';
import { ChatRoom } from '@/types/chat';
import { cn } from '@/lib/utils';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';

interface ChatRoomListProps {
  chatRooms: ChatRoom[];
  activeRoomId: number | null;
  currentUserId: number | null;
  onRoomSelect: (roomId: number) => void;
  onCreateRoom?: () => void;
  loading?: boolean;
  className?: string;
}

export const ChatRoomList: React.FC<ChatRoomListProps> = ({
  chatRooms,
  activeRoomId,
  currentUserId,
  onRoomSelect,
  onCreateRoom,
  loading = false,
  className
}) => {
  const formatLastMessageTime = (timestamp: number) => {
    const date = new Date(timestamp);
    const now = new Date();
    const diffInHours = (now.getTime() - date.getTime()) / (1000 * 60 * 60);

    if (diffInHours < 24) {
      return format(date, 'HH:mm', { locale: vi });
    } else if (diffInHours < 168) { // 7 days
      return format(date, 'EEE', { locale: vi });
    } else {
      return format(date, 'dd/MM', { locale: vi });
    }
  };

  const getUserInitials = (name: string) => {
    return name
      .split(' ')
      .map(word => word[0])
      .join('')
      .toUpperCase()
      .slice(0, 2);
  };
  const getRoomDisplayName = (room: ChatRoom) => {
    // Generate name from participants (excluding current user)
    const otherParticipants = room.participants.filter(p => p.id !== currentUserId);
    if (otherParticipants.length > 0) {
      return otherParticipants.map(p => p.name).join(', ');
    }
    return `Booking #${room.bookingId}`;
  };

  const getRoomDescription = (room: ChatRoom) => {
    const subject = room.lastMessage?.senderId === currentUserId
      ? 'Bạn:'
      : room.participants.find(p => p.id !== currentUserId)?.name || 'Khách';

    if (room.lastMessage?.content) {
      // Truncate long messages
      const content = room.lastMessage.content;
      return content.length > 50 ? `${content.substring(0, 50)}...` : content;
    } else if (room.lastMessage?.mediaType === 'image') {
      return `${subject} đã gửi một hình ảnh`;
    } else if (room.lastMessage?.mediaDataId) {
      return `${subject} đã gửi một tệp tin`;
    }
    return 'Chưa có tin nhắn';
  };

  if (loading) {
    return (
      <div className={cn("space-y-2 p-4", className)}>
        {Array.from({ length: 5 }).map((_, index) => (
          <div key={index} className="flex items-center space-x-3 p-3 animate-pulse">
            <div className="w-12 h-12 bg-muted rounded-full"></div>
            <div className="flex-1 space-y-2">
              <div className="h-4 bg-muted rounded w-3/4"></div>
              <div className="h-3 bg-muted rounded w-1/2"></div>
            </div>
          </div>
        ))}
      </div>
    );
  }

  return (
    <div className={cn("h-full flex flex-col", className)}>
      {/* Header */}
      <div className="flex items-center justify-between p-4 border-b">
        <h2 className="text-lg font-semibold">Tin nhắn</h2>
        {onCreateRoom && (
          <Button
            variant="ghost"
            size="sm"
            onClick={onCreateRoom}
            className="h-8 w-8 p-0"
          >
            <svg className="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
            </svg>
          </Button>
        )}
      </div>

      {/* Chat rooms list */}
      <div className="flex-1 overflow-y-auto">
        {chatRooms.length === 0 ? (
          <div className="flex flex-col items-center justify-center h-full text-center text-muted-foreground p-8">
            <div className="w-16 h-16 bg-muted rounded-full flex items-center justify-center mb-4">
              <svg className="w-8 h-8" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" />
              </svg>
            </div>
            <p className="text-sm font-medium">Chưa có cuộc trò chuyện nào</p>
            <p className="text-xs mt-1">Bắt đầu cuộc trò chuyện đầu tiên của bạn!</p>
            {onCreateRoom && (
              <Button
                variant="outline"
                size="sm"
                onClick={onCreateRoom}
                className="mt-4"
              >
                Tạo cuộc trò chuyện mới
              </Button>
            )}
          </div>
        ) : (
          <div className="space-y-1 p-2">
            {chatRooms.map((room) => (
              <div
                key={room.id}
                onClick={() => onRoomSelect(room.id)}
                className={cn(
                  "flex items-center gap-3 p-3 rounded-lg cursor-pointer transition-colors hover:bg-muted/50",
                  activeRoomId === room.id && "bg-muted"
                )}
              >
                {/* Room avatar */}
                <div className="relative">
                  <Avatar className="w-12 h-12">
                    <AvatarImage
                      src={`/api/chat-rooms/${room.id}/avatar`}
                      alt={getRoomDisplayName(room)}
                    />
                    <AvatarFallback>
                      {getUserInitials(getRoomDisplayName(room))}
                    </AvatarFallback>
                  </Avatar>
                  {/* Online indicator */}
                  {room.participants && room.participants.length > 0 && (
                    <div className="absolute -bottom-1 -right-1 w-4 h-4 bg-green-500 border-2 border-background rounded-full"></div>
                  )}
                </div>

                {/* Room info */}
                <div className="flex-1 min-w-0">
                  <div className="flex items-center justify-between gap-2">
                    <h3 className="font-medium text-sm truncate">
                      {getRoomDisplayName(room)}
                    </h3>
                    {room.lastMessage && (
                      <span className="text-xs text-muted-foreground flex-shrink-0">
                        {formatLastMessageTime(room.lastMessage.createdAt)}
                      </span>
                    )}
                  </div>

                  <div className="flex items-center justify-between gap-2 mt-1">
                    <p className="text-xs text-muted-foreground truncate">
                      {getRoomDescription(room)}
                    </p>

                    {/* Unread count */}
                    {room.unreadCount && room.unreadCount > 0 && (
                      <Badge
                        variant="destructive"
                        className="h-5 min-w-5 px-1.5 text-xs flex-shrink-0"
                      >
                        {room.unreadCount > 99 ? '99+' : room.unreadCount}
                      </Badge>
                    )}
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default ChatRoomList;
