package utils

import (
	"chat_service/core/domain/constant"
	"strings"
)

func DetermineMediaType(mimeType string) constant.MediaType {
	switch {
	case strings.HasPrefix(mimeType, "image/"):
		return constant.ImageMediaType
	case strings.HasPrefix(mimeType, "video/"):
		return constant.VideoMediaType
	case strings.HasPrefix(mimeType, "audio/"):
		return constant.AudioMediaType
	default:
		return constant.DocumentMediaType
	}
}
