package mapper

import (
	"chat_service/core/domain/constant"
	"chat_service/core/domain/entity"
	"chat_service/infrastructure/repository/model"
)

func ToMediaDataEntity(mediaData *model.MediaDataModel) *entity.MediaDataEntity {
	if mediaData == nil {
		return nil
	}
	return &entity.MediaDataEntity{
		BaseEntity: entity.BaseEntity{
			ID:        mediaData.ID,
			CreatedAt: mediaData.CreatedAt.Unix(),
			UpdatedAt: mediaData.UpdatedAt.Unix(),
		},
		URL:          mediaData.URL,
		FileName:     mediaData.FileName,
		FileSize:     mediaData.FileSize,
		MimeType:     mediaData.MimeType,
		MediaType:    constant.MediaType(mediaData.MediaType),
		ThumbnailURL: mediaData.ThumbnailURL,
	}
}

func ToMediaDataModel(mediaData *entity.MediaDataEntity) *model.MediaDataModel {
	if mediaData == nil {
		return nil
	}

	return &model.MediaDataModel{
		BaseModel: model.BaseModel{
			ID: mediaData.ID,
		},
		URL:          mediaData.URL,
		FileName:     mediaData.FileName,
		FileSize:     mediaData.FileSize,
		MimeType:     mediaData.MimeType,
		MediaType:    string(mediaData.MediaType),
		ThumbnailURL: mediaData.ThumbnailURL,
	}
}

func ToMediaDataEntities(mediaDataList []*model.MediaDataModel) []*entity.MediaDataEntity {
	var entities []*entity.MediaDataEntity
	for _, mediaData := range mediaDataList {
		entities = append(entities, ToMediaDataEntity(mediaData))
	}
	return entities
}
