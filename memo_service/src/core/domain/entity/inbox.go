package entity

type InboxStatus string

func (s InboxStatus) String() string {
	return string(s)
}

const (
	UNREAD   InboxStatus = "UNREAD"
	ARCHIVED InboxStatus = "ARCHIVED"
)

type Inbox struct {
	BaseEntity
	SenderID   int64       `json:"senderId"`
	ReceiverID int64       `json:"receiverId"`
	Status     InboxStatus `json:"status"`
	Message    string      `json:"message"`
}
