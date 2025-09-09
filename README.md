# Hotel Booking Platform

A full-stack microservices hotel booking system featuring customer and admin interfaces, real-time chat, AI-powered assistance, and comprehensive property management.

## Features

- **Hotel Search** - Full-text search with filters and interactive maps
- **Real-time Booking** - Availability management with distributed locking
- **Live Chat** - WebSocket-based messaging between guests and hosts
- **Reviews & Wishlists** - Rate properties and save favorites
- **AI Chatbot** - RAG-powered assistant using Gemini
- **Admin Dashboard** - Property, booking, and review management

## Tech Stack

| Category | Technologies |
|----------|-------------|
| **Frontend** | Next.js 15, React 19, TypeScript, Tailwind CSS 4 |
| **Backend** | Spring Boot 3.5 (Java 21), Gin (Go 1.24), .NET 9 |
| **Databases** | PostgreSQL, Redis 7.2, Elasticsearch 8.16, Qdrant |
| **Messaging** | Apache Kafka 3.7 |
| **Infrastructure** | Docker, Docker Compose |

## Architecture

```
┌─────────────┐  ┌─────────────┐
│   Web UI    │  │  Admin UI   │
│  (Next.js)  │  │  (Next.js)  │
└──────┬──────┘  └──────┬──────┘
       │                │
       └───────┬────────┘
               ▼
        ┌─────────────┐
        │ API Gateway │ :8080
        │   (Spring)  │
        └──────┬──────┘
               │
    ┌──────────┼──────────┬──────────────┐
    ▼          ▼          ▼              ▼
┌───────┐ ┌────────┐ ┌────────┐    ┌──────────┐
│ Auth  │ │ Search │ │Booking │ ...│ 12 more  │
│ :8081 │ │ :8085  │ │ :8084  │    │ services │
└───┬───┘ └───┬────┘ └───┬────┘    └────┬─────┘
    │         │          │              │
    └─────────┴──────────┴──────────────┘
               │
    ┌──────────┼──────────┬─────────────┐
    ▼          ▼          ▼             ▼
┌────────┐ ┌───────┐ ┌───────┐   ┌─────────┐
│Postgres│ │ Redis │ │ Kafka │   │Elastic  │
└────────┘ └───────┘ └───────┘   └─────────┘
```

## Project Structure

```
hotel_booking/
├── web_ui/                  # Customer frontend (Next.js) :3000
├── admin_ui/                # Admin dashboard (Next.js) :3001
├── api_gateway/             # Spring Cloud Gateway :8080
├── authentication_service/  # JWT auth, OAuth2 :8081
├── accommodation_service/   # Property management :8083
├── booking_service/         # Booking management (Go) :8084
├── search_service/          # Elasticsearch search :8085
├── notification_service/    # Email via Kafka (Go) :8082
├── chat_service/            # WebSocket chat (Go) :8090
├── profile_service/         # User profiles :8086
├── promotion_service/       # Discounts (.NET) :8087
├── file_service/            # File uploads :8088
├── rating_service/          # Reviews & ratings :8089
├── location_service/        # Geography (.NET) :8091
├── payment_service/         # Payments :8092
├── inventory_service/       # Room availability :8093
├── ai_service/              # RAG chatbot :8094
└── docker-compose.yml
```

## Quick Start

### Prerequisites

- Docker & Docker Compose
- Node.js 20+ (for frontend development)
- Java 21 / Go 1.24 / .NET 9 (for backend development)

### Run with Docker

```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop all services
docker-compose down
```

### Frontend Development

```bash
# Customer UI (http://localhost:3000)
cd web_ui && npm install && npm run dev

# Admin UI (http://localhost:3001)
cd admin_ui && npm install && npm run dev
```

### Backend Development

```bash
# Java services
cd <service_name> && ./mvnw spring-boot:run

# Go services
cd <service_name>/src && go run main.go

# .NET services
cd promotion_service && dotnet run --project PromotionService.Api
```

## API Overview

All requests route through the API Gateway at `http://localhost:8080`.

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/authentication_service/api/public/v1/auth/login` | POST | User login |
| `/authentication_service/api/public/v1/auth/register` | POST | User registration |
| `/accommodation_service/api/public/v1/accommodations` | GET | List properties |
| `/search_service/api/public/v1/search` | GET | Search hotels |
| `/booking_service/api/public/v1/bookings` | POST | Create booking |
| `/rating_service/api/public/v1/ratings` | GET/POST | Reviews |

## License

MIT
