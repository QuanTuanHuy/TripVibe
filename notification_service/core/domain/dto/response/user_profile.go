package response

type UserProfileDto struct {
	ID        int64  `json:"id"`
	Name      string `json:"name"`
	Gender    string `json:"gender"`
	Phone     string `json:"phone"`
	Email     string `json:"email"`
	AvatarUrl string `json:"avatarUrl"`
}
