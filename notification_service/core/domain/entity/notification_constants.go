package entity

// Notification types
const (
	EmailNotification = "EMAIL"
	SMSNotification   = "SMS"
	PushNotification  = "PUSH"
)

// Notification statuses
const (
	StatusPending    = "PENDING"
	StatusProcessing = "PROCESSING"
	StatusSent       = "SENT"
	StatusFailed     = "FAILED"
)
