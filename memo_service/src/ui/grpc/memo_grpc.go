package grpcserver

import (
	"context"
	"memo_service/src/core/domain/dto/request"
	"memo_service/src/core/domain/entity"
	"memo_service/src/core/service"
	pb "memo_service/src/pkg/proto"

	"go.uber.org/zap"
	"google.golang.org/grpc/codes"
	"google.golang.org/grpc/status"
)

type MemoGRPCServer struct {
	pb.UnimplementedMemoServiceServer
	memoService service.IMemoService
	logger      *zap.Logger
}

func (s *MemoGRPCServer) CreateMemo(ctx context.Context, req *pb.CreateMemoDto) (*pb.Memo, error) {
	if req == nil {
		return nil, status.Errorf(codes.InvalidArgument, "CreateMemoDto cannot be nil")
	}
	if req.Content == "" {
		return nil, status.Errorf(codes.InvalidArgument, "Content cannot be empty")
	}
	createMemo := &request.CreateMemoDto{
		Content:    req.Content,
		Visibility: req.Visibility,
	}
	memo, err := s.memoService.CreateMemo(ctx, req.UserId, createMemo)
	if err != nil {
		s.logger.Error("failed to create memo", zap.Error(err))
		return nil, status.Errorf(codes.Internal, "failed to create memo: %v", err)
	}
	return convertToMemoPb(memo), nil
}
func (s *MemoGRPCServer) GetMemoById(ctx context.Context, req *pb.GetMemoRequest) (*pb.Memo, error) {
	if req == nil {
		return nil, status.Errorf(codes.InvalidArgument, "GetMemoRequest cannot be nil")
	}
	if req.Id <= 0 {
		return nil, status.Errorf(codes.InvalidArgument, "MemoId must be greater than 0")
	}
	memo, err := s.memoService.GetMemoByID(ctx, 1, req.Id)
	if err != nil {
		s.logger.Error("failed to get memo by id", zap.Error(err))
		return nil, status.Errorf(codes.Internal, "failed to get memo: %v", err)
	}
	return convertToMemoPb(memo), nil
}

func NewMemoGRPCServer(memoService service.IMemoService, logger *zap.Logger) *MemoGRPCServer {
	return &MemoGRPCServer{
		memoService: memoService,
		logger:      logger,
	}
}

func convertToMemoPb(memo *entity.Memo) *pb.Memo {
	if memo == nil {
		return nil
	}
	parentId := int64(0)
	if memo.ParentID != nil {
		parentId = *memo.ParentID
	}
	return &pb.Memo{
		Id:         memo.ID,
		CreatorId:  memo.CreatorID,
		Content:    memo.Content,
		Visibility: string(memo.Visibility),
		CreatedAt:  memo.CreatedAt,
		UpdatedAt:  memo.UpdatedAt,
		Pinned:     memo.Pinned,
		ParentId:   parentId,
	}
}
