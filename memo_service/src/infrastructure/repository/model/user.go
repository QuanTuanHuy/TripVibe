package model

type UserModel struct {
	BaseModel
	Username string `gorm:"column:username"`
	Email    string `gorm:"column:email"`
	Avatar   string `gorm:"column:avatar"`
}

func (u UserModel) TableName() string {
	return "users"
}
