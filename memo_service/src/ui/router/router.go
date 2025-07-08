package router

import (
	"memo_service/src/ui/controller"

	"github.com/gin-gonic/gin"
	"go.uber.org/fx"
)

type RegisterRoutersIn struct {
	fx.In
	App                *gin.Engine
	MemoController     *controller.MemoController
	MarkdownController *controller.MarkdownController
}

func RegisterGinRouters(p RegisterRoutersIn) {
	router := p.App.Group("/api")

	memoV1 := router.Group("/v1/memos")
	{
		memoV1.POST("", p.MemoController.CreateMemo)
		memoV1.GET("/:id", p.MemoController.GetMemoByID)
	}

	markdownV1 := router.Group("/v1/markdown")
	{
		markdownV1.POST("/convert", p.MarkdownController.ConvertToHTML)
		markdownV1.GET("/languages", p.MarkdownController.GetSupportedLanguages)
		markdownV1.GET("/styles", p.MarkdownController.GetAvailableStyles)
	}
}
