package request

type AuthWsMessage struct {
	Type   string `json:"type"`
	Token  string `json:"token"`
	RoomID int64  `json:"roomId"`
}
