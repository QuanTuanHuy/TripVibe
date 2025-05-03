"use client";

import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Badge } from "@/components/ui/badge";
import { cn } from "@/lib/utils";

interface ChatListItemProps {
  avatar?: string;
  name: string;
  message: string;
  time: string;
  unreadCount?: number;
  active?: boolean;
  onClick?: () => void;
}

export default function ChatListItem({
  avatar,
  name,
  message,
  time,
  unreadCount = 0,
  active = false,
  onClick
}: ChatListItemProps) {
  // Truncate long messages
  const truncatedMessage = message.length > 40
    ? `${message.substring(0, 40)}...`
    : message;

  return (
    <div
      className={cn(
        "flex items-center p-4 border-b cursor-pointer hover:bg-muted/50 transition-colors",
        active ? "bg-muted" : ""
      )}
      onClick={onClick}
    >
      <Avatar className="h-10 w-10 mr-3 flex-shrink-0">
        <AvatarImage src={avatar} alt={name} />
        <AvatarFallback>{name.charAt(0)}</AvatarFallback>
      </Avatar>

      <div className="flex-1 min-w-0">
        <div className="flex items-center justify-between">
          <div className="font-medium truncate">{name}</div>
          <div className="text-xs text-muted-foreground flex-shrink-0 ml-2">{time}</div>
        </div>

        <div className="flex items-center justify-between mt-1">
          <div className="text-sm text-muted-foreground truncate">
            {truncatedMessage}
          </div>

          {unreadCount > 0 && (
            <Badge variant="default" className="ml-2 flex-shrink-0">
              {unreadCount}
            </Badge>
          )}
        </div>
      </div>
    </div>
  );
}