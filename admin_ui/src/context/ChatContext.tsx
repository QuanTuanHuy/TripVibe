'use client';

import React, { createContext, useContext, ReactNode } from 'react';
import { useChat } from '@/hooks/chat';
import { Message, TypingUser, MessageType } from '@/types/chat';

interface ChatContextValue {
  // WebSocket state
  isConnected: boolean;
  connectionError: string | null;

  // Messages
  messages: Message[];
  messagesLoading: boolean;
  messagesError: string | null;
  hasMoreMessages: boolean;
  loadingMoreMessages: boolean;

  // Typing
  typingUsers: TypingUser[];
  isTyping: boolean;
  typingMessage: string;
  hasTypingUsers: boolean;

  // Actions
  sendMessage: (content: string, messageType?: MessageType) => Promise<Message | null | undefined>;
  sendMediaMessage: (file: File) => Promise<Message | null | undefined>;
  markMessageAsRead: (messageId: number) => Promise<void>;
  markAllAsRead: () => Promise<void>;
  loadMoreMessages: () => void;
  connectToRoom: (roomId: number) => Promise<void>;
  disconnect: () => void;

  // Typing actions
  startTyping: () => void;
  stopTyping: () => void;

  // Utility
  refetchMessages: () => Promise<void>;
  reconnect: () => void;
  getUnreadMessages: () => Message[];
  getMessagesBySender: (senderId: number) => Message[];
}

const ChatContext = createContext<ChatContextValue | undefined>(undefined);

interface ChatProviderProps {
  children: ReactNode;
  roomId: number | null;
  currentUserId: number | null;
  autoConnect?: boolean;
}

export const ChatProvider: React.FC<ChatProviderProps> = ({
  children,
  roomId,
  currentUserId,
  autoConnect = true
}) => {
  const chatState = useChat({
    roomId,
    currentUserId,
    autoConnect
  });

  return (
    <ChatContext.Provider value={chatState}>
      {children}
    </ChatContext.Provider>
  );
};

export const useChatContext = (): ChatContextValue => {
  const context = useContext(ChatContext);
  if (context === undefined) {
    throw new Error('useChatContext must be used within a ChatProvider');
  }
  return context;
};

export default ChatContext;