# WebSocket Authentication Changes - Backend Implementation

## Tổng quan
Frontend đã được cập nhật để sử dụng **Authentication Message** thay vì token trong URL. Backend cần thay đổi để hỗ trợ flow mới này.

## Flow Authentication mới

### Frontend Flow:
1. ✅ Kết nối WebSocket mà không có token trong URL
2. ✅ Gửi auth message ngay sau khi connect: `{type: 'auth', token: 'jwt_token', roomId: 123}`
3. ✅ Đợi response `auth_success` hoặc `auth_error`
4. ✅ Chỉ gửi/nhận message khác khi đã authenticated

### Backend cần implement:
1. 🔄 Accept WebSocket connection mà không cần auth ngay
2. 🔄 Đợi auth message đầu tiên
3. 🔄 Validate JWT token từ auth message
4. 🔄 Gửi `auth_success` hoặc `auth_error` response
5. 🔄 Tiếp tục handle message bình thường sau khi auth

## Files cần thay đổi

### 1. `/src/ui/controller/ws_controller.go`

#### Thêm struct mới:
```go
type AuthMessage struct {
    Type   string `json:"type"`
    Token  string `json:"token"`
    RoomId int64  `json:"roomId"`
}

type AuthResponse struct {
    Type    string      `json:"type"`
    Success bool        `json:"success"`
    Message string      `json:"message,omitempty"`
    Data    interface{} `json:"data,omitempty"`
}
```

#### Sửa HandleConnection method:
```go
func (wc *WebSocketController) HandleConnection(c *gin.Context) {
    // Chỉ cần validate roomId từ query, không cần token
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

    // Upgrade connection mà không cần auth
    conn, err := upgrader.Upgrade(c.Writer, c.Request, nil)
    if err != nil {
        log.Error(c, "Failed to upgrade connection: ", err)
        apihelper.AbortErrorHandle(c, common.GeneralServiceUnavailable)
        return
    }

    // Tạo temporary client (chưa authenticated)
    tempClient := &ws.ClientConnection{
        UserID:     0, // Chưa biết user ID
        Connection: conn,
    }

    // Handle auth và messages
    go wc.handleAuthAndMessages(tempClient, roomID)
}
```

#### Thêm method mới:
```go
func (wc *WebSocketController) handleAuthAndMessages(client *ws.ClientConnection, roomID int64) {
    defer func() {
        client.Connection.Close()
    }()

    // Đợi auth message đầu tiên (timeout 30s)
    client.Connection.SetReadDeadline(time.Now().Add(30 * time.Second))
    
    _, messageBytes, err := client.Connection.ReadMessage()
    if err != nil {
        log.Error(nil, "Failed to read auth message: ", err)
        wc.sendAuthError(client, "Failed to read authentication message")
        return
    }

    // Parse auth message
    var authMessage AuthMessage
    if err := json.Unmarshal(messageBytes, &authMessage); err != nil {
        log.Error(nil, "Failed to parse auth message: ", err)
        wc.sendAuthError(client, "Invalid authentication message format")
        return
    }

    if authMessage.Type != "auth" {
        log.Error(nil, "Expected auth message, got: ", authMessage.Type)
        wc.sendAuthError(client, "Expected authentication message")
        return
    }

    // Validate JWT token
    userID, err := wc.parseJWTToken(authMessage.Token)
    if err != nil {
        log.Error(nil, "Invalid JWT token: ", err)
        wc.sendAuthError(client, "Invalid authentication token")
        return
    }

    // Verify room access
    ctx := context.Background()
    _, err = wc.chatRoomService.GetChatRoomByID(ctx, userID, roomID)
    if err != nil {
        log.Error(nil, "User has no access to room: ", err)
        wc.sendAuthError(client, "Access denied to chat room")
        return
    }

    // Update client với real user ID
    client.UserID = userID

    // Send auth success
    wc.sendAuthSuccess(client)

    // Register client và join room
    wc.wsManager.RegisterClient(client)
    wc.wsManager.JoinRoom(roomID, userID)

    // Notify others về user connection
    wc.wsManager.BroadcastToRoomExcept(roomID, userID, &ws.WebSocketMessage{
        Type: "user_connected",
        Payload: map[string]interface{}{
            "userId": userID,
            "action": "connected",
        },
    })

    // Remove read deadline và continue với normal messages
    client.Connection.SetReadDeadline(time.Time{})
    
    // Handle regular messages
    wc.handleMessages(client, roomID)
}

func (wc *WebSocketController) sendAuthSuccess(client *ws.ClientConnection) {
    response := AuthResponse{
        Type:    "auth_success",
        Success: true,
        Message: "Authentication successful",
    }
    
    if data, err := json.Marshal(response); err == nil {
        client.Connection.WriteMessage(websocket.TextMessage, data)
    }
}

func (wc *WebSocketController) sendAuthError(client *ws.ClientConnection, message string) {
    response := AuthResponse{
        Type:    "auth_error",
        Success: false,
        Message: message,
    }
    
    if data, err := json.Marshal(response); err == nil {
        client.Connection.WriteMessage(websocket.TextMessage, data)
    }
}

func (wc *WebSocketController) parseJWTToken(tokenString string) (int64, error) {
    // JWT configuration - should match middleware
    secretKey := "MnAYFOKwxBOznHpxA3Wx4cJjeC3vYNtwzI6HRYT9SD++1BM9Pk32pTGETroljFsS"
    
    type JWTClaims struct {
        Subject string `json:"sub"`
        Scope   string `json:"scope"`
        Email   string `json:"email"`
        jwt.RegisteredClaims
    }
    
    token, err := jwt.ParseWithClaims(tokenString, &JWTClaims{}, func(token *jwt.Token) (interface{}, error) {
        if _, ok := token.Method.(*jwt.SigningMethodHMAC); !ok {
            return nil, fmt.Errorf("unexpected signing method: %v", token.Header["alg"])
        }
        return []byte(secretKey), nil
    })

    if err != nil {
        return 0, fmt.Errorf("failed to parse JWT token: %w", err)
    }

    if claims, ok := token.Claims.(*JWTClaims); ok && token.Valid {
        userID, err := strconv.ParseInt(claims.Subject, 10, 64)
        if err != nil {
            return 0, fmt.Errorf("failed to parse user ID from JWT subject: %w", err)
        }
        return userID, nil
    }

    return 0, fmt.Errorf("invalid JWT token")
}
```

#### Cập nhật handleMessages method:
```go
func (wc *WebSocketController) handleMessages(client *ws.ClientConnection, roomID int64) {
    defer func() {
        // Notify others về user disconnection
        wc.wsManager.BroadcastToRoomExcept(roomID, client.UserID, &ws.WebSocketMessage{
            Type: "user_disconnected",
            Payload: map[string]interface{}{
                "userId": client.UserID,
                "action": "disconnected",
            },
        })
        wc.wsManager.UnregisterClient(client)
    }()

    for {
        _, messageBytes, err := client.Connection.ReadMessage()
        if err != nil {
            if websocket.IsUnexpectedCloseError(err, websocket.CloseGoingAway, websocket.CloseAbnormalClosure) {
                log.Error(nil, "WebSocket error: ", err)
            }
            break
        }

        // Parse incoming JSON message
        var incomingMessage struct {
            Type   string      `json:"type"`
            Data   interface{} `json:"data"`
            RoomId int64       `json:"roomId"`
        }
        
        if err := json.Unmarshal(messageBytes, &incomingMessage); err != nil {
            log.Error(nil, "Failed to parse WebSocket message: ", err)
            wc.sendErrorToClient(client, "Invalid message format")
            continue
        }

        log.Info(nil, "Received WebSocket message from user ", client.UserID, " type: ", incomingMessage.Type)

        switch incomingMessage.Type {
        case "message":
            wc.handleNewMessage(client, roomID, incomingMessage.Data)
        case "typing":
            wc.handleTypingIndicator(client, roomID, incomingMessage.Data)
        case "read":
            wc.handleReadReceipt(client, roomID, incomingMessage.Data)
        default:
            log.Warn(nil, "Unknown WebSocket message type: ", incomingMessage.Type)
            wc.sendErrorToClient(client, "Unknown message type")
        }
    }
}

// Thêm helper methods để handle different message types
func (wc *WebSocketController) handleNewMessage(client *ws.ClientConnection, roomID int64, data interface{}) {
    // Parse message data và create message in database
    // Broadcast to all room participants
}

func (wc *WebSocketController) handleTypingIndicator(client *ws.ClientConnection, roomID int64, data interface{}) {
    // Broadcast typing indicator to other participants (exclude sender)
}

func (wc *WebSocketController) handleReadReceipt(client *ws.ClientConnection, roomID int64, data interface{}) {
    // Handle read receipt - update database và broadcast
}

func (wc *WebSocketController) sendErrorToClient(client *ws.ClientConnection, errorMessage string) {
    // Send error message to specific client
}
```

### 2. `/src/infrastructure/ws_manager.go`

#### Thêm method BroadcastToRoomExcept:
```go
func (m *WebSocketManager) BroadcastToRoomExcept(roomID int64, exceptUserID int64, message *ws.WebSocketMessage) {
    m.broadcast <- &BroadcastMessage{
        RoomID:    roomID,
        Message:   message,
        ExceptUID: &exceptUserID, // Add field to exclude specific user
    }
}
```

#### Cập nhật BroadcastMessage struct:
```go
type BroadcastMessage struct {
    RoomID    int64                `json:"roomId"`
    Message   *ws.WebSocketMessage `json:"message"`
    ExceptUID *int64               `json:"exceptUid,omitempty"` // Optional: exclude this user
}
```

#### Cập nhật broadcast logic trong run() method:
```go
case message := <-m.broadcast:
    m.mutex.RLock()
    users := m.rooms[message.RoomID]
    for _, userID := range users {
        // Skip excluded user if specified
        if message.ExceptUID != nil && userID == *message.ExceptUID {
            continue
        }
        
        if client, ok := m.clients[userID]; ok {
            messageBytes, err := json.Marshal(message.Message)
            if err != nil {
                log.Error(nil, "Failed to marshal message: ", err)
                continue
            }

            err = client.Connection.WriteMessage(websocket.TextMessage, messageBytes)
            if err != nil {
                log.Error(nil, "Failed to send message: ", err)
                m.unregister <- client
            }
        }
    }
    m.mutex.RUnlock()
```

### 3. Thêm imports cần thiết:

```go
import (
    "context"
    "encoding/json"
    "fmt"
    "strconv"
    "time"
    
    "github.com/golang-jwt/jwt/v5"
    // ... existing imports
)
```

## Message Types Backend cần hỗ trợ

### Incoming (từ Frontend):
- `auth` - Authentication message
- `message` - Chat message
- `typing` - Typing indicator
- `read` - Read receipt

### Outgoing (tới Frontend):
- `auth_success` - Authentication successful
- `auth_error` - Authentication failed
- `message` - New chat message
- `typing` - Typing indicator
- `read` - Read receipt
- `user_connected` - User joined room
- `user_disconnected` - User left room
- `error` - General error

## Ưu điểm của approach này:

1. ✅ **Security**: Token không xuất hiện trong URL logs
2. ✅ **Flexibility**: Có thể handle auth errors gracefully
3. ✅ **Maintainability**: Clear separation of concerns
4. ✅ **Scalability**: Dễ extend với additional auth logic
5. ✅ **Browser compatibility**: Works với mọi browser

## Testing checklist:

- [ ] Connect với valid token → auth_success
- [ ] Connect với invalid token → auth_error
- [ ] Connect với missing token → auth_error
- [ ] Send message before auth → should be ignored
- [ ] Send message after auth → should work
- [ ] User connection/disconnection notifications
- [ ] Multiple users trong same room
- [ ] Typing indicators
- [ ] Read receipts

## Notes:

- JWT secret key phải match với authentication middleware
- Timeout 30s cho auth message để tránh hanging connections
- User connection notifications để update online status
- Error handling comprehensive để avoid crashes
