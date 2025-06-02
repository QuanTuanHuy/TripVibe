package huy.project.accommodation_service.core.init;

import huy.project.accommodation_service.core.domain.dto.request.CreateAccommodationTypeDto;
import huy.project.accommodation_service.core.service.IAccommodationTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccommodationTypeInitializer implements CommandLineRunner {
    private final IAccommodationTypeService accommodationTypeService;

    @Override
    public void run(String... args) {
        log.info("Initializing accommodation types...");

        List<CreateAccommodationTypeDto> defaultAccommodationTypes = List.of(
                createAccommodationType("Nhà & căn hộ nguyên căn", "Toàn bộ ngôi nhà hoặc căn hộ cho riêng bạn",
                        "https://images.unsplash.com/photo-1560448204-e02f11c3d0e2",
                        "🏠", true),

                createAccommodationType("Căn hộ", "Căn hộ hiện đại với đầy đủ tiện nghi",
                        "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267",
                        "🏢", true),

                createAccommodationType("Khách sạn", "Khách sạn truyền thống với dịch vụ chuyên nghiệp",
                        "https://images.unsplash.com/photo-1566073771259-6a8506099945",
                        "🏨", true),

                createAccommodationType("Chỗ nghỉ nhà dân", "Trải nghiệm sống cùng gia đình địa phương",
                        "https://images.unsplash.com/photo-1564013799919-ab600027ffc6",
                        "🏘️", true),

                createAccommodationType("Nhà khách", "Chỗ nghỉ ấm cúng được điều hành bởi chủ nhà",
                        "https://images.unsplash.com/photo-1578683010236-d716f9a3f461",
                        "🏡", false),

                createAccommodationType("Nhà trọ", "Lựa chọn tiết kiệm cho khách du lịch bụi",
                        "https://images.unsplash.com/photo-1555854877-bab0e460b1e1",
                        "🛏️", false),

                createAccommodationType("Biệt thự", "Biệt thự riêng tư với không gian rộng rãi",
                        "https://images.unsplash.com/photo-1613490493576-7fde63acd811",
                        "🏡", true),

                createAccommodationType("Nhà nghỉ B&B", "Bed & Breakfast với bữa sáng miễn phí",
                        "https://images.unsplash.com/photo-1551882547-ff40c63fe5fa",
                        "🍳", false),

                createAccommodationType("Nhà nghỉ mát", "Nơi nghỉ dưỡng trong lành tại vùng núi cao",
                        "https://images.unsplash.com/photo-1506905925346-21bda4d32df4",
                        "🏔️", false),

                createAccommodationType("Khách sạn tình nhân", "Không gian riêng tư dành cho các cặp đôi",
                        "https://images.unsplash.com/photo-1551632811-561732d1e306",
                        "💕", false),

                createAccommodationType("Khách sạn khoang ngủ (capsule)", "Hình thức lưu trú hiện đại tiết kiệm không gian",
                        "https://images.unsplash.com/photo-1571902943202-507ec2618e8f",
                        "🛌", false),

                createAccommodationType("Nhà nghỉ giữa thiên nhiên", "Hòa mình vào thiên nhiên hoang dã",
                        "https://images.unsplash.com/photo-1508873696983-2dfd5898f08b",
                        "🌲", false),

                createAccommodationType("Resort", "Khu nghỉ dưỡng cao cấp với đầy đủ tiện ích",
                        "https://images.unsplash.com/photo-1571896349842-33c89424de2d",
                        "🏖️", true),

                createAccommodationType("Nhà nghỉ trang trại", "Trải nghiệm cuộc sống nông thôn thực sự",
                        "https://images.unsplash.com/photo-1500382017468-9049fed747ef",
                        "🚜", false),

                createAccommodationType("Khu cắm trại", "Cắm trại ngoài trời gần gũi với thiên nhiên",
                        "https://images.unsplash.com/photo-1504851149312-7a075b496cc7",
                        "⛺", false),

                createAccommodationType("Nhà nghỉ nông thôn", "Nghỉ dưỡng thanh bình tại vùng quê",
                        "https://images.unsplash.com/photo-1449824913935-59a10b8d2000",
                        "🌾", false),

                createAccommodationType("Lều trại sang trọng", "Cắm trại phong cách với tiện nghi cao cấp",
                        "https://images.unsplash.com/photo-1445308394109-4ec2920981b1",
                        "🏕️", false),

                createAccommodationType("Nhà nghỉ ven đường", "Thuận tiện cho những chuyến đi dài",
                        "https://images.unsplash.com/photo-1551882547-ff40c63fe5fa",
                        "🚗", false),

                createAccommodationType("Công viên nghỉ mát", "Khu nghỉ dưỡng trong công viên thiên nhiên",
                        "https://images.unsplash.com/photo-1441974231531-c6227db76b6e",
                        "🌳", false),

                createAccommodationType("Thuyền", "Trải nghiệm độc đáo nghỉ ngơi trên mặt nước",
                        "https://images.unsplash.com/photo-1544551763-46a013bb70d5",
                        "⛵", false),

                createAccommodationType("Nhà gỗ", "Cabin gỗ ấm áp giữa thiên nhiên",
                        "https://images.unsplash.com/photo-1449824913935-59a10b8d2000",
                        "🪵", false)
        );
        accommodationTypeService.createIfNotExists(defaultAccommodationTypes);
    }

    private CreateAccommodationTypeDto createAccommodationType(String name, String description,
                                                               String imageUrl, String iconUrl,
                                                               Boolean isHighlighted) {
        return new CreateAccommodationTypeDto(name, description, imageUrl, iconUrl, isHighlighted);
    }
}
