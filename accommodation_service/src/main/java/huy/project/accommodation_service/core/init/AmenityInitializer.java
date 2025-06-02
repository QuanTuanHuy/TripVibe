package huy.project.accommodation_service.core.init;

import huy.project.accommodation_service.core.domain.constant.AmenityGroupType;
import huy.project.accommodation_service.core.domain.dto.request.CreateAmenityGroupRequestDto;
import huy.project.accommodation_service.core.domain.dto.request.CreateAmenityRequestDto;
import huy.project.accommodation_service.core.service.IAmenityGroupService;
import huy.project.accommodation_service.core.service.IAmenityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AmenityInitializer implements CommandLineRunner {
    private final IAmenityGroupService amenityGroupService;
    private final IAmenityService amenityService;

    @Override
    public void run(String... args) {
        log.info("Initializing amenity groups and amenities...");
        
        // Create amenity groups first
        List<CreateAmenityGroupRequestDto> amenityGroups = createAmenityGroups();
        amenityGroupService.createIfNotExists(amenityGroups);
        
        // Create amenities for each group
        List<CreateAmenityRequestDto> amenities = createAmenities();
        amenityService.createIfNotExists(amenities);
        
        log.info("Amenity groups and amenities initialization completed.");
    }

    private List<CreateAmenityGroupRequestDto> createAmenityGroups() {
        List<CreateAmenityGroupRequestDto> groups = new ArrayList<>();

        // 1. Popular Amenities - Most requested by guests
        groups.add(createAmenityGroup("Tiện nghi phổ biến", AmenityGroupType.COMMON.getType(), 
            "Các tiện nghi được yêu cầu nhiều nhất", "🌟", 1, true));

        // 2. Property Facilities - Building/property level amenities
        groups.add(createAmenityGroup("Tiện nghi khu vực", AmenityGroupType.PROPERTY.getType(), 
            "Tiện nghi của toàn bộ khu vực lưu trú", "🏢", 2, false));

        // 3. Unit/Room Amenities - Room specific amenities
        groups.add(createAmenityGroup("Tiện nghi phòng", AmenityGroupType.UNIT.getType(), 
            "Tiện nghi có sẵn trong phòng", "🏠", 3, false));

        // 4. Bathroom Amenities
        groups.add(createAmenityGroup("Tiện nghi phòng tắm", AmenityGroupType.Bathroom.getType(), 
            "Tiện nghi trong phòng tắm", "🚿", 4, false));

        // 5. Kitchen Amenities
        groups.add(createAmenityGroup("Tiện nghi bếp", AmenityGroupType.KITCHEN.getType(), 
            "Tiện nghi nhà bếp và dụng cụ nấu ăn", "🍳", 5, false));

        // 6. Living Room Amenities
        groups.add(createAmenityGroup("Tiện nghi phòng khách", AmenityGroupType.LIVING_ROOM.getType(), 
            "Tiện nghi trong khu vực sinh hoạt chung", "🛋️", 6, false));

        // 7. Service Amenities
        groups.add(createAmenityGroup("Dịch vụ", AmenityGroupType.SERVICE.getType(), 
            "Các dịch vụ hỗ trợ khách hàng", "🛎️", 7, false));

        // 8. Parking Amenities
        groups.add(createAmenityGroup("Tiện nghi đậu xe", AmenityGroupType.PARKING.getType(), 
            "Các tiện nghi về chỗ đậu xe", "🚗", 8, false));

        // 9. Pool & Wellness
        groups.add(createAmenityGroup("Hồ bơi & Chăm sóc sức khỏe", AmenityGroupType.POOL.getType(), 
            "Tiện nghi hồ bơi và chăm sóc sức khỏe", "🏊", 9, false));

        // 10. Gym & Fitness
        groups.add(createAmenityGroup("Gym & Thể thao", AmenityGroupType.GYM.getType(), 
            "Tiện nghi tập gym và thể thao", "💪", 10, false));

        // 11. Business Facilities
        groups.add(createAmenityGroup("Tiện nghi kinh doanh", AmenityGroupType.FACILITY.getType(), 
            "Tiện nghi phục vụ công việc", "💼", 11, false));

        // 12. Accessibility
        groups.add(createAmenityGroup("Tiện nghi người khuyết tật", AmenityGroupType.ACCOMMODATION.getType(), 
            "Tiện nghi hỗ trợ người khuyết tật", "♿", 12, false));

        return groups;
    }

    private CreateAmenityGroupRequestDto createAmenityGroup(String name, String type, String description, 
                                                          String icon, Integer displayOrder, Boolean isPopular) {
        return new CreateAmenityGroupRequestDto(name, type, description, icon, displayOrder, isPopular);
    }

    private List<CreateAmenityRequestDto> createAmenities() {
        List<CreateAmenityRequestDto> amenities = new ArrayList<>();

        // Group 1: Popular Amenities (groupId: 1)
        amenities.add(createAmenity("WiFi miễn phí", "📶", "Internet không dây tốc độ cao", 
            false, "24/7", true, true, 1L));
        amenities.add(createAmenity("Điều hòa không khí", "❄️", "Hệ thống điều hòa nhiệt độ", 
            false, "24/7", true, true, 1L));
        amenities.add(createAmenity("Bãi đậu xe miễn phí", "🚗", "Chỗ đậu xe không mất phí", 
            false, "24/7", true, true, 1L));
        amenities.add(createAmenity("Dịch vụ phòng", "🛎️", "Phục vụ đồ ăn và nước uống tại phòng", 
            true, "6:00-22:00", true, true, 1L));
        amenities.add(createAmenity("Bữa sáng", "🥐", "Bữa sáng buffet hoặc theo thực đơn", 
            true, "6:00-10:00", true, true, 1L));
        amenities.add(createAmenity("Hồ bơi", "🏊", "Hồ bơi ngoài trời hoặc trong nhà", 
            false, "6:00-22:00", true, true, 1L));
        amenities.add(createAmenity("Thang máy", "🛗", "Thang máy phục vụ các tầng", 
            false, "24/7", false, true, 1L));

        // Group 2: Property Facilities (groupId: 2)
        amenities.add(createAmenity("Lễ tân 24/7", "🏨", "Dịch vụ lễ tân 24 giờ", 
            false, "24/7", false, true, 2L));
        amenities.add(createAmenity("Bảo vệ 24/7", "🔒", "Dịch vụ bảo vệ suốt ngày đêm", 
            false, "24/7", false, true, 2L));
        amenities.add(createAmenity("Khu vườn", "🌳", "Khu vườn cảnh quan đẹp", 
            false, "6:00-22:00", false, false, 2L));
        amenities.add(createAmenity("Sân thượng", "🏢", "Sân thượng với tầm nhìn đẹp", 
            false, "6:00-22:00", false, false, 2L));
        amenities.add(createAmenity("Quầy bar", "🍸", "Quầy bar phục vụ đồ uống", 
            true, "17:00-02:00", false, true, 2L));
        amenities.add(createAmenity("Nhà hàng", "🍽️", "Nhà hàng tại chỗ", 
            true, "6:00-22:00", false, true, 2L));
        amenities.add(createAmenity("Cửa hàng tiện lợi", "🏪", "Cửa hàng mua sắm tại chỗ", 
            false, "6:00-22:00", false, false, 2L));
        amenities.add(createAmenity("ATM", "🏧", "Máy rút tiền tự động", 
            false, "24/7", false, false, 2L));
        amenities.add(createAmenity("Khu vui chơi trẻ em", "🎠", "Khu vui chơi an toàn cho trẻ", 
            false, "8:00-20:00", false, true, 2L));

        // Group 3: Unit/Room Amenities (groupId: 3)
        amenities.add(createAmenity("TV màn hình phẳng", "📺", "Tivi LCD/LED", 
            false, "24/7", false, true, 3L));
        amenities.add(createAmenity("Tủ lạnh mini", "🧊", "Tủ lạnh mini trong phòng", 
            false, "24/7", false, true, 3L));
        amenities.add(createAmenity("Két sắt", "🔐", "Két an toàn trong phòng", 
            false, "24/7", false, true, 3L));
        amenities.add(createAmenity("Bàn làm việc", "💻", "Bàn và ghế làm việc", 
            false, "24/7", false, false, 3L));
        amenities.add(createAmenity("Sofa", "🛋️", "Ghế sofa thoải mái", 
            false, "24/7", false, false, 3L));
        amenities.add(createAmenity("Ban công", "🌅", "Ban công với tầm nhìn đẹp", 
            false, "24/7", false, true, 3L));
        amenities.add(createAmenity("Máy pha cà phê", "☕", "Máy pha cà phê trong phòng", 
            false, "24/7", false, false, 3L));
        amenities.add(createAmenity("Ấm đun nước", "🫖", "Ấm đun nước điện", 
            false, "24/7", false, false, 3L));
        amenities.add(createAmenity("Máy sấy tóc", "💨", "Máy sấy tóc chuyên dụng", 
            false, "24/7", false, false, 3L));
        amenities.add(createAmenity("Dép đi trong phòng", "🥿", "Dép thoải mái", 
            false, "24/7", false, false, 3L));
        amenities.add(createAmenity("Áo choàng tắm", "🥼", "Áo choàng tắm cao cấp", 
            false, "24/7", false, false, 3L));

        // Group 4: Bathroom Amenities (groupId: 4)
        amenities.add(createAmenity("Bồn tắm", "🛁", "Bồn tắm riêng", 
            false, "24/7", false, true, 4L));
        amenities.add(createAmenity("Vòi sen", "🚿", "Vòi sen tắm đứng", 
            false, "24/7", false, true, 4L));
        amenities.add(createAmenity("Đồ dùng nhà tắm miễn phí", "🧴", "Dầu gội, sữa tắm, xà phòng", 
            false, "24/7", false, true, 4L));
        amenities.add(createAmenity("Khăn tắm", "🏖️", "Khăn tắm sạch sẽ", 
            false, "24/7", false, false, 4L));
        amenities.add(createAmenity("Gương lớn", "🪞", "Gương lớn trong nhà tắm", 
            false, "24/7", false, false, 4L));
        amenities.add(createAmenity("Toilet riêng", "🚽", "Nhà vệ sinh riêng", 
            false, "24/7", false, true, 4L));
        amenities.add(createAmenity("Giấy vệ sinh", "🧻", "Giấy vệ sinh chất lượng", 
            false, "24/7", false, false, 4L));

        // Group 5: Kitchen Amenities (groupId: 5)
        amenities.add(createAmenity("Bếp đầy đủ", "🍳", "Bếp gas/điện với đầy đủ dụng cụ", 
            false, "24/7", false, true, 5L));
        amenities.add(createAmenity("Tủ lạnh", "❄️", "Tủ lạnh full size", 
            false, "24/7", false, true, 5L));
        amenities.add(createAmenity("Lò vi sóng", "📱", "Lò vi sóng tiện lợi", 
            false, "24/7", false, false, 5L));
        amenities.add(createAmenity("Máy rửa bát", "🍽️", "Máy rửa chén bát tự động", 
            false, "24/7", false, false, 5L));
        amenities.add(createAmenity("Máy pha cà phê", "☕", "Máy pha cà phê cao cấp", 
            false, "24/7", false, false, 5L));
        amenities.add(createAmenity("Nồi cơm điện", "🍚", "Nồi cơm điện tiện dụng", 
            false, "24/7", false, false, 5L));
        amenities.add(createAmenity("Bộ dao kéo nhà bếp", "🔪", "Dụng cụ nấu ăn đầy đủ", 
            false, "24/7", false, false, 5L));
        amenities.add(createAmenity("Bàn ăn", "🪑", "Bàn ghế ăn thoải mái", 
            false, "24/7", false, false, 5L));

        // Group 6: Living Room Amenities (groupId: 6)
        amenities.add(createAmenity("TV thông minh", "📱", "Smart TV với Netflix, YouTube", 
            false, "24/7", false, true, 6L));
        amenities.add(createAmenity("Hệ thống âm thanh", "🔊", "Loa bluetooth/wifi", 
            false, "24/7", false, false, 6L));
        amenities.add(createAmenity("Bàn trà", "☕", "Bàn trà trung tâm", 
            false, "24/7", false, false, 6L));
        amenities.add(createAmenity("Thảm trải sàn", "🧶", "Thảm trang trí", 
            false, "24/7", false, false, 6L));
        amenities.add(createAmenity("Đèn đọc sách", "💡", "Đèn chiếu sáng tốt", 
            false, "24/7", false, false, 6L));

        // Group 7: Services (groupId: 7)
        amenities.add(createAmenity("Dịch vụ giặt ủi", "👔", "Giặt ủi quần áo", 
            true, "8:00-17:00", false, true, 7L));
        amenities.add(createAmenity("Đưa đón sân bay", "✈️", "Dịch vụ đưa đón sân bay", 
            true, "24/7", false, true, 7L));
        amenities.add(createAmenity("Thuê xe đạp", "🚲", "Cho thuê xe đạp", 
            true, "8:00-18:00", false, false, 7L));
        amenities.add(createAmenity("Massage", "💆", "Dịch vụ massage thư giãn", 
            true, "9:00-21:00", false, false, 7L));
        amenities.add(createAmenity("Tour du lịch", "🗺️", "Tổ chức tour tham quan", 
            true, "8:00-18:00", false, false, 7L));
        amenities.add(createAmenity("Đặt vé", "🎫", "Hỗ trợ đặt vé máy bay, xe", 
            true, "8:00-17:00", false, false, 7L));
        amenities.add(createAmenity("Dịch vụ fax/photocopy", "📠", "Fax và photocopy", 
            true, "8:00-17:00", false, false, 7L));

        // Group 8: Parking Amenities (groupId: 8)
        amenities.add(createAmenity("Bãi đậu xe có mái che", "🏠", "Chỗ đậu xe có mái che", 
            false, "24/7", false, true, 8L));
        amenities.add(createAmenity("Bãi đậu xe trong nhà", "🏢", "Parking ngầm", 
            false, "24/7", false, true, 8L));
        amenities.add(createAmenity("Valet parking", "🚗", "Dịch vụ đậu xe hộ", 
            true, "6:00-22:00", false, false, 8L));
        amenities.add(createAmenity("Trạm sạc xe điện", "🔌", "Trạm sạc cho xe điện", 
            false, "24/7", false, false, 8L));

        // Group 9: Pool & Wellness (groupId: 9)
        amenities.add(createAmenity("Hồ bơi ngoài trời", "🌞", "Hồ bơi ngoài trời với tầm nhìn đẹp", 
            false, "6:00-22:00", false, true, 9L));
        amenities.add(createAmenity("Hồ bơi trong nhà", "🏊", "Hồ bơi trong nhà", 
            false, "6:00-22:00", false, true, 9L));
        amenities.add(createAmenity("Jacuzzi", "🛁", "Bồn tắm jacuzzi", 
            false, "6:00-22:00", false, false, 9L));
        amenities.add(createAmenity("Sauna", "🧖", "Phòng xông hơi khô", 
            false, "6:00-22:00", false, false, 9L));
        amenities.add(createAmenity("Spa", "💆‍♀️", "Dịch vụ spa cao cấp", 
            true, "9:00-21:00", false, false, 9L));
        amenities.add(createAmenity("Hồ bơi trẻ em", "👶", "Hồ bơi an toàn cho trẻ em", 
            false, "8:00-18:00", false, false, 9L));

        // Group 10: Gym & Fitness (groupId: 10)
        amenities.add(createAmenity("Phòng gym", "💪", "Phòng tập gym hiện đại", 
            false, "5:00-23:00", false, true, 10L));
        amenities.add(createAmenity("Sân tennis", "🎾", "Sân tennis chất lượng cao", 
            false, "6:00-22:00", false, false, 10L));
        amenities.add(createAmenity("Sân bóng đá mini", "⚽", "Sân bóng đá 5 người", 
            false, "6:00-22:00", false, false, 10L));
        amenities.add(createAmenity("Yoga class", "🧘", "Lớp học yoga", 
            true, "6:00-8:00, 18:00-20:00", false, false, 10L));
        amenities.add(createAmenity("Sân cầu lông", "🏸", "Sân cầu lông trong nhà", 
            false, "6:00-22:00", false, false, 10L));

        // Group 11: Business Facilities (groupId: 11)
        amenities.add(createAmenity("Phòng họp", "🏢", "Phòng họp hiện đại", 
            true, "8:00-18:00", false, true, 11L));
        amenities.add(createAmenity("Trung tâm kinh doanh", "💼", "Khu vực làm việc chung", 
            false, "24/7", false, true, 11L));
        amenities.add(createAmenity("Máy in/scan", "🖨️", "Dịch vụ in ấn", 
            true, "8:00-17:00", false, false, 11L));
        amenities.add(createAmenity("Phòng hội nghị", "👥", "Phòng hội nghị lớn", 
            true, "8:00-18:00", false, false, 11L));
        amenities.add(createAmenity("Coworking space", "💻", "Không gian làm việc chung", 
            false, "24/7", false, false, 11L));

        // Group 12: Accessibility (groupId: 12)
        amenities.add(createAmenity("Thang máy cho người khuyết tật", "♿", "Thang máy có hỗ trợ", 
            false, "24/7", false, true, 12L));
        amenities.add(createAmenity("Phòng tắm cho người khuyết tật", "🚿", "Phòng tắm được thiết kế đặc biệt", 
            false, "24/7", false, true, 12L));
        amenities.add(createAmenity("Đường dành cho xe lăn", "🦽", "Đường đi thuận tiện cho xe lăn", 
            false, "24/7", false, true, 12L));
        amenities.add(createAmenity("Hỗ trợ thị giác", "👁️", "Hỗ trợ cho người khiếm thị", 
            false, "24/7", false, false, 12L));
        amenities.add(createAmenity("Hỗ trợ thính giác", "👂", "Hỗ trợ cho người khiếm thính", 
            false, "24/7", false, false, 12L));

        return amenities;
    }

    private CreateAmenityRequestDto createAmenity(String name, String icon, String description, 
                                                Boolean isPaid, String availableTime, 
                                                Boolean isHighlighted, Boolean isFilterable, Long groupId) {
        return new CreateAmenityRequestDto(
                name, icon, description, isPaid, availableTime, isHighlighted, isFilterable, groupId);
    }
}
