package model

import "time"

type BaseModel struct {
	ID        int64     `gorm:"column:id;primary_key"`
	CreatedAt time.Time `gorm:"column:created_at" sql:"default:current_timestamp"`
	UpdatedAt time.Time `gorm:"column:updated_at" sql:"default:current_timestamp"`
}
