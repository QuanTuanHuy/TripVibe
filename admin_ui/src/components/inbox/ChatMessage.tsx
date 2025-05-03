"use client";

import { format } from "date-fns";
import { vi } from "date-fns/locale";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { cn } from "@/lib/utils";

interface ChatMessageProps {
  content: string;
  timestamp: Date;
  isOwn: boolean;
  sender?: string;
  avatar?: string;
  type?: string;
}

export default function ChatMessage({
  content,
  timestamp,
  isOwn,
  sender,
  avatar,
  type = "USER",
}: ChatMessageProps) {
  // Format the timestamp
  const formattedTime = format(timestamp, "HH:mm", { locale: vi });
  
  // If it's a system message, display it centered
  if (type === "SYSTEM") {
    return (
      <div className="flex justify-center my-2">
        <div className="bg-muted px-3 py-2 rounded-md text-xs text-muted-foreground">
          {content}
        </div>
      </div>
    );
  }
  
  return (
    <div className={cn(
      "flex items-start gap-2 max-w-[80%]",
      isOwn ? "ml-auto flex-row-reverse" : ""
    )}>
      {!isOwn && (
        <Avatar className="h-8 w-8">
          <AvatarImage src={avatar} alt={sender} />
          <AvatarFallback>{sender?.charAt(0) || "G"}</AvatarFallback>
        </Avatar>
      )}
      
      <div>
        <div className={cn(
          "rounded-lg py-2 px-3",
          isOwn 
            ? "bg-primary text-primary-foreground rounded-tr-none" 
            : "bg-secondary text-secondary-foreground rounded-tl-none"
        )}>
          {content}
        </div>
        <div className={cn(
          "text-xs text-muted-foreground mt-1",
          isOwn ? "text-right" : "text-left"
        )}>
          {formattedTime}
        </div>
      </div>
    </div>
  );
}