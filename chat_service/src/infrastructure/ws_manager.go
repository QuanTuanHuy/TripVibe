package infrastructure

import (
	"chat_service/core/domain/ws"
	"encoding/json"
	"errors"
	"fmt"
	"github.com/golibs-starter/golib/log"
	"github.com/gorilla/websocket"
	"sync"
)

type WebSocketManager struct {
	// Map of userID -> connection
	clients map[int64]*ws.ClientConnection
	// Map of roomID -> list of userIDs
	rooms      map[int64][]int64
	register   chan *ws.ClientConnection
	unregister chan *ws.ClientConnection
	broadcast  chan *BroadcastMessage
	mutex      sync.RWMutex
}

type BroadcastMessage struct {
	RoomID  int64                `json:"roomId"`
	Message *ws.WebSocketMessage `json:"message"`
}

func NewWebSocketManager() *WebSocketManager {
	manager := &WebSocketManager{
		clients:    make(map[int64]*ws.ClientConnection),
		rooms:      make(map[int64][]int64),
		register:   make(chan *ws.ClientConnection),
		unregister: make(chan *ws.ClientConnection),
		broadcast:  make(chan *BroadcastMessage),
		mutex:      sync.RWMutex{},
	}

	go manager.run()
	return manager
}

func (m *WebSocketManager) run() {
	for {
		select {
		case client := <-m.register:
			m.mutex.Lock()
			m.clients[client.UserID] = client
			m.mutex.Unlock()
			log.Info(nil, "Client registered: ", client.UserID)

		case client := <-m.unregister:
			m.mutex.Lock()
			if _, ok := m.clients[client.UserID]; ok {
				delete(m.clients, client.UserID)
				client.Connection.Close()

				// Remove user from all rooms
				for roomID, users := range m.rooms {
					for i, userID := range users {
						if userID == client.UserID {
							m.rooms[roomID] = append(users[:i], users[i+1:]...)
							break
						}
					}
				}
			}
			m.mutex.Unlock()
			log.Info(nil, "Client unregistered: ", client.UserID)

		case message := <-m.broadcast:
			m.mutex.RLock()
			users := m.rooms[message.RoomID]
			for _, userID := range users {
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
		}
	}
}

func (m *WebSocketManager) RegisterClient(client *ws.ClientConnection) {
	m.register <- client
}

func (m *WebSocketManager) UnregisterClient(client *ws.ClientConnection) {
	m.unregister <- client
}

func (m *WebSocketManager) BroadcastToRoom(roomID int64, message *ws.WebSocketMessage) {
	m.broadcast <- &BroadcastMessage{
		RoomID:  roomID,
		Message: message,
	}
}

// BroadcastToRoomWithError broadcasts a message to a room with error handling
func (m *WebSocketManager) BroadcastToRoomWithError(roomID int64, message *ws.WebSocketMessage) error {
	select {
	case m.broadcast <- &BroadcastMessage{
		RoomID:  roomID,
		Message: message,
	}:
		return nil
	default:
		log.Error(nil, "Failed to queue broadcast message, channel full", "roomID", roomID)
		return errors.New("broadcast channel full")
	}
}

// SendToUser sends a message to a specific user
func (m *WebSocketManager) SendToUser(userID int64, message *ws.WebSocketMessage) error {
	m.mutex.RLock()
	client, exists := m.clients[userID]
	m.mutex.RUnlock()

	if !exists {
		log.Warn(nil, "User not connected", "userID", userID)
		return errors.New("user not connected")
	}

	messageBytes, err := json.Marshal(message)
	if err != nil {
		log.Error(nil, "Failed to marshal message for user", "userID", userID, "error", err)
		return err
	}

	// Use goroutine to prevent blocking
	go func() {
		err := client.Connection.WriteMessage(websocket.TextMessage, messageBytes)
		if err != nil {
			log.Error(nil, "Failed to send message to user", "userID", userID, "error", err)
			m.unregister <- client
		}
	}()

	return nil
}

// GetConnectedUsersInRoom returns list of connected users in a room
func (m *WebSocketManager) GetConnectedUsersInRoom(roomID int64) []int64 {
	m.mutex.RLock()
	defer m.mutex.RUnlock()

	roomUsers, exists := m.rooms[roomID]
	if !exists {
		return []int64{}
	}

	// Filter only connected users
	var connectedUsers []int64
	for _, userID := range roomUsers {
		if _, connected := m.clients[userID]; connected {
			connectedUsers = append(connectedUsers, userID)
		}
	}

	return connectedUsers
}

// BroadcastToRoomWithAck broadcasts message to room and sends acknowledgments
func (m *WebSocketManager) BroadcastToRoomWithAck(roomID int64, message *ws.WebSocketMessage, excludeUserID *int64) error {
	connectedUsers := m.GetConnectedUsersInRoom(roomID)
	
	successCount := 0
	var errors []error

	for _, userID := range connectedUsers {
		// Skip excluded user if specified
		if excludeUserID != nil && userID == *excludeUserID {
			continue
		}

		err := m.SendToUser(userID, message)
		if err != nil {
			errors = append(errors, err)
			log.Error(nil, "Failed to send message to user in room", 
				"userID", userID, "roomID", roomID, "error", err)
		} else {
			successCount++
		}
	}

	log.Info(nil, "Broadcast completed", 
		"roomID", roomID, "totalUsers", len(connectedUsers), 
		"successCount", successCount, "errorCount", len(errors))

	if len(errors) > 0 && successCount == 0 {
		return fmt.Errorf("failed to send to all users in room %d", roomID)
	}

	return nil
}

func (m *WebSocketManager) JoinRoom(roomID int64, userID int64) {
	m.mutex.Lock()
	defer m.mutex.Unlock()

	if _, ok := m.rooms[roomID]; !ok {
		m.rooms[roomID] = []int64{}
	}

	// Check if user is already in the room
	for _, id := range m.rooms[roomID] {
		if id == userID {
			return
		}
	}

	m.rooms[roomID] = append(m.rooms[roomID], userID)
	log.Info(nil, "User ", userID, " joined room ", roomID)
}
