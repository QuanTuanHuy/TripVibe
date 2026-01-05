# 🏨 Hotel Booking Platform

Hệ thống đặt phòng khách sạn trực tuyến được xây dựng theo kiến trúc **Microservices**, hỗ trợ đầy đủ các tính năng từ tìm kiếm, đặt phòng, thanh toán đến quản lý khách sạn.

## 📋 Mục lục

- [Tổng quan](#-tổng-quan)
- [Kiến trúc hệ thống](#-kiến-trúc-hệ-thống)
- [Công nghệ sử dụng](#-công-nghệ-sử-dụng)
- [Danh sách Services](#-danh-sách-services)
- [Yêu cầu hệ thống](#-yêu-cầu-hệ-thống)
- [Hướng dẫn cài đặt](#-hướng-dẫn-cài-đặt)
- [Cấu trúc thư mục](#-cấu-trúc-thư-mục)
- [API Documentation](#-api-documentation)

## 🎯 Tổng quan

Hotel Booking Platform là một hệ thống đặt phòng khách sạn hoàn chỉnh với các tính năng chính:

### Tính năng cho Khách hàng (Guest)
- 🔍 Tìm kiếm khách sạn theo vị trí, ngày, số khách
- 📅 Đặt phòng trực tuyến với quy trình wizard
- 💳 Thanh toán qua VNPay
- ⭐ Đánh giá và nhận xét khách sạn
- 💬 Chat trực tiếp với chủ khách sạn
- ❤️ Lưu danh sách yêu thích
- 📧 Nhận thông báo qua email/hệ thống

### Tính năng cho Chủ khách sạn (Host)
- 🏠 Quản lý thông tin khách sạn và phòng
- 📊 Thống kê đặt phòng và doanh thu
- ✅ Duyệt/từ chối yêu cầu đặt phòng
- 🎫 Tạo và quản lý khuyến mãi
- 💬 Trả lời tin nhắn khách hàng

### Tính năng cho Admin
- 👥 Quản lý người dùng
- 🏨 Quản lý danh mục khách sạn
- 📍 Quản lý địa điểm
- 🛏️ Quản lý loại phòng, tiện nghi

## 🏗 Kiến trúc hệ thống

```
                                    ┌─────────────────┐
                                    │    Client UI    │
                                    │  (Next.js App)  │
                                    └────────┬────────┘
                                             │
                                    ┌────────▼────────┐
                                    │   API Gateway   │
                                    │ (Spring Cloud)  │
                                    └────────┬────────┘
                                             │
        ┌────────────────────────────────────┼────────────────────────────────────┐
        │                                    │                                    │
┌───────▼───────┐  ┌───────▼───────┐  ┌─────▼─────┐  ┌───────▼───────┐  ┌────────▼────────┐
│Authentication │  │ Accommodation │  │  Booking  │  │    Search     │  │     Payment     │
│   Service     │  │   Service     │  │  Service  │  │   Service     │  │     Service     │
│   (Spring)    │  │   (Spring)    │  │   (Go)    │  │   (Spring)    │  │    (Spring)     │
└───────────────┘  └───────────────┘  └───────────┘  └───────────────┘  └─────────────────┘
        │                  │                │                │                    │
        └──────────────────┴────────────────┴────────────────┴────────────────────┘
                                             │
                    ┌────────────────────────┼────────────────────────┐
                    │                        │                        │
            ┌───────▼───────┐       ┌────────▼────────┐      ┌───────▼───────┐
            │   PostgreSQL  │       │      Redis      │      │     Kafka     │
            │   (Database)  │       │    (Cache)      │      │ (Message Bus) │
            └───────────────┘       └─────────────────┘      └───────────────┘
```

## 🛠 Công nghệ sử dụng

### Backend Services

| Service | Ngôn ngữ | Framework | Port |
|---------|----------|-----------|------|
| API Gateway | Java 21 | Spring Cloud Gateway | 8080 |
| Authentication Service | Java 21 | Spring Boot 3.3 | 8081 |
| Notification Service | Go 1.24 | Gin + Golibs | 8082 |
| Accommodation Service | Java 21 | Spring Boot 3.3 | 8083 |
| Booking Service | Go 1.24 | Gin + Golibs | 8084 |
| Search Service | Java 21 | Spring Boot 3.3 | 8085 |
| Profile Service | Java 21 | Spring Boot 3.3 | 8086 |
| Promotion Service | C# | .NET 9 | 8087 |
| File Service | Java 21 | Spring Boot 3.3 | 8088 |
| Rating Service | Java 21 | Spring Boot 3.3 | 8089 |
| Chat Service | Go 1.24 | Gin + WebSocket | 8090 |
| Location Service | C# | .NET 9 | 8091 |
| Payment Service | Java 21 | Spring Boot 3.3 | 8092 |
| Inventory Service | Java 21 | Spring Boot 3.3 | 8093 |
| AI Service | Java 21 | Spring Boot 3.5 + Spring AI | - |
| Memo Service | Go 1.24 | Gin + gRPC | - |

### Frontend

| Application | Framework | Port |
|-------------|-----------|------|
| Web UI (Customer) | Next.js 15 + React 19 | 3000 |
| Admin UI | Next.js 15 + React 19 | 3001 |

### Infrastructure

| Component | Technology | Port |
|-----------|------------|------|
| Database | PostgreSQL | 5433 |
| Cache | Redis 7.2 | 6379 |
| Message Broker | Apache Kafka 3.7 | 9094 |
| Search Engine | Elasticsearch 8.16 | 9200 |

## 📦 Danh sách Services

### 1. API Gateway
- Routing và load balancing
- Rate limiting
- Circuit breaker (Resilience4j)

### 2. Authentication Service
- Đăng ký, đăng nhập (JWT)
- OAuth2 Resource Server
- Quản lý phiên đăng nhập

### 3. Accommodation Service
- CRUD khách sạn, phòng
- Quản lý tiện nghi, loại phòng
- Quản lý hình ảnh khách sạn

### 4. Booking Service
- Tạo, hủy đặt phòng
- Check-in, Check-out
- Quick Booking
- Thống kê đặt phòng

### 5. Search Service
- Full-text search với Elasticsearch
- Lọc theo giá, vị trí, tiện nghi
- Gợi ý tìm kiếm

### 6. Payment Service
- Tích hợp VNPay
- Xử lý webhook thanh toán
- Quản lý giao dịch

### 7. Rating Service
- Đánh giá và nhận xét
- Tính điểm trung bình
- Lọc đánh giá

### 8. Chat Service
- Real-time messaging (WebSocket)
- Lịch sử tin nhắn
- Typing indicator

### 9. Notification Service
- Push notification
- Email notification
- In-app notification

### 10. Location Service
- Quản lý địa điểm (quốc gia, tỉnh/thành, quận/huyện)
- Tìm kiếm địa điểm

### 11. Promotion Service
- Tạo mã giảm giá
- Áp dụng khuyến mãi
- Quản lý thời hạn

### 12. Inventory Service
- Quản lý số lượng phòng trống
- Lock inventory khi đặt phòng
- Batch processing

### 13. Profile Service
- Quản lý thông tin người dùng
- Wishlist
- Lịch sử đặt phòng

### 14. File Service
- Upload/download files
- Quản lý media

### 15. AI Service
- Tích hợp Spring AI
- Chatbot hỗ trợ khách hàng
- Gợi ý khách sạn

## 💻 Yêu cầu hệ thống

### Development
- **Java**: JDK 21+
- **Go**: 1.24+
- **.NET**: SDK 9.0+
- **Node.js**: 20+
- **Docker**: 24+
- **Docker Compose**: v2+

### Production (Recommended)
- CPU: 8 cores+
- RAM: 16GB+
- Storage: 100GB+ SSD

## 🚀 Hướng dẫn cài đặt

### 1. Clone repository

```bash
git clone <repository-url>
cd hotel_booking
```

### 2. Khởi động Infrastructure

```bash
# Khởi động tất cả services với Docker Compose
docker-compose up -d

# Hoặc chỉ khởi động infrastructure
docker-compose up -d booking_postgre_db redis broker elastic
```

### 3. Chạy Frontend

```bash
# Web UI (Customer)
cd web_ui
npm install
npm run dev

# Admin UI (port 3001)
cd admin_ui
npm install
npm run dev
```

### 4. Truy cập ứng dụng

| Application | URL |
|-------------|-----|
| Web UI | http://localhost:3000 |
| Admin UI | http://localhost:3001 |
| API Gateway | http://localhost:8080 |
| Elasticsearch | http://localhost:9200 |

## 📁 Cấu trúc thư mục

```
hotel_booking/
├── docker-compose.yml          # Docker orchestration
├── api_gateway/                # API Gateway (Spring Cloud)
├── authentication_service/     # Auth Service (Spring Boot)
├── accommodation_service/      # Accommodation Service (Spring Boot)
├── booking_service/           # Booking Service (Go)
├── search_service/            # Search Service (Spring Boot)
├── payment_service/           # Payment Service (Spring Boot)
├── rating_service/            # Rating Service (Spring Boot)
├── profile_service/           # Profile Service (Spring Boot)
├── notification_service/      # Notification Service (Go)
├── chat_service/              # Chat Service (Go + WebSocket)
├── file_service/              # File Service (Spring Boot)
├── inventory_service/         # Inventory Service (Spring Boot)
├── location_service/          # Location Service (.NET 9)
├── promotion_service/         # Promotion Service (.NET 9)
├── ai_service/                # AI Service (Spring AI)
├── memo_service/              # Memo Service (Go + gRPC)
├── web_ui/                    # Customer Web UI (Next.js)
└── admin_ui/                  # Admin Web UI (Next.js)
```

### Cấu trúc mỗi Service

#### Java Services (Spring Boot)
```
service_name/
├── src/
│   ├── main/
│   │   ├── java/huy/project/service_name/
│   │   │   ├── core/           # Business logic
│   │   │   │   ├── domain/     # Entities, DTOs
│   │   │   │   ├── port/       # Interfaces
│   │   │   │   └── usecase/    # Use cases
│   │   │   ├── infrastructure/ # External implementations
│   │   │   ├── kernel/         # Shared utilities
│   │   │   └── ui/             # Controllers
│   │   └── resources/
│   └── test/
├── Dockerfile
└── pom.xml
```

#### Go Services
```
service_name/
├── src/
│   ├── core/
│   │   ├── domain/         # Entities
│   │   ├── port/           # Interfaces
│   │   ├── service/        # Services
│   │   └── usecase/        # Use cases
│   ├── infrastructure/     # External implementations
│   ├── kernel/             # Shared utilities
│   └── ui/                 # Controllers, Router
├── go.mod
└── main.go
```

#### .NET Services
```
ServiceName/
├── ServiceName.Api/            # Web API
├── ServiceName.Core/           # Business logic
├── ServiceName.Infrastructure/ # Data access
└── ServiceName.Kernel/         # Shared utilities
```

## 📚 API Documentation

### Authentication

```http
POST /api/public/v1/auth/register    # Đăng ký
POST /api/public/v1/auth/login       # Đăng nhập
POST /api/public/v1/auth/refresh     # Refresh token
POST /api/public/v1/auth/logout      # Đăng xuất
```

### Accommodations

```http
GET    /api/public/v1/accommodations           # Danh sách khách sạn
GET    /api/public/v1/accommodations/:id       # Chi tiết khách sạn
POST   /api/public/v1/accommodations           # Tạo khách sạn (Host)
PUT    /api/public/v1/accommodations           # Cập nhật khách sạn
DELETE /api/public/v1/accommodations/:id       # Xóa khách sạn
```

### Bookings

```http
POST   /api/public/v1/bookings                 # Tạo đặt phòng
GET    /api/public/v1/bookings                 # Danh sách đặt phòng
GET    /api/public/v1/bookings/:id             # Chi tiết đặt phòng
PUT    /api/public/v1/bookings/:id/confirm     # Xác nhận đặt phòng
PUT    /api/public/v1/bookings/:id/cancel      # Hủy đặt phòng
PUT    /api/public/v1/bookings/:id/approve     # Duyệt đặt phòng (Host)
PUT    /api/public/v1/bookings/:id/reject      # Từ chối đặt phòng (Host)
PUT    /api/public/v1/bookings/:id/checkin     # Check-in
PUT    /api/public/v1/bookings/:id/checkout    # Check-out
```

### Payments

```http
POST   /api/public/v1/payments                 # Tạo thanh toán
GET    /api/public/v1/payments/:id             # Chi tiết thanh toán
POST   /api/public/v1/payments/webhook/vnpay   # VNPay callback
```

### Search

```http
GET    /api/public/v1/search                   # Tìm kiếm khách sạn
GET    /api/public/v1/search/suggestions       # Gợi ý tìm kiếm
```

## 🔒 Roles & Permissions

| Role | Permissions |
|------|-------------|
| GUEST | Tìm kiếm, đặt phòng, đánh giá, chat |
| HOST | Quản lý khách sạn, duyệt đặt phòng, thống kê |
| ADMIN | Quản lý danh mục, người dùng |
| SUPER_ADMIN | Toàn quyền hệ thống |

## 🌐 Environment Variables

### Database
```env
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/db_name
SPRING_DATASOURCE_USERNAME=user_booking
SPRING_DATASOURCE_PASSWORD=secret
```

### Redis
```env
SPRING_DATA_REDIS_HOST=localhost
SPRING_DATA_REDIS_PORT=6379
```

### Kafka
```env
SPRING_KAFKA_BOOTSTRAP_SERVERS=localhost:9094
```

### Elasticsearch
```env
SPRING_ELASTICSEARCH_URIS=http://localhost:9200
```

## 📝 License

This project is licensed under the MIT License.

## 👨‍💻 Author

**QuanTuanHuy**

---

⭐ Star this repository if you find it helpful!
