'use client';

import React, { useState } from 'react';
import { useChatRooms } from '@/hooks/chat';
import { ChatProvider, useChatContext } from '@/context/ChatContext';
import { SidebarProvider } from '@/context/SidebarContext';
import { ChatRoomList } from '@/components/chat/ChatRoomList';
import { MessageList } from '@/components/chat/MessageList';
import { MessageInput } from '@/components/chat/MessageInput';
import { TypingIndicator } from '@/components/chat/TypingIndicator';
import ChatHeader from '@/components/chat/ChatHeader';
import RightSidebar from '@/components/chat/RightSidebar';
import { Button } from '@/components/ui/button';
import { MessageType } from '@/types/chat';
import { Participant } from '@/types/chat/sidebar';

// Active chat component - when a room is selected
interface ActiveChatProps {
  currentUserId: number | null;
  activeRoomId: number;
}

const ActiveChat: React.FC<ActiveChatProps> = ({ currentUserId, activeRoomId }) => {
  const {
    isConnected,
    connectionError,
    messages,
    hasMoreMessages,
    loadingMoreMessages,
    typingUsers,
    hasTypingUsers,
    sendMessage,
    sendMediaMessage,
    loadMoreMessages,
    startTyping,
    stopTyping,
    reconnect
  } = useChatContext();

  const { chatRooms } = useChatRooms();

  const currentRoom = chatRooms.find(room => room.id === activeRoomId);

  // Convert participants to the format expected by Sidebar
  const participants: Participant[] = currentRoom?.participants?.map(p => ({
    id: p.id,
    name: p.name,
    role: p.role as 'GUEST' | 'OWNER' | 'ADMIN',
    isOnline: false, // This should come from user status
    lastSeen: Date.now() - Math.random() * 86400000 // Mock data
  })) || [];

  // Wrapper functions to handle return types
  const handleSendMessage = async (content: string, type?: MessageType) => {
    await sendMessage(content, type);
  };

  const handleSendMediaMessage = async (file: File) => {
    await sendMediaMessage(file);
  }; return (
    <div className="flex-1 flex flex-col min-h-0">
      {/* Chat Header */}
      <ChatHeader
        participants={participants}
        roomId={activeRoomId}
        onCall={() => console.log('Voice call')}
        onVideoCall={() => console.log('Video call')}
      />

      <div className="flex-1 flex min-h-0">
        {/* Messages area */}
        <div className="flex-1 flex flex-col min-h-0">
          <MessageList
            messages={messages}
            currentUserId={currentUserId}
            loading={loadingMoreMessages}
            hasMore={hasMoreMessages}
            onLoadMore={loadMoreMessages}
            className="flex-1"
          />

          {/* Typing indicator */}
          {hasTypingUsers && (
            <TypingIndicator typingUsers={typingUsers} />
          )}

          {/* Message input */}
          <MessageInput
            onSendMessage={handleSendMessage}
            onSendMediaMessage={handleSendMediaMessage}
            onStartTyping={startTyping}
            onStopTyping={stopTyping}
            disabled={!isConnected}
            placeholder={
              !isConnected
                ? "Đang kết nối..."
                : "Nhập tin nhắn..."
            }
          />

          {/* Connection error */}
          {connectionError && (
            <div className="p-3 bg-destructive/10 border-t border-destructive/20">
              <div className="flex items-center justify-between">
                <span className="text-sm text-destructive">
                  Lỗi kết nối: {connectionError}
                </span>
                <Button
                  variant="outline"
                  size="sm"
                  onClick={reconnect}
                  className="text-xs"
                >
                  Thử lại
                </Button>
              </div>
            </div>
          )}
        </div>

        {/* Right Sidebar */}
        <RightSidebar
          roomId={activeRoomId}
          participants={participants}
          messages={messages}
        />
      </div>
    </div>
  );
};

// Main inbox page component
interface InboxPageProps {
  currentUserId?: number | null;
  initialRoomId?: number | null;
}

export const InboxPage: React.FC<InboxPageProps> = ({
  currentUserId = null,
  initialRoomId = null
}) => {
  const [activeRoomId, setActiveRoomId] = useState<number | null>(initialRoomId);

  const effectiveUserId = currentUserId || 1;
  return (
    <ChatProvider
      roomId={activeRoomId}
      currentUserId={effectiveUserId}
      autoConnect={!!activeRoomId}
    >      <SidebarProvider>
        <div className="h-full flex bg-background">
          {/* Left sidebar - Chat rooms */}
          <div className="w-80 border-r flex flex-col">
            <InboxSidebar
              activeRoomId={activeRoomId}
              currentUserId={effectiveUserId}
              onRoomSelect={setActiveRoomId}
            />
          </div>
          {/* Center - Chat area */}
          <div className="flex-1 flex flex-col min-h-0">
            {activeRoomId ? (
              <ActiveChat
                currentUserId={effectiveUserId}
                activeRoomId={activeRoomId}
              />
            ) : (
              <InboxEmptyState />
            )}
          </div>
        </div>
      </SidebarProvider>
    </ChatProvider>
  );
};

// Sidebar component
interface InboxSidebarProps {
  activeRoomId: number | null;
  currentUserId: number | null;
  onRoomSelect: (roomId: number | null) => void;
}

const InboxSidebar: React.FC<InboxSidebarProps> = ({
  activeRoomId,
  currentUserId,
  onRoomSelect
}) => {
  const { chatRooms, loading } = useChatRooms();

  return (
    <ChatRoomList
      chatRooms={chatRooms}
      activeRoomId={activeRoomId}
      currentUserId={currentUserId}
      onRoomSelect={onRoomSelect}
      onCreateRoom={() => {
        // Handle create new room
        console.log('Create new room');
      }}
      loading={loading}
      className="h-full"
    />
  );
};

// Empty state component
const InboxEmptyState: React.FC = () => (
  <div className="flex-1 flex items-center justify-center text-center text-muted-foreground">
    <div className="space-y-4">
      <div className="w-20 h-20 bg-muted rounded-full flex items-center justify-center mx-auto">
        <svg className="w-10 h-10" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" />
        </svg>
      </div>
      <div>
        <h3 className="text-lg font-medium text-foreground">Chào mừng đến với Inbox!</h3>
        <p className="text-sm mt-1">Chọn một cuộc trò chuyện để bắt đầu nhắn tin</p>
      </div>
    </div>
  </div>
);

export default InboxPage;
