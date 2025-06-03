package huy.project.accommodation_service.core.init;

import huy.project.accommodation_service.core.domain.dto.request.CreatePriceTypeDto;
import huy.project.accommodation_service.core.service.IPriceTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PriceTypeInitializer implements CommandLineRunner {
    private final IPriceTypeService priceTypeService;

    @Override
    public void run(String... args) {
        log.info("Initializing price type data...");
        try {
            createPriceTypes();
            log.info("Price types initialization completed successfully.");
        } catch (Exception e) {
            log.error("Failed to initialize price types: {}", e.getMessage(), e);
        }
    }

    private void createPriceTypes() {
        List<CreatePriceTypeDto> priceTypes = List.of(
            // Giá tiêu chuẩn - Standard Rate
            new CreatePriceTypeDto("Standard Rate", "Giá tiêu chuẩn với chính sách hủy miễn phí"),

            // Giá không hoàn tiền - Non-refundable Rate  
            new CreatePriceTypeDto("Non-refundable Rate", "Giá ưu đãi không hoàn tiền - tiết kiệm tới 10%"),

            // Giá đặt sớm - Advance Purchase
            new CreatePriceTypeDto("Advance Purchase", "Giá ưu đãi khi đặt trước 30 ngày"),
            new CreatePriceTypeDto("Early Bird Rate", "Giá chim non cho khách đặt sớm"),
            
            // Giá phút chót - Last Minute
            new CreatePriceTypeDto("Last Minute Deal", "Giá ưu đãi đặt phút chót trong 7 ngày"),
            new CreatePriceTypeDto("Same Day Rate", "Giá đặc biệt cho đặt phòng cùng ngày"),
            
            // Giá kỳ nghỉ lễ - Holiday Rates
            new CreatePriceTypeDto("Holiday Rate", "Giá đặc biệt cho các kỳ nghỉ lễ"),
            new CreatePriceTypeDto("Peak Season Rate", "Giá cao điểm mùa du lịch"),
            new CreatePriceTypeDto("Special Event Rate", "Giá đặc biệt cho các sự kiện lớn"),
            
            // Giá cuối tuần - Weekend Rates
            new CreatePriceTypeDto("Weekend Rate", "Giá cuối tuần (thứ 7 & chủ nhật)"),
            new CreatePriceTypeDto("Friday-Sunday Rate", "Gói cuối tuần 3 ngày"),
            
            // Giá lưu trú dài hạn - Extended Stay
            new CreatePriceTypeDto("Weekly Rate", "Giá ưu đãi lưu trú 7 ngày trở lên"),
            new CreatePriceTypeDto("Monthly Rate", "Giá tháng cho lưu trú dài hạn"),
            new CreatePriceTypeDto("Extended Stay Discount", "Giá giảm cho lưu trú từ 5 ngày"),
            
            // Giá dành cho nhóm - Group Rates
            new CreatePriceTypeDto("Group Rate", "Giá ưu đãi cho nhóm từ 5 phòng trở lên"),
            new CreatePriceTypeDto("Corporate Rate", "Giá doanh nghiệp cho khách công ty"),
            new CreatePriceTypeDto("Government Rate", "Giá đặc biệt cho cán bộ nhà nước"),
            
            // Giá thành viên - Membership Rates
            new CreatePriceTypeDto("Member Rate", "Giá đặc biệt cho thành viên"),
            new CreatePriceTypeDto("VIP Rate", "Giá ưu đãi cho khách VIP"),
            new CreatePriceTypeDto("Loyalty Program Rate", "Giá thành viên chương trình khách hàng thân thiết"),
            
            // Giá bao gồm dịch vụ - Package Rates
            new CreatePriceTypeDto("Breakfast Included", "Giá bao gồm bữa sáng"),
            new CreatePriceTypeDto("Half Board", "Giá bao gồm bữa sáng và tối"),
            new CreatePriceTypeDto("Full Board", "Giá bao gồm 3 bữa ăn"),
            new CreatePriceTypeDto("All Inclusive", "Giá trọn gói bao gồm tất cả"),
            
            // Giá khuyến mãi - Promotional Rates
            new CreatePriceTypeDto("Flash Sale", "Giá khuyến mãi chớp nhoáng"),
            new CreatePriceTypeDto("Happy Hour Rate", "Giá giờ vàng (giảm giá trong khung giờ nhất định)"),
            new CreatePriceTypeDto("Student Discount", "Giá ưu đãi sinh viên"),
            new CreatePriceTypeDto("Senior Citizen Rate", "Giá ưu đãi người cao tuổi"),
            
            // Giá theo độ tuổi - Age-based Rates
            new CreatePriceTypeDto("Family Rate", "Giá ưu đãi gia đình có trẻ em"),
            new CreatePriceTypeDto("Children Free", "Trẻ em miễn phí khi ở cùng bố mẹ"),
            new CreatePriceTypeDto("Young Adult Rate", "Giá ưu đãi cho người trẻ (18-25 tuổi)"),
            
            // Giá theo mục đích - Purpose-based Rates
            new CreatePriceTypeDto("Business Rate", "Giá dành cho khách công tác"),
            new CreatePriceTypeDto("Conference Rate", "Giá đặc biệt cho khách tham dự hội nghị"),
            new CreatePriceTypeDto("Wedding Rate", "Giá đặc biệt cho khách tham dự đám cưới"),
            new CreatePriceTypeDto("Honeymoon Package", "Gói tuần trăng mật"),
            
            // Giá theo kênh đặt - Channel-based Rates
            new CreatePriceTypeDto("Direct Booking Rate", "Giá ưu đãi khi đặt trực tiếp"),
            new CreatePriceTypeDto("Mobile App Rate", "Giá đặc biệt khi đặt qua ứng dụng"),
            new CreatePriceTypeDto("Website Exclusive", "Giá độc quyền trên website"),
            
            // Giá theo điều kiện đặc biệt - Special Condition Rates
            new CreatePriceTypeDto("Free Cancellation", "Giá hủy miễn phí đến 24h trước"),
            new CreatePriceTypeDto("Flexible Rate", "Giá linh hoạt có thể thay đổi"),
            new CreatePriceTypeDto("Pay at Hotel", "Giá thanh toán tại khách sạn"),
            new CreatePriceTypeDto("Prepaid Rate", "Giá trả trước không hoàn lại"),
            
            // Giá theo phương tiện di chuyển - Transportation Rates
            new CreatePriceTypeDto("Flight + Hotel", "Gói máy bay + khách sạn"),
            new CreatePriceTypeDto("Park & Stay", "Gói đỗ xe + lưu trú"),
            new CreatePriceTypeDto("Airport Transfer Included", "Giá bao gồm đưa đón sân bay"),
            
            // Giá theo hoạt động - Activity-based Rates
            new CreatePriceTypeDto("Spa Package", "Gói dịch vụ spa"),
            new CreatePriceTypeDto("Golf Package", "Gói chơi golf"),
            new CreatePriceTypeDto("Adventure Package", "Gói tour phiêu lưu"),
            new CreatePriceTypeDto("City Tour Included", "Giá bao gồm tour thành phố"),
            
            // Giá theo thời tiết/mùa - Seasonal Rates
            new CreatePriceTypeDto("Summer Special", "Giá đặc biệt mùa hè"),
            new CreatePriceTypeDto("Winter Promotion", "Khuyến mãi mùa đông"),
            new CreatePriceTypeDto("Monsoon Rate", "Giá mùa mưa"),
            new CreatePriceTypeDto("Dry Season Rate", "Giá mùa khô"),
            
            // Giá theo loại phòng - Room-type Rates
            new CreatePriceTypeDto("Ocean View Premium", "Giá cao cấp phòng hướng biển"),
            new CreatePriceTypeDto("Garden View Rate", "Giá phòng hướng vườn"),
            new CreatePriceTypeDto("City View Rate", "Giá phòng hướng thành phố"),
            new CreatePriceTypeDto("Suite Upgrade", "Giá nâng cấp lên suite"),
            
            // Giá theo độ sang trọng - Luxury Rates
            new CreatePriceTypeDto("Premium Rate", "Giá cao cấp với dịch vụ đặc biệt"),
            new CreatePriceTypeDto("Deluxe Package", "Gói sang trọng"),
            new CreatePriceTypeDto("Executive Level", "Giá tầng điều hành"),
            new CreatePriceTypeDto("Concierge Service", "Giá bao gồm dịch vụ quản gia"),
            
            // Giá đặc biệt khác - Other Special Rates
            new CreatePriceTypeDto("Mystery Rate", "Giá bí ẩn - tiết lộ sau khi đặt"),
            new CreatePriceTypeDto("Opaque Rate", "Giá không rõ tên khách sạn"),
            new CreatePriceTypeDto("Bid Rate", "Giá đấu thầu"),
            new CreatePriceTypeDto("Reverse Auction", "Giá đấu giá ngược")
        );

        priceTypeService.createIfNotExists(priceTypes);
    }
}
