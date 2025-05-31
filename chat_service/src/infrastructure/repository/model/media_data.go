package model

type MediaDataModel struct {
	BaseModel
	URL          string  `gorm:"not null;column:url"`
	FileName     string  `gorm:"not null;column:file_name"`
	FileSize     int64   `gorm:"not null;column:file_size"`
	MimeType     string  `gorm:"not null;column:mime_type"`
	MediaType    string  `gorm:"not null;column:media_type"`
	ThumbnailURL *string `gorm:"column:thumbnail_url"`
}

func (MediaDataModel) TableName() string {
	return "media_data"
}
