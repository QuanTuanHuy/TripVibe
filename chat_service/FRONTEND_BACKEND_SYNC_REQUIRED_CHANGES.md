# Frontend-Backend WebSocket Synchronization Required Changes

## 📋 Summary

Phân tích chi tiết về việc đồng bộ hóa payload và authentication flow giữa Frontend (TypeScript) và Backend (Go) cho WebSocket chat functionality.

## 🔍 Current State Analysis

### Frontend WebSocket Implementation (`useWebSocket.ts`)

#### **Authentication Flow:**
```typescript
// 1. Connection establishment
ws.current = new WebSocket(url);

// 2. Authentication message on connection
const authMessage = {
    type: 'auth',
    token: token,
    roomId: roomId
};
ws.current?.send(JSON.stringify(authMessage));

// 3. Expected responses
if (data.type === 'auth_success') { ... }
if (data.type === 'auth_error') { ... }
```

#### **Message Format (Outgoing):**
```typescript
const wsMessage: WebSocketMessage = {
    type: 'message',           // Consistent: 'message'
    data: message,             // Field name: 'data'
    roomId: roomId!            // Include roomId
};
```

#### **Message Types Handled (Incoming):**
- `'auth_success'`, `'auth_error'`
- `'message'`, `'typing'`, `'read'`, `'error'`
- `'user_connected'`, `'user_disconnected'`

#### **WebSocketMessage Interface:**
```typescript
export interface WebSocketMessage {
    type: 'message' | 'typing' | 'read' | 'error' | 'auth' | 'auth_success' | 'auth_error' | 'user_connected' | 'user_disconnected';
    data: any;
    roomId?: number; // Optional for auth messages
}
```

### Backend WebSocket Implementation (Go)

#### **Current Authentication Flow:**
```go
// Uses middleware-based auth on connection
userIDStr, exists := c.Get("userID")  // From JWT middleware
roomIDStr := c.Query("roomId")        // From query parameter
```

#### **Current Message Format:**
```go
type WebSocketMessage struct {
    Type    string      `json:"type"`
    Payload interface{} `json:"payload"`  // ❌ Different field name
}
```

#### **Current Message Types:**
```go
const (
    MessageTypeNewMessage    = "new_message"     // ❌ Different from frontend 'message'
    MessageTypeReadMessage   = "read_message"    // ❌ Different from frontend 'read'
    MessageTypeUserConnected = "user_connected"  // ✅ Matches frontend
)
```

#### **Current Message Handling:**
```go
// Simple echo implementation - no JSON parsing
log.Info(nil, "Received message: ", string(message))
wc.wsManager.BroadcastToRoom(roomID, &ws.WebSocketMessage{
    Type:    ws.MessageTypeNewMessage,
    Payload: string(message),  // ❌ Raw string, not structured data
})
```

## 🚨 Critical Mismatches Identified

### 1. **Field Name Mismatch**
| Frontend | Backend | Status |
|----------|---------|--------|
| `data`   | `payload` | ❌ MISMATCH |
| `roomId` | Not included | ❌ MISSING |

### 2. **Message Type Mismatch**
| Frontend | Backend | Status |
|----------|---------|--------|
| `'message'` | `'new_message'` | ❌ MISMATCH |
| `'read'` | `'read_message'` | ❌ MISMATCH |
| `'typing'` | Not implemented | ❌ MISSING |
| `'auth'` | Not implemented | ❌ MISSING |
| `'auth_success'` | Not implemented | ❌ MISSING |
| `'auth_error'` | Not implemented | ❌ MISSING |

### 3. **Authentication Flow Mismatch**
| Frontend Approach | Backend Approach | Status |
|-------------------|------------------|--------|
| Message-based auth after connection | Middleware auth before connection | ❌ MISMATCH |
| JWT in message payload | JWT in query parameter/header | ❌ MISMATCH |

### 4. **Message Structure Mismatch**
| Frontend Sends | Backend Expects | Status |
|----------------|-----------------|--------|
| JSON structured object | Raw string | ❌ MISMATCH |
| `{type, data, roomId}` | Simple echo | ❌ MISMATCH |

## 🔧 Required Backend Changes

### **Phase 1: Update WebSocket Message Structure**

#### 1.1 Update `ws/message.go`
```go
package ws

// Updated to match frontend WebSocketMessage interface
type WebSocketMessage struct {
    Type   string      `json:"type"`
    Data   interface{} `json:"data"`    // Changed from 'payload' to 'data'
    RoomId *int64      `json:"roomId,omitempty"` // Add roomId, optional for auth
}

// Updated message types to match frontend
const (
    // Authentication types
    MessageTypeAuth        = "auth"
    MessageTypeAuthSuccess = "auth_success"
    MessageTypeAuthError   = "auth_error"
    
    // Chat message types  
    MessageTypeMessage     = "message"        // Changed from 'new_message'
    MessageTypeTyping      = "typing"         // New
    MessageTypeRead        = "read"           // Changed from 'read_message'
    MessageTypeError       = "error"          // New
    
    // User status types
    MessageTypeUserConnected    = "user_connected"
    MessageTypeUserDisconnected = "user_disconnected"
)

// Authentication message structures
type AuthMessage struct {
    Type   string `json:"type"`   // "auth"
    Token  string `json:"token"`
    RoomId int64  `json:"roomId"`
}

type AuthResponse struct {
    Type string      `json:"type"`   // "auth_success" or "auth_error"
    Data interface{} `json:"data"`
}

// Incoming message from frontend
type IncomingWebSocketMessage struct {
    Type   string      `json:"type"`
    Data   interface{} `json:"data"`
    RoomId *int64      `json:"roomId,omitempty"`
}
```

#### 1.2 Update `infrastructure/ws_manager.go`
```go
// Add support for broadcasting to all users except one
type BroadcastMessage struct {
    RoomID    int64                `json:"roomId"`
    Message   *ws.WebSocketMessage `json:"message"`
    ExceptUID *int64               `json:"exceptUid,omitempty"` // New field
}

// Add new method for broadcasting except specific user
func (m *WebSocketManager) BroadcastToRoomExcept(roomID int64, message *ws.WebSocketMessage, exceptUserID int64) {
    m.broadcast <- &BroadcastMessage{
        RoomID:    roomID,
        Message:   message,
        ExceptUID: &exceptUserID,
    }
}

// Update broadcast logic in run() method
case message := <-m.broadcast:
    m.mutex.RLock()
    users := m.rooms[message.RoomID]
    for _, userID := range users {
        // Skip if this user should be excluded
        if message.ExceptUID != nil && userID == *message.ExceptUID {
            continue
        }
        
        if client, ok := m.clients[userID]; ok {
            // ... existing broadcast logic
        }
    }
    m.mutex.RUnlock()
```

### **Phase 2: Implement Authentication Message Pattern**

#### 2.1 Update `controller/ws_controller.go`
```go
package controller

import (
    "context"
    "encoding/json"
    "fmt"
    "strconv"
    "time"
    
    "chat_service/core/constant"
    "chat_service/core/domain/common"
    "chat_service/core/domain/ws"
    "chat_service/core/service"
    "chat_service/infrastructure"
    "chat_service/kernel/apihelper"
    
    "github.com/gin-gonic/gin"
    "github.com/golang-jwt/jwt/v5"
    "github.com/golibs-starter/golib/log"
    "github.com/gorilla/websocket"
)

// JWT Claims structure
type JWTClaims struct {
    UserID int64 `json:"sub"`
    jwt.RegisteredClaims
}

// Remove auth middleware requirement from HandleConnection
func (wc *WebSocketController) HandleConnection(c *gin.Context) {
    roomIDStr := c.Query("roomId")
    if roomIDStr == "" {
        log.Error(c, "Missing roomId parameter")
        apihelper.AbortErrorHandle(c, common.GeneralBadRequest)
        return
    }

    roomID, err := strconv.ParseInt(roomIDStr, 10, 64)
    if err != nil {
        log.Error(c, "Invalid roomId: ", err)
        apihelper.AbortErrorHandle(c, common.GeneralBadRequest)
        return
    }

    // Upgrade HTTP connection to WebSocket (no auth check here)
    conn, err := upgrader.Upgrade(c.Writer, c.Request, nil)
    if err != nil {
        log.Error(c, "Failed to upgrade connection: ", err)
        apihelper.AbortErrorHandle(c, common.GeneralServiceUnavailable)
        return
    }

    // Handle authentication via WebSocket messages
    go wc.handleAuthAndMessages(conn, roomID)
}

// New method to handle authentication handshake
func (wc *WebSocketController) handleAuthAndMessages(conn *websocket.Conn, roomID int64) {
    defer conn.Close()

    // Wait for authentication message
    _, message, err := conn.ReadMessage()
    if err != nil {
        log.Error(nil, "Failed to read auth message: ", err)
        return
    }

    var authMsg ws.AuthMessage
    if err := json.Unmarshal(message, &authMsg); err != nil {
        log.Error(nil, "Failed to parse auth message: ", err)
        wc.sendAuthError(conn, "Invalid authentication message format")
        return
    }

    if authMsg.Type != ws.MessageTypeAuth {
        log.Error(nil, "Expected auth message, got: ", authMsg.Type)
        wc.sendAuthError(conn, "Expected authentication message")
        return
    }

    // Parse and validate JWT token
    token, err := jwt.ParseWithClaims(authMsg.Token, &JWTClaims{}, func(token *jwt.Token) (interface{}, error) {
        // Replace with your actual JWT secret
        return []byte("your-jwt-secret"), nil
    })

    if err != nil || !token.Valid {
        log.Error(nil, "Invalid JWT token: ", err)
        wc.sendAuthError(conn, "Invalid authentication token")
        return
    }

    claims, ok := token.Claims.(*JWTClaims)
    if !ok {
        log.Error(nil, "Invalid JWT claims")
        wc.sendAuthError(conn, "Invalid token claims")
        return
    }

    userID := claims.UserID

    // Verify user has access to the room
    ctx := context.Background()
    _, err = wc.chatRoomService.GetChatRoomByID(ctx, userID, roomID)
    if err != nil {
        log.Error(nil, "Failed to verify room access: ", err)
        wc.sendAuthError(conn, "Access denied to chat room")
        return
    }

    // Authentication successful
    wc.sendAuthSuccess(conn)

    // Create client and register
    client := &ws.ClientConnection{
        UserID:     userID,
        Connection: conn,
    }

    wc.wsManager.RegisterClient(client)
    wc.wsManager.JoinRoom(roomID, userID)

    // Notify other users in the room
    wc.wsManager.BroadcastToRoomExcept(roomID, &ws.WebSocketMessage{
        Type: ws.MessageTypeUserConnected,
        Data: map[string]interface{}{
            "userId": userID,
        },
    }, userID)

    // Handle subsequent messages
    wc.handleMessages(client, roomID)
}

func (wc *WebSocketController) sendAuthSuccess(conn *websocket.Conn) {
    response := ws.AuthResponse{
        Type: ws.MessageTypeAuthSuccess,
        Data: map[string]string{"status": "authenticated"},
    }
    
    responseBytes, _ := json.Marshal(response)
    conn.WriteMessage(websocket.TextMessage, responseBytes)
}

func (wc *WebSocketController) sendAuthError(conn *websocket.Conn, errorMsg string) {
    response := ws.AuthResponse{
        Type: ws.MessageTypeAuthError,
        Data: map[string]string{"error": errorMsg},
    }
    
    responseBytes, _ := json.Marshal(response)
    conn.WriteMessage(websocket.TextMessage, responseBytes)
}
```

### **Phase 3: Implement Structured Message Handling**

#### 3.1 Update `handleMessages` method
```go
func (wc *WebSocketController) handleMessages(client *ws.ClientConnection, roomID int64) {
    defer func() {
        wc.wsManager.UnregisterClient(client)
        
        // Notify others that user disconnected
        wc.wsManager.BroadcastToRoomExcept(roomID, &ws.WebSocketMessage{
            Type: ws.MessageTypeUserDisconnected,
            Data: map[string]interface{}{
                "userId": client.UserID,
            },
        }, client.UserID)
    }()

    for {
        _, message, err := client.Connection.ReadMessage()
        if err != nil {
            if websocket.IsUnexpectedCloseError(err, websocket.CloseGoingAway, websocket.CloseAbnormalClosure) {
                log.Error(nil, "WebSocket error: ", err)
            }
            break
        }

        var incomingMsg ws.IncomingWebSocketMessage
        if err := json.Unmarshal(message, &incomingMsg); err != nil {
            log.Error(nil, "Failed to parse message: ", err)
            wc.sendErrorToClient(client, "Invalid message format")
            continue
        }

        log.Info(nil, "Received message from user ", client.UserID, ": ", incomingMsg.Type)

        switch incomingMsg.Type {
        case ws.MessageTypeMessage:
            wc.handleNewMessage(client, roomID, incomingMsg)
        case ws.MessageTypeTyping:
            wc.handleTypingIndicator(client, roomID, incomingMsg)
        case ws.MessageTypeRead:
            wc.handleReadReceipt(client, roomID, incomingMsg)
        default:
            log.Warn(nil, "Unknown message type: ", incomingMsg.Type)
            wc.sendErrorToClient(client, "Unknown message type")
        }
    }
}

func (wc *WebSocketController) handleNewMessage(client *ws.ClientConnection, roomID int64, incomingMsg ws.IncomingWebSocketMessage) {
    // Parse message data
    messageData, ok := incomingMsg.Data.(map[string]interface{})
    if !ok {
        wc.sendErrorToClient(client, "Invalid message data format")
        return
    }

    content, ok := messageData["content"].(string)
    if !ok || content == "" {
        wc.sendErrorToClient(client, "Message content is required")
        return
    }

    // Create message in database
    ctx := context.Background()
    newMessage, err := wc.chatRoomService.CreateMessage(ctx, roomID, client.UserID, content, constant.TextMessage)
    if err != nil {
        log.Error(nil, "Failed to create message: ", err)
        wc.sendErrorToClient(client, "Failed to save message")
        return
    }

    // Broadcast to all users in room
    wc.wsManager.BroadcastToRoom(roomID, &ws.WebSocketMessage{
        Type: ws.MessageTypeMessage,
        Data: map[string]interface{}{
            "id":       newMessage.ID,
            "content":  newMessage.Content,
            "senderId": newMessage.SenderID,
            "type":     constant.TextMessage,
            "createdAt": newMessage.CreatedAt.Unix(),
        },
    })
}

func (wc *WebSocketController) handleTypingIndicator(client *ws.ClientConnection, roomID int64, incomingMsg ws.IncomingWebSocketMessage) {
    // Broadcast typing indicator to other users (not sender)
    wc.wsManager.BroadcastToRoomExcept(roomID, &ws.WebSocketMessage{
        Type: ws.MessageTypeTyping,
        Data: map[string]interface{}{
            "userId":   client.UserID,
            "isTyping": incomingMsg.Data,
            "roomId":   roomID,
        },
    }, client.UserID)
}

func (wc *WebSocketController) handleReadReceipt(client *ws.ClientConnection, roomID int64, incomingMsg ws.IncomingWebSocketMessage) {
    // Handle read receipt logic
    wc.wsManager.BroadcastToRoomExcept(roomID, &ws.WebSocketMessage{
        Type: ws.MessageTypeRead,
        Data: incomingMsg.Data,
    }, client.UserID)
}

func (wc *WebSocketController) sendErrorToClient(client *ws.ClientConnection, errorMsg string) {
    errorResponse := &ws.WebSocketMessage{
        Type: ws.MessageTypeError,
        Data: map[string]string{"error": errorMsg},
    }
    
    responseBytes, _ := json.Marshal(errorResponse)
    client.Connection.WriteMessage(websocket.TextMessage, responseBytes)
}
```

## 🚀 Implementation Plan

### **Step 1: Update WebSocket Domain Models**
- [ ] Update `ws/message.go` with new message structure
- [ ] Add authentication message types and structs
- [ ] Update message type constants

### **Step 2: Update WebSocket Manager**
- [ ] Add `BroadcastToRoomExcept` method
- [ ] Update broadcast logic to support excluding users

### **Step 3: Implement Authentication Flow**
- [ ] Remove auth middleware from WebSocket route
- [ ] Implement message-based authentication in controller
- [ ] Add JWT parsing and validation
- [ ] Add auth success/error responses

### **Step 4: Implement Structured Message Handling**
- [ ] Add JSON parsing for incoming messages
- [ ] Implement message type routing (message, typing, read)
- [ ] Add database integration for message persistence
- [ ] Add error handling and client error responses

### **Step 5: Testing**
- [ ] Test authentication handshake flow
- [ ] Test message sending and broadcasting
- [ ] Test typing indicators
- [ ] Test user connection/disconnection notifications
- [ ] Test error handling scenarios

## ⚠️ Breaking Changes

### **Router Configuration Update**
```go
// Remove auth middleware from WebSocket route
// Before:
// wsGroup.Use(authMiddleware).GET("/chats", wsController.HandleConnection)

// After:
wsGroup.GET("/chats", wsController.HandleConnection)
```

### **Frontend URL Update Required**
```typescript
// May need to update WebSocket URL in frontend if route changes
// Current: `/ws/chats?roomId=${room.id}`
// Ensure this matches the backend route configuration
```

## 🔒 Security Considerations

1. **JWT Secret Management**: Ensure JWT secret is properly configured and secured
2. **Token Validation**: Implement proper token expiration and validation
3. **Room Access Control**: Verify user permissions for each room access
4. **Input Validation**: Validate all incoming message data
5. **Rate Limiting**: Consider implementing rate limiting for message sending

## 📝 Additional Notes

- All message types now match between frontend and backend
- Authentication is handled via WebSocket messages rather than HTTP headers
- Message structure is consistent with `{type, data, roomId}` format
- Error handling provides clear feedback to clients
- Broadcasting supports excluding specific users (for typing indicators, user status)

This implementation will fully synchronize the WebSocket communication protocol between the TypeScript frontend and Go backend.
