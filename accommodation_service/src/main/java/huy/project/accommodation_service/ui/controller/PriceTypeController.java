package huy.project.accommodation_service.ui.controller;

import huy.project.accommodation_service.core.domain.dto.request.CreatePriceTypeDto;
import huy.project.accommodation_service.core.domain.dto.request.PriceTypeParams;
import huy.project.accommodation_service.core.domain.dto.response.ListDataResponse;
import huy.project.accommodation_service.core.domain.entity.PriceTypeEntity;
import huy.project.accommodation_service.core.service.IPriceTypeService;
import huy.project.accommodation_service.ui.resource.Resource;
import huy.project.accommodation_service.ui.resource.constant.Constant;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/v1/price_types")
@RequiredArgsConstructor
public class PriceTypeController {
    private final IPriceTypeService priceTypeService;

    @GetMapping
    public ResponseEntity<Resource<ListDataResponse<PriceTypeEntity>>> getPriceTypes(
            @RequestParam(name = "page", defaultValue = Constant.DEFAULT_PAGE) Integer page,
            @RequestParam(name = "pageSize", defaultValue = Constant.DEFAULT_SIZE) Integer pageSize,
            @RequestParam(name = "sortBy", required = false) String sortBy,
            @RequestParam(name = "sortType", required = false) String sortType,
            @RequestParam(name = "name", required = false) String name
    ) {
        var params = PriceTypeParams.builder()
                .page(page).pageSize(pageSize)
                .sortBy(sortBy).sortType(sortType)
                .name(name)
                .build();
        var result = priceTypeService.getPriceTypes(params);
        return ResponseEntity.ok(new Resource<>(ListDataResponse.of(result.getFirst(), result.getSecond())));
    }

    @PostMapping
    public ResponseEntity<Resource<PriceTypeEntity>> createPriceType(
            @RequestBody CreatePriceTypeDto req
    ) {
        return ResponseEntity.ok(new Resource<>(priceTypeService.createPriceType(req)));
    }
}
