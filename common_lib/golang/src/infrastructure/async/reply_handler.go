package async

import (
	"context"
	"time"

	"go.uber.org/zap"

	"github.com/quantuanhuy/lib/src/config"
	entity "github.com/quantuanhuy/lib/src/core/entity/async"
	msgEntity "github.com/quantuanhuy/lib/src/core/entity/msg"
	"github.com/quantuanhuy/lib/src/core/port"
)

// ReplyMessageHandler handles incoming reply messages
type ReplyMessageHandler struct {
	asyncManager   port.IAsyncRequestManagerPort
	correlationMgr port.ICorrelationManagerPort
	logger         *zap.Logger
	config         *config.AsyncConfig
}

// NewReplyMessageHandler creates a new reply message handler
func NewReplyMessageHandler(
	asyncManager port.IAsyncRequestManagerPort,
	correlationMgr port.ICorrelationManagerPort,
	logger *zap.Logger,
	config *config.AsyncConfig,
) *ReplyMessageHandler {
	return &ReplyMessageHandler{
		asyncManager:   asyncManager,
		correlationMgr: correlationMgr,
		logger:         logger,
		config:         config,
	}
}

// HandleMessage handles incoming Kafka messages
func (h *ReplyMessageHandler) HandleMessage(ctx context.Context, msg *msgEntity.KafkaMessage) error {
	// Parse async message
	asyncMsg, err := entity.FromJSON(msg.Value)
	if err != nil {
		h.logger.Error("Failed to parse async message", zap.Error(err))
		return err
	}

	if asyncMsg.Type != entity.MessageTypeReply {
		h.logger.Warn("Expected reply message, got",
			zap.String("type", string(asyncMsg.Type)),
			zap.String("correlation_id", asyncMsg.CorrelationID))
		return nil
	}

	return h.handleReply(ctx, asyncMsg)
}

// handleReply handles reply messages
func (h *ReplyMessageHandler) handleReply(ctx context.Context, msg *entity.AsyncMessage) error {
	h.logger.Info("Processing async reply",
		zap.String("correlation_id", msg.CorrelationID),
		zap.String("status", string(msg.Status)))

	// Get correlation data
	correlationData, callback, err := h.correlationMgr.GetCorrelation(ctx, msg.CorrelationID)
	if err != nil {
		h.logger.Error("Failed to get correlation data",
			zap.String("correlation_id", msg.CorrelationID),
			zap.Error(err))
		return err
	}

	if correlationData == nil {
		h.logger.Warn("No correlation data found",
			zap.String("correlation_id", msg.CorrelationID))
		return nil
	}

	// Create reply entity
	reply := &entity.AsyncReply{
		ID:            msg.ID,
		CorrelationID: msg.CorrelationID,
		RequestID:     correlationData.ID,
		Status:        msg.Status,
		Result:        msg.Payload,
		Error:         msg.Error,
		ProcessedAt:   msg.Timestamp,
		Duration:      time.Since(correlationData.CreatedAt),
	}

	// Update correlation data
	correlationData.Status = msg.Status
	correlationData.UpdatedAt = time.Now()

	// Store updated correlation data
	if err := h.correlationMgr.StoreCorrelation(ctx, msg.CorrelationID, correlationData, callback); err != nil {
		h.logger.Error("Failed to update correlation data",
			zap.String("correlation_id", msg.CorrelationID),
			zap.Error(err))
	}

	// Process reply using async manager
	h.logger.Info("Completed async reply",
		zap.String("correlation_id", msg.CorrelationID),
		zap.String("status", string(msg.Status)),
		zap.Duration("duration", reply.Duration))

	// Handle callback if present
	if callback != nil {
		go func() {
			defer func() {
				if r := recover(); r != nil {
					h.logger.Error("Panic in callback function",
						zap.String("correlation_id", msg.CorrelationID),
						zap.Any("panic", r))
				}
			}()

			callback(reply)
		}()
	}

	// Clean up correlation data after processing
	if err := h.correlationMgr.RemoveCorrelation(ctx, msg.CorrelationID); err != nil {
		h.logger.Error("Failed to remove correlation data",
			zap.String("correlation_id", msg.CorrelationID),
			zap.Error(err))
	}

	return nil
}

// GetPendingReplies returns pending replies for debugging
func (h *ReplyMessageHandler) GetPendingReplies(ctx context.Context) ([]string, error) {
	// This would need to be implemented in the correlation manager
	// For now, return empty slice
	return []string{}, nil
}

// CleanupExpiredReplies removes expired correlation data
func (h *ReplyMessageHandler) CleanupExpiredReplies(ctx context.Context) error {
	return h.correlationMgr.CleanupExpired(ctx)
}
