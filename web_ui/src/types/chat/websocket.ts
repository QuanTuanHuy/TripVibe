import { MessageType } from './chat';

export interface WebSocketMessage {
    type: string;
    data: any;
    roomId?: number;
}

// WebSocket message types based on backend
export interface WSAuthMessage {
    type: 'auth';
    token: string;
}

export interface WSNewMessage {
    type: 'new_message';
    data: {
        content: string;
        messageType: MessageType;
    };
    roomId: number;
}

export interface WSTypingMessage {
    type: 'typing_indicator';
    data: {
        isTyping: boolean;
    };
    roomId: number;
}

export interface WSReadMessage {
    type: 'message_read';
    data: {
        messageId: number;
    };
    roomId: number;
}

export interface WSIncomingMessage {
    type: 'new_message' | 'typing_indicator' | 'message_read' | 'user_connected' | 'user_disconnected' | 'auth' | 'auth_success' | 'auth_error';
    data: any;
    roomId?: number;
}
