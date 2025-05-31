package entity

import "chat_service/core/domain/constant"

type MessageEntity struct {
	BaseEntity
	ChatRoomID  int64                `json:"chatRoomId"`
	SenderID    *int64               `json:"senderId"`
	Content     string               `json:"content"`
	Type        constant.MessageType `json:"type"`
	IsRead      bool                 `json:"isRead"`
	MediaDataID *int64               `json:"mediaDataId,omitempty"`
	MediaData   *MediaDataEntity     `json:"mediaData,omitempty"`
}
