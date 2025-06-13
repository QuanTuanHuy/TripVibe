'use client';

import React, { useState } from 'react';
import { ScrollArea } from '@/components/ui/scroll-area';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import {
    Download,
    ExternalLink,
    Image as ImageIcon,
    Play,
    Calendar
} from 'lucide-react';
import { Message } from '@/types/chat';
import { useMediaGallery } from '@/hooks/chat/useMediaGallery';
import { format } from 'date-fns';
import { vi } from 'date-fns/locale';

interface MediaGalleryProps {
    messages: Message[];
    className?: string;
}

const MediaGallery: React.FC<MediaGalleryProps> = ({
    messages,
    className = ''
}) => {
    const { mediaItems, totalMedia } = useMediaGallery(messages);
    const [selectedImage, setSelectedImage] = useState<string | null>(null);

    const formatFileSize = (bytes: number) => {
        if (bytes === 0) return '0 B';
        const k = 1024;
        const sizes = ['B', 'KB', 'MB', 'GB'];
        const i = Math.floor(Math.log(bytes) / Math.log(k));
        return parseFloat((bytes / Math.pow(k, i)).toFixed(1)) + ' ' + sizes[i];
    };

    const groupMediaByDate = () => {
        const groups: { [key: string]: typeof mediaItems } = {};

        mediaItems.forEach(item => {
            const date = format(new Date(item.createdAt * 1000), 'yyyy-MM-dd');
            if (!groups[date]) {
                groups[date] = [];
            }
            groups[date].push(item);
        });

        return Object.entries(groups).sort(([a], [b]) => b.localeCompare(a));
    };

    if (totalMedia === 0) {
        return (
            <div className={`flex flex-col items-center justify-center p-8 text-center ${className}`}>
                <ImageIcon className="h-12 w-12 text-muted-foreground mb-4" />
                <h3 className="font-medium text-sm mb-2">No media shared</h3>
                <p className="text-xs text-muted-foreground">
                    Photos and videos shared in this conversation will appear here
                </p>
            </div>
        );
    }

    const groupedMedia = groupMediaByDate();

    return (
        <div className={className}>
            {/* Header */}
            <div className="px-4 py-3 border-b">
                <div className="flex items-center justify-between">
                    <h3 className="font-medium text-sm">Media Gallery</h3>
                    <Badge variant="secondary" className="h-5 text-xs">
                        {totalMedia}
                    </Badge>
                </div>
            </div>

            <ScrollArea className="flex-1">
                <div className="p-4 space-y-6">
                    {groupedMedia.map(([date, items]) => (
                        <div key={date} className="space-y-3">
                            {/* Date header */}
                            <div className="flex items-center space-x-2 text-xs text-muted-foreground">
                                <Calendar className="h-3 w-3" />
                                <span>
                                    {format(new Date(date), 'EEEE, MMMM d, yyyy', { locale: vi })}
                                </span>
                            </div>

                            {/* Media grid */}
                            <div className="grid grid-cols-3 gap-2">
                                {items.map(item => (
                                    <div
                                        key={item.id}
                                        className="relative aspect-square bg-muted rounded-lg overflow-hidden cursor-pointer group"
                                        onClick={() => setSelectedImage(item.url)}
                                    >
                                        {/* Thumbnail/Preview */}
                                        {item.type === 'image' ? (
                                            <img
                                                src={item.thumbnail || item.url}
                                                alt={item.filename}
                                                className="w-full h-full object-cover"
                                            />
                                        ) : (
                                            <div className="w-full h-full flex items-center justify-center bg-muted">
                                                <Play className="h-6 w-6 text-muted-foreground" />
                                            </div>
                                        )}

                                        {/* Overlay on hover */}
                                        <div className="absolute inset-0 bg-black/50 opacity-0 group-hover:opacity-100 transition-opacity flex items-center justify-center space-x-2">
                                            <Button
                                                size="sm"
                                                variant="secondary"
                                                className="h-7 w-7 p-0"
                                                onClick={(e) => {
                                                    e.stopPropagation();
                                                    window.open(item.url, '_blank');
                                                }}
                                            >
                                                <ExternalLink className="h-3 w-3" />
                                            </Button>

                                            <Button
                                                size="sm"
                                                variant="secondary"
                                                className="h-7 w-7 p-0"
                                                onClick={(e) => {
                                                    e.stopPropagation();
                                                    // Handle download
                                                }}
                                            >
                                                <Download className="h-3 w-3" />
                                            </Button>
                                        </div>

                                        {/* Type indicator */}
                                        {item.type === 'video' && (
                                            <div className="absolute top-1 right-1">
                                                <Badge variant="secondary" className="h-4 px-1 text-xs">
                                                    <Play className="h-2 w-2" />
                                                </Badge>
                                            </div>
                                        )}
                                    </div>
                                ))}
                            </div>

                            {/* Media details */}
                            <div className="space-y-2">
                                {items.map(item => (
                                    <div
                                        key={`${item.id}-details`}
                                        className="flex items-center justify-between text-xs text-muted-foreground"
                                    >
                                        <div className="flex items-center space-x-2 flex-1 min-w-0">
                                            <span className="truncate">{item.filename}</span>
                                            {item.size > 0 && (
                                                <>
                                                    <span>•</span>
                                                    <span>{formatFileSize(item.size)}</span>
                                                </>
                                            )}
                                        </div>
                                        <span>{item.senderName}</span>
                                    </div>
                                ))}
                            </div>
                        </div>
                    ))}
                </div>
            </ScrollArea>

            {/* Image viewer modal - Simple implementation */}
            {selectedImage && (
                <div
                    className="fixed inset-0 bg-black/80 z-50 flex items-center justify-center"
                    onClick={() => setSelectedImage(null)}
                >
                    <div className="relative max-w-4xl max-h-4xl">
                        <img
                            src={selectedImage}
                            alt="Full size"
                            className="max-w-full max-h-full object-contain"
                        />
                        <Button
                            variant="secondary"
                            size="sm"
                            className="absolute top-4 right-4"
                            onClick={() => setSelectedImage(null)}
                        >
                            Close
                        </Button>
                    </div>
                </div>
            )}
        </div>
    );
};

export default MediaGallery;
