# BÁO CÁO DỰ ÁN HỆ THỐNG ĐẶT PHÒNG

## TỔNG QUAN DỰ ÁN

### Mô tả sản phẩm
**TripVibe** là một nền tảng đặt phòng trực tuyến hiện đại được xây dựng theo kiến trúc vi dịch vụ, cung cấp thị trường trực tuyến hoàn chỉnh kết nối du khách với chủ nhà. Hệ thống tích hợp các tính năng tiên tiến:

- **Công cụ tìm kiếm thông minh**: Elasticsearch với định vị địa lý và bộ lọc phức tạp
- **Quản lý đặt phòng**: Quy trình đặt phòng với khóa phân tán và tình trạng trống thời gian thực
- **Trải nghiệm người dùng**: Hệ thống đa vai trò, danh sách yêu thích, tính năng xã hội, gợi ý cá nhân hóa
- **Giao tiếp thời gian thực**: Trò chuyện WebSocket tích hợp với quy trình đặt phòng
- **Tin cậy và An toàn**: Hệ thống đánh giá đa tiêu chí, đánh giá cộng đồng, đảm bảo chất lượng
- **Xuất sắc về Kỹ thuật**: 13 vi dịch vụ (Java, Go, .NET), kiến trúc hướng sự kiện, bộ nhớ đệm tiên tiến

### Mục tiêu dự án
- Tạo ra một nền tảng đặt phòng enterprise-grade với tính năng phức tạp
- Áp dụng kiến trúc microservices đa ngôn ngữ (Go, Java, .NET) 
- Cung cấp real-time experience với chat và notifications
- Quản lý inventory và pricing sophisticated
- Bảo mật và authorization toàn diện
- Performance optimization với multi-tier caching

## KIẾN TRÚC HỆ THỐNG

### Tổng quan kiến trúc
Hệ thống **TripVibe** được thiết kế theo mô hình **Microservices Architecture** enterprise-grade với kiến trúc phân tán phức tạp, tận dụng ưu điểm của nhiều công nghệ khác nhau:

#### **Kiến trúc Microservices Đa ngôn ngữ:**
- **13 Independent Services**: Phân chia theo domain business logic với technology stack tối ưu
  - **Java Spring Boot (9 services)**: Authentication, Accommodation, Profile, API Gateway, File, Inventory, Payment, Rating, Search
  - **Go (3 services)**: Booking, Notification, Chat - tối ưu cho high-performance và real-time processing
  - **(.NET Core (2 services)**: Location, Promotion - leverage enterprise features và analytics capabilities

#### **Event-Driven Architecture:**
- **Apache Kafka**: Central message broker với topic-based event streaming
- **Asynchronous Communication**: Non-blocking inter-service messaging với eventual consistency
- **Event Sourcing**: Complete audit trail với event replay capabilities cho data consistency
- **Saga Pattern**: Distributed transaction coordination cho complex business workflows

#### **Data Architecture:**
- **Database per Service**: Isolation và independent scaling theo domain requirements
  - **PostgreSQL (11 services)**: Primary choice cho ACID compliance và complex queries
  - **MySQL (1 service)**: Authentication service - optimized cho user management và security
  - **Redis**: Distributed caching, session storage, distributed locking mechanism
  - **Elasticsearch**: Search indexing với full-text search, geo-location, và analytics

#### **Real-time & Communication:**
- **WebSocket Integration**: Real-time chat system với persistent connections
- **Server-Sent Events**: Live notifications và status updates
- **Multi-channel Notifications**: Email, SMS, Push notifications với unified API
- **Connection Management**: Auto-reconnection, heartbeat monitoring, graceful degradation

#### **Security & Performance:**
- **API Gateway**: Centralized entry point với intelligent load balancing, rate limiting, CORS handling
- **JWT Authentication**: Access + Refresh token system với role-based authorization
- **Multi-tier Caching**: Application cache, Redis distributed cache, database query cache
- **Clean Architecture**: Domain-driven design với ports & adapters pattern cho maintainability

#### **DevOps & Infrastructure:**
- **Containerization**: Docker containers cho tất cả services với Docker Compose orchestration
- **Service Discovery**: Dynamic service registration với health checks
- **Monitoring**: Comprehensive logging, metrics collection, distributed tracing
- **Scalability**: Horizontal scaling capabilities với load balancing strategies

### Sơ đồ kiến trúc

```
┌─────────────────┐    ┌─────────────────┐
│    Admin UI     │    │     Web UI      │
│   (Next.js)     │    │  (TripVibe)     │
│                 │    │   (Next.js)     │
└─────────────────┘    └─────────────────┘
         │                       │
         └───────────┬───────────┘
                     │
         ┌─────────────────────┐
         │     API Gateway     │
         │   (Spring Boot)     │
         │ Load Balancer &     │
         │     Routing         │
         └─────────────────────┘
                     │
    ┌────────────────┼────────────────┐
    │                │                │
┌───────────────┐ ┌─────────────┐ ┌──────────────┐
│Authentication │ │   Booking   │ │Accommodation │
│   Service     │ │  Service    │ │   Service    │
│   (Java)      │ │    (Go)     │ │   (Java)     │
│    MySQL      │ │ PostgreSQL  │ │ PostgreSQL   │
└───────────────┘ └─────────────┘ └──────────────┘
    │                │                │
    │                │                │
┌───────────────┐ ┌─────────────┐ ┌──────────────┐
│   Profile     │ │    Chat     │ │  Inventory   │
│   Service     │ │  Service    │ │   Service    │
│   (Java)      │ │    (Go)     │ │   (Java)     │
│ PostgreSQL    │ │ PostgreSQL  │ │ PostgreSQL   │
└───────────────┘ └─────────────┘ └──────────────┘
                     │
         ┌─────────────────────┐
         │       Kafka         │
         │   (Message Bus)     │
         │  Event Streaming    │
         └─────────────────────┘
                     │
    ┌────────────────┼────────────────┐
    │                │                │
┌─────────────┐ ┌─────────────┐ ┌──────────────┐
│   Search    │ │   Rating    │ │     File     │
│  Service    │ │  Service    │ │   Service    │
│   (Java)    │ │   (Java)    │ │   (Java)     │
│Elasticsearch│ │ PostgreSQL  │ │ PostgreSQL   │
└─────────────┘ └─────────────┘ └──────────────┘
    │                │                │
    │                │                │
┌─────────────┐ ┌─────────────┐ ┌──────────────┐
│Notification │ │  Location   │ │  Promotion   │
│  Service    │ │  Service    │ │   Service    │
│    (Go)     │ │   (.NET)    │ │   (.NET)     │
│ PostgreSQL  │ │ PostgreSQL  │ │ PostgreSQL   │
└─────────────┘ └─────────────┘ └──────────────┘
                     │
         ┌─────────────────────┐
         │      Payment        │
         │      Service        │
         │      (Java)         │
         │    PostgreSQL       │
         └─────────────────────┘
                     │
         ┌─────────────────────┐
         │       Redis         │
         │   (Distributed      │
         │     Caching)        │
         └─────────────────────┘
```

## CÔNG NGHỆ ĐÃ SỬ DỤNG

### Backend Technologies (13 Microservices)

#### 1. Java Spring Boot (9 services)
- **Authentication Service**: Xác thực, phân quyền với 11 roles và 100+ privileges
- **Accommodation Service**: Quản lý chỗ nghỉ, amenities, inventory, pricing
- **Profile Service**: Quản lý hồ sơ người dùng, wishlist, view history, friends
- **API Gateway**: Centralized routing và load balancing
- **File Service**: Quản lý file và media storage
- **Inventory Service**: Quản lý tồn kho phòng với distributed locking
- **Payment Service**: Xử lý thanh toán và transaction management
- **Rating Service**: Đánh giá và review với comprehensive analytics
- **Search Service**: Elasticsearch-based search với advanced filtering

**Công nghệ sử dụng:**
- Spring Boot 3.3.9
- Java 21
- Spring Security với JWT
- Spring Data JPA
- Spring Kafka
- Maven

#### 2. .NET Core (2 services)
- **Location Service**: Geographic management với tourist attractions, trending places analytics
- **Promotion Service**: Quản lý khuyến mãi

**Công nghệ .NET sử dụng:**
- **.NET 9.0**: Modern framework với C# latest features
- **Entity Framework Core**: ORM với PostgreSQL provider
- **StackExchange.Redis**: Distributed caching
- **ASP.NET Core Web API**: RESTful API framework
- **Clean Architecture**: Domain-driven design patterns

#### 3. Go (3 services)
- **Booking Service**: Xử lý đặt phòng và quản lý booking lifecycle
- **Notification Service**: Multi-channel notification system với Kafka event-driven processing
- **Chat Service**: Hệ thống chat real-time với WebSocket

**Công nghệ Go sử dụng:**
- Framework: Gin
- Go version: 1.24
- GORM: Database ORM
- Kafka: Segmentio/kafka-go client (Notification), Sarama client (Booking/Chat)
- Redis: go-redis
- JWT: golang-jwt
- WebSocket: gorilla/websocket (Chat Service)
- Uber FX: Dependency injection
- SMTP: Email delivery integration

### Frontend Technologies

#### Next.js Applications
1. **Web UI (TripVibe)**: Giao diện khách hàng chính
2. **Admin UI**: Giao diện quản trị

**Web UI - TripVibe Platform:**

**Core Technologies:**
- **Next.js 15.2.4**: App Router với Server Components và Server Actions
- **React 19.0**: Latest React với modern hooks và features
- **TypeScript 5+**: Strict type checking và type safety toàn diện
- **Tailwind CSS**: Utility-first styling với PostCSS processing
- **Node.js**: Runtime environment cho server-side operations

**UI/UX Framework:**
- **Radix UI Primitives**: Accessible component foundation
  - Alert Dialog, Avatar, Checkbox, Dialog, Dropdown Menu
  - Label, Popover, Scroll Area, Select, Separator, Slider
  - Switch, Tabs, Tooltip - comprehensive UI primitives
- **Shadcn UI Components**: Modern component library foundation
- **Lucide React Icons**: Consistent iconography system
- **React Icons**: Extended icon library
- **Class Variance Authority**: Type-safe component variants
- **Tailwind Merge**: Conditional class combination utility
- **Tailwindcss Animate**: Smooth animations và transitions

**State Management & Context:**
- **React Context API**: Global state management
  - AuthContext: Authentication state và user management
  - BookingContext: Booking flow state management
  - ChatContext: Real-time chat state với WebSocket
  - FavoritesContext: Wishlist và favorite properties
  - SidebarContext: UI sidebar state management
- **Custom Hooks**: Reusable logic encapsulation
- **React Hook Form**: Advanced form state management với validation

**Maps & Location:**
- **Leaflet 1.9.4**: Interactive maps với full GIS capabilities
- **React Leaflet 5.0**: React integration cho Leaflet maps
- **@types/leaflet**: TypeScript definitions cho Leaflet

**Date & Time Handling:**
- **React DatePicker**: Advanced date selection components
- **React Day Picker**: Calendar components với multi-date support
- **date-fns 3.6.0**: Modern date manipulation library

**API Integration & Services:**
- **Axios 1.9.0**: HTTP client với interceptors và request/response handling
- **API Client Architecture**: Centralized API service layer
  - Authentication Service integration
  - Booking Service integration
  - Chat Service integration
  - Location Service integration
  - Profile Service integration
  - Rating Service integration
  - Search Service integration
  - Wishlist Service integration

**Real-time Features:**
- **WebSocket Integration**: Real-time chat functionality
- **Typing Indicators**: Live typing status trong chat
- **Message Status**: Read receipts và delivery confirmation
- **Connection Management**: Auto-reconnect và heartbeat monitoring

**Authentication & Security:**
- **JWT Token Management**: Access và refresh token handling
- **JWT Decode**: Token parsing và validation
- **Role-based Access Control**: Multi-role user management
- **Protected Routes**: Route guards với authentication checks
- **Session Management**: Secure session handling với auto-refresh

**UI/UX Features:**
- **Theme Management**: Dark/Light theme với next-themes
- **Responsive Design**: Mobile-first approach với Tailwind breakpoints
- **Toast Notifications**: Sonner toast system cho user feedback
- **Loading States**: Skeleton loaders và spinner components
- **Error Handling**: Comprehensive error boundaries và user feedback
- **Accessibility**: ARIA compliance với Radix UI primitives

**Application Structure:**

**Main Pages & Routes:**
- **Home Page**: Hero section, search bar, promotions, destinations, property types
- **Search Results**: Advanced filtering, sorting, map view, pagination
- **Hotel Details**: Gallery, amenities, rooms, pricing, reviews, booking
- **Booking Flow**: Multi-step wizard với guest info, payment, confirmation
- **User Account**: Profile management, trip history, preferences
- **My Trips**: Booking management, check-in/out, modifications
- **My Reviews**: Rating và review management
- **My Wishlists**: Favorite properties management
- **Inbox**: Real-time chat với hosts và customer support

**Authentication Flow:**
- **Login/Register**: Email/password authentication với OTP verification
- **Password Reset**: Secure password recovery process
- **Social Login**: Ready for OAuth integration
- **Session Management**: Multi-device session handling

**Search & Booking:**
- **Advanced Search**: Location, dates, guests, filters, sorting
- **Map Integration**: Interactive property maps với clustering
- **Real-time Availability**: Live room availability checking
- **Price Comparison**: Dynamic pricing với promotions
- **Booking Wizard**: Step-by-step booking process với validation
- **Payment Integration**: Secure payment processing (ready for gateway)

**Chat System:**
- **Real-time Messaging**: WebSocket-based chat với hosts
- **Message Types**: Text, media, system notifications
- **Typing Indicators**: Live typing status
- **Message History**: Persistent chat history với pagination
- **File Sharing**: Image và file upload trong chat
- **Connection Status**: Online/offline indicators

**Performance Optimizations:**
- **Code Splitting**: Route-based code splitting với Next.js
- **Image Optimization**: next/image với automatic optimization
- **Font Optimization**: Geist font family với next/font
- **Bundle Optimization**: Tree shaking và dead code elimination
- **Caching Strategy**: Client-side caching với SWR patterns
- **Lazy Loading**: Component lazy loading cho performance

**Development Features:**
- **TypeScript Integration**: Full type safety across components
- **ESLint Configuration**: Code quality enforcement
- **Path Aliases**: Clean import paths với @ alias
- **Environment Configuration**: Multi-environment setup
- **Development Tools**: Hot reload, error overlay, debugging tools

**Integration Points:**
- **API Gateway**: Centralized backend communication
- **Microservices**: Direct integration với 13 backend services
- **File Service**: Media upload và processing
- **Notification Service**: Toast notifications và email alerts
- **Analytics**: User behavior tracking (ready for implementation)

**Deployment Ready:**
- **Production Build**: Optimized build với Next.js
- **Static Generation**: SSG cho SEO optimization
- **Server-Side Rendering**: SSR cho dynamic content
- **CDN Ready**: Asset optimization cho CDN deployment
- **Environment Variables**: Secure configuration management

#### Frontend Architecture & Design

**Core Architecture:**
- **Next.js 15.2.4 App Router**: Modern routing với Server Components và Server Actions
- **React 19.0**: Latest React features với concurrent rendering
- **TypeScript 5+**: Strict type safety với advanced type definitions
- **Clean Architecture**: Component-based architecture với separation of concerns

**Application Structure:**
```
src/
├── app/                    # Next.js App Router pages
│   ├── (auth)/            # Authentication routes (login, register)
│   ├── booking/           # Booking process pages
│   ├── hotel/             # Hotel listing và details
│   ├── inbox/             # Chat inbox
│   ├── myaccount/         # User account management
│   ├── myreviews/         # Review management
│   ├── mytrips/           # Trip management
│   ├── mywishlists/       # Wishlist management
│   └── search/            # Search results
├── components/            # Reusable UI components
│   ├── auth/              # Authentication components
│   ├── BookingFlow/       # Booking wizard components
│   ├── chat/              # Real-time chat components
│   ├── ui/                # Base UI components (Shadcn/Radix)
│   └── [feature]/         # Feature-specific components
├── context/               # React Context providers
├── hooks/                 # Custom React hooks
├── services/              # API service layer
├── types/                 # TypeScript type definitions
└── utils/                 # Utility functions
```

**State Management Architecture:**
- **Context-based State**: Multi-context architecture cho different domains
  - **AuthContext**: User authentication, session, role management
  - **BookingContext**: Booking flow state, form data persistence
  - **ChatContext**: Real-time WebSocket state, message handling
  - **FavoritesContext**: Wishlist management, local storage sync
  - **SidebarContext**: UI state management cho responsive design

**Component Architecture:**
- **Atomic Design**: Components organized theo atomic design principles
- **Reusable UI Components**: Shadcn UI foundation với custom extensions
- **Feature Components**: Domain-specific components với business logic
- **Layout Components**: Consistent layout patterns với responsive design

**Performance Optimizations:**
- **Code Splitting**: Automatic code splitting với Next.js
- **Image Optimization**: next/image với automatic optimization
- **Font Optimization**: Font loading optimization với next/font
- **Bundle Analysis**: Regular bundle size monitoring
- **Lazy Loading**: Component và route lazy loading
- **Caching Strategy**: Intelligent caching với SWR patterns

### Database & Storage

#### Databases
- **PostgreSQL**: Database chính cho hầu hết services
- **MySQL**: Authentication service
- **Redis**: Caching và session storage

#### Message Queue
- **Apache Kafka**: Event streaming và communication giữa services

#### Search Engine
- **Elasticsearch**: Full-text search và analytics

### DevOps & Infrastructure

#### Containerization
- **Docker**: Container cho tất cả services
- **Docker Compose**: Orchestration cho development

## TECHNICAL HIGHLIGHTS

### Kiến trúc Microservices Đa ngôn ngữ
- **13 Services độc lập**: Java Spring Boot (9), Go (3), .NET Core (2)
- **Domain-driven Design**: Mỗi service quản lý business domain riêng biệt với clear boundaries
- **Independent Scaling**: Horizontal scaling theo nhu cầu từng service cụ thể
- **Technology Optimization**: Ngôn ngữ phù hợp cho từng use case - Go cho performance, .NET cho analytics, Java cho enterprise features

### Event-driven Architecture
- **Apache Kafka**: Message streaming cho inter-service communication
- **Asynchronous Processing**: Non-blocking operations và improved performance
- **Event Sourcing**: Complete audit trail và data consistency
- **Saga Pattern**: Distributed transaction management

### Advanced Caching Strategy
- **Multi-tier Caching**: L1 (Application) + L2 (Redis) + L3 (Database)
- **Intelligent Cache Invalidation**: Smart cache refresh strategies
- **Performance Optimization**: Sub-second response times
- **Cache Monitoring**: Real-time cache performance analytics

### Real-time Features
- **WebSocket Integration**: Real-time chat và live updates
- **Server-Sent Events**: Live notifications và status updates
- **Connection Management**: Auto-reconnection và heartbeat monitoring
- **Scalable Broadcasting**: Efficient message distribution

### Security & Authentication
- **JWT Token Management**: Access + Refresh token với rotation
- **Role-based Access Control**: 11 roles với 100+ granular permissions
- **API Security**: Rate limiting, CORS, input validation
- **Data Encryption**: Secure storage và transmission

### Database Architecture
- **Database per Service**: Isolation và independent scaling cho từng domain
- **PostgreSQL**: Primary database cho 11 services (high consistency requirements)
- **MySQL**: Authentication service (optimized cho user management)
- **Redis**: Distributed caching, session storage, distributed locking
- **Elasticsearch**: Search indexing với full-text search và geo-queries
- **ACID Compliance**: Transaction safety với rollback capabilities
- **Query Optimization**: Performance tuning và strategic indexing
- **Data Consistency**: Event-driven eventual consistency patterns

### Search & Analytics
- **Elasticsearch Integration**: Full-text search với advanced filtering
- **Geo-location Queries**: Distance-based search và map integration
- **Search Analytics**: Query performance monitoring và optimization
- **Real-time Indexing**: Live data synchronization

### Performance & Monitoring
- **Load Balancing**: API Gateway với intelligent routing và health checks
- **Service Discovery**: Dynamic service registration và discovery patterns  
- **Health Monitoring**: Comprehensive health checks với dependency tracking
- **Error Handling**: Centralized exception management với proper error propagation
- **Distributed Tracing**: Request tracking across service boundaries
- **Caching Strategy**: Multi-tier caching (Redis distributed, in-memory local)
- **Background Processing**: Async workers cho data synchronization và cleanup

## CHI TIẾT CÁC MICROSERVICES

### 1. Authentication Service (Java Spring Boot)
**Chức năng chính:**
- **User Authentication**: Đăng ký, đăng nhập với email/password
- **JWT Token Management**: Access token và refresh token lifecycle
- **Multi-Role Authorization**: Hệ thống phân quyền với 11+ roles và 100+ privileges
- **Session Management**: Quản lý multiple device sessions và logout
- **OTP System**: Email OTP cho registration verification
- **Security Features**: Password encryption, token security, audit logging

**Tính năng chi tiết:**

**Authentication System:**
- **User Registration**: Email-based registration với OTP verification
- **Login/Logout**: Secure authentication với JWT tokens
- **Password Management**: BCrypt encryption, password validation
- **Account Activation**: Email OTP confirmation process
- **Multiple Sessions**: Support login trên multiple devices

**JWT & Token Management:**
- **Access Token**: Short-lived JWT cho API authentication (configurable expiry)
- **Refresh Token**: Long-lived token cho token renewal
- **Token Claims**: User ID, email, roles, permissions embedded trong JWT
- **Custom JWT Decoder**: Centralized JWT validation across services
- **Token Revocation**: Logout invalidates refresh tokens

**Comprehensive Role System (11 Roles):**
- **GUEST**: View accommodations, rooms, pricing, reviews, locations, promotions
- **CUSTOMER**: Guest privileges + booking management, payment, messaging
- **HOST**: Customer privileges + property management, pricing, promotions
- **PROPERTY_MANAGER**: Enhanced property management với bulk operations
- **CUSTOMER_SUPPORT**: User support, booking assistance, dispute resolution
- **FINANCE_MANAGER**: Payment processing, financial reports, pricing analytics
- **CONTENT_MODERATOR**: Content approval, review moderation, host verification
- **MARKETING_MANAGER**: Promotion management, analytics, content marketing
- **OPERATIONS_MANAGER**: Business operations, reporting, location management
- **ADMIN**: Comprehensive system administration (không có DELETE_USER, DELETE_ROLE)
- **SUPER_ADMIN**: Tất cả privileges including sensitive operations

**Granular Privilege System (100+ Privileges):**
- **User Management**: VIEW_USERS, CREATE_USER, UPDATE_USER, SUSPEND_USER, ACTIVATE_USER
- **Accommodation Management**: Full CRUD + approval workflow
- **Booking Management**: Comprehensive booking lifecycle management
- **Payment Management**: Process payments, refunds, financial reporting
- **Content Management**: Create, update, moderate, publish content
- **System Administration**: Settings, logs, backup, maintenance, integrations
- **Security & Audit**: Access controls, audit logs, security settings
- **Analytics & Reporting**: Financial, booking, user analytics với export

**Advanced Authorization:**
- **Role-Based Access Control (RBAC)**: Dynamic role-privilege mapping
- **Privilege Inheritance**: Hierarchical privilege system
- **Context-Aware Permissions**: Location và resource-based access control
- **Permission Caching**: Redis caching cho fast authorization checks
- **Authorization Utils**: Helper methods cho common permission checks

**Session & Security Management:**
- **Multi-Device Sessions**: Track và manage sessions across devices
- **Session Analytics**: Monitor login patterns, device tracking
- **Logout All Devices**: Security feature to revoke all user sessions
- **Refresh Token Rotation**: Enhanced security với token rotation
- **Device Fingerprinting**: Track login devices cho security

**OTP & Verification:**
- **Email OTP**: 6-digit OTP cho registration verification
- **OTP Expiration**: Configurable OTP timeout (hiện tại 5 seconds cho testing)
- **Notification Integration**: OTP delivery qua Notification Service
- **OTP Caching**: Redis storage cho OTP với automatic expiration

**Security Features:**
- **Password Encryption**: BCrypt với salt
- **JWT Security**: HMAC-SHA256 signing với secret key rotation
- **Request Filtering**: URL-based security với role protection
- **CORS Configuration**: Secure cross-origin requests
- **Session Security**: Secure session management với proper cleanup

**API Security:**
- **Public Endpoints**: Login, register, refresh token (no auth required)
- **Protected Endpoints**: User management, role management (auth required)
- **Role-Protected Routes**: Granular access control per endpoint
- **OAuth2 Resource Server**: JWT-based resource protection

**Integration:**
- **All Services**: Central authentication cho entire microservices ecosystem
- **Notification Service**: OTP delivery và user notifications
- **Profile Service**: User profile synchronization
- **Admin/Web UI**: Authentication integration với frontend applications

**Đặc điểm kỹ thuật:**
- **Spring Security**: Comprehensive security framework
- **JWT Implementation**: Custom JWT utils với NimbusDS
- **Clean Architecture**: Use Cases, Ports & Adapters pattern
- **Database Transactions**: ACID compliance với rollback support
- **Caching Strategy**: Multi-layer caching (roles, privileges, users)
- **Event-Driven**: User events published qua Kafka
- **PostgreSQL**: Reliable user data persistence
- **Error Handling**: Comprehensive exception handling với security logging
- **Performance**: Optimized authorization checks với caching

### 2. Accommodation Service (Java Spring Boot)
**Chức năng chính:** Quản lý toàn diện chỗ nghỉ và phòng với hệ thống phức tạp

**Công nghệ:** Java 17, Spring Boot, MySQL, Redis, Kafka

**Tính năng chi tiết:**

**CRUD Operations:**
- **Accommodation Management**: Tạo, đọc, cập nhật, xóa chỗ nghỉ
- **Unit/Room Management**: Quản lý phòng/đơn vị trong accommodation
- **Metadata Management**: Thông tin chi tiết, mô tả, quy tắc

**Hệ thống Amenities (Tiện ích):**
- **Amenity Groups**: Nhóm tiện ích phân cấp (ACCOMMODATION/UNIT)
- **Accommodation Amenities**: Tiện ích cấp chỗ nghỉ (WiFi, bể bơi, gym)
- **Unit Amenities**: Tiện ích cấp phòng (TV, minibar, ban công)
- **Fee Management**: Quản lý phí sử dụng và yêu cầu đặt trước

**Pricing System:**
- **Price Groups**: Nhóm giá linh hoạt theo mùa/sự kiện
- **Price Calendar**: Lịch giá chi tiết theo ngày
- **Dynamic Pricing**: Tích hợp với promotion service

**Media Management:**
- **Image Upload**: Upload ảnh cho accommodation và unit
- **Image Processing**: Resize, optimize, CDN integration
- **Thumbnail Generation**: Tự động tạo thumbnail

**Multi-language Support:**
- **Accommodation Languages**: Hỗ trợ đa ngôn ngữ
- **Localized Content**: Nội dung theo từng ngôn ngữ

**Technical Features:**
- **Caching Strategy**: Redis cho performance optimization
- **Event-Driven**: Kafka events cho sync với search/inventory
- **Authorization**: Role-based access control
- **Validation**: Comprehensive data validation
- **Audit Logging**: Theo dõi thay đổi dữ liệu

### 3. Booking Service (Go)
**Chức năng chính:**
- Quản lý toàn bộ booking lifecycle (tạo, xác nhận, phê duyệt, hủy)
- Xử lý đặt phòng với lock inventory
- Quick Booking (đặt phòng nhanh cho người dùng thường xuyên)
- Check-in/Check-out management
- Booking statistics và reporting
- Tích hợp với inventory và payment services

**Tính năng chi tiết:**

**Booking Management:**
- **CreateBooking**: Tạo đặt phòng mới với validation
- **ConfirmBooking**: Xác nhận thanh toán thành công
- **ApproveBooking**: Chủ nhà phê duyệt đặt phòng  
- **RejectBooking**: Chủ nhà từ chối đặt phòng
- **CancelBooking**: Khách hàng hủy đặt phòng
- **CheckInBooking**: Thực hiện check-in
- **CheckOutBooking**: Thực hiện check-out và phát sinh events

**Quick Booking System:**
- **CreateQuickBooking**: Tạo template đặt phòng nhanh
- **UpdateQuickBooking**: Cập nhật thông tin template
- **GetQuickBookings**: Lấy danh sách template của user
- **OneClickBooking**: Đặt phòng 1-click từ template

**Booking Analytics:**
- **GetBookingStatistics**: Thống kê cho host
- **GetAllBookings**: Danh sách đặt phòng với filter
- **GetDetailBooking**: Chi tiết đặt phòng với cache

**Integration Features:**
- **Inventory Lock**: Tích hợp với Inventory Service để lock phòng
- **Chat Room Creation**: Tự động tạo chat room khi confirm booking
- **Email Notifications**: Gửi email thông báo cho các trạng thái
- **Kafka Events**: Publish events cho các services khác
- **Cache Management**: Redis cache cho performance

**Đặc điểm kỹ thuật:**
- Clean Architecture với Use Cases pattern
- Database transactions với rollback support
- Event-driven architecture qua Kafka
- JWT authentication middleware
- Multi-level caching với Redis
- PostgreSQL database
- Comprehensive error handling

### 4. Rating Service (Java Spring Boot)
**Chức năng chính:**
- **Comprehensive Rating Management**: Multi-criteria rating system với 6 đánh giá chi tiết (Cleanliness, Location, Staff, Value for Money, Comfort, Facilities)
- **Advanced Rating Analytics**: Rating summaries, distribution analytics, monthly trend analysis với statistical insights
- **Real-time Search Synchronization**: Automated sync worker với Kafka integration để cập nhật search service ratings
- **Rating Helpfulness System**: User engagement tracking với helpful/unhelpful voting cho rating quality assessment
- **Sophisticated Rating Validation**: Business rule enforcement với booking verification và duplicate prevention
- **Performance-Optimized Caching**: Redis-based caching strategy với cache invalidation cho frequently accessed rating data
- **Host Response Management**: Two-way communication system cho host responses tới guest ratings

**Tính năng chi tiết:**

**Multi-Criteria Rating System:**
- **6 Rating Criteria**: CLEANLINESS, LOCATION, STAFF, VALUE_FOR_MONEY, COMFORT, FACILITIES với detailed scoring (1-10 scale)
- **Overall Rating Calculation**: Composite scoring algorithm với weighted averages từ multiple criteria
- **Rating Detail Tracking**: Individual criteria scores với aggregated analytics và trend analysis
- **Rating Validation**: Business rules để ensure only legitimate guests có thể rate (booking verification)
- **Duplicate Prevention**: System prevents multiple ratings từ same user cho same unit

**Advanced Analytics & Statistics:**
- **Rating Summary Management**: Comprehensive aggregation với numberOfRatings, totalRating, distribution analytics
- **Rating Distribution**: Statistical breakdown theo rating values (1-10) với percentage distributions
- **Criteria Averages**: Detailed analytics cho từng criteria với historical trend tracking
- **Monthly Trend Analysis**: Time-series data tracking với year-month granularity cho performance insights
- **Statistical Insights**: Average calculations, percentile analysis, trend identification

**Automated Search Service Sync:**
- **Scheduled Sync Worker**: Every 5 minutes sync ratings với search service qua Kafka messaging
- **Sync Tracking**: isSyncedWithSearchService flag để track sync status và prevent duplicate syncing
- **Kafka Integration**: Asynchronous messaging với SyncRatingMessage events cho real-time search updates
- **Batch Processing**: Optimized batch processing cho rating calculations và updates

**Rating Helpfulness & Engagement:**
- **Helpfulness Voting**: Users có thể vote helpful/unhelpful cho ratings để improve rating quality
- **Engagement Tracking**: numberOfHelpful, numberOfUnhelpful counters cho rating credibility assessment
- **User Engagement Analytics**: Track user interaction patterns với rating content
- **Quality Assessment**: Rating credibility scoring based on community feedback

**Host Response System:**
- **Two-way Communication**: Host có thể respond tới guest ratings để address concerns
- **Response Management**: Complete CRUD operations cho rating responses với moderation capabilities
- **Communication Tracking**: Timeline tracking của rating-response interactions
- **Professional Engagement**: Platform cho constructive dialogue between hosts và guests

**Performance & Caching:**
- **Redis Integration**: Strategic caching cho rating summaries, frequently accessed ratings
- **Cache Invalidation**: Smart cache updates khi rating data changes để maintain consistency
- **Query Optimization**: Efficient database queries với proper indexing cho rating retrieval
- **Bulk Operations**: Optimized batch processing cho rating calculations và updates

**API & Integration Features:**
- **Comprehensive REST API**: Full CRUD operations với advanced filtering và pagination
- **Advanced Filtering**: Filter by accommodation, unit, date range, rating value, language
- **Pagination Support**: Efficient data retrieval với customizable page sizes
- **Sorting Options**: Multiple sort criteria (date, rating value, helpfulness)
- **Authentication Integration**: JWT-based security với user authorization

**Data Models & Relationships:**
- **Rating Entity**: Core rating với user, booking, accommodation, unit associations
- **Rating Details**: Individual criteria scores với flexible rating criteria system
- **Rating Summary**: Aggregated statistics với distribution và criteria averages
- **Rating Trends**: Historical data tracking với monthly granularity
- **Pending Reviews**: Workflow management cho review processes

**Business Logic & Validation:**
- **Booking Verification**: Ensure users can only rate accommodations họ đã stayed at
- **Rating Value Validation**: Enforce rating ranges (1-10) với business rule compliance
- **Duplicate Prevention**: Comprehensive checks để prevent multiple ratings từ same user
- **Language Support**: Multi-language rating content với language associations
- **Transaction Safety**: Complete ACID compliance với rollback capabilities

**Background Processing:**
- **Automated Workers**: Scheduled tasks cho rating summary updates và search sync
- **Monthly Aggregation**: Automatic calculation của monthly rating trends
- **Data Consistency**: Background processes để maintain data integrity across services
- **Performance Monitoring**: Worker performance tracking với success/failure metrics

**Integration Points:**
- **Search Service**: Real-time rating sync để update search results với current ratings
- **Booking Service**: Integration để verify booking completion before allowing ratings
- **Profile Service**: User profile integration cho rating author information
- **Accommodation Service**: Integration để ensure rating target validity

**Technical Architecture:**
- **Clean Architecture**: Use Cases pattern với domain-driven design
- **Spring Boot 3.3.10**: Modern framework với Java 21 features
- **MapStruct**: Object mapping với type-safe transformations
- **OpenFeign**: Service-to-service communication với circuit breaker patterns
- **Transactional Management**: Comprehensive transaction handling với rollback support

**Monitoring & Analytics:**
- **Rating Quality Metrics**: Track rating distribution, average scores, trend analysis
- **User Engagement**: Monitor helpful votes, response rates, rating participation
- **Performance Metrics**: Query performance, cache hit rates, sync success rates
- **Business Intelligence**: Rating insights cho accommodation quality assessment

### 5. Search Service (Java Spring Boot)
**Chức năng chính:**
- **Elasticsearch-based Search**: Full-text search với fuzzy matching cho accommodations
- **Advanced Filtering**: Multi-criteria filtering với nested queries
- **Real-time Availability**: Date-based availability search với capacity checking
- **Geo-location Search**: Distance-based filtering với radius support
- **Performance Optimization**: Elasticsearch indexing và caching strategies

**Tính năng chi tiết:**

**Full-text Search Engine:**
- **Elasticsearch Integration**: Native Elasticsearch client với Spring Data Elasticsearch
- **Fuzzy Matching**: Auto-correct search với fuzziness support
- **Field-specific Search**: Search across accommodation names, descriptions
- **Index Management**: Automatic index creation và document mapping
- **Search Optimization**: Query performance tuning và index optimization

**Advanced Filtering System:**
- **Multi-criteria Filtering**: Name, location, price range, rating, amenities
- **Nested Queries**: Complex queries cho units và availability
- **Boolean Queries**: Must, should, filter combinations
- **Range Filters**: Price range, rating range, capacity filtering
- **Term Filtering**: Exact matches cho IDs và categories

**Date & Availability Search:**
- **Real-time Availability**: Check unit availability cho specific date ranges
- **Date Range Queries**: Start/end date filtering với calendar integration
- **Capacity Matching**: Filter by number of adults/children
- **Unit-level Availability**: Nested queries cho detailed availability checking
- **Booking Conflict Prevention**: Ensure no double bookings

**Geo-location Features:**
- **Geo-distance Queries**: Search within radius từ coordinates
- **Location-based Filtering**: Province/city/district filtering
- **Distance Calculation**: Calculate distance từ user location
- **Map Integration**: Support cho map-based search
- **Geographic Sorting**: Sort by proximity to location

**Amenity & Policy Filtering:**
- **Accommodation Amenities**: WiFi, pool, gym, parking filtering
- **Unit Amenities**: Kitchen, balcony, TV, AC filtering
- **Booking Policies**: Cancellation, payment, pet policies
- **Multi-amenity Support**: AND/OR logic cho multiple amenities
- **Hierarchical Filtering**: Category-based amenity grouping

**Sorting & Pagination:**
- **Multiple Sort Options**: Price (asc/desc), rating, distance, relevance
- **Default Sorting**: Rating-based default sorting
- **Pagination Support**: Page-based results với size control
- **Result Counting**: Total results và page information
- **Performance Pagination**: Efficient large dataset handling

**Data Synchronization:**
- **Event-driven Updates**: Kafka events từ Accommodation Service
- **Real-time Indexing**: Automatic document updates khi data changes
- **Unit Management**: Add/remove units từ accommodations
- **Availability Sync**: Real-time availability updates
- **Batch Operations**: Bulk indexing cho initial data load

**Integration với Other Services:**
- **Accommodation Service**: Fetch detailed accommodation data
- **Booking Service**: Availability updates từ booking events
- **Frontend Integration**: API endpoints cho web/mobile applications
- **Thumbnail Generation**: Integration với accommodation thumbnails

**Performance & Caching:**
- **Elasticsearch Optimization**: Index tuning và query optimization
- **Search Response Caching**: Cache frequently searched results
- **Lazy Loading**: Load detailed data on demand
- **Connection Pooling**: Efficient Elasticsearch connections
- **Query Performance**: Monitor và optimize slow queries

**Search Analytics:**
- **Search Tracking**: Log search queries và results
- **Popular Searches**: Track most searched terms
- **Filter Analytics**: Monitor filter usage patterns
- **Performance Metrics**: Query response times và hit rates
- **Search Optimization**: Data-driven search improvements

**API Endpoints:**
- **Search API**: POST `/accommodations/search` - Full search functionality
- **Thumbnail API**: POST `/accommodations/search/thumbnail` - Lightweight search
- **Individual Lookup**: GET `/accommodations/{id}` - Single accommodation
- **Management APIs**: CRUD operations cho admin features

**Đặc điểm kỹ thuật:**
- **Elasticsearch 8.x**: Modern search engine với advanced features
- **Spring Data Elasticsearch**: Native Spring integration
- **Native Query Builder**: Complex query construction
- **Document Mapping**: Auto-mapping entities to Elasticsearch documents
- **Kafka Integration**: Event-driven data synchronization
- **Clean Architecture**: Use Cases, Ports & Adapters pattern
- **Performance Monitoring**: Query performance tracking
- **Error Handling**: Comprehensive exception handling cho search failures

### 6. Inventory Service (Java Spring Boot)
**Chức năng chính:**
- **Lock/Unlock Inventory Management**: Hệ thống khóa phòng phức tạp với distributed locking, hỗ trợ multi-unit booking, validation thời gian và số lượng phòng available
- **Booking Lifecycle Management**: Xử lý hoàn chỉnh lifecycle từ lock → confirm → check-in → check-out → cancel, với state management chi tiết cho từng room availability
- **Room Availability Tracking**: Quản lý real-time tình trạng phòng (AVAILABLE, TEMPORARILY_LOCKED, BOOKED, CHECKED_IN, CHECKED_OUT, OCCUPIED, MAINTENANCE, CLEANING)
- **Distributed Locking System**: Redis-based distributed lock với timeout, lease time, auto-expiration để đảm bảo consistency trong môi trường multi-instance
- **Transactional Safety**: Complete transaction management với rollback, error handling, authorization validation cho tất cả booking operations
- **Check-in/Check-out Management**: Hệ thống check-in/out với guest count tracking, housekeeping management (needsCleaning, needsMaintenance), occupancy tracking
- **Integration Hub**: Tích hợp chặt chẽ với booking_service, accommodation_service, payment_service thông qua event-driven architecture
- **Pricing Management**: Dynamic pricing với base price, date-specific pricing, price update capabilities cho từng room availability

**Tính năng chi tiết:**

**Advanced Lock Management:**
- **Multi-level Locking**: Accommodation-level distributed lock + room-level inventory lock
- **Lock Request Validation**: Comprehensive validation cho accommodation ID, unit ID, quantity, date ranges
- **Timeout Protection**: Auto-expiration locks (30 minutes default) để prevent deadlocks
- **Race Condition Prevention**: Redis distributed locking với acquire timeout (5 seconds)
- **Lock ID Generation**: Unique lock identifier với accommodation, user, timestamp
- **Concurrent Booking Protection**: Ensure multiple users không thể book cùng room

**Room State Management:**
- **Complete State Machine**: 7 states với proper transitions (AVAILABLE → TEMPORARILY_LOCKED → BOOKED → CHECKED_IN → OCCUPIED → CHECKED_OUT → AVAILABLE)
- **Status Validation**: Ensure only valid state transitions
- **Availability Calculation**: Real-time calculation của available rooms cho date ranges
- **Room Conflict Detection**: Prevent overlapping bookings và invalid state changes
- **Expiry Management**: Auto-release expired locks để maintain availability accuracy

**Booking Operations:**
- **Lock Rooms**: Temporary lock rooms cho booking process với expiration
- **Confirm Booking**: Convert temporary locks thành confirmed bookings
- **Cancel Booking**: Release booking và restore room availability
- **Release Lock**: Manual lock release khi booking process cancelled
- **Authorization Checks**: Ensure only authorized users có thể perform operations

**Check-in/Check-out Process:**
- **Check-in Validation**: Verify booking exists, guest authorization, valid check-in dates
- **Guest Count Tracking**: Track số lượng khách thực tế check-in
- **Room Occupancy**: Update room status to CHECKED_IN/OCCUPIED
- **Check-out Process**: Update status, mark rooms cần cleaning
- **Housekeeping Integration**: Auto-flag rooms need cleaning sau check-out

**Pricing & Availability:**
- **Dynamic Room Pricing**: Room-specific pricing với base price và date-specific adjustments
- **Price History**: Track pricing changes over time
- **Availability Calendar**: Detailed availability cho từng room trong date range
- **Capacity Management**: Track available rooms vs. booked rooms

**Technical Architecture:**
- **Clean Architecture**: Use Cases pattern với dependency injection
- **Hexagonal Architecture**: Ports & Adapters pattern cho external integrations
- **Transaction Management**: ACID compliance với proper rollback handling
- **Redis Integration**: Distributed locking và caching cho performance
- **PostgreSQL**: Reliable data persistence với optimized queries
- **Event-Driven**: Kafka integration cho inter-service communication

**Error Handling & Resilience:**
- **Comprehensive Validation**: Input validation với descriptive error messages
- **Exception Handling**: Proper exception hierarchy với rollback capabilities
- **Lock Timeout Handling**: Graceful handling của lock acquisition failures
- **Retry Logic**: Automatic retry cho transient failures
- **Circuit Breaker**: Protection against cascading failures

**Performance Optimization:**
- **Connection Pooling**: Efficient database connection management
- **Query Optimization**: Optimized queries cho availability checks
- **Bulk Operations**: Batch processing cho multiple room updates
- **Caching Strategy**: Cache frequently accessed room availability data
- **Async Processing**: Non-blocking operations khi possible

**Integration Points:**
- **Booking Service**: Receive booking events, provide inventory status
- **Accommodation Service**: Sync room information và availability updates
- **Payment Service**: Coordinate với payment completion events
- **Search Service**: Provide real-time availability data cho search results
- **Notification Service**: Send availability change notifications

**API Security:**
- **JWT Authentication**: Secure endpoints với user authentication
- **Role-based Authorization**: Different access levels cho guests, hosts, admins
- **Request Validation**: Comprehensive input validation và sanitization
- **Audit Logging**: Track all inventory operations cho security và debugging

**Monitoring & Observability:**
- **Lock Metrics**: Monitor lock acquisition success rates và timeouts
- **Performance Metrics**: Track API response times và throughput
- **Error Tracking**: Comprehensive error logging với context
- **Business Metrics**: Track booking success rates, cancellation patterns
- **Health Checks**: Service health monitoring với dependency checks

### 7. File Service (Java Spring Boot)
**Chức năng chính:**
- **Advanced File Management**: Complete file lifecycle management với upload, download, delete operations và comprehensive metadata tracking
- **Intelligent Image Processing**: Multi-version image generation với Thumbnailator library - automatic thumbnail, gallery, banner size creation
- **File Classification System**: Sophisticated categorization với 10+ predefined categories (hotel_cover, room_gallery, user_avatar, review_photo, etc.)
- **Multi-format Support**: Support đa định dạng image (JPEG, PNG, GIF, BMP, WebP) với automatic format detection và optimization
- **Secure Storage Architecture**: File storage abstraction với secure access controls, user ownership validation, và privacy settings
- **Metadata & Tagging**: Rich metadata tracking với dimensions, file sizes, reference linking, tagging system cho advanced search
- **CDN-Ready Infrastructure**: URL generation patterns ready cho CDN integration với versioned asset management

**Tính năng chi tiết:**

**Advanced Image Processing:**
- **Multi-Size Generation**: Automatic generation của 4 image versions - THUMBNAIL (320x240), GALLERY (1024x768), BANNER (1920x1080), ORIGINAL
- **Smart Cropping**: Intelligent cropping cho thumbnails với center positioning để maintain visual focus
- **Aspect Ratio Preservation**: Maintain original aspect ratios cho gallery và banner sizes với proportional scaling
- **Format Optimization**: Support JPEG, PNG, GIF, BMP, WebP với automatic format detection và conversion capabilities
- **Quality Control**: Optimized image compression với quality preservation cho different use cases

**File Classification & Organization:**
- **10+ Predefined Categories**: HOTEL_COVER, HOTEL_GALLERY, ROOM_COVER, ROOM_GALLERY, USER_AVATAR, USER_BACKGROUND, BLOG_COVER, BLOG_CONTENT, REVIEW_PHOTO, DOCUMENT, OTHER
- **Reference Linking**: Advanced entity association với accommodationId, unitId, userId, và generic referenceId/referenceType pattern
- **Group Management**: File grouping với UUID-based groupId cho batch operations và related file management
- **Tag System**: Flexible tagging với comma-separated tags cho enhanced categorization và search capabilities
- **Privacy Controls**: Public/private file access controls với user-based ownership validation

**Storage & Infrastructure:**
- **Abstracted Storage Layer**: Clean architecture với IFileStoragePort interface cho pluggable storage backends
- **Unique Naming**: UUID-based file naming để prevent conflicts và enhance security
- **Version Management**: Multiple file versions stored với organized naming conventions (_thumbnail, _gallery, _cover suffixes)
- **Metadata Tracking**: Complete metadata storage với original filename, content type, file size, upload timestamp, creator tracking
- **CDN Integration**: Ready-to-use URL patterns với configurable download endpoints cho CDN implementation

**Security & Access Control:**
- **User Ownership**: Strict user-based file ownership với validation trên upload/delete operations
- **Privacy Settings**: Granular privacy controls với public/private file designation
- **Secure Download**: Controlled file access với proper content-type headers và security validation
- **Path Traversal Protection**: Security measures against directory traversal attacks
- **Content Validation**: File type validation với MIME type checking và content verification

**API & Integration:**
- **RESTful Endpoints**: Complete REST API với standardized response formats
- **Bulk Operations**: Multi-file upload với transaction safety và atomic operations
- **Flexible Download**: Download by ID hoặc filename với proper HTTP headers và content disposition
- **Classification API**: Advanced upload với immediate classification, tagging, và metadata assignment
- **Batch Management**: Group-based file operations với UUID tracking

**Performance & Optimization:**
- **Efficient Processing**: Streaming-based image processing để minimize memory usage
- **Transaction Safety**: Database transactions với rollback support cho data consistency
- **Lazy Loading**: On-demand image processing với caching capabilities
- **Resource Management**: Proper resource cleanup với automatic memory management
- **Error Recovery**: Comprehensive error handling với partial success support

**Technical Architecture:**
- **Spring Boot 3.3.9**: Modern framework với Java 21 features
- **Thumbnailator Library**: Professional-grade image processing với high-quality algorithms
- **Clean Architecture**: Use Cases pattern với dependency injection và port/adapter pattern
- **MapStruct Integration**: Type-safe object mapping cho DTOs và responses
- **Transaction Management**: ACID compliance với proper rollback handling

**Metadata & Analytics:**
- **Rich Metadata**: Width, height, aspect ratio, file size, upload timestamp tracking
- **Usage Analytics**: File access patterns, download statistics, usage metrics
- **Storage Analytics**: Storage usage tracking, file type distribution, size analytics
- **Version Tracking**: Multiple versions với individual metadata cho each size variant
- **Reference Analytics**: Entity association tracking cho usage insights

**Integration Points:**
- **Accommodation Service**: Hotel/room image management với category-based organization
- **Profile Service**: User avatar và background image handling
- **Chat Service**: Media sharing trong chat conversations
- **Rating Service**: Review photos với proper categorization
- **Frontend Applications**: Direct file upload/download integration với drag-drop support

**Error Handling & Monitoring:**
- **Comprehensive Validation**: File type, size, format validation với descriptive error messages
- **Processing Error Recovery**: Graceful handling của image processing failures
- **Storage Error Handling**: Backup strategies và retry mechanisms cho storage failures
- **Performance Monitoring**: Processing time tracking, storage utilization monitoring
- **Health Checks**: Service health với storage backend connectivity checks

### 8. Chat Service (Go)
**Chức năng chính:**
- Real-time chat system với WebSocket cho booking discussions
- Chat room management gắn liền với booking
- Message persistence với nhiều loại message
- Typing indicators và read receipts
- Media message sharing (images, files)
- User presence tracking và connection management

**Tính năng chi tiết:**

**Real-time Communication:**
- **WebSocket Connection**: Kết nối real-time với JWT authentication
- **Room-based Chat**: Mỗi booking có một chat room riêng
- **Message Broadcasting**: Gửi tin nhắn real-time cho tất cả participants
- **Connection Management**: Auto-reconnect, heartbeat, user presence

**Message Features:**
- **Text Messages**: Tin nhắn văn bản cơ bản
- **Media Messages**: Upload và chia sẻ hình ảnh, files
- **System Messages**: Thông báo hệ thống tự động
- **Read Receipts**: Đánh dấu tin nhắn đã đọc
- **Message History**: Pagination và lưu trữ lịch sử chat

**Chat Room Management:**
- **Auto Creation**: Tự động tạo chat room khi booking được confirm
- **Participant Management**: Tourist và owner tự động được thêm vào room
- **Permissions**: Kiểm tra quyền truy cập chat room
- **Room Search**: Tìm kiếm và filter chat rooms

**Interactive Features:**
- **Typing Indicators**: Hiển thị khi user đang gõ tin nhắn
- **Online Status**: Tracking user online/offline status
- **Message Status**: Delivered, read indicators
- **Unread Count**: Đếm tin nhắn chưa đọc

**Integration:**
- **File Service**: Tích hợp upload media qua File Service
- **Booking Service**: Tự động tạo chat room từ booking events
- **Authentication**: JWT authentication với middleware

**Đặc điểm kỹ thuật:**
- **WebSocket với Gorilla**: High-performance WebSocket connections
- **Clean Architecture**: Use Cases, Ports & Adapters pattern
- **PostgreSQL**: Message và chat room persistence
- **Concurrent Safety**: Thread-safe connection management
- **Auto-reconnection**: Client-side reconnection logic
- **Heartbeat Monitoring**: Connection health checking
- **Event Broadcasting**: Room-based message distribution

**Tech Stack:**
- Gin framework cho REST APIs
- WebSocket với gorilla/websocket
- PostgreSQL cho data persistence
- JWT authentication
- File upload integration
- Context-based request handling

### 9. Notification Service (Go)
**Chức năng chính:**
- **Multi-Channel Notification System**: Hỗ trợ EMAIL, SMS, PUSH notifications với unified API
- **Event-Driven Architecture**: Kafka-based async processing với producer-consumer pattern
- **Comprehensive Notification Management**: CRUD operations với advanced status tracking và lifecycle management
- **Intelligent Retry System**: Auto-retry với exponential backoff, max retry limits, failed notification processor
- **Real-time Status Tracking**: Complete notification status machine (PENDING → PROCESSING → SENT/FAILED → CANCELLED)
- **Email Service Integration**: SMTP email delivery với templating và HTML content support
- **High-Performance Processing**: Concurrent message processing với worker pools và async operations
- **Caching & Performance**: Redis caching cho notification data và user preferences

**Tính năng chi tiết:**

**Multi-Type Notification Support:**
- **Email Notifications**: SMTP integration với HTML templates, rich content, sender/recipient management
- **SMS Notifications**: SMS gateway integration với delivery confirmation
- **Push Notifications**: Mobile push notification support với device targeting
- **Unified API**: Single API interface cho tất cả notification types
- **Metadata Support**: Flexible metadata storage cho custom notification data

**Event-Driven Kafka Integration:**
- **Email Producer**: Kafka producer cho email notification events với partitioning by recipient
- **Email Consumer**: Kafka consumer với consumer groups cho scalable processing
- **Message Headers**: Tracing headers với notification_id, type cho debugging
- **Idempotent Processing**: Prevent duplicate message processing với Kafka configurations
- **Topic Management**: Dedicated topics cho từng notification type (notification_service.email)

**Advanced Retry & Resilience:**
- **Intelligent Retry Logic**: Exponential backoff với configurable max retry attempts
- **Failed Notification Processor**: Background worker auto-retry failed notifications trong time windows
- **Retry Scheduling**: Time-based retry với cutoff thresholds (24h window)
- **Status Management**: Comprehensive status tracking với timestamps (lastRetryAt, sentAt)
- **Circuit Breaker**: Protection against cascading failures với retry limits

**Notification Lifecycle Management:**
- **Create Notification**: Tạo notification với validation và auto-status assignment (PENDING)
- **Queue Processing**: Async queueing với Kafka với immediate processing trigger
- **Status Updates**: Real-time status updates qua database transactions
- **Delivery Confirmation**: Mark notifications as SENT với delivery timestamps
- **Failure Handling**: Auto-mark failed notifications với error tracking

**Performance & Scalability:**
- **Concurrent Processing**: Goroutine-based concurrent message processing
- **Connection Pooling**: Efficient database connection management với GORM
- **Redis Caching**: Cache notification preferences, user data, frequently accessed notifications
- **Batch Operations**: Bulk notification processing cho high-volume scenarios
- **Kafka Optimizations**: Batch timeout, least bytes balancer cho efficient message delivery

**Email Service Features:**
- **SMTP Integration**: Full SMTP client integration với authentication
- **Rich Content Support**: HTML content, plain text fallback, attachments support
- **Sender Management**: Configurable sender information với name và email
- **Recipient Management**: Multiple recipients, CC, BCC support
- **Template System**: Email templating với variable substitution
- **Delivery Tracking**: Email delivery confirmation và bounce handling

**Database & Storage:**
- **Notification Persistence**: Complete notification data storage với PostgreSQL
- **Metadata Storage**: JSON metadata storage cho flexible notification data
- **Audit Trail**: Complete audit trail với creation, update, retry timestamps
- **User Integration**: Integration với user service cho recipient validation
- **Transaction Safety**: ACID compliance với proper rollback handling

**API & Integration:**
- **REST API**: Complete REST API cho notification management (CRUD operations)
- **Pagination Support**: Efficient pagination cho notification listing với total counts
- **Filter Support**: Advanced filtering by status, type, user, date ranges
- **Authentication**: JWT authentication integration với other services
- **Inter-service Communication**: Internal APIs cho other services integration

**Background Processing:**
- **Failed Notification Processor**: Background service retry failed notifications theo schedule
- **Cleanup Workers**: Auto-cleanup old notifications, expired retries
- **Health Monitoring**: Service health checks với dependency monitoring
- **Graceful Shutdown**: Proper resource cleanup on service shutdown
- **Process Monitoring**: Track processing rates, success/failure metrics

**Technical Architecture:**
- **Clean Architecture**: Use cases, ports & adapters pattern với dependency injection
- **Hexagonal Architecture**: Clear separation of concerns với port interfaces
- **Uber FX**: Dependency injection framework cho modular architecture
- **GORM**: Advanced ORM với auto-migration, relationship management
- **Gin Framework**: High-performance HTTP framework với middleware support
- **Context Management**: Request context propagation cho tracing và cancellation

**Error Handling & Monitoring:**
- **Comprehensive Logging**: Structured logging với context và error details
- **Error Classification**: Different error types với appropriate handling strategies
- **Metrics Collection**: Business metrics cho notification success rates, retry patterns
- **Health Checks**: Service health endpoints với dependency status
- **Distributed Tracing**: Request tracing across service boundaries

**Security & Compliance:**
- **Data Privacy**: Secure handling của personal notification data
- **Authentication**: JWT-based authentication cho API access
- **Input Validation**: Comprehensive request validation và sanitization
- **Audit Logging**: Complete audit logs cho compliance requirements
- **Rate Limiting**: Protection against notification spam và abuse

**Integration Points:**
- **Authentication Service**: OTP email delivery cho user verification
- **Booking Service**: Booking confirmation, status change notifications
- **Payment Service**: Payment confirmation, refund notification emails
- **Profile Service**: Welcome emails, profile update notifications
- **Chat Service**: Chat message notifications, mentions
- **Admin Services**: System notifications, maintenance alerts

### 10. Location Service (.NET Core)
**Chức năng chính:**
- **Comprehensive Geographic Management**: Quản lý hierarchical địa điểm (Country → Province → Location) với PostgreSQL và Entity Framework
- **Tourist Attraction System**: Complete attraction management với categories, schedules, multi-language support, và image galleries
- **Trending Places Analytics**: Dynamic trending place tracking với ranking algorithms, temporal analysis, và metadata-driven insights
- **Advanced Caching Strategy**: Redis-based multi-level caching với cache keys optimization và TTL management
- **GIS & Coordinate Management**: Geographic coordinates handling với latitude/longitude precision, spatial data management
- **Multi-language Content**: Internationalization support cho attractions với language associations và localized content
- **Image & Media Management**: Integrated image handling với attraction galleries, thumbnails, và CDN-ready URLs

**Tính năng chi tiết:**

**Geographic Hierarchy Management:**
- **Country Management**: Complete country data với ISO codes, currency, timezone, language, region, sub-region, flag URLs
- **Province System**: Province management với unique codes, country associations, created/updated timestamps
- **Location Precision**: Detailed location tracking với latitude/longitude coordinates, province/country linking
- **Geographic Search**: Location-based querying với coordinate filtering và geographic boundary searches
- **Administrative Data**: Complete administrative divisions với hierarchical relationships

**Tourist Attraction Features:**
- **Attraction CRUD**: Full lifecycle management cho tourist attractions với detailed descriptions
- **Category System**: Attraction categorization với descriptions, icons, activation status
- **Schedule Management**: Attraction operating schedules với time-based availability
- **Multi-language Support**: Language associations cho attractions với localized content delivery
- **Location Integration**: Seamless integration với location hierarchy cho geographic context

**Trending Places Analytics:**
- **Dynamic Ranking**: Algorithmic trending calculation với score-based ranking systems
- **Temporal Analysis**: Time-based trending với start/end dates, seasonal analysis
- **Type-based Filtering**: Flexible trending categorization (ATTRACTION, LOCATION, HOTEL) với metadata support
- **Metadata-driven Insights**: JSON metadata storage cho flexible analytics và custom properties
- **Active Status Management**: Enable/disable trending places với activation controls
- **Tag System**: Flexible tagging system cho enhanced categorization và search

**Advanced Caching Architecture:**
- **Redis Integration**: StackExchange.Redis với distributed caching across service instances
- **Cache Key Optimization**: Structured cache keys với parameter-based generation
- **TTL Management**: Configurable time-to-live với different expiration strategies
- **Cache Strategies**: Get-or-set patterns với automatic cache population
- **Performance Optimization**: Cache-first approaches cho frequently accessed data

**Image & Media Management:**
- **Multi-entity Images**: Image associations với attractions, locations, trending places
- **Gallery Support**: Multiple images per attraction với ordering và categorization
- **URL Management**: CDN-ready image URLs với thumbnail generation capabilities
- **Entity Type Mapping**: Flexible image-to-entity relationships với type-based queries

**API & Integration:**
- **RESTful APIs**: Complete REST API surface với standardized response formats
- **Pagination Support**: Efficient pagination với page info và total count tracking
- **Query Parameters**: Advanced filtering với multi-parameter search capabilities
- **Response Standardization**: Unified response format với Resource wrapper patterns
- **Error Handling**: Comprehensive exception handling với domain-specific error codes

**Database Design & Performance:**
- **PostgreSQL Integration**: Entity Framework Core với database-first approach
- **Relationship Management**: Complex foreign key relationships với cascade operations
- **Index Optimization**: Strategic indexing cho frequently queried columns
- **Migration Management**: Database versioning với Entity Framework migrations
- **Connection Pooling**: Efficient database connection management

**Technical Architecture:**
- **.NET 9.0**: Latest .NET framework với modern C# features
- **Clean Architecture**: Domain-driven design với ports & adapters pattern
- **Dependency Injection**: Built-in DI container với service registration
- **Entity Framework Core**: ORM với code-first và database-first support
- **AutoMapper**: Object-to-object mapping cho DTOs và entities
- **Structured Logging**: ILogger integration với structured logging patterns

**Business Logic & Use Cases:**
- **Create Operations**: Validation-rich creation processes với business rule enforcement
- **Batch Operations**: Efficient bulk operations cho data imports và updates
- **Data Aggregation**: Complex queries với relationship loading và performance optimization
- **Cache Invalidation**: Smart cache invalidation strategies với data consistency
- **Audit Trails**: Complete audit logging với created/updated timestamps

**Performance Features:**
- **Lazy Loading**: Entity Framework lazy loading với performance optimization
- **Bulk Operations**: Efficient batch processing cho large datasets
- **Query Optimization**: Optimized LINQ queries với minimal database round trips
- **Memory Management**: Efficient memory usage với proper object disposal
- **Async Operations**: Full async/await pattern implementation cho scalability

**Integration Points:**
- **Search Service**: Location data feeding cho geo-location search capabilities
- **Accommodation Service**: Geographic data cho accommodation location context
- **Booking Service**: Location information cho booking geographic context
- **Frontend Applications**: API integration cho maps, location selection, attraction browsing
- **External APIs**: Ready for integration với Google Maps, OpenStreetMap, tourism APIs

**Monitoring & Observability:**
- **Health Checks**: Service health monitoring với dependency checks
- **Performance Metrics**: Query performance tracking và optimization insights
- **Error Tracking**: Comprehensive error logging với stack traces và context
- **Business Metrics**: Usage analytics cho attractions, locations, trending data
- **Cache Monitoring**: Cache hit rates, performance metrics, memory usage tracking

### 11. Payment Service (Java Spring Boot)
**Chức năng:**
- Payment processing
- Multiple payment methods
- Transaction management
- Refund handling

### 12. Promotion Service (.NET Core)
**Chức năng:**
- Quản lý khuyến mãi và vouchers
- Discount calculation
- Campaign management
- Integration với pricing system

### 13. API Gateway (Java Spring Boot)
**Chức năng:**
- Central entry point
- Load balancing
- Authentication middleware
- Rate limiting
- CORS handling

## TÍNH NĂNG CHÍNH

Hệ thống TripVibe cung cấp bộ tính năng toàn diện cho nền tảng đặt phòng hiện đại, được thiết kế để phục vụ tất cả các stakeholder từ khách hàng, chủ nhà đến quản trị viên. Mỗi tính năng được phát triển với sự tập trung vào trải nghiệm người dùng, hiệu suất và khả năng mở rộng.

### 1. Hệ thống Quản lý Người dùng Đa cấp
**Authentication & Authorization System:**
- **Multi-Role Authentication**: 11 vai trò người dùng với phân quyền chi tiết (GUEST, CUSTOMER, HOST, PROPERTY_MANAGER, CUSTOMER_SUPPORT, FINANCE_MANAGER, CONTENT_MODERATOR, MARKETING_MANAGER, OPERATIONS_MANAGER, ADMIN, SUPER_ADMIN)
- **Granular Permissions**: 100+ quyền cụ thể cho từng chức năng hệ thống
- **JWT Token Management**: Access + Refresh token với rotation và multi-device sessions
- **OTP Verification**: Email OTP cho registration với Redis-based expiration

**User Profile Management:**
- **Complete Profile System**: Personal information, contact details, preferences
- **Avatar & Media**: File upload với image processing và CDN integration
- **Social Features**: Friends management, travel companions, activity tracking
- **Wishlist System**: Multiple wishlists với organization và sharing capabilities
- **Member Levels**: Tiered membership system với exclusive benefits

### 2. Quản lý Accommodation & Property
**Comprehensive Property Management:**
- **Accommodation CRUD**: Complete property lifecycle management
- **Multi-Unit Support**: Rooms/units với individual pricing và availability
- **Rich Media Management**: Multi-image galleries với automatic processing, thumbnails, CDN-ready URLs
- **Location Integration**: Geographic data với tourist attractions và trending places

**Advanced Amenities System:**
- **Hierarchical Amenities**: Accommodation-level (WiFi, pool, gym) và Unit-level (TV, kitchen, balcony)
- **Fee Management**: Amenity charges và pre-booking requirements
- **Amenity Groups**: Organized categorization với ACCOMMODATION/UNIT types
- **Popular Amenities**: Highlighted amenities cho marketing và search

**Dynamic Pricing Engine:**
- **Flexible Pricing Rules**: Seasonal rates, weekend multipliers, holiday pricing
- **Price Calendar**: Daily pricing với historical tracking
- **Rule Priority System**: Multiple pricing rules với priority-based application
- **Discount Management**: Early bird, last-minute discounts với automatic calculation
- **Real-time Price Calculation**: Dynamic pricing với guest count, length of stay factors

### 3. Advanced Search & Discovery Engine
**Elasticsearch-powered Search:**
- **Full-text Search**: Fuzzy matching với accommodation names, descriptions
- **Multi-criteria Filtering**: Location, dates, guests, price range, ratings, amenities
- **Geo-location Queries**: Distance-based search với radius support và map integration
- **Real-time Availability**: Live availability checking với booking conflict prevention
- **Performance Optimization**: Caching, pagination, lazy loading cho large datasets

**Smart Filtering & Sorting:**
- **Advanced Filters**: Property types, amenities (AND/OR logic), policies, ratings
- **Geographic Filtering**: Province/city/district với distance calculations
- **Sort Options**: Price, rating, distance, relevance với customizable defaults
- **Search Analytics**: Query tracking và optimization insights

### 4. Sophisticated Inventory Management
**Distributed Room Locking:**
- **Redis-based Distributed Locks**: Multi-instance consistency với timeout protection
- **Room State Machine**: 7 states (AVAILABLE → TEMPORARILY_LOCKED → BOOKED → CHECKED_IN → OCCUPIED → CHECKED_OUT → AVAILABLE)
- **Inventory Validation**: Comprehensive checks cho availability, conflicts, authorization
- **Lock Expiration**: Auto-release expired locks (30 minutes default) để prevent deadlocks

**Booking Lifecycle Management:**
- **Lock Rooms**: Temporary inventory hold cho booking process
- **Confirm Booking**: Convert locks thành confirmed reservations
- **Check-in/Check-out**: Guest management với occupancy tracking
- **Cancellation**: Flexible cancellation với availability restoration
- **Housekeeping Integration**: Auto-flag rooms need cleaning/maintenance

### 5. Comprehensive Booking System
**Multi-step Booking Flow:**
- **Wizard-based Process**: Step-by-step booking với validation at each stage
- **Guest Information Management**: Detailed guest data collection với validation
- **Real-time Availability**: Live room availability checking với conflict prevention
- **Price Calculation**: Dynamic pricing với taxes, fees, promotions display
- **Payment Integration**: Secure payment processing (ready for gateway integration)

**Booking Management:**
- **Quick Booking Templates**: One-click booking cho frequent users
- **Booking Modifications**: Flexible changes với policy enforcement
- **Booking Analytics**: Statistics cho hosts với detailed insights
- **Email Confirmations**: Automated confirmations với QR codes, itinerary details
- **Integration Hub**: Chat room creation, inventory locking, notification triggers

### 6. Advanced Rating & Review System
**Multi-criteria Rating Framework:**
- **6-Category Rating System**: Cleanliness, Location, Staff, Value for Money, Comfort, Facilities (1-10 scale)
- **Overall Rating Calculation**: Composite scoring với weighted averages
- **Rating Analytics**: Distribution analysis, trend tracking, statistical insights
- **Monthly Trend Analysis**: Time-series data với year-month granularity

**Community Engagement Features:**
- **Rating Helpfulness**: Community voting (helpful/unhelpful) cho rating quality assessment
- **Host Response System**: Two-way communication cho constructive dialogue
- **Review Management**: Rich text reviews với photo attachments
- **Booking Verification**: Ensure only verified guests có thể submit ratings

**Automated Search Synchronization:**
- **Scheduled Sync Worker**: Every 5 minutes sync ratings với search service
- **Kafka Integration**: Real-time rating updates qua event streaming
- **Cache Management**: Redis-based caching với intelligent invalidation

### 7. Real-time Communication System
**WebSocket-based Chat:**
- **Instant Messaging**: Real-time chat với automatic chat room creation
- **Booking Integration**: Auto-create chat rooms khi booking confirmed
- **Message Types**: Text, media, system notifications với persistent history
- **Typing Indicators**: Live typing status trong conversations
- **Connection Management**: Auto-reconnection, heartbeat monitoring, presence tracking

**Enhanced Chat Features:**
- **Message Status**: Delivery và read receipt tracking
- **File Sharing**: Image và document sharing trong chat với File Service integration
- **Unread Count**: Real-time unread message tracking
- **Message History**: Pagination và search capabilities trong chat history

### 8. Multi-channel Notification System
**Comprehensive Notification Management:**
- **Multi-channel Support**: EMAIL, SMS, PUSH notifications với unified API
- **Event-driven Processing**: Kafka-based async notification với producer-consumer pattern
- **Intelligent Retry System**: Auto-retry với exponential backoff, failed notification recovery
- **Status Tracking**: Complete notification lifecycle (PENDING → PROCESSING → SENT/FAILED → CANCELLED)

**Email & Communication:**
- **SMTP Integration**: Email delivery với templating và HTML content support
- **Notification Templates**: OTP verification, booking confirmations, status updates
- **User Preferences**: Notification settings với channel preferences
- **Delivery Analytics**: Success rates, retry patterns, performance monitoring

### 9. Advanced File & Media Management
**Intelligent File Processing:**
- **Multi-size Image Generation**: Automatic THUMBNAIL (320x240), GALLERY (1024x768), BANNER (1920x1080), ORIGINAL
- **Smart Cropping**: Intelligent cropping cho thumbnails với aspect ratio preservation
- **Format Support**: JPEG, PNG, GIF, BMP, WebP với automatic optimization
- **CDN-ready Infrastructure**: URL patterns ready cho CDN integration

**File Organization & Security:**
- **Category System**: 10+ predefined categories (HOTEL_COVER, ROOM_GALLERY, USER_AVATAR, etc.)
- **Reference Linking**: Entity associations với accommodationId, unitId, userId
- **Privacy Controls**: Public/private access với user ownership validation
- **Metadata Management**: Complete file metadata với versioning support

### 10. Geographic & Location Services
**Comprehensive Location Management:**
- **Hierarchical Structure**: Country → Province → Location với relationship management
- **Tourist Attractions**: POI management với multi-language support, images, schedules
- **Trending Places Analytics**: Dynamic ranking với score-based algorithms, temporal analysis
- **Geographic Search**: Integration với search service cho location-based filtering

**Advanced Features:**
- **Multi-language Content**: Localized attraction descriptions và content
- **Image Management**: Multi-entity image associations với gallery support
- **Cache Strategy**: Redis-based distributed caching với performance optimization
- **Analytics Integration**: Location usage tracking, trending analysis

### 11. Enhanced User Experience Features
**Modern Frontend Architecture:**
- **Next.js 15.2.4**: App Router với Server Components, Server Actions
- **Real-time Updates**: WebSocket integration, live notifications, typing indicators
- **Theme System**: Dark/Light theme với system preference sync
- **Responsive Design**: Mobile-first approach với progressive enhancement

**Performance & Accessibility:**
- **Advanced Caching**: Multi-tier caching (Application, Redis, Database)
- **Loading States**: Skeleton loaders, optimistic updates, smooth transitions
- **Error Handling**: Graceful error recovery với user-friendly messages
- **Accessibility**: WCAG 2.1 compliance, screen reader support, keyboard navigation

**Interactive Features:**
- **Interactive Maps**: Leaflet-based maps với property clustering, location visualization
- **Advanced Search UI**: Real-time filtering, sorting, map view với instant results
- **Booking Wizard**: Multi-step flow với validation, progress tracking
- **Toast Notifications**: Real-time feedback cho user actions

## KIẾN TRÚC CLEAN ARCHITECTURE

### Domain-Driven Design
Các services được thiết kế theo Clean Architecture principles:

```
┌─────────────────────────────────────┐
│              UI Layer               │
│     (Controllers, REST APIs)       │
├─────────────────────────────────────┤
│           Application Layer         │
│         (Use Cases, DTOs)          │
├─────────────────────────────────────┤
│            Domain Layer             │
│    (Entities, Business Logic)      │
├─────────────────────────────────────┤
│         Infrastructure Layer        │
│   (Database, External Services)    │
└─────────────────────────────────────┘
```

### Patterns được áp dụng
- **Repository Pattern**: Data access abstraction
- **Use Case Pattern**: Business logic encapsulation
- **Port & Adapter**: Dependency inversion
- **Event Sourcing**: Kafka events
- **CQRS**: Command Query Responsibility Segregation

## CACHE STRATEGY

### Multi-level Caching
- **Application Cache**: In-memory caching
- **Redis Cache**: Distributed caching
- **Database Query Cache**: PostgreSQL query cache

### Cache Management
- Netflix EVCache-inspired architecture
- Health monitoring và statistics
- TTL management
- Cache invalidation strategies

## MESSAGE STREAMING

### Kafka Topics
- **Booking Commands**: Booking lifecycle events
- **Rating Commands**: Rating và review events
- **Search Commands**: Search index updates
- **Notification Events**: Real-time notifications

### Event-Driven Architecture
- **Apache Kafka**: Central message broker cho inter-service communication
- **Event Sourcing**: Complete audit trail với event replay capabilities  
- **Asynchronous Processing**: Non-blocking operations với improved system resilience
- **Saga Pattern**: Distributed transaction coordination cho complex workflows
- **Event Topics**: Booking lifecycle, rating updates, search indexing, notifications
- **Producer-Consumer Pattern**: Decoupled services với reliable message delivery
- **Dead Letter Queues**: Failed event handling với retry mechanisms
- **Message Ordering**: Ordered event processing cho data consistency

## DATABASE DESIGN

### Database per Service Architecture
- **PostgreSQL (11 services)**: Accommodation, Profile, Booking, Inventory, Rating, Search, File, Notification, Location, Promotion, Payment
- **MySQL (1 service)**: Authentication service - optimized cho user management và security
- **Redis (Distributed)**: Cross-service caching, session storage, distributed locking
- **Elasticsearch (Search)**: Full-text search indexing, geo-location queries, analytics

### Database Features & Optimization
- **ACID Compliance**: Full transactional integrity với rollback support
- **Connection Pooling**: Efficient database connection management
- **Strategic Indexing**: Optimized indexes cho frequently queried columns
- **Foreign Key Constraints**: Data integrity với cascading operations
- **Migration Management**: Version-controlled database schema evolution

### Data Consistency Patterns
- **Strong Consistency**: Within service boundaries với ACID transactions
- **Eventual Consistency**: Cross-service communication qua Kafka events
- Saga pattern cho distributed transactions
- Event sourcing
- Compensating actions

## TESTING STRATEGY

### Backend Testing
- Unit tests với JUnit
- Integration tests
- API testing
- Performance testing

### Frontend Testing
- Component testing
- E2E testing
- Visual regression testing

## DEPLOYMENT & DEVOPS

### Containerization
- Docker containers cho tất cả services
- Docker Compose cho development
- Kubernetes ready

### CI/CD Pipeline
- Automated building
- Testing pipeline
- Deployment automation

### Monitoring & Logging
- Application metrics
- Error tracking
- Performance monitoring
- Distributed tracing

## SECURITY MEASURES

### Authentication & Authorization
- JWT tokens
- Role-based access control
- API rate limiting
- CORS configuration

### Data Security
- Database encryption
- Secure communication (HTTPS)
- Input validation
- SQL injection prevention

## PERFORMANCE OPTIMIZATION

### Caching Strategy
- Multi-level caching
- Redis distributed cache
- Database query optimization

### Database Optimization
- Index optimization
- Query performance tuning
- Connection pooling

### Frontend Performance (TripVibe Web UI)
- **Next.js App Router**: Server Components cho improved performance và SEO
- **Automatic Code Splitting**: Route-based code splitting với dynamic imports
- **Image Optimization**: next/image với automatic format conversion (WebP, AVIF)
- **Font Optimization**: Geist font family với next/font automatic optimization
- **Bundle Analysis**: Regular monitoring với webpack-bundle-analyzer
- **Tree Shaking**: Dead code elimination với ES modules
- **Lazy Loading**: Component lazy loading với React.lazy và Suspense
- **Prefetching**: Next.js automatic prefetching cho improved navigation
- **Streaming SSR**: Streaming Server-Side Rendering cho faster initial load
- **Static Generation**: SSG cho SEO-critical pages
- **Client-side Caching**: SWR-based caching cho API responses
- **Compression**: Gzip/Brotli compression cho assets
- **Critical CSS**: Inline critical CSS cho faster initial paint
- **Resource Hints**: DNS prefetch, preload, preconnect optimizations

## SCALABILITY CONSIDERATIONS

### Horizontal Scaling
- Stateless services
- Load balancing
- Database sharding capability

### Vertical Scaling
- Performance optimization
- Resource allocation
- Caching strategies

## FUTURE ENHANCEMENTS

### Planned Features
1. **Mobile Applications**: React Native apps
2. **AI/ML Integration**: Recommendation system
3. **Analytics Dashboard**: Business intelligence
4. **Multi-language Support**: i18n implementation
5. **Payment Gateway Expansion**: More payment options

### Technical Improvements
1. **Kubernetes Deployment**: Container orchestration
2. **API Versioning**: Backward compatibility
3. **GraphQL Implementation**: Flexible data fetching
4. **Machine Learning**: Personalization engine

## DEMO & SCREENSHOTS

### Giao diện người dùng TripVibe

#### 1. **Homepage - Trang chủ**
```
📸 Screenshot: Homepage với search bar trung tâm, hero section
- Modern landing page với call-to-action rõ ràng
- Search interface prominent với location autocomplete
- Featured destinations và property promotions
- Responsive design từ mobile đến desktop
```

#### 2. **Search Results - Kết quả tìm kiếm**
```
📸 Screenshot: Search page với map integration
- Advanced filtering sidebar với real-time updates
- Property cards với pricing, ratings, amenities
- Interactive map với clustering và location pins
- Sort options và pagination để easy navigation
```

#### 3. **Property Details - Chi tiết accommodation**
```
📸 Screenshot: Property detail page với gallery
- Professional photo gallery với zoom functionality
- Detailed amenities, policies, và location info
- Real-time availability calendar với pricing
- Review section với multi-criteria ratings
```

#### 4. **Booking Flow - Quy trình đặt phòng**
```
📸 Screenshot: Multi-step booking wizard
- Guest information form với validation
- Date selection với availability checking
- Payment integration (ready for gateway)
- Confirmation page với booking details
```

#### 5. **Chat System - Real-time messaging**
```
📸 Screenshot: Chat interface với WebSocket
- Real-time messaging với typing indicators
- Media sharing capabilities trong conversations
- Online/offline status tracking
- Message history với search functionality
```

#### 6. **User Dashboard - Account management**
```
📸 Screenshot: User dashboard với trip management
- My Trips với booking history và status
- Profile management với avatar upload
- Wishlist organization với sharing options
- Review management với host responses
```

### UI/UX Design Highlights

**Modern Design Language:**
- **Clean & Minimalist**: White space optimization với focus trên content
- **Consistent Color Scheme**: Professional brand colors với accessibility
- **Typography Hierarchy**: Clear information architecture
- **Icon System**: Lucide icons cho consistency

**User Experience Excellence:**
- **Intuitive Navigation**: Logical flow từ search đến booking completion
- **Progressive Disclosure**: Information layering để avoid overwhelming
- **Error Handling**: Friendly messages với recovery suggestions
- **Performance**: Fast loading với skeleton screens

**Responsive Design:**
- **Mobile-First**: Touch-friendly interactions cho mobile users
- **Progressive Enhancement**: Enhanced features cho desktop
- **Cross-browser Compatibility**: Consistent experience across platforms
- **Accessibility**: WCAG 2.1 compliance với screen reader support

## KẾT LUẬN

Dự án **TripVibe** thể hiện việc áp dụng thành công các công nghệ và kiến trúc hiện đại trong việc xây dựng một nền tảng đặt phòng enterprise-grade, hoàn chỉnh và có thể sử dụng thực tế.

### Thành tựu kỹ thuật nổi bật

**Kiến trúc Microservices đa ngôn ngữ:**
- **13 services độc lập** với 3 technology stack: Java Spring Boot (9), Go (3), .NET Core (2)
- **Domain-driven design** với clean architecture và separation of concerns
- **Event-driven communication** qua Apache Kafka với real-time data consistency
- **Independent scaling** và deployment cho từng service

**Tính năng phức tạp & Enterprise-ready:**
- **Advanced Authentication & Authorization**: 11 roles, 100+ privileges, JWT với multi-device sessions
- **Sophisticated Inventory Management**: Distributed locking, room state machine, real-time availability
- **Comprehensive Search Engine**: Elasticsearch với geo-location, fuzzy matching, advanced filtering
- **Real-time Communication**: WebSocket chat, notifications, typing indicators
- **Multi-tier Caching**: Application + Redis + Database caching với intelligent invalidation
- **Advanced File Processing**: Multi-size image generation, smart cropping, CDN-ready infrastructure

**Performance & Scalability:**
- **Distributed systems patterns**: Circuit breaker, retry mechanisms, graceful degradation  
- **Background processing**: Scheduled workers, async operations với Kafka
- **Comprehensive monitoring**: Health checks, metrics, distributed tracing
- **Security best practices**: OWASP compliance, secure authentication, data encryption

### Giá trị tạo ra

**Cho Developer Community:**
- **Best practices showcase**: Modern architecture patterns và implementation
- **Multi-language integration**: Thực hành tích hợp các technology stack khác nhau
- **Comprehensive documentation**: Chi tiết technical design và implementation guides
- **Reusable components**: Libraries và patterns có thể áp dụng cho projects khác

**Cho Business Applications:**
- **Production-ready codebase**: Có thể deploy và sử dụng ngay trong môi trường thực tế
- **Scalable architecture**: Đáp ứng growth từ startup đến enterprise level
- **Modern user experience**: Responsive design, real-time features, accessibility compliance
- **Comprehensive functionality**: Đầy đủ features cho một booking platform hoàn chỉnh

### Tương lai phát triển
Dự án này không chỉ là một showcase technical mà còn là foundation cho việc mở rộng thành một platform booking thực sự với các tính năng như payment integration, mobile apps, advanced analytics, và AI-powered recommendations.

---

**Ngày tạo báo cáo**: 23/06/2025  
**Tác giả**: Huy QT  
**Phiên bản**: 1.0
