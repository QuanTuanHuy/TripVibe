package controller

import (
	"chat_service/core/domain/common"
	"chat_service/core/domain/constant"
	"chat_service/core/domain/ws"
	"chat_service/core/service"
	"chat_service/infrastructure"
	"chat_service/kernel/apihelper"
	"context"
	"encoding/json"
	"github.com/gin-gonic/gin"
	"github.com/golibs-starter/golib/log"
	"github.com/gorilla/websocket"
	"net/http"
	"strconv"
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
	userIDFromContext, exists := c.Get("userID")
	if !exists {
		log.Error(c, "User ID not found in context")
		apihelper.AbortErrorHandle(c, common.GeneralUnauthorized)
		return
	}
	userID := userIDFromContext.(int64)

	roomIDStr := c.Query("roomId")

	roomID, err := strconv.ParseInt(roomIDStr, 10, 64)
	if err != nil {
		log.Error(c, "Invalid roomId: ", err)
		apihelper.AbortErrorHandle(c, common.GeneralBadRequest)
		return
	}

	// Verify the user has access to this room
	_, err = wc.chatRoomService.GetChatRoomByID(c, userID, roomID)

	if err != nil {
		log.Error(c, "Failed to verify room access: ", err)
		apihelper.AbortErrorHandle(c, common.GeneralForbidden)
		return
	}

	// Upgrade HTTP connection to WebSocket
	conn, err := upgrader.Upgrade(c.Writer, c.Request, nil)
	if err != nil {
		log.Error(c, "Failed to upgrade connection: ", err)
		apihelper.AbortErrorHandle(c, common.GeneralServiceUnavailable)
		return
	}

	client := &ws.ClientConnection{
		UserID:     userID,
		Connection: conn,
	}

	// Register client and join room
	wc.wsManager.RegisterClient(client)
	wc.wsManager.JoinRoom(roomID, userID)

	// Notify other users in the room
	wc.wsManager.BroadcastToRoom(roomID, &ws.WebSocketMessage{
		Payload: map[string]interface{}{
			"userId": userID,
		},
	})

	// Handle incoming messages
	go wc.handleMessages(client, roomID)
}

func (wc *WebSocketController) handleMessages(client *ws.ClientConnection, roomID int64) {
	defer func() {
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

		// Parse incoming WebSocket message
		var wsMessage ws.WebSocketMessage
		if err := json.Unmarshal(message, &wsMessage); err != nil {
			log.Error(nil, "Failed to parse WebSocket message: ", err)
			continue
		}

		// Handle different message types
		switch wsMessage.Type {
		case ws.MessageTypeStatusUpdate:
			wc.handleStatusUpdate(client, roomID, &wsMessage)
		case ws.MessageTypeAcknowledgment:
			wc.handleAcknowledgment(client, roomID, &wsMessage)
		case ws.MessageTypeNewMessage:
			// Process new message (existing functionality)
			log.Info(nil, "Received message from user ", client.UserID, ": ", wsMessage.Payload)
			// Broadcast the message to the room with error handling
			if err := wc.wsManager.BroadcastToRoomWithError(roomID, &wsMessage); err != nil {
				log.Error(nil, "Failed to broadcast message to room ", roomID, ": ", err)
			}
		default:
			log.Warn(nil, "Unknown message type: ", wsMessage.Type)
		}
	}
}

// handleStatusUpdate processes status update messages (e.g., marking messages as delivered/read)
func (wc *WebSocketController) handleStatusUpdate(client *ws.ClientConnection, roomID int64, message *ws.WebSocketMessage) {
	payload, ok := message.Payload.(map[string]interface{})
	if !ok {
		log.Error(nil, "Invalid status update payload")
		return
	}

	messageIDFloat, exists := payload["messageId"]
	if !exists {
		log.Error(nil, "Missing messageId in status update")
		return
	}

	messageID, ok := messageIDFloat.(float64)
	if !ok {
		log.Error(nil, "Invalid messageId type in status update")
		return
	}

	statusStr, exists := payload["status"]
	if !exists {
		log.Error(nil, "Missing status in status update")
		return
	}

	status, ok := statusStr.(string)
	if !ok {
		log.Error(nil, "Invalid status type in status update")
		return
	}

	// Convert string status to constant
	var messageStatus constant.MessageStatus
	switch status {
	case string(constant.MessageStatusDelivered):
		messageStatus = constant.MessageStatusDelivered
	case string(constant.MessageStatusRead):
		messageStatus = constant.MessageStatusRead
	default:
		log.Error(nil, "Invalid status value: ", status)
		return
	}

	// Update message status through service
	ctx := context.Background() // Use background context for WebSocket operations
	_, err := wc.chatRoomService.UpdateMessageStatus(ctx, int64(messageID), messageStatus, client.UserID)
	if err != nil {
		log.Error(nil, "Failed to update message status: ", err)
		// Send error response back to client
		errorResponse := &ws.WebSocketMessage{
			Type: ws.MessageTypeStatusUpdate,
			Payload: map[string]interface{}{
				"error":     "Failed to update message status",
				"messageId": int64(messageID),
			},
		}
		wc.wsManager.SendToUser(client.UserID, errorResponse)
		return
	}

	log.Info(nil, "Message status updated successfully: messageID=", int64(messageID), " status=", messageStatus, " userID=", client.UserID)
}

// handleAcknowledgment processes acknowledgment messages
func (wc *WebSocketController) handleAcknowledgment(client *ws.ClientConnection, roomID int64, message *ws.WebSocketMessage) {
	payload, ok := message.Payload.(map[string]interface{})
	if !ok {
		log.Error(nil, "Invalid acknowledgment payload")
		return
	}

	ackIDFloat, exists := payload["ackId"]
	if !exists {
		log.Error(nil, "Missing ackId in acknowledgment")
		return
	}

	ackID, ok := ackIDFloat.(string)
	if !ok {
		log.Error(nil, "Invalid ackID type in acknowledgment")
		return
	}

	log.Info(nil, "Received acknowledgment from user ", client.UserID, " for ackId: ", ackID)

	// Process acknowledgment (implementation depends on business requirements)
	// For now, just log the acknowledgment
}
