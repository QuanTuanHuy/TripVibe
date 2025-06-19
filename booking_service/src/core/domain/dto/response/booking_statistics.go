package response

type BookingStatisticsResponse struct {
	TotalBookings int64            `json:"totalBookings"`
	TotalRevenue  float64          `json:"totalRevenue"`
	StatusCounts  map[string]int64 `json:"statusCounts"`
}
