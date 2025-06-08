'use client';

import React, { useState, useRef } from 'react';
import { MessageType } from '@/types/chat';
import { cn } from '@/lib/utils';
import { Button } from '@/components/ui/button';
import { Textarea } from '@/components/ui/textarea';
import {
  Popover,
  PopoverContent,
  PopoverTrigger
} from '@/components/ui/popover';
import {
  Tooltip,
  TooltipContent,
  TooltipTrigger
} from '@/components/ui/tooltip';

interface MessageInputProps {
  onSendMessage: (content: string, type?: MessageType) => Promise<void>;
  onSendMediaMessage: (file: File) => Promise<void>;
  onStartTyping: () => void;
  onStopTyping: () => void;
  disabled?: boolean;
  placeholder?: string;
  className?: string;
}

export const MessageInput: React.FC<MessageInputProps> = ({
  onSendMessage,
  onSendMediaMessage,
  onStartTyping,
  onStopTyping,
  disabled = false,
  placeholder = "Nhập tin nhắn...",
  className
}) => {
  const [message, setMessage] = useState('');
  const [isComposing, setIsComposing] = useState(false);
  const fileInputRef = useRef<HTMLInputElement>(null);
  const textareaRef = useRef<HTMLTextAreaElement>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!message.trim() || disabled) return;

    const messageToSend = message.trim();
    setMessage('');
    onStopTyping();

    try {
      await onSendMessage(messageToSend, MessageType.TEXT);
      // Focus back to textarea after sending
      textareaRef.current?.focus();
    } catch (error) {
      console.error('Failed to send message:', error);
      // Restore message on error
      setMessage(messageToSend);
    }
  };

  const handleKeyDown = (e: React.KeyboardEvent<HTMLTextAreaElement>) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSubmit(e);
    }
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLTextAreaElement>) => {
    const value = e.target.value;
    setMessage(value);

    console.log('Input changed:', { value: value.length, isComposing });

    // Handle typing indicators
    if (value.trim() && !isComposing) {
      console.log('Starting typing indicator');
      onStartTyping();
      setIsComposing(true);
    } else if (!value.trim() && isComposing) {
      console.log('Stopping typing indicator');
      onStopTyping();
      setIsComposing(false);
    }
  };

  const handleFileSelect = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    // Reset input
    e.target.value = '';

    try {
      await onSendMediaMessage(file);
    } catch (error) {
      console.error('Failed to send file:', error);
    }
  };

  const handleEmojiSelect = (emoji: string) => {
    const newMessage = message + emoji;
    setMessage(newMessage);
    textareaRef.current?.focus();
  };

  const commonEmojis = ['😀', '😂', '😍', '🥰', '😊', '👍', '👎', '❤️', '🔥', '👏', '🎉', '😢'];

  return (
    <div className={cn("border-t bg-background p-4", className)}>
      <form onSubmit={handleSubmit} className="flex gap-2 items-end">
        {/* File upload */}
        <div className="flex gap-1">
          <input
            ref={fileInputRef}
            type="file"
            accept="image/*,.pdf,.doc,.docx,.txt"
            onChange={handleFileSelect}
            className="hidden"
            disabled={disabled}
          />

          <Tooltip>
            <TooltipTrigger asChild>
              <Button
                type="button"
                variant="ghost"
                size="icon"
                onClick={() => fileInputRef.current?.click()}
                disabled={disabled}
                className="h-10 w-10 flex-shrink-0"
              >
                <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15.172 7l-6.586 6.586a2 2 0 102.828 2.828l6.414-6.586a4 4 0 00-5.656-5.656l-6.415 6.585a6 6 0 108.486 8.486L20.5 13" />
                </svg>
              </Button>
            </TooltipTrigger>
            <TooltipContent>
              <p>Đính kèm file</p>
            </TooltipContent>
          </Tooltip>

          {/* Emoji picker */}
          <Popover>
            <PopoverTrigger asChild>
              <Button
                type="button"
                variant="ghost"
                size="icon"
                disabled={disabled}
                className="h-10 w-10 flex-shrink-0"
              >
                <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M14.828 14.828a4 4 0 01-5.656 0M9 10h.01M15 10h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
              </Button>
            </PopoverTrigger>
            <PopoverContent className="w-80 p-3">
              <div className="grid grid-cols-6 gap-2">
                {commonEmojis.map((emoji) => (
                  <Button
                    key={emoji}
                    variant="ghost"
                    size="sm"
                    onClick={() => handleEmojiSelect(emoji)}
                    className="h-8 w-8 p-0 text-lg hover:bg-muted"
                  >
                    {emoji}
                  </Button>
                ))}
              </div>
            </PopoverContent>
          </Popover>
        </div>

        {/* Message input */}
        <div className="flex-1 relative">
          <Textarea
            ref={textareaRef}
            value={message}
            onChange={handleInputChange}
            onKeyDown={handleKeyDown}
            placeholder={placeholder}
            disabled={disabled}
            rows={1}
            className="min-h-10 max-h-32 resize-none pr-12"
          />
        </div>

        {/* Send button */}
        <Button
          type="submit"
          disabled={disabled || !message.trim()}
          size="icon"
          className="h-10 w-10 flex-shrink-0"
        >
          <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8" />
          </svg>
        </Button>
      </form>
    </div>
  );
};

export default MessageInput;
