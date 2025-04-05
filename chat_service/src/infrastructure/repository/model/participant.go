package model

type ParticipantModel struct {
	UserID   int64  `gorm:"primary_key;column:user_id"`
	UserName string `gorm:"column:user_name"`
	Role     string `gorm:"column:role"`
}

func (ParticipantModel) TableName() string {
	return "participants"
}
