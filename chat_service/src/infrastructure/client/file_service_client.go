package client

import (
	"chat_service/core/domain/constant"
	"chat_service/core/domain/dto/response"
	"chat_service/core/port"
	"context"
	"fmt"
	"mime/multipart"

	"github.com/golibs-starter/golib/log"
)

type FileServiceClient struct {
	apiClient *ApiClient
}

func (f *FileServiceClient) UploadFile(ctx context.Context, fileHeader *multipart.FileHeader) (*response.FileUploadResponse, error) {
	err := f.SetAuthToken(ctx)
	if err != nil {
		return nil, err
	}

	file, err := fileHeader.Open()
	if err != nil {
		return nil, err
	}
	defer file.Close()
	fileContent := make([]byte, fileHeader.Size)
	_, err = file.Read(fileContent)
	if err != nil {
		return nil, fmt.Errorf("failed to read file content: %w", err)
	}
	fileName := fileHeader.Filename

	var uploadResponses []response.FileUploadResponse
	err = f.apiClient.UploadFile(
		ctx, constant.FILE_SERVICE, constant.UPLOAD_ENDPOINT, "files", fileName, fileContent, nil, &uploadResponses)
	if err != nil {
		return nil, fmt.Errorf("failed to upload file: %w", err)
	}

	if len(uploadResponses) > 0 {
		return &uploadResponses[0], nil
	}

	return nil, fmt.Errorf("no file uploaded")
}

func (f *FileServiceClient) SetAuthToken(ctx context.Context) error {
	token, ok := ctx.Value("token").(string)
	if !ok {
		log.Error(ctx, "error getting token from context")
		return fmt.Errorf("error getting token from context")
	}

	err := f.apiClient.SetAuthToken(constant.FILE_SERVICE, token)
	if err != nil {
		log.Error(ctx, "error setting auth token for inventory service, ", err)
		return err
	}
	return nil
}

func NewFileServiceClient(apiClient *ApiClient) port.IFileServicePort {
	return &FileServiceClient{
		apiClient: apiClient,
	}
}
