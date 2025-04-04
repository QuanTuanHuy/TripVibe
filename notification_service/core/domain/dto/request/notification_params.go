package request

type NotificationParams struct {
	UserID *int64
	Status *string
	Type   *string
	BaseSpec
}
