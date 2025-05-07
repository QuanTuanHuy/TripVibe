package model

type UnitModel struct {
	BaseModel
	Name            string `gorm:"type:varchar(255);not null"`
	AccommodationID int64  `gorm:"type:int;not null"`
	Quantity        int    `gorm:"type:int;not null"`
}

func (UnitModel) TableName() string {
	return "units"
}
