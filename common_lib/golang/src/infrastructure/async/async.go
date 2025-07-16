package async

// Package async provides implementations for the asynchronous request-reply pattern
// This package includes:
// - RedisCorrelationManager: Redis-based correlation management
// - KafkaAsyncRequestManager: Kafka-based async request management
// - RequestMessageHandler: Handler for incoming request messages
// - ReplyMessageHandler: Handler for incoming reply messages
// - AsyncFactory: Factory for creating and managing async components

// All implementations follow the interfaces defined in the core/port package
// and work together to provide a complete async request-reply system
