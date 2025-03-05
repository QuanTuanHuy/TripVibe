package model

type UserModel struct {
	BaseModel
	LastName    string `gorm:"column:last_name;type:varchar(255);not null"`
	FirstName   string `gorm:"column:first_name;type:varchar(255);not null"`
	Email       string `gorm:"column:email;type:varchar(255);not null"`
	PhoneNumber string `gorm:"column:phone_number;type:varchar(255);not null"`
}

func (u UserModel) TableName() string {
	return "users"
}
