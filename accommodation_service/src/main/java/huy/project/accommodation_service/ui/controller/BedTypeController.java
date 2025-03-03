package huy.project.accommodation_service.ui.controller;

import huy.project.accommodation_service.core.domain.dto.request.BedTypeParams;
import huy.project.accommodation_service.core.domain.dto.request.CreateBedTypeDto;
import huy.project.accommodation_service.core.domain.dto.response.ListDataResponse;
import huy.project.accommodation_service.core.domain.entity.BedTypeEntity;
import huy.project.accommodation_service.core.service.IBedTypeService;
import huy.project.accommodation_service.ui.resource.Resource;
import huy.project.accommodation_service.ui.resource.constant.Constant;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/v1/bed_types")
@RequiredArgsConstructor
public class BedTypeController {
    private final IBedTypeService bedTypeService;

    @PostMapping
    public ResponseEntity<Resource<BedTypeEntity>> createBedType(
            @RequestBody CreateBedTypeDto req
    ) {
        return ResponseEntity.ok(new Resource<>(bedTypeService.createBedType(req)));
    }

    @GetMapping
    public ResponseEntity<Resource<ListDataResponse<BedTypeEntity>>> getBedTypes(
            @RequestParam(name = "page", defaultValue = Constant.DEFAULT_PAGE) Integer page,
            @RequestParam(name = "pageSize", defaultValue = Constant.DEFAULT_SIZE) Integer pageSize,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "sortBy", required = false) String sortBy,
            @RequestParam(name = "sortType", required = false) String sortType
    ) {
        BedTypeParams params = BedTypeParams.builder()
                .page(page).pageSize(pageSize)
                .sortBy(sortBy).sortType(sortType)
                .name(name)
                .build();
        var result = bedTypeService.getBedTypes(params);
        var response = ListDataResponse.of(result.getFirst(), result.getSecond());
        return ResponseEntity.ok(new Resource<>(response));
    }
}
