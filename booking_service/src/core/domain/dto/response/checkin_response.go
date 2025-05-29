package response

type CheckInResponse struct {
	Success     bool    `json:"success"`
	Message     string  `json:"message"`
	CheckInTime *string `json:"checkInTime"`
}
