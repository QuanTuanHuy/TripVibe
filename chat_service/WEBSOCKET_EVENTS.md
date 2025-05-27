# WebSocket Message Status Events Documentation

## Overview
This document describes the WebSocket events implementation for real-time message status updates in the chat service, as part of Task 1.2.5.

## Message Types

### 1. StatusUpdate
Broadcasted when a message status changes (sent → delivered → read).

**Message Format:**
```json
{
  "type": "STATUS_UPDATE",
  "payload": {
    "messageId": 123,
    "oldStatus": "sent",
    "newStatus": "delivered", 
    "updatedBy": 456,
    "timestamp": 1640995200
  }
}
```

**Fields:**
- `messageId`: ID of the message whose status changed
- `oldStatus`: Previous status (`sent`, `delivered`, `read`)
- `newStatus`: New status (`sent`, `delivered`, `read`)
- `updatedBy`: ID of user who triggered the status change
- `timestamp`: Unix timestamp of the change

### 2. Acknowledgment
Sent to confirm receipt of operations or status updates.

**Message Format:**
```json
{
  "type": "ACKNOWLEDGMENT",
  "payload": {
    "ackId": "ack-12345",
    "success": true,
    "timestamp": 1640995200
  }
}
```

**Fields:**
- `ackId`: Unique identifier for the operation being acknowledged
- `success`: Whether the operation was successful
- `timestamp`: Unix timestamp of the acknowledgment

## Client Usage Examples

### Handling Status Updates
```javascript
websocket.onmessage = function(event) {
  const message = JSON.parse(event.data);
  
  if (message.type === 'STATUS_UPDATE') {
    const payload = message.payload;
    console.log(`Message ${payload.messageId} status changed from ${payload.oldStatus} to ${payload.newStatus}`);
    
    // Update UI to reflect status change
    updateMessageStatus(payload.messageId, payload.newStatus);
  }
};
```

### Sending Status Updates
```javascript
// Mark a message as read
websocket.send(JSON.stringify({
  type: 'STATUS_UPDATE',
  payload: {
    messageId: 123,
    status: 'read'
  }
}));
```

### Handling Acknowledgments
```javascript
websocket.onmessage = function(event) {
  const message = JSON.parse(event.data);
  
  if (message.type === 'ACKNOWLEDGMENT') {
    const payload = message.payload;
    console.log(`Operation ${payload.ackId} ${payload.success ? 'succeeded' : 'failed'}`);
  }
};
```

## Server Implementation

### Event Emission
The server automatically emits status update events when:
- A message is marked as delivered via `MarkMessagesAsDelivered`
- A message is marked as read via `UpdateMessageStatus`
- A bulk status update occurs via `BulkUpdateMessageStatus`

### Error Handling
- Connection failures during broadcast are logged but don't fail the operation
- Invalid message formats are logged and ignored
- Authorization checks are performed before processing status updates

### Room Management
- Status updates are broadcast to all connected users in the room
- Users receive updates for messages they can access
- Users cannot update status of their own messages (except for read status)

## Integration Points

### UseCase Layer
```go
type IEventEmitter interface {
    EmitMessageStatusChange(ctx context.Context, roomID int64, messageID int64, oldStatus, newStatus constant.MessageStatus, updatedBy int64) error
}
```

### WebSocket Manager
```go
func (wsm *WebSocketManager) BroadcastToRoomWithError(roomID int64, message *WebSocketMessage) error
func (wsm *WebSocketManager) SendToUser(userID int64, message *WebSocketMessage) error
func (wsm *WebSocketManager) BroadcastToRoomWithAck(roomID int64, message *WebSocketMessage, ackID string) error
```

## Testing

### Unit Tests
- `websocket_event_emitter_test.go` - Tests for event emission functionality
- Mock WebSocket manager for isolated testing
- Coverage for success and error scenarios

### Integration Tests
- End-to-end WebSocket message flow
- Status update propagation
- Error handling scenarios

## Configuration

No additional configuration is required. The WebSocket events are automatically enabled when the WebSocketEventEmitter is injected into the UpdateMessageUseCase.

## Monitoring

Events are logged with appropriate levels:
- `INFO`: Successful status updates and event emissions
- `ERROR`: Failed operations, connection issues
- `WARN`: Unknown message types, invalid payloads

## Security Considerations

- Authorization is checked before processing status updates
- Users can only update messages in rooms they have access to
- Message validation prevents invalid status transitions
- Rate limiting should be considered for production deployments
