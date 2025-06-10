'use client';

import React from 'react';
import { ScrollArea } from '@/components/ui/scroll-area';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import {
    ExternalLink,
    Link as LinkIcon,
    Calendar,
    Globe
} from 'lucide-react';
import { Message } from '@/types/chat';
import { useMediaGallery } from '@/hooks/chat/useMediaGallery';
import { format } from 'date-fns';
import { vi } from 'date-fns/locale';

interface SharedLinksProps {
    messages: Message[];
    className?: string;
}

const SharedLinks: React.FC<SharedLinksProps> = ({
    messages,
    className = ''
}) => {
    const { linkItems, totalLinks } = useMediaGallery(messages);

    const getDomainIcon = (domain: string) => {
        // You could expand this to show specific icons for popular domains
        const domainLower = domain.toLowerCase();

        if (domainLower.includes('youtube') || domainLower.includes('youtu.be')) {
            return '🎥';
        }
        if (domainLower.includes('github')) {
            return '👨‍💻';
        }
        if (domainLower.includes('google')) {
            return '🔍';
        }
        if (domainLower.includes('facebook') || domainLower.includes('fb.')) {
            return '📘';
        }
        if (domainLower.includes('twitter') || domainLower.includes('x.com')) {
            return '🐦';
        }
        if (domainLower.includes('instagram')) {
            return '📷';
        }
        if (domainLower.includes('linkedin')) {
            return '💼';
        }

        return '🌐';
    };

    const groupLinksByDate = () => {
        const groups: { [key: string]: typeof linkItems } = {};

        linkItems.forEach(item => {
            const date = format(new Date(item.createdAt), 'yyyy-MM-dd');
            if (!groups[date]) {
                groups[date] = [];
            }
            groups[date].push(item);
        });

        return Object.entries(groups).sort(([a], [b]) => b.localeCompare(a));
    };

    const groupLinksByDomain = () => {
        const groups: { [key: string]: typeof linkItems } = {};

        linkItems.forEach(item => {
            const domain = item.domain;
            if (!groups[domain]) {
                groups[domain] = [];
            }
            groups[domain].push(item);
        });

        return Object.entries(groups).sort(([, a], [, b]) => b.length - a.length);
    };

    if (totalLinks === 0) {
        return (
            <div className={`flex flex-col items-center justify-center p-8 text-center ${className}`}>
                <LinkIcon className="h-12 w-12 text-muted-foreground mb-4" />
                <h3 className="font-medium text-sm mb-2">No links shared</h3>
                <p className="text-xs text-muted-foreground">
                    Links shared in this conversation will appear here
                </p>
            </div>
        );
    }

    const groupedByDate = groupLinksByDate();
    const groupedByDomain = groupLinksByDomain();

    return (
        <div className={className}>
            {/* Header */}
            <div className="px-4 py-3 border-b">
                <div className="flex items-center justify-between">
                    <h3 className="font-medium text-sm">Shared Links</h3>
                    <Badge variant="secondary" className="h-5 text-xs">
                        {totalLinks}
                    </Badge>
                </div>
            </div>

            <ScrollArea className="flex-1">
                <div className="p-4 space-y-6">
                    {/* Domain summary */}
                    {groupedByDomain.length > 1 && (
                        <div className="space-y-3">
                            <h4 className="text-xs font-medium text-muted-foreground">By Domain</h4>
                            <div className="grid grid-cols-2 gap-2">
                                {groupedByDomain.slice(0, 6).map(([domain, items]) => (
                                    <div
                                        key={domain}
                                        className="flex items-center space-x-2 p-2 rounded-md bg-muted/50 text-xs"
                                    >
                                        <span className="text-sm">{getDomainIcon(domain)}</span>
                                        <div className="flex-1 min-w-0">
                                            <div className="truncate font-medium">{domain}</div>
                                            <div className="text-muted-foreground">{items.length} link{items.length > 1 ? 's' : ''}</div>
                                        </div>
                                    </div>
                                ))}
                            </div>
                        </div>
                    )}

                    {/* Links by date */}
                    <div className="space-y-6">
                        {groupedByDate.map(([date, items]) => (
                            <div key={date} className="space-y-3">
                                {/* Date header */}
                                <div className="flex items-center space-x-2 text-xs text-muted-foreground">
                                    <Calendar className="h-3 w-3" />
                                    <span>
                                        {format(new Date(date), 'EEEE, MMMM d, yyyy', { locale: vi })}
                                    </span>
                                </div>

                                {/* Links list */}
                                <div className="space-y-3">
                                    {items.map(item => (
                                        <div
                                            key={item.id}
                                            className="flex space-x-3 p-3 rounded-lg border hover:bg-muted/50 transition-colors cursor-pointer"
                                            onClick={() => window.open(item.url, '_blank')}
                                        >
                                            {/* Thumbnail or icon */}
                                            <div className="flex-shrink-0 w-12 h-12 rounded-lg bg-muted flex items-center justify-center">
                                                {item.thumbnail ? (
                                                    <img
                                                        src={item.thumbnail}
                                                        alt={item.title || item.domain}
                                                        className="w-full h-full object-cover rounded-lg"
                                                    />
                                                ) : (
                                                    <span className="text-lg">{getDomainIcon(item.domain)}</span>
                                                )}
                                            </div>

                                            {/* Link info */}
                                            <div className="flex-1 min-w-0">
                                                <div className="flex items-start justify-between">
                                                    <div className="flex-1 min-w-0">
                                                        <h4 className="text-sm font-medium truncate">
                                                            {item.title || item.domain}
                                                        </h4>

                                                        {item.description && (
                                                            <p className="text-xs text-muted-foreground mt-1 line-clamp-2">
                                                                {item.description}
                                                            </p>
                                                        )}

                                                        <div className="flex items-center space-x-2 mt-2 text-xs text-muted-foreground">
                                                            <Globe className="h-3 w-3" />
                                                            <span className="truncate">{item.domain}</span>
                                                            <span>•</span>
                                                            <span>
                                                                {format(new Date(item.createdAt), 'HH:mm', { locale: vi })}
                                                            </span>
                                                            <span>•</span>
                                                            <span>{item.senderName}</span>
                                                        </div>
                                                    </div>

                                                    <Button
                                                        variant="ghost"
                                                        size="sm"
                                                        className="h-8 w-8 p-0 ml-2"
                                                        onClick={(e) => {
                                                            e.stopPropagation();
                                                            window.open(item.url, '_blank');
                                                        }}
                                                    >
                                                        <ExternalLink className="h-4 w-4" />
                                                    </Button>
                                                </div>
                                            </div>
                                        </div>
                                    ))}
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            </ScrollArea>
        </div>
    );
};

export default SharedLinks;
