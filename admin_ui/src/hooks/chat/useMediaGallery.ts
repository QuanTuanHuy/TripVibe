import { useState, useEffect, useMemo } from 'react';
import { Message } from '@/types/chat';
import { MediaItem, FileItem, LinkItem } from '@/types/chat/sidebar';
import { userStore } from '@/stores/userStore';

export const useMediaGallery = (messages: Message[]) => {
    const [mediaItems, setMediaItems] = useState<MediaItem[]>([]);
    const [fileItems, setFileItems] = useState<FileItem[]>([]);
    const [linkItems, setLinkItems] = useState<LinkItem[]>([]);
    const [loading, setLoading] = useState(false);

    // Extract media, files, and links from messages
    const extractedData = useMemo(() => {
        const media: MediaItem[] = [];
        const files: FileItem[] = [];
        const links: LinkItem[] = [];

        messages.forEach(message => {
            if (message.type === 'media' && message.mediaType === 'image') {
                const mediaItem: MediaItem = {
                    id: message.mediaDataId!,
                    messageId: message.id,
                    url: message.mediaUrl || '',
                    type: 'image',
                    filename: `media_${message.mediaDataId}`,
                    size: message.mediaSize || 0,
                    createdAt: new Date(message.createdAt).getTime(),
                    senderId: message.senderId!,
                    senderName: userStore.getUser(message.senderId!)
                };
                media.push(mediaItem);
            }

            if (message.mediaType === 'file' || (message.type === 'media' && message.mediaDataId)) {
                // Similar logic for files
                const fileItem: FileItem = {
                    id: message.mediaDataId || message.id,
                    messageId: message.id,
                    url: message.mediaUrl!,
                    filename: message.mediaName || `file_${message.mediaDataId}`,
                    type: message.mimeType || 'application/octet-stream',
                    size: message.mediaSize || 0,
                    createdAt: new Date(message.createdAt).getTime(),
                    senderId: message.senderId || 0,
                    senderName: userStore.getUser(message.senderId!)
                };
                files.push(fileItem);
            }

            // Extract links from text messages
            if (message.type === 'text' && message.content) {
                const urlRegex = /(https?:\/\/[^\s]+)/g;
                const urls = message.content.match(urlRegex);

                if (urls) {
                    urls.forEach(url => {
                        const linkItem: LinkItem = {
                            id: Date.now() + Math.random(), // Generate unique ID
                            messageId: message.id,
                            url,
                            domain: new URL(url).hostname,
                            createdAt: new Date(message.createdAt).getTime(),
                            senderId: message.senderId || 0,
                            senderName: userStore.getUser(message.senderId!)
                        };
                        links.push(linkItem);
                    });
                }
            }
        });

        return { media, files, links };
    }, [messages]);

    useEffect(() => {
        setMediaItems(extractedData.media);
        setFileItems(extractedData.files);
        setLinkItems(extractedData.links);
    }, [extractedData]);

    return {
        mediaItems,
        fileItems,
        linkItems,
        loading,
        totalMedia: mediaItems.length,
        totalFiles: fileItems.length,
        totalLinks: linkItems.length
    };
};

export default useMediaGallery;
