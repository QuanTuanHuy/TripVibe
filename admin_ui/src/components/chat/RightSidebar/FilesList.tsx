'use client';

import React from 'react';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import {
    Download,
    ExternalLink,
    File,
    FileText,
    FileImage,
    FileVideo,
    FileAudio,
    Archive,
    Calendar
} from 'lucide-react';
import { Message } from '@/types/chat';
import { useMediaGallery } from '@/hooks/chat/useMediaGallery';
import { format } from 'date-fns';
import { vi } from 'date-fns/locale';
import { ScrollArea } from '@/components/ui/scroll-area';

interface FilesListProps {
    messages: Message[];
    className?: string;
}

const FilesList: React.FC<FilesListProps> = ({
    messages,
    className = ''
}) => {
    const { fileItems, totalFiles } = useMediaGallery(messages);

    const formatFileSize = (bytes: number) => {
        if (bytes === 0) return '0 B';
        const k = 1024;
        const sizes = ['B', 'KB', 'MB', 'GB'];
        const i = Math.floor(Math.log(bytes) / Math.log(k));
        return parseFloat((bytes / Math.pow(k, i)).toFixed(1)) + ' ' + sizes[i];
    };

    const getFileIcon = (type: string, filename: string) => {
        const extension = filename.split('.').pop()?.toLowerCase() || '';

        if (type.startsWith('image/') || ['jpg', 'jpeg', 'png', 'gif', 'webp'].includes(extension)) {
            return <FileImage className="h-4 w-4 text-blue-500" />;
        }

        if (type.startsWith('video/') || ['mp4', 'avi', 'mov', 'mkv'].includes(extension)) {
            return <FileVideo className="h-4 w-4 text-purple-500" />;
        }

        if (type.startsWith('audio/') || ['mp3', 'wav', 'flac', 'm4a'].includes(extension)) {
            return <FileAudio className="h-4 w-4 text-green-500" />;
        }

        if (['pdf'].includes(extension)) {
            return <FileText className="h-4 w-4 text-red-500" />;
        }

        if (['zip', 'rar', '7z', 'tar'].includes(extension)) {
            return <Archive className="h-4 w-4 text-orange-500" />;
        }

        return <File className="h-4 w-4 text-gray-500" />;
    };

    const groupFilesByDate = () => {
        const groups: { [key: string]: typeof fileItems } = {};

        fileItems.forEach(item => {
            const date = format(new Date(item.createdAt * 1000), 'yyyy-MM-dd');
            if (!groups[date]) {
                groups[date] = [];
            }
            groups[date].push(item);
        });

        return Object.entries(groups).sort(([a], [b]) => b.localeCompare(a));
    };

    if (totalFiles === 0) {
        return (
            <div className={`flex flex-col items-center justify-center p-8 text-center ${className}`}>
                <File className="h-12 w-12 text-muted-foreground mb-4" />
                <h3 className="font-medium text-sm mb-2">No files shared</h3>
                <p className="text-xs text-muted-foreground">
                    Documents and files shared in this conversation will appear here
                </p>
            </div>
        );
    }

    const groupedFiles = groupFilesByDate();

    return (
        <div className={className}>
            {/* Header */}
            <div className="px-4 py-3 border-b">
                <div className="flex items-center justify-between">
                    <h3 className="font-medium text-sm">Files & Documents</h3>
                    <Badge variant="secondary" className="h-5 text-xs">
                        {totalFiles}
                    </Badge>
                </div>
            </div>

            <ScrollArea className="flex-1">
                <div className="p-4 space-y-6">
                    {groupedFiles.map(([date, items]) => (
                        <div key={date} className="space-y-3">
                            {/* Date header */}
                            <div className="flex items-center space-x-2 text-xs text-muted-foreground">
                                <Calendar className="h-3 w-3" />
                                <span>
                                    {format(new Date(date), 'EEEE, MMMM d, yyyy', { locale: vi })}
                                </span>
                            </div>

                            {/* Files list */}
                            <div className="space-y-2">
                                {items.map(item => (
                                    <div
                                        key={item.id}
                                        className="flex items-center space-x-3 p-3 rounded-lg border hover:bg-muted/50 transition-colors"
                                    >
                                        {/* File icon */}
                                        <div className="flex-shrink-0">
                                            {getFileIcon(item.type, item.filename)}
                                        </div>

                                        {/* File info */}
                                        <div className="flex-1 min-w-0">
                                            <div className="flex items-center space-x-2">
                                                <div className="text-sm font-medium truncate">
                                                    {item.filename.length > 28
                                                        ? `${item.filename.slice(0, 28)}...`
                                                        : item.filename}
                                                </div>
                                            </div>
                                            {item.size > 0 && (
                                                <div className="-ml-1 text-xs text-muted-foreground">
                                                    {formatFileSize(item.size)}
                                                </div>
                                            )}

                                            <div className="flex items-center space-x-2 text-xs text-muted-foreground">
                                                <span>Shared by {item.senderName}</span>
                                                <span>•</span>
                                                <span>
                                                    {format(new Date(item.createdAt), 'HH:mm', { locale: vi })}
                                                </span>
                                            </div>
                                        </div>

                                        {/* Actions */}
                                        <div className="-ml-8 flex items-center space-x-1">
                                            <Button
                                                variant="ghost"
                                                size="sm"
                                                className="h-8 w-8 p-0"
                                                onClick={() => window.open(item.url, '_blank')}
                                                title="Open file"
                                            >
                                                <ExternalLink className="h-4 w-4" />
                                            </Button>

                                            <Button
                                                variant="ghost"
                                                size="sm"
                                                className="h-8 w-8 p-0"
                                                onClick={() => {
                                                    // Handle download
                                                    const link = document.createElement('a');
                                                    link.href = item.url;
                                                    link.download = item.filename;
                                                    link.click();
                                                }}
                                                title="Download file"
                                            >
                                                <Download className="h-4 w-4" />
                                            </Button>
                                        </div>
                                    </div>
                                ))}
                            </div>
                        </div>
                    ))}
                </div>
            </ScrollArea>
        </div>
    );
};

export default FilesList;
