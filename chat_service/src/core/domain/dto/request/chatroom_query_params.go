package request

type ChatRoomQueryParams struct {
	UserID     *int64
	ChatUserID *int64
	Page       *int
	PageSize   *int
}
