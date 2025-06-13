'use client';

import React from 'react';
import { Button } from '@/components/ui/button';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Badge } from '@/components/ui/badge';
import {
    Info,
    Phone,
    Video,
    MoreVertical,
    UserCheck,
    UserX
} from 'lucide-react';

import { useSidebar } from '@/context/SidebarContext';
import { Participant } from '@/types/chat/sidebar';
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuSeparator, DropdownMenuTrigger } from '../ui/dropdown-menu';

interface ChatHeaderProps {
    participants: Participant[];
    roomId: number;
    onCall?: () => void;
    onVideoCall?: () => void;
    className?: string;
}

const ChatHeader: React.FC<ChatHeaderProps> = ({
    participants,
    roomId,
    onCall,
    onVideoCall,
    className = ''
}) => {
    const { toggleSidebar, isOpen } = useSidebar();

    // Get the other participant (not the current user)
    const otherParticipant = participants.find(p => p.role !== 'GUEST') || participants[0];
    const participantCount = participants.length;

    const handleInfoClick = () => {
        toggleSidebar('info');
    };

    return (
        <div className={`flex items-center justify-between px-4 py-3 border-b bg-background ${className}`}>
            {/* Left side - Participant info */}
            <div className="flex items-center space-x-3">
                <Avatar className="h-10 w-10">
                    <AvatarImage src={otherParticipant?.avatar} alt={otherParticipant?.name} />
                    <AvatarFallback>
                        {otherParticipant?.name?.charAt(0)?.toUpperCase() || 'U'}
                    </AvatarFallback>
                </Avatar>

                <div className="flex flex-col">
                    <div className="flex items-center space-x-2">
                        <h3 className="font-semibold text-sm">
                            {otherParticipant?.name || 'Unknown'}
                        </h3>
                        {otherParticipant?.isOnline ? (
                            <Badge variant="secondary" className="h-5 px-1.5 text-xs bg-green-100 text-green-700">
                                <UserCheck className="h-3 w-3 mr-1" />
                                Online
                            </Badge>
                        ) : (
                            <Badge variant="secondary" className="h-5 px-1.5 text-xs bg-gray-100 text-gray-600">
                                <UserX className="h-3 w-3 mr-1" />
                                Offline
                            </Badge>
                        )}
                    </div>

                    <div className="flex items-center space-x-2 text-xs text-muted-foreground">
                        <span>{participantCount} participant{participantCount > 1 ? 's' : ''}</span>
                        {otherParticipant?.role && (
                            <>
                                <span>•</span>
                                <span className="capitalize">{otherParticipant.role.toLowerCase()}</span>
                            </>
                        )}
                    </div>
                </div>
            </div>

            {/* Right side - Actions */}
            <div className="flex items-center space-x-2">
                {/* Call button */}
                {onCall && (
                    <Button
                        variant="ghost"
                        size="sm"
                        onClick={onCall}
                        className="h-8 w-8 p-0"
                        title="Voice call"
                    >
                        <Phone className="h-4 w-4" />
                    </Button>
                )}

                {/* Video call button */}
                {onVideoCall && (
                    <Button
                        variant="ghost"
                        size="sm"
                        onClick={onVideoCall}
                        className="h-8 w-8 p-0"
                        title="Video call"
                    >
                        <Video className="h-4 w-4" />
                    </Button>
                )}

                {/* Info button - toggle sidebar */}
                <Button
                    variant="ghost"
                    size="sm"
                    onClick={handleInfoClick}
                    className={`h-8 w-8 p-0 ${isOpen ? 'bg-muted' : ''}`}
                    title="Conversation info"
                >
                    <Info className="h-4 w-4" />
                </Button>

                {/* More actions */}
                <DropdownMenu>
                    <DropdownMenuTrigger asChild>
                        <Button
                            variant="ghost"
                            size="sm"
                            className="h-8 w-8 p-0"
                        >
                            <MoreVertical className="h-4 w-4" />
                        </Button>
                    </DropdownMenuTrigger>
                    <DropdownMenuContent align="end" className="w-48">
                        <DropdownMenuItem>
                            <span>Mute conversation</span>
                        </DropdownMenuItem>
                        <DropdownMenuItem>
                            <span>Search in conversation</span>
                        </DropdownMenuItem>
                        <DropdownMenuSeparator />
                        <DropdownMenuItem>
                            <span>Archive conversation</span>
                        </DropdownMenuItem>
                        <DropdownMenuItem className="text-destructive">
                            <span>Delete conversation</span>
                        </DropdownMenuItem>
                    </DropdownMenuContent>
                </DropdownMenu>
            </div>
        </div>
    );
};

export default ChatHeader;
