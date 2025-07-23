package controller

import (
	"memo_service/src/core/common"
	"memo_service/src/core/domain/dto/request"
	"memo_service/src/core/service"
	"memo_service/src/ui/helper"
	"strconv"

	"github.com/gin-gonic/gin"
	"go.uber.org/zap"
)

type MemoController struct {
	memoService service.IMemoService
	logger      *zap.Logger
}

func (m *MemoController) CreateMemo(ctx *gin.Context) {
	var createMemoDto request.CreateMemoDto
	if err := ctx.ShouldBindJSON(&createMemoDto); err != nil {
		helper.AbortErrorHandle(ctx, common.GeneralBadRequest)
		return
	}

	var userID int64 = 1
	memo, err := m.memoService.CreateMemo(ctx, userID, &createMemoDto)
	if err != nil {
		m.logger.Error("Failed to create memo", zap.Error(err))
		helper.AbortErrorHandle(ctx, common.GeneralServiceUnavailable)
		return
	}

	helper.SuccessfulHandle(ctx, memo)
}

func (m *MemoController) GetMemoByID(ctx *gin.Context) {
	memoID, err := strconv.ParseInt(ctx.Param("id"), 10, 64)
	if err != nil {
		helper.AbortErrorHandle(ctx, common.GeneralBadRequest)
		return
	}

	var userID int64 = 1
	memo, err := m.memoService.GetMemoByID(ctx, userID, memoID)
	if err != nil {
		m.logger.Error("Failed to get memo by ID", zap.Error(err))
		helper.AbortErrorHandle(ctx, common.GeneralBadRequest)
		return
	}

	helper.SuccessfulHandle(ctx, memo)
}

func NewMemoController(memoService service.IMemoService, logger *zap.Logger) *MemoController {
	return &MemoController{
		memoService: memoService,
		logger:      logger,
	}
}
