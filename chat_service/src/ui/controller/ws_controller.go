package controller

import (
	"chat_service/core/domain/common"
	"chat_service/core/domain/constant"
	"chat_service/core/domain/dto/request"
	"chat_service/core/domain/dto/response"
	"chat_service/core/domain/ws"
	"chat_service/core/service"
	"chat_service/infrastructure"
	"chat_service/kernel/apihelper"
	"context"
	"encoding/json"
	"fmt"
	"net/http"
	"strconv"
	"time"

	"github.com/gin-gonic/gin"
	"github.com/golang-jwt/jwt/v5"
	"github.com/golibs-starter/golib/log"
	"github.com/gorilla/websocket"
)

type WebSocketController struct {
	wsManager       *infrastructure.WebSocketManager
	chatRoomService service.IChatRoomService
}

var upgrader = websocket.Upgrader{
	ReadBufferSize:  1024,
	WriteBufferSize: 1024,
	CheckOrigin: func(r *http.Request) bool {
		return true
	},
}

func NewWebSocketController(wsManager *infrastructure.WebSocketManager, chatRoomService service.IChatRoomService) *WebSocketController {
	return &WebSocketController{
		wsManager:       wsManager,
		chatRoomService: chatRoomService,
	}
}

// HandleConnection establishes a WebSocket connection
func (wc *WebSocketController) HandleConnection(c *gin.Context) {
	roomID, err := strconv.ParseInt(c.Query("roomId"), 10, 64)
	if err != nil || roomID <= 0 {
		log.Error(c, "Invalid room ID: ", c.Query("roomId"))
		apihelper.AbortErrorHandle(c, common.GeneralBadRequest)
		return
	}

	conn, err := upgrader.Upgrade(c.Writer, c.Request, nil)
	if err != nil {
		log.Error(c, "Failed to upgrade connection: ", err)
		apihelper.AbortErrorHandle(c, common.GeneralServiceUnavailable)
		return
	}

	tempClient := &ws.ClientConnection{
		Connection: conn,
		UserID:     0,
	}

	go wc.handleAuthAndMessages(tempClient, roomID)
}

func (wc *WebSocketController) handleAuthAndMessages(client *ws.ClientConnection, roomID int64) {
	defer func() {
		client.Connection.Close()
	}()

	client.Connection.SetReadDeadline(time.Now().Add(30 * time.Second))

	_, messageBytes, err := client.Connection.ReadMessage()
	if err != nil {
		log.Error(nil, "Failed to read auth message: ", err)
	}

	var authMessage request.AuthWsMessage
	if err := json.Unmarshal(messageBytes, &authMessage); err != nil {
		log.Error(nil, "Failed to unmarshal auth message: ", err)
		wc.sendAuthError(client, "Invalid auth message format")
		return
	}
	if authMessage.Type != ws.MessageTypeAuth {
		log.Error(nil, "Invalid auth message type: ", authMessage.Type)
		wc.sendAuthError(client, "Invalid auth message type")
		return
	}

	userID, err := wc.parseJWTToken(authMessage.Token)
	if err != nil {
		log.Error(nil, "Failed to parse JWT token: ", err)
		wc.sendAuthError(client, "Invalid authentication token")
		return
	}

	ctx := context.Background()
	_, err = wc.chatRoomService.GetChatRoomByID(ctx, userID, roomID)
	if err != nil {
		log.Error(nil, "Chat room not found: ", err)
		wc.sendAuthError(client, "Access denied to chat room")
		return
	}

	client.UserID = userID

	wc.sendAuthSuccess(client, roomID)

	wc.wsManager.RegisterClient(client)
	wc.wsManager.JoinRoom(roomID, userID)

	wc.wsManager.BroadcastToRoomExcept(userID, roomID, &ws.WebSocketMessage{
		Type: ws.MessageTypeUserConnected,
		Payload: map[string]interface{}{
			"userId": userID,
			"action": "connected",
		},
	})

	client.Connection.SetReadDeadline(time.Time{})

	wc.handleMessages(client, roomID)
}

func (wc *WebSocketController) handleMessages(client *ws.ClientConnection, roomID int64) {
	defer func() {
		wc.wsManager.BroadcastToRoomExcept(client.UserID, roomID, &ws.WebSocketMessage{
			Type: ws.MessageTypeUserDisconnected,
			Payload: map[string]interface{}{
				"userId": client.UserID,
				"action": "disconnected",
			},
		})
		wc.wsManager.UnregisterClient(client)
	}()

	for {
		_, message, err := client.Connection.ReadMessage()
		if err != nil {
			if websocket.IsUnexpectedCloseError(err, websocket.CloseGoingAway, websocket.CloseAbnormalClosure) {
				log.Error(nil, "WebSocket error: ", err)
			}
			break
		}

		log.Info(nil, "Received message from user ", client.UserID, ": ", string(message))

		var incommingMessage struct {
			Type   string      `json:"type"`
			Data   interface{} `json:"data"`
			RoomID *int64      `json:"roomId,omitempty"`
		}

		if err := json.Unmarshal(message, &incommingMessage); err != nil {
			log.Error(nil, "Failed to unmarshal message: ", err)
			wc.sendErrorToClient(client, "Invalid message format")
			continue
		}

		log.Info(nil, "Received websocket message from user ", client.UserID, ": ", incommingMessage.Type)

		switch incommingMessage.Type {
		case ws.MessageTypeNewMessage:
			wc.handleNewMessage(client, roomID, incommingMessage.Data)
		case ws.MessageTypeTyping:
			wc.handleTypingIndicator(client, roomID, incommingMessage.Data)
		case ws.MessageTypeReadMessage:
			wc.handleReadMessage(client, roomID, incommingMessage.Data)
		default:
			log.Error(nil, "Unknown message type: ", incommingMessage.Type)
			wc.sendErrorToClient(client, "Unknown message type: "+incommingMessage.Type)
		}
	}
}

func (wc *WebSocketController) handleReadMessage(client *ws.ClientConnection, roomID int64, data interface{}) {
	messageData, ok := data.(map[string]interface{})
	if !ok {
		log.Error(nil, "Invalid read message data format")
		wc.sendErrorToClient(client, "Invalid read message data format")
		return
	}

	messageID, ok := messageData["messageId"].(float64)
	if !ok || messageID <= 0 {
		log.Error(nil, "Invalid message ID for read message")
		wc.sendErrorToClient(client, "Invalid message ID for read message")
		return
	}

	ctx := context.Background()
	err := wc.chatRoomService.MarkMessageAsRead(ctx, roomID, client.UserID, int64(messageID))
	if err != nil {
		log.Error(ctx, "Failed to mark message as read: ", err)
		wc.sendErrorToClient(client, "Failed to mark message as read")
		return
	}

	wc.wsManager.BroadcastToRoomExcept(client.UserID, roomID, &ws.WebSocketMessage{
		Type: ws.MessageTypeReadMessage,
		Payload: map[string]interface{}{
			"messageId": messageID,
			"userId":    client.UserID,
		},
		RoomID: &roomID,
	})
}

func (wc *WebSocketController) handleTypingIndicator(client *ws.ClientConnection, roomID int64, data interface{}) {
	typingData, ok := data.(map[string]interface{})
	if !ok {
		log.Error(nil, "Invalid typing indicator data format")
		wc.sendErrorToClient(client, "Invalid typing indicator data format")
		return
	}

	// Optionally, you can validate the typing indicator data further
	if _, ok := typingData["isTyping"].(bool); !ok {
		log.Error(nil, "Invalid typing indicator format")
		wc.sendErrorToClient(client, "Invalid typing indicator format")
		return
	}

	wc.wsManager.BroadcastToRoomExcept(client.UserID, roomID, &ws.WebSocketMessage{
		Type: ws.MessageTypeTyping,
		Payload: map[string]interface{}{
			"userId":   client.UserID,
			"isTyping": typingData["isTyping"],
		},
		RoomID: &roomID,
	})
}

func (wc *WebSocketController) handleNewMessage(client *ws.ClientConnection, roomID int64, data interface{}) {
	messageData, ok := data.(map[string]interface{})
	if !ok {
		log.Error(nil, "Invalid message data format")
		wc.sendErrorToClient(client, "Invalid message data format")
		return
	}

	content, ok := messageData["content"].(string)
	if !ok || content == "" {
		log.Error(nil, "Message content is empty or invalid")
		wc.sendErrorToClient(client, "Message content cannot be empty")
		return
	}
	messageType, _ := messageData["messageType"].(string)
	if messageType != "" && messageType != string(constant.TextMessage) {
		log.Error(nil, "Unsupported message type: ", messageType)
		wc.sendErrorToClient(client, "Unsupported message type: "+messageType)
		return
	}

	senderID := client.UserID
	ctx := context.Background()
	message, err := wc.chatRoomService.CreateMessage(ctx, roomID, senderID, content, constant.TextMessage)
	if err != nil {
		log.Error(ctx, "Failed to create message: ", err)
		wc.sendErrorToClient(client, "Failed to create message")
		return
	}

	wc.wsManager.BroadcastToRoomExcept(0, roomID, &ws.WebSocketMessage{
		Type:    ws.MessageTypeNewMessage,
		Payload: message,
		RoomID:  &roomID,
	})
}

func (wc *WebSocketController) sendAuthSuccess(client *ws.ClientConnection, roomID int64) {
	response := response.AuthWsResponse{
		Type:    ws.MessageTypeAuthSuccess,
		Success: true,
		Message: "Authentication successful",
	}

	if data, err := json.Marshal(response); err == nil {
		client.Connection.WriteMessage(websocket.TextMessage, data)
	}
}

func (wc *WebSocketController) sendAuthError(client *ws.ClientConnection, message string) {
	response := response.AuthWsResponse{
		Type:    ws.MessageTypeAuthError,
		Success: false,
		Message: message,
	}

	if data, err := json.Marshal(response); err == nil {
		client.Connection.WriteMessage(websocket.TextMessage, data)
	}
}

func (wc *WebSocketController) sendErrorToClient(client *ws.ClientConnection, error string) {
	errResponse := &ws.WebSocketMessage{
		Type: ws.MessageTypeError,
		Payload: map[string]string{
			"error": error,
		},
	}

	if data, err := json.Marshal(errResponse); err == nil {
		client.Connection.WriteMessage(websocket.TextMessage, data)
	}
}

func (wc *WebSocketController) parseJWTToken(tokenString string) (int64, error) {
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
