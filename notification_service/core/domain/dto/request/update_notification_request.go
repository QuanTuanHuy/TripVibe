package request

type UpdateNotificationDto struct {
	Status      *string
	RetryCount  *int
	LastRetryAt *int64
	SentAt      *int64
}
