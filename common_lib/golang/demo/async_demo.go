package main

import (
	"context"
	"fmt"
	"log"
	"time"

	entity "github.com/quantuanhuy/lib/src/core/entity/async"
)

// SimpleAsyncDemo demonstrates the basic usage of the async request-reply pattern
func SimpleAsyncDemo() {
	fmt.Println("=== Simple Async Request-Reply Demo ===")

	ctx := context.Background()

	// Mock async manager for demonstration
	asyncManager := &MockAsyncManager{
		handlers: make(map[string]entity.AsyncRequestHandler),
	}

	// Register a simple handler
	asyncManager.RegisterHandler("hello", func(request *entity.AsyncRequest) (*entity.AsyncReply, error) {
		name := request.Payload["name"].(string)

		return &entity.AsyncReply{
			CorrelationID: request.CorrelationID,
			RequestID:     request.ID,
			Status:        entity.AsyncRequestStatusCompleted,
			Result: map[string]interface{}{
				"greeting":  fmt.Sprintf("Hello, %s!", name),
				"timestamp": time.Now(),
			},
			ProcessedAt: time.Now(),
			Duration:    100 * time.Millisecond,
		}, nil
	})

	// Send a request
	correlationID, err := asyncManager.SendRequest(ctx, "hello", map[string]interface{}{
		"name": "World",
	}, 30*time.Second)

	if err != nil {
		log.Printf("Error sending request: %v", err)
		return
	}

	fmt.Printf("Sent request with correlation ID: %s\n", correlationID)

	// Wait for reply
	reply, err := asyncManager.WaitForReply(ctx, correlationID, 30*time.Second)
	if err != nil {
		log.Printf("Error waiting for reply: %v", err)
		return
	}

	fmt.Printf("Received reply: %+v\n", reply)
	fmt.Printf("Greeting: %s\n", reply.Result["greeting"])
}

// BookingSystemDemo demonstrates a more complex booking system scenario
func BookingSystemDemo() {
	fmt.Println("\n=== Booking System Demo ===")

	ctx := context.Background()

	// Mock async manager
	asyncManager := &MockAsyncManager{
		handlers: make(map[string]entity.AsyncRequestHandler),
	}

	// Register booking handler
	asyncManager.RegisterHandler("book-service", func(request *entity.AsyncRequest) (*entity.AsyncReply, error) {
		serviceType := request.Payload["service_type"].(string)
		customerID := request.Payload["customer_id"].(string)
		bookingDate := request.Payload["booking_date"].(string)

		// Simulate booking logic
		time.Sleep(500 * time.Millisecond)

		confirmationNumber := fmt.Sprintf("CONF-%d", time.Now().Unix())

		return &entity.AsyncReply{
			CorrelationID: request.CorrelationID,
			RequestID:     request.ID,
			Status:        entity.AsyncRequestStatusCompleted,
			Result: map[string]interface{}{
				"booking_id":          fmt.Sprintf("BK-%d", time.Now().Unix()),
				"confirmation_number": confirmationNumber,
				"service_type":        serviceType,
				"customer_id":         customerID,
				"booking_date":        bookingDate,
				"status":              "confirmed",
				"booked_at":           time.Now(),
			},
			ProcessedAt: time.Now(),
			Duration:    500 * time.Millisecond,
		}, nil
	})

	// Send booking request
	correlationID, err := asyncManager.SendRequest(ctx, "book-service", map[string]interface{}{
		"service_type": "spa-treatment",
		"customer_id":  "CUST-001",
		"booking_date": "2024-01-15",
		"duration":     90,
		"preferences":  "quiet room",
	}, 30*time.Second)

	if err != nil {
		log.Printf("Error sending booking request: %v", err)
		return
	}

	fmt.Printf("Sent booking request with correlation ID: %s\n", correlationID)

	// Wait for booking confirmation
	reply, err := asyncManager.WaitForReply(ctx, correlationID, 30*time.Second)
	if err != nil {
		log.Printf("Error waiting for booking reply: %v", err)
		return
	}

	if reply.Status == entity.AsyncRequestStatusCompleted {
		fmt.Printf("Booking confirmed!\n")
		fmt.Printf("Booking ID: %s\n", reply.Result["booking_id"])
		fmt.Printf("Confirmation Number: %s\n", reply.Result["confirmation_number"])
		fmt.Printf("Service Type: %s\n", reply.Result["service_type"])
		fmt.Printf("Status: %s\n", reply.Result["status"])
	} else {
		fmt.Printf("Booking failed: %s\n", reply.Error)
	}
}

func main() {
	fmt.Println("Async Request-Reply Pattern Demo")
	fmt.Println("=================================")

	// Run different demo scenarios
	SimpleAsyncDemo()
	BookingSystemDemo()

	fmt.Println("\n=== Demo completed ===")
}

// MockAsyncManager is a simplified mock implementation for demonstration
type MockAsyncManager struct {
	handlers map[string]entity.AsyncRequestHandler
}

func (m *MockAsyncManager) SendRequest(ctx context.Context, requestType string, payload map[string]interface{}, timeout time.Duration) (string, error) {
	correlationID := fmt.Sprintf("corr-%d", time.Now().UnixNano())

	// Simulate async processing
	go func() {
		time.Sleep(10 * time.Millisecond) // Simulate network delay

		if handler, exists := m.handlers[requestType]; exists {
			request := &entity.AsyncRequest{
				ID:            fmt.Sprintf("req-%d", time.Now().UnixNano()),
				CorrelationID: correlationID,
				RequestType:   requestType,
				Payload:       payload,
				CreatedAt:     time.Now(),
			}

			handler(request)
		}
	}()

	return correlationID, nil
}

func (m *MockAsyncManager) WaitForReply(ctx context.Context, correlationID string, timeout time.Duration) (*entity.AsyncReply, error) {
	// Simulate waiting for reply
	time.Sleep(100 * time.Millisecond)

	// For demo purposes, we'll simulate a successful reply
	return &entity.AsyncReply{
		CorrelationID: correlationID,
		Status:        entity.AsyncRequestStatusCompleted,
		Result: map[string]interface{}{
			"mock": "response",
		},
		ProcessedAt: time.Now(),
	}, nil
}

func (m *MockAsyncManager) RegisterHandler(requestType string, handler entity.AsyncRequestHandler) {
	m.handlers[requestType] = handler
}
