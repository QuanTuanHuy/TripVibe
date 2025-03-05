package model

type AccommodationModel struct {
	BaseModel
	Name string `gorm:"column:name;type:varchar(512);not null" json:"name"`
}

func (AccommodationModel) TableName() string {
	return "accommodations"
}
