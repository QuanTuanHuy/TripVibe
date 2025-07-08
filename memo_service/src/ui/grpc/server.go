package grpcserver

import (
	"fmt"
	"memo_service/src/core/service"
	pb "memo_service/src/pkg/proto"
	"net"

	"go.uber.org/zap"
	"google.golang.org/grpc"
	"google.golang.org/grpc/reflection"
)

type GRPCServer struct {
	server     *grpc.Server
	memoServer *MemoGRPCServer
	logger     *zap.Logger
	Port       int
}

func (s *GRPCServer) Start() error {
	address := fmt.Sprintf(":%d", s.Port)
	listener, err := net.Listen("tcp", address)
	if err != nil {
		s.logger.Error("failed to listen", zap.Error(err))
		return err
	}

	s.logger.Info("starting gRPC server", zap.String("address", address))

	return s.server.Serve(listener)
}

func (s *GRPCServer) Stop() {
	s.logger.Info("stopping gRPC server")
	s.server.GracefulStop()
}

func (s *GRPCServer) GetServer() *grpc.Server {
	return s.server
}

func NewGRPCServer(
	memoService service.IMemoService,
	logger *zap.Logger,
) *GRPCServer {
	grpcServer := grpc.NewServer()

	memoServer := NewMemoGRPCServer(memoService, logger)

	pb.RegisterMemoServiceServer(grpcServer, memoServer)

	reflection.Register(grpcServer)

	return &GRPCServer{
		server:     grpcServer,
		memoServer: memoServer,
		logger:     logger,
		Port:       50051,
	}
}
