package utils

import (
	"github.com/gin-gonic/gin"
	"github.com/golibs-starter/golib/log"
	"strconv"
)

func ParseIntParam(c *gin.Context, paramName string, bitSize int) (*int64, error) {
	if paramStr := c.Query(paramName); paramStr != "" {
		val, err := strconv.ParseInt(paramStr, 10, bitSize)
		if err != nil {
			log.Error(c, "error parsing "+paramName, err)
			return nil, err
		}
		return &val, nil
	}
	return nil, nil
}
