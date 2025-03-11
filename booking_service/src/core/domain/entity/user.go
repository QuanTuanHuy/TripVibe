package entity

type UserEntity struct {
	BaseEntity
	LastName    string `json:"lastName"`
	FirstName   string `json:"firstName"`
	Email       string `json:"email"`
	PhoneNumber string `json:"phoneNumber"`
}
