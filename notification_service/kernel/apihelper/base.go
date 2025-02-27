package apihelper

import (
	"github.com/gin-gonic/gin"
	"net/http"
	"notification_service/core/common"
)

// AbortErrorHandle handle abort error
func AbortErrorHandle(c *gin.Context, code int) {
	errorResponse := common.GetErrorResponse(code)
	c.JSON(errorResponse.HTTPCode, gin.H{
		"code":    errorResponse.ServiceCode,
		"message": errorResponse.Message,
	})
}

// AbortErrorHandleCustomMessage handle abort with custom message
func AbortErrorHandleCustomMessage(c *gin.Context, code int, message string) {
	errorResponse := common.GetErrorResponse(code)
	c.JSON(errorResponse.HTTPCode, gin.H{
		"code":    errorResponse.ServiceCode,
		"message": message,
		"data":    nil,
	})
}

// AbortErrorResponseHandle handle abort with error response
func AbortErrorResponseHandle(c *gin.Context, errorResponse *common.ErrorResponse) {
	c.JSON(errorResponse.HTTPCode, gin.H{
		"code":    errorResponse.ServiceCode,
		"message": errorResponse.Message,
		"data":    nil,
	})
}

// SuccessfulHandle handle successful response
func SuccessfulHandle(c *gin.Context, data interface{}) {
	c.JSON(http.StatusOK, gin.H{
		"code":    http.StatusOK,
		"message": http.StatusText(http.StatusOK),
		"data":    data,
	})
}

func MakeDataResponseWithPagination(limit int64, offset int64, data interface{}, total int64) (response gin.H) {
	return gin.H{
		"limit":  limit,
		"offset": offset,
		"total":  total,
		"data":   data,
	}
}

func BuildResponseListRequestForApp(limit, offset, total int64, objects map[string]interface{}) gin.H {
	response := gin.H{
		"limit":  limit,
		"offset": offset,
		"total":  total,
	}

	for key := range objects {
		response[key] = objects[key]
	}

	return response
}
