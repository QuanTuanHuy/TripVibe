package ws

import "github.com/gorilla/websocket"

type ClientConnection struct {
	UserID     int64
	Connection *websocket.Conn
}
