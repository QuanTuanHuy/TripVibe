package model

type AccommodationModel struct {
	BaseModel
	OwnerID int64  `gorm:"column:owner_id;type:bigint;not null" json:"ownerId"`
	Name    string `gorm:"column:name;type:varchar(512);not null" json:"name"`
}

func (AccommodationModel) TableName() string {
	return "accommodations"
}
