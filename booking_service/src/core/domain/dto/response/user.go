package response

import "booking_service/core/domain/entity"

type UserResponse struct {
	ID          int64  `json:"id"`
	LastName    string `json:"lastName"`
	FirstName   string `json:"firstName"`
	Email       string `json:"email"`
	PhoneNumber string `json:"phoneNumber"`
}

func ToUserResponse(user *entity.UserEntity) *UserResponse {
	if user == nil {
		return nil
	}
	return &UserResponse{
		ID:          user.ID,
		LastName:    user.LastName,
		FirstName:   user.FirstName,
		Email:       user.Email,
		PhoneNumber: user.PhoneNumber,
	}
}
