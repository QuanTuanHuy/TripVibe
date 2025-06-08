import { useEffect, useRef, useState } from 'react';
import { websocketService } from '@/services/chat';
import { WSIncomingMessage } from '@/types/chat';

export const useWebSocket = (roomId: number | null) => {
  const [isConnected, setIsConnected] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const messageHandlersRef = useRef<((message: WSIncomingMessage) => void)[]>([]);
  const reconnectTimeoutRef = useRef<NodeJS.Timeout | undefined>(undefined);

  useEffect(() => {
    if (!roomId) {
      setIsConnected(false);
      return;
    }

    const connectWebSocket = async () => {
      try {
        setError(null);
        await websocketService.connect(roomId);
      } catch (err) {
        setError(err instanceof Error ? err.message : 'Connection failed');
        console.error('WebSocket connection failed:', err);
      }
    };

    const handleConnection = (connected: boolean) => {
      setIsConnected(connected);
      if (connected) {
        setError(null);
        // Clear any pending reconnection attempts
        if (reconnectTimeoutRef.current) {
          clearTimeout(reconnectTimeoutRef.current);
          reconnectTimeoutRef.current = undefined;
        }
      } else if (!connected && roomId) {
        // Schedule reconnection if disconnected unexpectedly
        reconnectTimeoutRef.current = setTimeout(() => {
          connectWebSocket();
        }, 3000);
      }
    };

    const handleError = (error: Event) => {
      setError('WebSocket error occurred');
      console.error('WebSocket error:', error);
    };

    // Add event handlers
    websocketService.addConnectionHandler(handleConnection);
    websocketService.addErrorHandler(handleError);

    // Initial connection
    connectWebSocket();

    return () => {
      // Cleanup
      websocketService.removeConnectionHandler(handleConnection);
      websocketService.removeErrorHandler(handleError);

      // Clear reconnection timeout
      if (reconnectTimeoutRef.current) {
        clearTimeout(reconnectTimeoutRef.current);
      }

      // Clear message handlers
      messageHandlersRef.current.forEach(handler => {
        websocketService.removeMessageHandler(handler);
      });
      messageHandlersRef.current = [];

      // Disconnect if this is the last component using this room
      if (websocketService.getCurrentRoomId() === roomId) {
        websocketService.disconnect();
      }
    };
  }, [roomId]);

  const addMessageHandler = (handler: (message: WSIncomingMessage) => void) => {
    messageHandlersRef.current.push(handler);
    websocketService.addMessageHandler(handler);
  };

  const removeMessageHandler = (handler: (message: WSIncomingMessage) => void) => {
    const index = messageHandlersRef.current.indexOf(handler);
    if (index > -1) {
      messageHandlersRef.current.splice(index, 1);
    }
    websocketService.removeMessageHandler(handler);
  };

  const sendMessage = (content: string, messageType?: string) => {
    if (!isConnected) {
      console.warn('Cannot send message: WebSocket not connected');
      return;
    }
    websocketService.sendMessage(content, messageType);
  };

  const sendTyping = (isTyping: boolean) => {
    if (!isConnected) return;
    websocketService.sendTyping(isTyping);
  };

  const sendReadReceipt = (messageId: number) => {
    if (!isConnected) return;
    websocketService.sendReadReceipt(messageId);
  };

  const reconnect = () => {
    if (roomId) {
      websocketService.disconnect();
      setTimeout(() => {
        websocketService.connect(roomId).catch(console.error);
      }, 1000);
    }
  };

  return {
    isConnected,
    error,
    addMessageHandler,
    removeMessageHandler,
    sendMessage,
    sendTyping,
    sendReadReceipt,
    reconnect,
    connect: websocketService.connect.bind(websocketService),
    disconnect: websocketService.disconnect.bind(websocketService),
    getCurrentRoomId: websocketService.getCurrentRoomId.bind(websocketService)
  };
};
