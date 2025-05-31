package entity

import "chat_service/core/domain/constant"

type MediaDataEntity struct {
	BaseEntity
	URL          string             `json:"url"`
	FileName     string             `json:"fileName"`
	FileSize     int64              `json:"fileSize"`
	MimeType     string             `json:"mimeType"`
	MediaType    constant.MediaType `json:"mediaType"`
	ThumbnailURL *string            `json:"thumbnailUrl,omitempty"`
}
