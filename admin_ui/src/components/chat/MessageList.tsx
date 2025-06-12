'use client';

import React, { useRef, useEffect } from 'react';
import Image from 'next/image';
import { format } from 'date-fns';
import { vi } from 'date-fns/locale';
import { Message, MessageType } from '@/types/chat';
import { cn } from '@/lib/utils';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Badge } from '@/components/ui/badge';

interface MessageListProps {
  messages: Message[];
  currentUserId: number | null;
  loading?: boolean;
  hasMore?: boolean;
  onLoadMore?: () => void;
  className?: string;
}

export const MessageList: React.FC<MessageListProps> = ({
  messages,
  currentUserId,
  loading = false,
  hasMore = false,
  onLoadMore,
  className
}) => {
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const scrollContainerRef = useRef<HTMLDivElement>(null);

  // Auto scroll to bottom when new messages arrive
  useEffect(() => {
    const scrollToBottom = () => {
      messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
    };

    const timeoutId = setTimeout(scrollToBottom, 100);
    return () => clearTimeout(timeoutId);
  }, [messages]);

  // Handle infinite scroll
  useEffect(() => {
    const container = scrollContainerRef.current;
    if (!container || !hasMore || loading) return;

    const handleScroll = () => {
      if (container.scrollTop === 0 && onLoadMore) {
        onLoadMore();
      }
    };

    container.addEventListener('scroll', handleScroll);
    return () => container.removeEventListener('scroll', handleScroll);
  }, [hasMore, loading, onLoadMore]);

  const formatMessageTime = (timestamp: number) => {
    return format(new Date(timestamp * 1000), 'HH:mm', { locale: vi });
  };

  const formatMessageDate = (timestamp: number) => {
    const date = new Date(timestamp * 1000);
    const today = new Date();
    const yesterday = new Date(today);
    yesterday.setDate(yesterday.getDate() - 1);

    if (date.toDateString() === today.toDateString()) {
      return 'Hôm nay';
    } else if (date.toDateString() === yesterday.toDateString()) {
      return 'Hôm qua';
    } else {
      return format(date, 'dd/MM/yyyy', { locale: vi });
    }
  };

  const shouldShowDateSeparator = (currentMessage: Message, previousMessage?: Message) => {
    if (!previousMessage) return true;

    const currentDate = new Date(currentMessage.createdAt).toDateString();
    const previousDate = new Date(previousMessage.createdAt).toDateString();

    return currentDate !== previousDate;
  };

  const getUserInitials = (name: string) => {
    return name
      .split(' ')
      .map(word => word[0])
      .join('')
      .toUpperCase()
      .slice(0, 2);
  };

  const renderMessage = (message: Message) => {
    const isOwnMessage = message.senderId === currentUserId;

    return (
      <div
        key={message.id}
        className={cn(
          'flex gap-3 max-w-[80%]',
          isOwnMessage ? 'ml-auto flex-row-reverse' : 'mr-auto'
        )}
      >
        {!isOwnMessage && (
          <Avatar className="w-8 h-8 flex-shrink-0">
            <AvatarImage
              src={`/api/avatars/${message.senderId}`}
              alt={message.senderName}
            />
            <AvatarFallback className="text-xs">
              {getUserInitials(message.senderName || 'Unknown')}
            </AvatarFallback>
          </Avatar>
        )}

        <div className={cn(
          'flex flex-col gap-1',
          isOwnMessage ? 'items-end' : 'items-start'
        )}>
          {!isOwnMessage && (
            <span className="text-xs text-muted-foreground font-medium">
              {message.senderName}
            </span>
          )}

          <div className={cn(
            'rounded-lg px-3 py-2 max-w-sm break-words',
            isOwnMessage
              ? 'bg-primary text-primary-foreground'
              : 'bg-muted'
          )}>
            {message.type === MessageType.TEXT && (
              <p className="text-sm">{message.content}</p>
            )}

            {message.type === MessageType.MEDIA && message.mediaType === 'image' && (
              <div className="space-y-2">
                {message.mediaUrl && (
                  <Image
                    src={message.mediaUrl}
                    alt="Hình ảnh"
                    width={300}
                    height={200}
                    className="rounded max-w-full h-auto max-h-64 object-cover"
                  />
                )}
                {message.content && (
                  <p className="text-sm">{message.content}</p>
                )}
              </div>
            )}

            {message.type === MessageType.MEDIA && message.mediaType !== 'image' && (
              <div className="flex items-center gap-2">
                <div className="p-2 bg-secondary rounded">
                  <svg
                    className="w-4 h-4"
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke="currentColor"
                  >
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                  </svg>
                </div>
                <div className="flex-1 min-w-0">
                  <p className="text-sm font-medium truncate">
                    {message.content || 'File đính kèm'}
                  </p>
                  {message.mediaUrl && (
                    <a
                      href={message.mediaUrl}
                      download
                      className="text-xs text-blue-500 hover:underline"
                    >
                      Tải xuống
                    </a>
                  )}
                </div>
              </div>
            )}
          </div>

          <div className="flex items-center gap-2 text-xs text-muted-foreground">
            <span>{formatMessageTime(message.createdAt)}</span>
            {isOwnMessage && (
              <span className={cn(
                message.isRead ? 'text-blue-500' : 'text-muted-foreground'
              )}>
                {message.isRead ? '✓✓' : '✓'}
              </span>
            )}
          </div>
        </div>
      </div>
    );
  };

  return (
    <div
      ref={scrollContainerRef}
      className={cn(
        'flex flex-col gap-4 p-4 h-full overflow-y-auto scroll-smooth',
        className
      )}
    >
      {/* Loading indicator for older messages */}
      {loading && (
        <div className="text-center py-2">
          <Badge variant="secondary" className="animate-pulse">
            Đang tải...
          </Badge>
        </div>
      )}

      {/* Messages */}
      {messages.map((message, index) => {
        const previousMessage = index > 0 ? messages[index - 1] : undefined;
        const showDateSeparator = shouldShowDateSeparator(message, previousMessage);

        return (
          <React.Fragment key={message.id}>
            {/* Date separator */}
            {showDateSeparator && (
              <div className="flex items-center justify-center py-2">
                <Badge variant="outline" className="text-xs">
                  {formatMessageDate(message.createdAt)}
                </Badge>
              </div>
            )}

            {/* Message */}
            {renderMessage(message)}
          </React.Fragment>
        );
      })}

      {/* Empty state */}
      {messages.length === 0 && !loading && (
        <div className="flex flex-col items-center justify-center h-full text-center text-muted-foreground">
          <div className="w-16 h-16 bg-muted rounded-full flex items-center justify-center mb-4">
            <svg className="w-8 h-8" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" />
            </svg>
          </div>
          <p className="text-sm">Chưa có tin nhắn nào</p>
          <p className="text-xs mt-1">Hãy bắt đầu cuộc trò chuyện!</p>
        </div>
      )}

      {/* Scroll anchor */}
      <div ref={messagesEndRef} />
    </div>
  );
};

export default MessageList;
