package apihelper

import (
	"chat_service/core/domain/common"
	"net/http"

	"github.com/gin-gonic/gin"
)

// AbortErrorHandle handle abort error
func AbortErrorHandle(c *gin.Context, code int) {
	errorResponse := common.GetErrorResponse(code)
	c.JSON(errorResponse.HTTPCode, gin.H{
		"meta": gin.H{
			"code":    errorResponse.ServiceCode,
			"message": errorResponse.Message,
		},
		"data": nil,
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
		"meta": gin.H{
			"code":    errorResponse.ServiceCode,
			"message": errorResponse.Message,
		},
		"data": nil,
	})
}

// SuccessfulHandle handle successful response
func SuccessfulHandle(c *gin.Context, data interface{}) {
	c.JSON(http.StatusOK, gin.H{
		"meta": gin.H{
			"code":    http.StatusOK,
			"message": "Success",
		},
		"data": data,
	})
}
