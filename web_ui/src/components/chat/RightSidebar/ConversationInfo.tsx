'use client';

import React from 'react';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Switch } from '@/components/ui/switch';
import {
    UserCheck,
    UserX,
    Bell,
    BellOff,
    Archive,
    Trash2,
    Download,
    Shield,
    Flag
} from 'lucide-react';
import { Participant } from '@/types/chat/sidebar';
import { ScrollArea } from '@/components/ui/scroll-area';
import { Separator } from '@/components/ui/separator';

interface ConversationInfoProps {
    roomId: number;
    participants: Participant[];
    className?: string;
}

const ConversationInfo: React.FC<ConversationInfoProps> = ({
    roomId,
    participants,
    className = ''
}) => {
    const [muted, setMuted] = React.useState(false);
    const [notifications, setNotifications] = React.useState(true);

    const formatLastSeen = (timestamp?: number) => {
        if (!timestamp) return 'Never';

        const now = Date.now();
        const diff = now - timestamp;
        const minutes = Math.floor(diff / 60000);
        const hours = Math.floor(diff / 3600000);
        const days = Math.floor(diff / 86400000);

        if (minutes < 1) return 'Just now';
        if (minutes < 60) return `${minutes}m ago`;
        if (hours < 24) return `${hours}h ago`;
        return `${days}d ago`;
    };

    return (
        <ScrollArea className={`${className}`}>
            <div className="p-4 space-y-6">
                {/* Participants */}
                <div className="space-y-4">
                    <h3 className="font-medium text-sm text-muted-foreground">
                        Participants ({participants.length})
                    </h3>

                    <div className="space-y-3">
                        {participants.map(participant => (
                            <div key={participant.id} className="flex items-center space-x-3">
                                <Avatar className="h-10 w-10">
                                    <AvatarImage src={participant.avatar} alt={participant.name} />
                                    <AvatarFallback>
                                        {participant.name.charAt(0).toUpperCase()}
                                    </AvatarFallback>
                                </Avatar>

                                <div className="flex-1 min-w-0">
                                    <div className="flex items-center space-x-2">
                                        <p className="text-sm font-medium truncate">
                                            {participant.name}
                                        </p>
                                        <Badge
                                            variant="secondary"
                                            className={`h-5 px-1.5 text-xs ${participant.isOnline
                                                ? 'bg-green-100 text-green-700'
                                                : 'bg-gray-100 text-gray-600'
                                                }`}
                                        >
                                            {participant.isOnline ? (
                                                <UserCheck className="h-3 w-3" />
                                            ) : (
                                                <UserX className="h-3 w-3" />
                                            )}
                                        </Badge>
                                    </div>

                                    <div className="flex items-center space-x-2 text-xs text-muted-foreground">
                                        <span className="capitalize">{participant.role.toLowerCase()}</span>
                                        {!participant.isOnline && participant.lastSeen && (
                                            <>
                                                <span>•</span>
                                                <span>Last seen {formatLastSeen(participant.lastSeen)}</span>
                                            </>
                                        )}
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>

                <Separator />

                {/* Conversation Settings */}
                <div className="space-y-4">
                    <h3 className="font-medium text-sm text-muted-foreground">Settings</h3>

                    <div className="space-y-3">
                        <div className="flex items-center justify-between">
                            <div className="flex items-center space-x-3">
                                {muted ? <BellOff className="h-4 w-4" /> : <Bell className="h-4 w-4" />}
                                <span className="text-sm">Notifications</span>
                            </div>
                            <Switch
                                checked={notifications && !muted}
                                onCheckedChange={setNotifications}
                                disabled={muted}
                            />
                        </div>

                        <div className="flex items-center justify-between">
                            <div className="flex items-center space-x-3">
                                <BellOff className="h-4 w-4" />
                                <span className="text-sm">Mute conversation</span>
                            </div>
                            <Switch
                                checked={muted}
                                onCheckedChange={setMuted}
                            />
                        </div>
                    </div>
                </div>

                <Separator />

                {/* Chat Actions */}
                <div className="space-y-4">
                    <h3 className="font-medium text-sm text-muted-foreground">Actions</h3>

                    <div className="space-y-2">
                        <Button variant="ghost" className="w-full justify-start h-9 px-3">
                            <Download className="h-4 w-4 mr-3" />
                            Export chat
                        </Button>

                        <Button variant="ghost" className="w-full justify-start h-9 px-3">
                            <Archive className="h-4 w-4 mr-3" />
                            Archive conversation
                        </Button>
                        <Button variant="ghost" className="w-full justify-start h-9 px-3 text-orange-600 hover:text-orange-700 hover:bg-orange-50">
                            <Shield className="h-4 w-4 mr-3" />
                            Block user
                        </Button>

                        <Button variant="ghost" className="w-full justify-start h-9 px-3 text-orange-600 hover:text-orange-700 hover:bg-orange-50">
                            <Flag className="h-4 w-4 mr-3" />
                            Report conversation
                        </Button>

                        <Button variant="ghost" className="w-full justify-start h-9 px-3 text-destructive hover:text-destructive hover:bg-destructive/5">
                            <Trash2 className="h-4 w-4 mr-3" />
                            Delete conversation
                        </Button>
                    </div>
                </div>

                {/* Booking Info - if available */}
                <Separator />

                <div className="space-y-4">
                    <h3 className="font-medium text-sm text-muted-foreground">Booking Information</h3>

                    <div className="bg-muted/50 rounded-lg p-3 space-y-2">
                        <div className="flex justify-between items-start">
                            <span className="text-xs text-muted-foreground">Booking ID</span>
                            <span className="text-xs font-mono">#{roomId}</span>
                        </div>

                        <div className="flex justify-between items-start">
                            <span className="text-xs text-muted-foreground">Hotel</span>
                            <span className="text-xs text-right">Sample Hotel Name</span>
                        </div>

                        <div className="flex justify-between items-start">
                            <span className="text-xs text-muted-foreground">Room Type</span>
                            <span className="text-xs text-right">Deluxe Room</span>
                        </div>

                        <div className="flex justify-between items-start">
                            <span className="text-xs text-muted-foreground">Status</span>
                            <Badge variant="outline" className="h-5 text-xs">
                                Confirmed
                            </Badge>
                        </div>
                    </div>

                    <Button variant="outline" size="sm" className="w-full">
                        View booking details
                    </Button>
                </div>
            </div>
        </ScrollArea>
    );
};

export default ConversationInfo;
