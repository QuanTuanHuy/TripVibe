package request

type MessageQueryParams struct {
	Cursor      *int64 // ID của tin nhắn cuối cùng trong trang trước, null cho trang đầu tiên
	Limit       int
	Direction   string // "newer" hoặc "older", mặc định là "older"
	SenderID    *int64
	MessageType *string
}
