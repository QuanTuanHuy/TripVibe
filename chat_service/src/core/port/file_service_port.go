package port

import (
	"chat_service/core/domain/dto/response"
	"context"
	"mime/multipart"
)

type IFileServicePort interface {
	UploadFile(ctx context.Context, file *multipart.FileHeader) (*response.FileUploadResponse, error)
}
