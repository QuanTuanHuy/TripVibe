package test

import (
	"context"

	schedulerEntity "github.com/quantuanhuy/lib/src/core/entity/scheduler"
	taskEntity "github.com/quantuanhuy/lib/src/core/entity/task"
)

// Mock handlers for testing reference - these are used in workflow_demo_test.go and other integration tests
type MockJobHandler struct {
	processed []string
}

func (h *MockJobHandler) Handle(ctx context.Context, job *schedulerEntity.Job) error {
	h.processed = append(h.processed, job.ID)
	return nil
}

func (h *MockJobHandler) GetType() string {
	return "mock"
}

type MockTaskHandler struct {
	processed []string
}

func (h *MockTaskHandler) Handle(ctx context.Context, t *taskEntity.Task) error {
	h.processed = append(h.processed, t.ID)
	return nil
}

func (h *MockTaskHandler) GetType() string {
	return "mock"
}
