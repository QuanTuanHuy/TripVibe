package request

type MessageQueryParams struct {
	Cursor      *int64  `form:"cursor"` // ID của tin nhắn cuối cùng trong trang trước, null cho trang đầu tiên
	Limit       int     `form:"limit"`
	Direction   string  `form:"direction"` // "older" hoặc "newer", mặc định là "older"
	SenderID    *int64  `form:"senderId"`
	MessageType *string `form:"messageType"`
}
