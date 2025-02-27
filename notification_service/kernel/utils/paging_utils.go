package utils

import (
	"github.com/gin-gonic/gin"
	"notification_service/core/domain/constant"
	"strconv"
)

func CalculateParameterForGetRequest(page, pageSize, total int64) (*int64, *int64, int64) {
	totalPage := int64(0)
	var nextPage *int64
	var prevPage *int64

	if total%pageSize == 0 {
		totalPage = total / pageSize
	} else {
		totalPage = total/pageSize + 1
	}
	if page > 1 {
		temp := page - 1
		prevPage = &temp
	}
	if page < totalPage {
		temp := page + 1
		nextPage = &temp
	}

	return nextPage, prevPage, totalPage
}

func GetPagingParams(c *gin.Context) (int, int) {
	pageSize, err := strconv.ParseInt(c.Query("pageSize"), 10, 32)
	if err != nil {
		pageSize = constant.DefaultPageSize
	}
	page, err := strconv.ParseInt(c.Query("page"), 10, 32)
	if err != nil {
		page = constant.DefaultOffset
	}
	return int(pageSize), int(page)
}
