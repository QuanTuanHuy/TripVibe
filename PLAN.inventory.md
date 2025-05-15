# Inventory Service - Detailed Implementation Plan

## Tổng quan

Inventory Service sẽ là một thành phần quan trọng trong hệ thống đặt phòng, chịu trách nhiệm quản lý thông tin về tình trạng sẵn có của phòng và giá cả theo thời gian thực. Service này sẽ hoạt động như một nguồn dữ liệu đáng tin cậy duy nhất cho việc kiểm tra tình trạng phòng trống và giá cả.

## Chức năng chính

### 1. Quản lý tình trạng phòng trống

- **Cơ chế Lock phòng**:
  - Lock phòng tạm thời trong quá trình người dùng thực hiện đặt phòng (15-30 phút)
  - Giải phóng lock khi hết thời gian hoặc khi booking hoàn tất/hủy
  - Theo dõi trạng thái phòng: Available, Temporarily Locked, Booked

- **Cập nhật thời gian thực**:
  - Cập nhật tình trạng phòng ngay khi có booking mới
  - Xử lý hủy đặt phòng và cập nhật trạng thái phòng

- **Lịch phòng trống**:
  - Quản lý lịch phòng trống theo ngày
  - Theo dõi check-in/check-out
  - Hỗ trợ scheduling cho dọn phòng và bảo trì

### 2. Đồng bộ kênh (Channel Management)

- **Tích hợp với các OTA**:
  - Đồng bộ hóa tình trạng phòng với Booking.com, Agoda, Airbnb, Expedia
  - Cập nhật thông tin hai chiều thông qua API
  - Xử lý conflict và race condition

- **Channel Manager API**:
  - Cung cấp API cho phép đồng bộ với các channel manager bên thứ ba
  - Webhook để nhận thông báo khi có thay đổi từ các kênh bên ngoài
  - Cơ chế queuing và retry khi gặp lỗi đồng bộ

### 3. Quản lý giá động (Dynamic Pricing)

- **Chiến lược giá**:
  - Thiết lập giá cơ bản theo loại phòng
  - Thiết lập giá theo mùa (cao điểm, thấp điểm)
  - Giá đặc biệt cho ngày lễ, sự kiện

- **Điều chỉnh giá tự động**:
  - Tăng/giảm giá dựa trên tỷ lệ lấp đầy (occupancy rate)
  - Điều chỉnh giá dựa trên đối thủ cạnh tranh
  - AI dự đoán nhu cầu và đề xuất giá tối ưu

- **Quản lý khuyến mãi**:
  - Hỗ trợ các loại giảm giá (phần trăm, số tiền cố định)
  - Ưu đãi đặt sớm (early booking) hoặc đặt phút chót (last minute)
  - Đặt lâu dài (long stay discount)

### 4. Báo cáo và Phân tích

- **Dashboard cho chủ khách sạn**:
  - Hiển thị tỷ lệ lấp đầy (occupancy rate) theo thời gian
  - So sánh hiệu suất giữa các kênh đặt phòng
  - Phân tích xu hướng giá và đặt phòng

- **Dự báo**:
  - Dự báo nhu cầu đặt phòng dựa trên dữ liệu lịch sử
  - Gợi ý điều chỉnh giá để tối ưu doanh thu
  - Cảnh báo khi có biến động bất thường

### 5. API và Tích hợp

- **Public API**:
  - Kiểm tra tình trạng phòng trống
  - Truy vấn giá theo ngày
  - Tạo/hủy booking tạm thời (booking hold)

- **Internal API**:
  - Tương tác với Booking Service
  - Tích hợp với Payment Service cho việc thanh toán đặt cọc
  - Tích hợp với Analytics Service cho việc phân tích dữ liệu

## Công nghệ đề xuất

- **Ngôn ngữ**: Go hoặc Java Spring Boot
- **Database**: 
  - PostgreSQL cho dữ liệu cơ bản
  - Redis cho cache và lock management
- **Message Queue**: Kafka hoặc RabbitMQ cho xử lý bất đồng bộ
- **Monitoring**: Prometheus + Grafana

## Lộ trình triển khai

1. **Giai đoạn 1**: Xây dựng core service với các chức năng quản lý cơ bản về tình trạng phòng
2. **Giai đoạn 2**: Tích hợp với Booking Service và Payment Service
3. **Giai đoạn 3**: Triển khai dynamic pricing và reporting
4. **Giai đoạn 4**: Tích hợp với các OTA và channel managers
5. **Giai đoạn 5**: Triển khai AI cho dynamic pricing và dự báo
