// Types for Right Sidebar components
export interface SidebarState {
    isOpen: boolean;
    activeTab: SidebarTab;
}

export type SidebarTab = 'info' | 'media' | 'files' | 'links';

export interface ConversationInfo {
    id: number;
    participants: Participant[];
    settings: ConversationSettings;
    bookingInfo?: BookingInfo;
}

export interface ConversationSettings {
    muted: boolean;
    notifications: boolean;
    archiveDate?: number;
}

export interface BookingInfo {
    id: number;
    hotelName: string;
    roomType: string;
    checkIn: string;
    checkOut: string;
    totalPrice: number;
    status: string;
}

export interface Participant {
    id: number;
    name: string;
    avatar?: string;
    role: 'GUEST' | 'OWNER' | 'ADMIN';
    isOnline: boolean;
    lastSeen?: number;
}

export interface MediaItem {
    id: number;
    messageId: number;
    url: string;
    type: 'image' | 'video';
    thumbnail?: string;
    filename: string;
    size: number;
    createdAt: number;
    senderId: number;
    senderName: string;
}

export interface FileItem {
    id: number;
    messageId: number;
    url: string;
    filename: string;
    type: string;
    size: number;
    createdAt: number;
    senderId: number;
    senderName: string;
}

export interface LinkItem {
    id: number;
    messageId: number;
    url: string;
    title?: string;
    description?: string;
    domain: string;
    thumbnail?: string;
    createdAt: number;
    senderId: number;
    senderName: string;
}

export interface ChatAction {
    id: string;
    label: string;
    icon: string;
    action: () => void;
    destructive?: boolean;
    requireConfirm?: boolean;
}
