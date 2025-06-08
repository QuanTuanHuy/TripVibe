'use client';

import React from 'react';
import { TypingUser } from '@/types/chat';
import { cn } from '@/lib/utils';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';

interface TypingIndicatorProps {
  typingUsers: TypingUser[];
  className?: string;
}

export const TypingIndicator: React.FC<TypingIndicatorProps> = ({
  typingUsers,
  className
}) => {
  console.log('TypingIndicator render:', { typingUsers, hasUsers: typingUsers.length > 0 });

  if (typingUsers.length === 0) {
    return null;
  }

  const getUserInitials = (name: string) => {
    return name
      .split(' ')
      .map(word => word[0])
      .join('')
      .toUpperCase()
      .slice(0, 2);
  };

  const getTypingMessage = () => {
    if (typingUsers.length === 1) {
      return `${typingUsers[0].userName} đang gõ...`;
    } else if (typingUsers.length === 2) {
      return `${typingUsers[0].userName} và ${typingUsers[1].userName} đang gõ...`;
    } else {
      return `${typingUsers[0].userName} và ${typingUsers.length - 1} người khác đang gõ...`;
    }
  };

  const TypingDots = () => (
    <div className="flex space-x-1">
      <div className="w-2 h-2 bg-muted-foreground rounded-full animate-bounce [animation-delay:-0.3s]"></div>
      <div className="w-2 h-2 bg-muted-foreground rounded-full animate-bounce [animation-delay:-0.15s]"></div>
      <div className="w-2 h-2 bg-muted-foreground rounded-full animate-bounce"></div>
    </div>
  );

  return (
    <div className={cn(
      "flex items-center gap-3 px-4 py-2 text-sm text-muted-foreground animate-fade-in",
      className
    )}>
      {/* Show avatar for single user */}
      {typingUsers.length === 1 && (
        <Avatar className="w-6 h-6">
          <AvatarImage
            src={`/api/avatars/${typingUsers[0].userId}`}
            alt={typingUsers[0].userName}
          />
          <AvatarFallback className="text-xs">
            {getUserInitials(typingUsers[0].userName)}
          </AvatarFallback>
        </Avatar>
      )}      {/* Show multiple avatars for multiple users */}
      {typingUsers.length > 1 && (
        <div className="flex -space-x-1">
          {typingUsers.slice(0, 3).map((user) => (
            <Avatar key={user.userId} className="w-6 h-6 border-2 border-background">
              <AvatarImage
                src={`/api/avatars/${user.userId}`}
                alt={user.userName}
              />
              <AvatarFallback className="text-xs">
                {getUserInitials(user.userName)}
              </AvatarFallback>
            </Avatar>
          ))}
        </div>
      )}

      <div className="flex items-center gap-2">
        <span className="text-xs">{getTypingMessage()}</span>
        <TypingDots />
      </div>
    </div>
  );
};

export default TypingIndicator;
