package entity

type NotificationEntity struct {
	BaseEntity
	Type       string
	Content    string
	ReceiverID int64
	IsRead     bool
	Status     string
}
