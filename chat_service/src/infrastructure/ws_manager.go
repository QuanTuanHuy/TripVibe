package infrastructure

import (
	"chat_service/core/domain/ws"
	"encoding/json"
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
