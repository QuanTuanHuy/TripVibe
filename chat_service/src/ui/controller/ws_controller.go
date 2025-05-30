package controller

import (
	"chat_service/core/domain/common"
	"chat_service/core/domain/ws"
	"chat_service/core/service"
	"chat_service/infrastructure"
	"chat_service/kernel/apihelper"
	"net/http"
	"strconv"

	"github.com/gin-gonic/gin"
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
	userIDStr, exists := c.Get("userID")
	if !exists {
		log.Error(c, "User ID not found in context")
		apihelper.AbortErrorHandle(c, common.GeneralUnauthorized)
		return
	}
	roomIDStr := c.Query("roomId")

	if roomIDStr == "" {
		log.Error(c, "Missing userId or roomId parameter")
		apihelper.AbortErrorHandle(c, common.GeneralBadRequest)
		return
	}

	userID := userIDStr.(int64)

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

		// Process message (in a production app, you'd parse and validate the message)
		log.Info(nil, "Received message from user ", client.UserID, ": ", string(message))

		// For simplicity, echo the message back
		wc.wsManager.BroadcastToRoom(roomID, &ws.WebSocketMessage{
			Type:    ws.MessageTypeNewMessage,
			Payload: string(message),
		})
	}
}
