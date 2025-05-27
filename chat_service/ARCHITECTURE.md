# Chat Service Architecture

## Tổng quan
Chat Service là một microservice được xây dựng bằng Go, áp dụng kiến trúc Clean Architecture với mục tiêu cung cấp tính năng chat realtime giữa khách hàng (tourist) và chủ khách sạn (owner) trong hệ thống booking giống Booking.com.

## Kiến trúc Tổng thể

### 1. Clean Architecture với Port-Adapter Pattern

```
┌─────────────────────────────────────────────────────────────┐
│                        UI Layer                             │  
│  ┌─────────────┐ ┌─────────────────┐ ┌─────────────────────┐│
│  │   REST      │ │   WebSocket     │ │    HTTP Routes      ││  
│  │ Controllers │ │  Controllers    │ │   & Middleware      ││
│  └─────────────┘ └─────────────────┘ └─────────────────────┘│
└─────────────────────────────────────────────────────────────┘
                                │
┌─────────────────────────────────────────────────────────────┐
│                      Service Layer                          │
│  ┌─────────────────────────────────────────────────────────┐│
│  │               Chat Room Service                         ││
│  │        (Orchestrates UseCases & External APIs)         ││
│  └─────────────────────────────────────────────────────────┘│
└─────────────────────────────────────────────────────────────┘
                                │
┌─────────────────────────────────────────────────────────────┐
│                       Core Layer                            │
│  ┌─────────────────────────────────────────────────────────┐│
│  │                  UseCase Layer                          ││
│  │ ┌──────────────┐ ┌──────────────┐ ┌──────────────────┐ ││
│  │ │ CreateChat   │ │ GetMessages  │ │ UpdateMessage    │ ││
│  │ │ RoomUseCase  │ │ UseCase      │ │ UseCase          │ ││
│  │ └──────────────┘ └──────────────┘ └──────────────────┘ ││
│  │          ↓ depends on ↓              ↓ depends on ↓     ││
│  └─────────────────────────────────────────────────────────┘│
│  ┌─────────────────────────────────────────────────────────┐│
│  │                    Port Layer                           ││
│  │ ┌──────────────┐ ┌──────────────┐ ┌──────────────────┐ ││
│  │ │IChatRoom     │ │IMessage      │ │IParticipant      │ ││
│  │ │Repository    │ │Repository    │ │Repository        │ ││
│  │ │Port          │ │Port          │ │Port              │ ││
│  │ └──────────────┘ └──────────────┘ └──────────────────┘ ││
│  │ ┌──────────────┐ ┌──────────────┐ ┌──────────────────┐ ││
│  │ │IWebSocket    │ │ICache        │ │IExternal         │ ││
│  │ │Manager       │ │Service       │ │Service           │ ││
│  │ │Port          │ │Port          │ │Port              │ ││
│  │ └──────────────┘ └──────────────┘ └──────────────────┘ ││
│  └─────────────────────────────────────────────────────────┘│
│  ┌─────────────────────────────────────────────────────────┐│
│  │                   Domain Entities                       ││
│  │ ┌──────────────┐ ┌──────────────┐ ┌──────────────────┐ ││
│  │ │ChatRoom      │ │Message       │ │Participant       │ ││
│  │ │Entity        │ │Entity        │ │Entity            │ ││
│  │ └──────────────┘ └──────────────┘ └──────────────────┘ ││
│  └─────────────────────────────────────────────────────────┘│
└─────────────────────────────────────────────────────────────┘
                                │ ↑ implements
┌─────────────────────────────────────────────────────────────┐
│                 Infrastructure Layer                        │
│  ┌─────────────────────────────────────────────────────────┐│
│  │                   Adapter Layer                         ││
│  │ ┌──────────────┐ ┌──────────────┐ ┌──────────────────┐ ││
│  │ │ChatRoom      │ │Message       │ │Participant       │ ││
│  │ │Adapter       │ │Adapter       │ │Adapter           │ ││
│  │ │(PostgreSQL)  │ │(PostgreSQL)  │ │(PostgreSQL)      │ ││
│  │ └──────────────┘ └──────────────┘ └──────────────────┘ ││
│  └─────────────────────────────────────────────────────────┘│
│  ┌─────────────────────────────────────────────────────────┐│
│  │                External Adapters                        ││
│  │ ┌──────────────┐ ┌──────────────┐ ┌──────────────────┐ ││
│  │ │WebSocket     │ │Redis Cache   │ │HTTP Client       │ ││
│  │ │Manager       │ │Adapter       │ │Adapter           │ ││
│  │ │Adapter       │ │              │ │(External APIs)   │ ││
│  │ └──────────────┘ └──────────────┘ └──────────────────┘ ││
│  └─────────────────────────────────────────────────────────┘│
└─────────────────────────────────────────────────────────────┘
```

#### Dependency Rule
- **UseCase Layer**: Chỉ phụ thuộc vào Port interfaces, không biết gì về implementation
- **Port Layer**: Định nghĩa contracts/interfaces được UseCase sử dụng
- **Adapter Layer**: Implement các Port interfaces, phụ thuộc vào external frameworks/databases
- **Domain Entities**: Hoàn toàn độc lập, không phụ thuộc vào layer nào khác

### 2. Domain Model

#### Core Entities
- **ChatRoomEntity**: Phòng chat liên kết với booking
- **MessageEntity**: Tin nhắn trong phòng chat  
- **ParticipantEntity**: Người tham gia (tourist/owner)
- **RoomParticipantEntity**: Quan hệ giữa user và room

#### Value Objects & Constants
- **MessageType**: text, image, system
- **UserRole**: tourist, owner

### 3. Technology Stack
- **Framework**: Gin (HTTP Router)
- **WebSocket**: Gorilla WebSocket
- **Database**: PostgreSQL với GORM
- **Cache**: Redis
- **Dependency Injection**: Uber FX
- **Libraries**: GoLibs ecosystem

## Chi tiết Components

### 1. UI Layer

#### Controllers
- **ChatController**: Xử lý REST API cho chat rooms và messages
  - `GET /rooms` - Lấy danh sách phòng chat
  - `POST /rooms` - Tạo phòng chat mới
  - `POST /rooms/:id/send_message` - Gửi tin nhắn
  - `GET /rooms/:id/messages` - Lấy lịch sử tin nhắn
  - `POST /rooms/:id/messages/:messageId/read` - Đánh dấu đã đọc

- **WebSocketController**: Xử lý WebSocket connections
  - `GET /ws` - Thiết lập kết nối WebSocket
  - Quản lý real-time messaging

#### Routing
```go
chatV1 := router.Group("/public/v1/chats")
{
    chatV1.POST("/rooms", p.ChatController.CreateChatRoom)
    chatV1.GET("/rooms", p.ChatController.GetChatRooms)
    chatV1.POST("/rooms/:roomId/send_message", p.ChatController.SendMessage)
    chatV1.GET("/rooms/:roomId/messages", p.ChatController.GetMessagesByRoomID)
    chatV1.POST("/rooms/:roomId/messages/:messageId/read", p.ChatController.MarkMessageAsRead)
    chatV1.GET("/rooms/:roomId/unread", p.ChatController.CountUnreadMessages)
    chatV1.GET("/ws", p.WebSocketController.HandleConnection)
}
```

### 2. Service Layer

#### ChatRoomService
Service chính cung cấp business logic cho chat functionality:

```go
type IChatRoomService interface {
    CreateChatRoom(ctx context.Context, bookingID int64, tourist *dto.ParticipantDto, owner *dto.ParticipantDto) (*entity.ChatRoomEntity, error)
    CreateMessage(ctx context.Context, roomID, senderID int64, content string, messageType constant.MessageType) (*entity.MessageEntity, error)
    GetMessagesByRoomId(ctx context.Context, userID, chatRoomID int64, params *request.MessageQueryParams) ([]*entity.MessageEntity, *response.PaginationResult, error)
    GetChatRooms(ctx context.Context, params *request.ChatRoomQueryParams) (*response.GetChatRoomResponse, error)
    // ... other methods
}
```

### 3. UseCase Layer (Core Business Logic)

#### Port-Based Architecture
Các UseCase chỉ phụ thuộc vào Port interfaces, không biết gì về implementation cụ thể:

**Các UseCase chính:**
- **CreateChatRoomUseCase**: 
  - Dependency: `IChatRoomPort`, `IParticipantPort`
  - Business logic: Validate booking, tạo room, thêm participants
  
- **CreateMessageUseCase**: 
  - Dependency: `IMessagePort`, `IWebSocketManagerPort`
  - Business logic: Validate message, lưu DB, broadcast realtime
  
- **GetChatRoomUseCase**: 
  - Dependency: `IChatRoomPort`, `ICacheServicePort`
  - Business logic: Query với cache, authorization check
  
- **GetMessageUseCase**: 
  - Dependency: `IMessagePort`, `ICacheServicePort`
  - Business logic: Pagination, access control, query optimization
  
- **UpdateMessageUseCase**: 
  - Dependency: `IMessagePort`, `IWebSocketManagerPort`
  - Business logic: Mark as read, notification, real-time update

```go
// Example UseCase structure
type CreateMessageUseCase struct {
    messagePort     port.IMessagePort            // Port interface
    chatRoomPort    port.IChatRoomPort           // Port interface  
    wsManagerPort   port.IWebSocketManagerPort   // Port interface
    cachePort       port.ICacheServicePort       // Port interface
}

func (uc *CreateMessageUseCase) Execute(ctx context.Context, req *CreateMessageRequest) (*MessageEntity, error) {
    // Business logic chỉ sử dụng port interfaces
    // Không biết gì về PostgreSQL, Redis, WebSocket implementation
    
    // Validate message content
    if req.Content == "" {
        return nil, errors.New("message content cannot be empty")
    }
    
    // Check room access permission
    room, err := uc.chatRoomPort.GetByID(ctx, req.RoomID)
    if err != nil {
        return nil, fmt.Errorf("failed to get chat room: %w", err)
    }
    
    // Create message entity
    message := &MessageEntity{
        ChatRoomID: req.RoomID,
        SenderID:   &req.SenderID,
        Content:    req.Content,
        Type:       req.MessageType,
        IsRead:     false,
    }
    
    // Save to database through port
    savedMessage, err := uc.messagePort.Create(ctx, message)
    if err != nil {
        return nil, fmt.Errorf("failed to create message: %w", err)
    }
    
    // Broadcast to room through WebSocket port
    if err := uc.wsManagerPort.BroadcastToRoom(req.RoomID, savedMessage); err != nil {
        // Log error but don't fail the operation
        log.Printf("failed to broadcast message: %v", err)
    }
    
    // Update room's last message
    room.LastMessageID = savedMessage.ID
    if _, err := uc.chatRoomPort.Update(ctx, room); err != nil {
        log.Printf("failed to update room last message: %v", err)
    }
    
    return savedMessage, nil
}
```

### 4. Port Layer (Contracts/Interfaces)

#### Repository Ports
```go
type IChatRoomPort interface {
    Create(ctx context.Context, entity *ChatRoomEntity) (*ChatRoomEntity, error)
    GetByID(ctx context.Context, id int64) (*ChatRoomEntity, error)
    GetByBookingID(ctx context.Context, bookingID int64) (*ChatRoomEntity, error)
    GetByUserID(ctx context.Context, userID int64, params *QueryParams) ([]*ChatRoomEntity, error)
    Update(ctx context.Context, entity *ChatRoomEntity) (*ChatRoomEntity, error)
    Delete(ctx context.Context, id int64) error
}

type IMessagePort interface {
    Create(ctx context.Context, entity *MessageEntity) (*MessageEntity, error)
    GetByRoomID(ctx context.Context, roomID int64, params *QueryParams) ([]*MessageEntity, error)
    GetByID(ctx context.Context, id int64) (*MessageEntity, error)
    Update(ctx context.Context, entity *MessageEntity) (*MessageEntity, error)
    MarkAsRead(ctx context.Context, messageID, userID int64) error
    CountUnread(ctx context.Context, roomID, userID int64) (int64, error)
}

type IParticipantPort interface {
    Create(ctx context.Context, entity *ParticipantEntity) (*ParticipantEntity, error)
    GetByUserID(ctx context.Context, userID int64) (*ParticipantEntity, error)
    GetByRoomID(ctx context.Context, roomID int64) ([]*ParticipantEntity, error)
}
```

#### External Service Ports
```go
type IWebSocketManagerPort interface {
    RegisterClient(userID int64, conn *websocket.Conn) error
    UnregisterClient(userID int64) error
    BroadcastToRoom(roomID int64, message interface{}) error
    BroadcastToUser(userID int64, message interface{}) error
    GetActiveUsers(roomID int64) []int64
}

type ICacheServicePort interface {
    Set(ctx context.Context, key string, value interface{}, ttl time.Duration) error
    Get(ctx context.Context, key string, dest interface{}) error
    Delete(ctx context.Context, key string) error
    Exists(ctx context.Context, key string) bool
}

type IExternalServicePort interface {
    GetBookingInfo(ctx context.Context, bookingID int64) (*BookingInfo, error)
    GetUserInfo(ctx context.Context, userID int64) (*UserInfo, error)
    SendNotification(ctx context.Context, userID int64, message string) error
}
```

### 5. Infrastructure Layer (Adapters)

#### Repository Adapters (Implement Repository Ports)
```go
// ChatRoomAdapter implements IChatRoomPort
type ChatRoomAdapter struct {
    db *gorm.DB
}

func (a *ChatRoomAdapter) Create(ctx context.Context, entity *ChatRoomEntity) (*ChatRoomEntity, error) {
    // PostgreSQL specific implementation
    // Convert entity to DB model, execute query, convert back
}

// MessageAdapter implements IMessagePort
type MessageAdapter struct {
    db *gorm.DB
}

// ParticipantAdapter implements IParticipantPort
type ParticipantAdapter struct {
    db *gorm.DB
}
```

#### External Service Adapters
```go
// WebSocketManager implements IWebSocketManagerPort
type WebSocketManager struct {
    clients    map[int64]*websocket.Conn
    rooms      map[int64][]int64
    register   chan *ClientConnection
    unregister chan *ClientConnection
    broadcast  chan *BroadcastMessage
    mutex      sync.RWMutex
}

func (wsm *WebSocketManager) BroadcastToRoom(roomID int64, message interface{}) error {
    // WebSocket specific implementation using Gorilla WebSocket
}

// RedisCacheAdapter implements ICacheServicePort
type RedisCacheAdapter struct {
    client *redis.Client
}

func (r *RedisCacheAdapter) Set(ctx context.Context, key string, value interface{}, ttl time.Duration) error {
    // Redis specific implementation
}

// HTTPClientAdapter implements IExternalServicePort
type HTTPClientAdapter struct {
    httpClient *http.Client
    baseURL    string
}

func (h *HTTPClientAdapter) GetBookingInfo(ctx context.Context, bookingID int64) (*BookingInfo, error) {
    // HTTP client implementation to call booking service
}
```

#### Dependency Injection
```go
// Infrastructure adapters được inject vào UseCase thông qua Port interfaces
func NewCreateMessageUseCase(
    messagePort port.IMessagePort,           // Adapter được inject qua interface
    chatRoomPort port.IChatRoomPort,         // Không phụ thuộc concrete type
    wsManagerPort port.IWebSocketManagerPort, // Tuân thủ Dependency Inversion
    cachePort port.ICacheServicePort,
) usecase.ICreateMessageUseCase {
    return &CreateMessageUseCase{
        messagePort:   messagePort,
        chatRoomPort:  chatRoomPort,
        wsManagerPort: wsManagerPort,
        cachePort:     cachePort,
    }
}
```

### 6. Domain Layer (Pure Business Objects)

#### Entities
```go
type ChatRoomEntity struct {
    BaseEntity
    BookingID     int64                
    LastMessageID int64                
    Participants  []*ParticipantEntity 
    LastMessage   *MessageEntity       
}

type MessageEntity struct {
    BaseEntity
    ChatRoomID int64                
    SenderID   *int64               
    Content    string               
    Type       constant.MessageType 
    IsRead     bool                 
}
```

## Data Flow (Port-Adapter Pattern)

### 1. Realtime Message Flow
```
User A → WebSocket → WSController → ChatRoomService 
                                        ↓
                                   CreateMessageUseCase (chỉ dùng ports)
                                        ↓
IMessagePort ← CreateMessageUseCase → IWebSocketManagerPort 
(port interface)                           (port interface)
        ↓                                       ↓
MessageAdapter                            WebSocketManager
(PostgreSQL)                              (Gorilla WS)
        ↓                                       ↓
   Database                               User B (broadcast)
```

### 2. HTTP API Flow với Port-Adapter
```
Client → REST API → ChatController → ChatRoomService 
                                          ↓
                                     GetMessageUseCase (chỉ dùng ports)
                                          ↓
ICacheServicePort ← GetMessageUseCase → IMessagePort
  (port interface)                    (port interface)
       ↓                                    ↓
RedisCacheAdapter                     MessageAdapter  
(Redis impl)                         (PostgreSQL impl)
       ↓                                    ↓
   Redis Cache                          Database
                                          ↓
Client ← JSON Response ← ChatController ← ChatRoomService ← GetMessageUseCase
```

### 3. Dependency Direction (Clean Architecture Rule)
```
UI Layer → Service Layer → UseCase (Core) ← Port Interfaces ← Infrastructure Adapters
   ↓             ↓              ↓                ↑                    ↑
Gin/HTTP    Orchestration   Business Logic   Contracts        External Dependencies
                                                              (DB, Cache, WebSocket)
```

**Quan trọng:** 
- UseCase chỉ phụ thuộc vào Port interfaces (inward dependency)
- Infrastructure Adapters implement Port interfaces (outward dependency)  
- Dependency injection đảo ngược dependency tại runtime

## Clean Architecture Benefits trong Chat Service

### 1. **Testability**
- UseCase có thể test dễ dàng với mock Port interfaces
- Không cần setup database/WebSocket thật khi unit test
- Integration test chỉ test Adapter layer riêng biệt

### 2. **Independence từ External Dependencies**  
- Có thể thay PostgreSQL → MongoDB chỉ cần viết adapter mới
- Có thể thay Gorilla WebSocket → Socket.IO chỉ cần implement IWebSocketManager
- Business logic không thay đổi khi đổi technical stack

### 3. **Scalability & Maintainability**
- Thêm feature mới: tạo UseCase + Port + Adapter
- Modify business logic: chỉ sửa UseCase layer
- Performance optimization: chỉ sửa Adapter implementation

### 4. **Clear Dependency Direction**
```
External Dependencies → Adapters → Ports ← UseCases ← Service ← UI
     (Framework)     (Infra)   (Contract) (Core)   (App)  (Interface)
```

## Security & Authentication

### Current Implementation
- Query parameter authentication (`userId`, `roomId`)
- Room access validation through `GetChatRoomByID`

### Cần cải thiện
- JWT token authentication
- Role-based access control
- Rate limiting
- Input validation & sanitization

## Database Schema

### Tables
1. **chat_rooms**
   - id, booking_id, last_message_id, created_at, updated_at

2. **messages** 
   - id, chat_room_id, sender_id, content, type, is_read, created_at, updated_at

3. **participants**
   - user_id, user_name, role

4. **room_participants**
   - chat_room_id, participant_id, joined_at

## Performance Considerations

### Current Strengths
- Clean separation of concerns
- Efficient WebSocket management
- Database connection pooling với GORM
- Redis caching support

### Optimization Opportunities
- Message pagination
- Connection pooling optimization
- Database indexing
- Caching strategies for frequent queries

## Deployment

### Dependencies
- PostgreSQL database
- Redis cache
- Environment configuration
- Network access cho WebSocket connections

### Configuration
- Database connections
- Redis configuration  
- WebSocket settings
- Logging levels
