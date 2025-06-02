package huy.project.accommodation_service.core.init;

import huy.project.accommodation_service.core.domain.dto.request.CreateUnitNameDto;
import huy.project.accommodation_service.core.service.IUnitNameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class UnitNameInitializer implements CommandLineRunner {
    private final IUnitNameService unitNameService;

    @Override
    public void run(String... args) {
        log.info("Initializing unit names...");
        try {
            createUnitNames();
            log.info("Unit names initialization completed successfully.");
        } catch (Exception e) {
            log.error("Failed to initialize unit names: {}", e.getMessage(), e);
        }
    }

    private void createUnitNames() {
        List<CreateUnitNameDto> unitNames = List.of(
            // Phòng khách sạn tiêu chuẩn
            new CreateUnitNameDto("Phòng Standard", "Phòng tiêu chuẩn với tiện nghi cơ bản"),
            new CreateUnitNameDto("Phòng Superior", "Phòng cao cấp hơn với không gian rộng rãi"),
            new CreateUnitNameDto("Phòng Deluxe", "Phòng sang trọng với tiện nghi hiện đại"),
            new CreateUnitNameDto("Phòng Executive", "Phòng dành cho doanh nhân với khu vực làm việc"),
            new CreateUnitNameDto("Phòng Premium", "Phòng cao cấp với dịch vụ đặc biệt"),
            new CreateUnitNameDto("Phòng Luxury", "Phòng hạng sang với thiết kế tinh tế"),
            
            // Phòng theo loại giường
            new CreateUnitNameDto("Phòng Giường Đôi", "Phòng với giường đôi cho 2 người"),
            new CreateUnitNameDto("Phòng 2 Giường Đơn", "Phòng với 2 giường đơn riêng biệt"),
            new CreateUnitNameDto("Phòng Giường Đôi/2 Giường Đơn", "Phòng linh hoạt với tùy chọn giường đôi hoặc 2 giường đơn"),
            new CreateUnitNameDto("Phòng Giường King", "Phòng với giường King size cao cấp"),
            new CreateUnitNameDto("Phòng Giường Queen", "Phòng với giường Queen size thoải mái"),
            new CreateUnitNameDto("Phòng 3 Giường Đơn", "Phòng với 3 giường đơn cho nhóm"),
            new CreateUnitNameDto("Phòng Giường Tầng", "Phòng với giường tầng tiết kiệm không gian"),
            
            // Phòng theo hướng nhìn
            new CreateUnitNameDto("Phòng Nhìn Ra Thành Phố", "Phòng có tầm nhìn ra quang cảnh thành phố"),
            new CreateUnitNameDto("Phòng Nhìn Ra Biển", "Phòng có tầm nhìn tuyệt đẹp ra biển"),
            new CreateUnitNameDto("Phòng Nhìn Ra Vườn", "Phòng có tầm nhìn xanh mát ra khu vườn"),
            new CreateUnitNameDto("Phòng Nhìn Ra Núi", "Phòng có tầm nhìn hùng vĩ ra núi rừng"),
            new CreateUnitNameDto("Phòng Nhìn Ra Hồ", "Phòng có tầm nhìn thơ mộng ra hồ nước"),
            new CreateUnitNameDto("Phòng Nhìn Ra Sông", "Phòng có tầm nhìn dịu dàng ra dòng sông"),
            new CreateUnitNameDto("Phòng Nhìn Ra Bể Bơi", "Phòng có tầm nhìn ra khu vực bể bơi"),
            new CreateUnitNameDto("Phòng Nhìn Ra Sân Golf", "Phòng có tầm nhìn ra sân golf xanh mướt"),
            
            // Phòng kết hợp loại giường và hướng nhìn
            new CreateUnitNameDto("Phòng Superior Giường Đôi/2 Giường Đơn", "Phòng Superior với tùy chọn giường linh hoạt"),
            new CreateUnitNameDto("Phòng Deluxe Giường Đôi/2 Giường Đơn", "Phòng Deluxe với tùy chọn giường đa dạng"),
            new CreateUnitNameDto("Phòng Superior Nhìn Ra Thành Phố", "Phòng Superior với tầm nhìn thành phố"),
            new CreateUnitNameDto("Phòng Deluxe Nhìn Ra Thành Phố", "Phòng Deluxe với tầm nhìn thành phố tuyệt đẹp"),
            new CreateUnitNameDto("Phòng Deluxe Giường Đôi/2 Giường Đơn Nhìn Ra Thành Phố", "Phòng Deluxe với giường linh hoạt và tầm nhìn thành phố"),
            new CreateUnitNameDto("Phòng Superior Nhìn Ra Biển", "Phòng Superior với tầm nhìn biển cả"),
            new CreateUnitNameDto("Phòng Deluxe Nhìn Ra Biển", "Phòng Deluxe với tầm nhìn biển tuyệt vời"),
            new CreateUnitNameDto("Phòng Premium Nhìn Ra Biển", "Phòng Premium với tầm nhìn biển đẹp nhất"),
            new CreateUnitNameDto("Phòng Deluxe Giường King Nhìn Ra Biển", "Phòng Deluxe với giường King và tầm nhìn biển"),
            new CreateUnitNameDto("Phòng Superior Giường Queen Nhìn Ra Vườn", "Phòng Superior với giường Queen và tầm nhìn vườn"),
            
            // Suite và phòng đặc biệt
            new CreateUnitNameDto("Junior Suite", "Suite nhỏ với không gian sinh hoạt riêng"),
            new CreateUnitNameDto("Executive Suite", "Suite dành cho doanh nhân với phòng họp"),
            new CreateUnitNameDto("Presidential Suite", "Suite tổng thống cao cấp nhất"),
            new CreateUnitNameDto("Family Suite", "Suite gia đình rộng rãi cho cả gia đình"),
            new CreateUnitNameDto("Honeymoon Suite", "Suite tuần trăng mật lãng mạn"),
            new CreateUnitNameDto("Penthouse Suite", "Suite penthouse sang trọng trên tầng cao"),
            new CreateUnitNameDto("Royal Suite", "Suite hoàng gia với dịch vụ VIP"),
            new CreateUnitNameDto("Diplomatic Suite", "Suite ngoại giao cho khách VIP"),
            
            // Phòng gia đình
            new CreateUnitNameDto("Phòng Gia Đình", "Phòng rộng rãi dành cho gia đình có trẻ em"),
            new CreateUnitNameDto("Phòng Gia Đình Nhìn Ra Biển", "Phòng gia đình với tầm nhìn biển"),
            new CreateUnitNameDto("Phòng Gia Đình 2 Phòng Ngủ", "Phòng gia đình với 2 phòng ngủ riêng biệt"),
            new CreateUnitNameDto("Phòng Kết Nối", "2 phòng liền kề có thể kết nối với nhau"),
            new CreateUnitNameDto("Phòng Triple", "Phòng cho 3 người với 3 giường đơn"),
            new CreateUnitNameDto("Phòng Quad", "Phòng cho 4 người với nhiều giường"),
            
            // Phòng đặc biệt
            new CreateUnitNameDto("Phòng Accessible", "Phòng tiện nghi cho người khuyết tật"),
            new CreateUnitNameDto("Phòng Không Hút Thuốc", "Phòng dành riêng cho khách không hút thuốc"),
            new CreateUnitNameDto("Phòng Hút Thuốc", "Phòng cho phép hút thuốc"),
            new CreateUnitNameDto("Phòng Tĩnh Lặng", "Phòng cách âm trong khu vực yên tĩnh"),
            new CreateUnitNameDto("Phòng Business", "Phòng dành cho khách doanh nhân"),
            new CreateUnitNameDto("Phòng VIP", "Phòng VIP với dịch vụ đặc biệt"),
            
            // Phòng theo tầng/vị trí
            new CreateUnitNameDto("Phòng Club Floor", "Phòng tại tầng club với dịch vụ riêng"),
            new CreateUnitNameDto("Phòng Tầng Cao", "Phòng tại tầng cao với tầm nhìn tốt"),
            new CreateUnitNameDto("Phòng Góc", "Phòng góc với nhiều cửa sổ và ánh sáng"),
            new CreateUnitNameDto("Phòng Ban Công", "Phòng có ban công riêng"),
            new CreateUnitNameDto("Phòng Terrace", "Phòng có sân thượng riêng"),
            new CreateUnitNameDto("Phòng Studio", "Phòng studio với khu vực bếp nhỏ"),
            
            // Apartment/Căn hộ
            new CreateUnitNameDto("Căn Hộ 1 Phòng Ngủ", "Căn hộ đầy đủ tiện nghi với 1 phòng ngủ"),
            new CreateUnitNameDto("Căn Hộ 2 Phòng Ngủ", "Căn hộ rộng rãi với 2 phòng ngủ"),
            new CreateUnitNameDto("Căn Hộ 3 Phòng Ngủ", "Căn hộ lớn với 3 phòng ngủ"),
            new CreateUnitNameDto("Căn Hộ Duplex", "Căn hộ 2 tầng với thiết kế hiện đại"),
            new CreateUnitNameDto("Căn Hộ Penthouse", "Căn hộ penthouse sang trọng"),
            
            // Villa/Biệt thự
            new CreateUnitNameDto("Villa 2 Phòng Ngủ", "Biệt thự riêng tư với 2 phòng ngủ"),
            new CreateUnitNameDto("Villa 3 Phòng Ngủ", "Biệt thự rộng lớn với 3 phòng ngủ"),
            new CreateUnitNameDto("Villa 4 Phòng Ngủ", "Biệt thự sang trọng với 4 phòng ngủ"),
            new CreateUnitNameDto("Villa Có Bể Bơi Riêng", "Biệt thự với bể bơi riêng"),
            new CreateUnitNameDto("Villa Hướng Biển", "Biệt thự với tầm nhìn trực diện ra biển"),
            new CreateUnitNameDto("Villa Sân Vườn", "Biệt thự với khu vườn riêng"),
            
            // Phòng hostel/dorm
            new CreateUnitNameDto("Giường Trong Phòng Dorm Nam", "Giường trong phòng dormitory nam"),
            new CreateUnitNameDto("Giường Trong Phòng Dorm Nữ", "Giường trong phòng dormitory nữ"),
            new CreateUnitNameDto("Giường Trong Phòng Dorm Hỗn Hợp", "Giường trong phòng dormitory hỗn hợp"),
            new CreateUnitNameDto("Phòng Dorm Riêng Tư", "Phòng dormitory riêng tư"),
            new CreateUnitNameDto("Phòng Capsule", "Phòng capsule hiện đại tiết kiệm không gian"),
            
            // Phòng đặc thù
            new CreateUnitNameDto("Phòng Spa", "Phòng tích hợp dịch vụ spa"),
            new CreateUnitNameDto("Phòng Yoga", "Phòng với không gian yoga riêng"),
            new CreateUnitNameDto("Phòng Conference", "Phòng với khu vực họp riêng"),
            new CreateUnitNameDto("Phòng Karaoke", "Phòng tích hợp hệ thống karaoke"),
            new CreateUnitNameDto("Phòng Game", "Phòng với khu vực giải trí game"),
            
            // Phòng theo chủ đề
            new CreateUnitNameDto("Phòng Theo Chủ Đề Romantic", "Phòng trang trí theo chủ đề lãng mạn"),
            new CreateUnitNameDto("Phòng Theo Chủ Đề Modern", "Phòng thiết kế hiện đại"),
            new CreateUnitNameDto("Phòng Theo Chủ Đề Classic", "Phòng thiết kế cổ điển"),
            new CreateUnitNameDto("Phòng Theo Chủ Đề Minimalist", "Phòng thiết kế tối giản"),
            new CreateUnitNameDto("Phòng Theo Chủ Đề Tropical", "Phòng thiết kế nhiệt đới"),
            
            // Phòng resort
            new CreateUnitNameDto("Bungalow", "Nhà gỗ độc lập trong khu resort"),
            new CreateUnitNameDto("Beach Villa", "Villa ven biển riêng tư"),
            new CreateUnitNameDto("Garden Villa", "Villa giữa khu vườn xanh mát"),
            new CreateUnitNameDto("Pool Villa", "Villa có bể bơi riêng"),
            new CreateUnitNameDto("Tree House", "Nhà trên cây độc đáo"),
            new CreateUnitNameDto("Overwater Bungalow", "Bungalow trên mặt nước"),
            new CreateUnitNameDto("Cliff Villa", "Villa trên vách đá với tầm nhìn tuyệt đẹp")
        );

        unitNameService.createIfNotExists(unitNames);
    }
}
