package response

type PaginationResult struct {
	TotalItems     int64
	HasMore        bool
	NextCursor     *int64
	PreviousCursor *int64
}
