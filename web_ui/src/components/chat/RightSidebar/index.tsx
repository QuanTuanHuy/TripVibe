'use client';

import React from 'react';
import { X } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Tabs, TabsList, TabsTrigger, TabsContent } from '@/components/ui/tabs';
import { useSidebar } from '@/context/SidebarContext';
import { Message } from '@/types/chat';
import { Participant } from '@/types/chat/sidebar';
import ConversationInfo from './ConversationInfo';
import MediaGallery from './MediaGallery';
import FilesList from './FilesList';
import SharedLinks from './SharedLinks';

interface RightSidebarProps {
    roomId: number;
    participants: Participant[];
    messages: Message[];
    className?: string;
}

const RightSidebar: React.FC<RightSidebarProps> = ({
    roomId,
    participants,
    messages,
    className = ''
}) => {
    const { isOpen, activeTab, setActiveTab, closeSidebar } = useSidebar();

    if (!isOpen) {
        return null;
    }

    return (
        <div className={`w-80 border-l bg-background flex flex-col ${className}`}>
            {/* Header */}
            <div className="flex items-center justify-between px-4 py-3 border-b">
                <h2 className="font-semibold text-lg">Conversation Info</h2>
                <Button
                    variant="ghost"
                    size="sm"
                    onClick={closeSidebar}
                    className="h-8 w-8 p-0"
                >
                    <X className="h-4 w-4" />
                </Button>
            </div>

            {/* Tabs */}
            <Tabs
                value={activeTab}
                onValueChange={(value) => setActiveTab(value as any)}
                className="flex-1 flex flex-col"
            >
                <TabsList className="grid grid-cols-4 mx-4 mt-3">
                    <TabsTrigger value="info" className="text-xs">
                        Info
                    </TabsTrigger>
                    <TabsTrigger value="media" className="text-xs">
                        Media
                    </TabsTrigger>
                    <TabsTrigger value="files" className="text-xs">
                        Files
                    </TabsTrigger>
                    <TabsTrigger value="links" className="text-xs">
                        Links
                    </TabsTrigger>
                </TabsList>

                <div className="flex-1 overflow-hidden">
                    <TabsContent value="info" className="h-full m-0 p-0">
                        <ConversationInfo
                            roomId={roomId}
                            participants={participants}
                            className="h-full"
                        />
                    </TabsContent>

                    <TabsContent value="media" className="h-full m-0 p-0">
                        <MediaGallery
                            messages={messages}
                            className="h-full"
                        />
                    </TabsContent>

                    <TabsContent value="files" className="h-full m-0 p-0">
                        <FilesList
                            messages={messages}
                            className="h-full"
                        />
                    </TabsContent>

                    <TabsContent value="links" className="h-full m-0 p-0">
                        <SharedLinks
                            messages={messages}
                            className="h-full"
                        />
                    </TabsContent>
                </div>
            </Tabs>
        </div>
    );
};

export default RightSidebar;
