package router

import (
	"memo_service/src/ui/controller"

	"github.com/gin-gonic/gin"
	"go.uber.org/fx"
)

type RegisterRoutersIn struct {
	fx.In
	App            *gin.Engine
	MemoController *controller.MemoController
}

func RegisterGinRouters(p RegisterRoutersIn) {
	router := p.App.Group("/api")

	memoV1 := router.Group("/v1/memos")
	{
		memoV1.POST("", p.MemoController.CreateMemo)
		memoV1.GET("/:id", p.MemoController.GetMemoByID)
	}
}
