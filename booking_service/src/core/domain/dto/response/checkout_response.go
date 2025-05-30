package response

type CheckOutResponse struct {
	Success      bool    `json:"success"`
	Message      string  `json:"message"`
	CheckOutTime *string `json:"checkOutTime,omitempty"`
}
