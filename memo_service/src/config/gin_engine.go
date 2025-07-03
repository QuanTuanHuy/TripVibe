package config

import (
	"github.com/gin-gonic/gin"
)

func NewGinEngine() *gin.Engine {
	engine := gin.New()

	engine.Use(gin.Logger())
	engine.Use(gin.Recovery())

	return engine
}
