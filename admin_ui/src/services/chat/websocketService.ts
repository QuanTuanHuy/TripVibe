import {
    WSAuthMessage,
    WSNewMessage,
    WSTypingMessage,
    WSReadMessage,
    WSIncomingMessage
} from '@/types/chat';

export class WebSocketService {
    private ws: WebSocket | null = null;
    private roomId: number | null = null;
    private token: string | null = null;
    private isAuthenticated = false;
    private reconnectAttempts = 0;
    private maxReconnectAttempts = 5;
    private reconnectDelay = 1000;
    private heartbeatInterval: NodeJS.Timeout | null = null;

    // Event handlers
    private messageHandlers: ((message: WSIncomingMessage) => void)[] = [];
    private connectionHandlers: ((connected: boolean) => void)[] = [];
    private errorHandlers: ((error: Event) => void)[] = [];

    constructor() {
        // Lấy token từ localStorage
        if (typeof window !== 'undefined') {
            this.token = localStorage.getItem('token');
        }
    }

    /**
     * Kết nối WebSocket với room ID
     */
    connect(roomId: number): Promise<void> {
        return new Promise((resolve, reject) => {
            if (this.ws?.readyState === WebSocket.OPEN) {
                this.disconnect();
            }
            this.roomId = roomId;
            const wsUrl = `${process.env.NEXT_PUBLIC_WS_URL || 'ws://localhost:8090'}/chat_service/api/v1/chats/ws/connect?roomId=${roomId}`;

            console.log(`Connecting to WebSocket: ${wsUrl}`);
            this.ws = new WebSocket(wsUrl);

            const connectionTimeout = setTimeout(() => {
                reject(new Error('WebSocket connection timeout'));
            }, 10000);

            this.ws.onopen = () => {
                clearTimeout(connectionTimeout);
                console.log('WebSocket connected, sending auth message immediately...');

                // Send auth message immediately after connection opens
                if (!this.token) {
                    reject(new Error('No authentication token available'));
                    return;
                }

                const authMessage: WSAuthMessage = {
                    type: 'auth',
                    token: this.token!
                };

                // Send auth message immediately
                this.ws!.send(JSON.stringify(authMessage));
                console.log('Auth message sent:', authMessage);

                // Set up auth response handler
                const handleAuthResponse = (event: MessageEvent) => {
                    try {
                        const message: WSIncomingMessage = JSON.parse(event.data);
                        console.log('Received auth response:', message);

                        if (message.type === 'auth_success') {
                            this.isAuthenticated = true;
                            this.ws!.removeEventListener('message', handleAuthResponse);
                            this.startHeartbeat();
                            this.notifyConnectionHandlers(true);
                            this.reconnectAttempts = 0;
                            resolve();
                        } else if (message.type === 'auth_error') {
                            this.ws!.removeEventListener('message', handleAuthResponse);
                            reject(new Error(message.data?.message || 'Authentication failed'));
                        }
                    } catch (error) {
                        console.error('Failed to parse auth response:', error);
                    }
                };

                // Listen for auth response
                this.ws!.addEventListener('message', handleAuthResponse);

                // Timeout for auth
                setTimeout(() => {
                    this.ws!.removeEventListener('message', handleAuthResponse);
                    reject(new Error('Authentication timeout'));
                }, 10000);
            };

            this.ws.onmessage = (event) => {
                try {
                    console.log('Raw WebSocket message received:', event.data);
                    const message: WSIncomingMessage = JSON.parse(event.data);
                    console.log('Parsed WebSocket message:', message);
                    // Only handle non-auth messages here, auth is handled in onopen
                    if (message.type !== 'auth_success' && message.type !== 'auth_error') {
                        this.handleMessage(message);
                    }
                } catch (error) {
                    console.error('Failed to parse WebSocket message:', error, 'Raw data:', event.data);
                }
            };

            this.ws.onclose = (event) => {
                clearTimeout(connectionTimeout);
                console.log('WebSocket disconnected:', event.code, event.reason);
                this.stopHeartbeat();
                this.notifyConnectionHandlers(false);

                // Auto-reconnect unless it's a normal closure
                if (event.code !== 1000 && this.reconnectAttempts < this.maxReconnectAttempts) {
                    this.reconnect();
                }
            };

            this.ws.onerror = (error) => {
                clearTimeout(connectionTimeout);
                console.error('WebSocket error:', error);
                this.notifyErrorHandlers(error);
                reject(error);
            };
        });
    }

    /**
     * Tự động kết nối lại
     */
    private reconnect(): void {
        if (this.reconnectAttempts >= this.maxReconnectAttempts) {
            console.error('Max reconnection attempts reached');
            return;
        }

        this.reconnectAttempts++;
        const delay = this.reconnectDelay * Math.pow(2, this.reconnectAttempts - 1);

        console.log(`Attempting to reconnect in ${delay}ms (attempt ${this.reconnectAttempts})`);

        setTimeout(() => {
            if (this.roomId) {
                this.connect(this.roomId).catch(console.error);
            }
        }, delay);
    }

    /**
     * Ngắt kết nối WebSocket
     */
    disconnect(): void {
        this.stopHeartbeat();
        if (this.ws) {
            this.ws.close(1000, 'User disconnected');
            this.ws = null;
        }
        this.isAuthenticated = false;
        this.roomId = null;
    }

    /**
     * Gửi message qua WebSocket
     */
    private send(message: any): void {
        console.log('Sending WebSocket message:', JSON.stringify(message));
        if (this.ws?.readyState === WebSocket.OPEN) {
            this.ws.send(JSON.stringify(message));
        } else {
            console.warn('WebSocket not connected');
        }
    }

    /**
     * Gửi tin nhắn text
     */
    sendMessage(content: string, messageType: string = 'text'): void {
        if (!this.isAuthenticated) {
            console.warn('WebSocket not authenticated');
            return;
        }

        const message: WSNewMessage = {
            type: 'new_message',
            data: { content, messageType: messageType as any },
            roomId: this.roomId!
        };

        this.send(message);
    }

    /**
     * Gửi typing indicator
     */
    sendTyping(isTyping: boolean): void {
        if (!this.isAuthenticated) {
            console.warn('Cannot send typing - not authenticated');
            return;
        }

        const message: WSTypingMessage = {
            type: 'typing_indicator',
            data: { isTyping },
            roomId: this.roomId!
        };

        console.log('Sending typing message:', message);
        this.send(message);
    }

    /**
     * Gửi read receipt
     */
    sendReadReceipt(messageId: number): void {
        if (!this.isAuthenticated) {
            return;
        }

        const message: WSReadMessage = {
            type: 'message_read',
            data: { messageId },
            roomId: this.roomId!
        };

        this.send(message);
    }

    /**
     * Xử lý message nhận được
     */
    private handleMessage(message: WSIncomingMessage): void {
        console.log('Received WebSocket message:', JSON.stringify(message, null, 2));
        this.notifyMessageHandlers(message);
    }

    /**
     * Heartbeat để duy trì kết nối
     */
    private startHeartbeat(): void {
        this.heartbeatInterval = setInterval(() => {
            if (this.ws?.readyState === WebSocket.OPEN) {
                // Just check connection status, don't send ping
                console.log('WebSocket heartbeat - connection alive');
            }
        }, 30000); // Check every 30 seconds
    }

    private stopHeartbeat(): void {
        if (this.heartbeatInterval) {
            clearInterval(this.heartbeatInterval);
            this.heartbeatInterval = null;
        }
    }

    // Event handler management
    addMessageHandler(handler: (message: WSIncomingMessage) => void): void {
        this.messageHandlers.push(handler);
    }

    removeMessageHandler(handler: (message: WSIncomingMessage) => void): void {
        const index = this.messageHandlers.indexOf(handler);
        if (index > -1) {
            this.messageHandlers.splice(index, 1);
        }
    }

    addConnectionHandler(handler: (connected: boolean) => void): void {
        this.connectionHandlers.push(handler);
    }

    removeConnectionHandler(handler: (connected: boolean) => void): void {
        const index = this.connectionHandlers.indexOf(handler);
        if (index > -1) {
            this.connectionHandlers.splice(index, 1);
        }
    }

    addErrorHandler(handler: (error: Event) => void): void {
        this.errorHandlers.push(handler);
    }

    removeErrorHandler(handler: (error: Event) => void): void {
        const index = this.errorHandlers.indexOf(handler);
        if (index > -1) {
            this.errorHandlers.splice(index, 1);
        }
    }

    private notifyMessageHandlers(message: WSIncomingMessage): void {
        this.messageHandlers.forEach(handler => {
            try {
                handler(message);
            } catch (error) {
                console.error('Error in message handler:', error);
            }
        });
    }

    private notifyConnectionHandlers(connected: boolean): void {
        this.connectionHandlers.forEach(handler => {
            try {
                handler(connected);
            } catch (error) {
                console.error('Error in connection handler:', error);
            }
        });
    }

    private notifyErrorHandlers(error: Event): void {
        this.errorHandlers.forEach(handler => {
            try {
                handler(error);
            } catch (error) {
                console.error('Error in error handler:', error);
            }
        });
    }

    /**
     * Kiểm tra trạng thái kết nối
     */
    isConnected(): boolean {
        return this.ws?.readyState === WebSocket.OPEN && this.isAuthenticated;
    }

    /**
     * Lấy roomId hiện tại
     */
    getCurrentRoomId(): number | null {
        return this.roomId;
    }

    /**
     * Cập nhật token xác thực
     */
    updateToken(token: string): void {
        this.token = token;
    }
}

// Singleton instance
export const websocketService = new WebSocketService();
export default websocketService;
