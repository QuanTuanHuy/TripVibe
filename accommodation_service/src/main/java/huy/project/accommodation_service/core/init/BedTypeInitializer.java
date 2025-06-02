package huy.project.accommodation_service.core.init;

import huy.project.accommodation_service.core.domain.dto.request.CreateBedTypeDto;
import huy.project.accommodation_service.core.service.IBedTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class BedTypeInitializer implements CommandLineRunner {
    private final IBedTypeService bedTypeService;

    @Override
    public void run(String... args) {
        log.info("Initializing bed types...");
        initializeBedTypes();
        log.info("Bed types initialization completed.");
    }

    private void initializeBedTypes() {
        List<CreateBedTypeDto> defaultBedTypes = List.of(
                // Giường đơn
                new CreateBedTypeDto("Giường đơn", 1),
                new CreateBedTypeDto("Single bed", 1),

                // Giường đôi nhỏ
                new CreateBedTypeDto("Giường đôi nhỏ", 2),
                new CreateBedTypeDto("Small double bed", 2),
                new CreateBedTypeDto("Double bed", 2),

                // Giường đôi lớn/Queen
                new CreateBedTypeDto("Giường cỡ Queen", 2),
                new CreateBedTypeDto("Queen bed", 2),
                new CreateBedTypeDto("Queen size bed", 2),

                // Giường King
                new CreateBedTypeDto("Giường cỡ King", 2),
                new CreateBedTypeDto("King bed", 2),
                new CreateBedTypeDto("King size bed", 2),

                // Giường đôi cỡ lớn
                new CreateBedTypeDto("Giường đôi cỡ lớn", 2),
                new CreateBedTypeDto("Large double bed", 2),

                // Giường tầng
                new CreateBedTypeDto("Giường tầng", 2),
                new CreateBedTypeDto("Bunk bed", 2),

                // Giường sofa
                new CreateBedTypeDto("Giường sofa", 1),
                new CreateBedTypeDto("Sofa bed", 1),
                new CreateBedTypeDto("Sofa", 1),

                // Giường futon
                new CreateBedTypeDto("Giường Futon", 2),
                new CreateBedTypeDto("Futon", 2),

                // Giường phụ
                new CreateBedTypeDto("Giường phụ", 1),
                new CreateBedTypeDto("Extra bed", 1),
                new CreateBedTypeDto("Rollaway bed", 1),

                // Giường Murphy (giường gấp)
                new CreateBedTypeDto("Giường gấp", 2),
                new CreateBedTypeDto("Murphy bed", 2),
                new CreateBedTypeDto("Wall bed", 2),

                // Giường trẻ em
                new CreateBedTypeDto("Giường trẻ em", 1),
                new CreateBedTypeDto("Toddler bed", 1),
                new CreateBedTypeDto("Children bed", 1),

                // Giường crib/nôi
                new CreateBedTypeDto("Nôi em bé", 1),
                new CreateBedTypeDto("Crib", 1),
                new CreateBedTypeDto("Baby cot", 1),

                // Giường Tatami (phong cách Nhật)
                new CreateBedTypeDto("Giường Tatami", 2),
                new CreateBedTypeDto("Tatami bed", 2),
                new CreateBedTypeDto("Japanese style bed", 2),

                // Giường đa năng
                new CreateBedTypeDto("Giường đa năng", 2),
                new CreateBedTypeDto("Daybed", 2),

                // Giường thổ cẩm (Dormitory)
                new CreateBedTypeDto("Giường ký túc xá", 1),
                new CreateBedTypeDto("Dormitory bed", 1),
                new CreateBedTypeDto("Dorm bed", 1),

                // Giường California King (cỡ siêu lớn)
                new CreateBedTypeDto("Giường California King", 2),
                new CreateBedTypeDto("California King bed", 2),

                // Giường Twin XL (dài hơn giường đơn)
                new CreateBedTypeDto("Giường Twin XL", 1),
                new CreateBedTypeDto("Twin XL bed", 1),

                // Giường điện (có thể điều chỉnh)
                new CreateBedTypeDto("Giường điều chỉnh", 2),
                new CreateBedTypeDto("Adjustable bed", 2),

                // Giường treo/Hammock
                new CreateBedTypeDto("Võng", 1),
                new CreateBedTypeDto("Hammock", 1));

        try {
            bedTypeService.createIfNotExists(defaultBedTypes);
            log.info("Successfully initialized {} bed types", defaultBedTypes.size());
        } catch (Exception e) {
            log.error("Failed to initialize bed types. Error: {}", e.getMessage(), e);
        }
    }
}
