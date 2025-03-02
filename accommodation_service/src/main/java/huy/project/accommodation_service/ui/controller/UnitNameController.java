package huy.project.accommodation_service.ui.controller;

import huy.project.accommodation_service.core.domain.dto.request.CreateUnitNameDto;
import huy.project.accommodation_service.core.domain.dto.request.UnitNameParams;
import huy.project.accommodation_service.core.domain.dto.response.ListDataResponse;
import huy.project.accommodation_service.core.domain.entity.UnitNameEntity;
import huy.project.accommodation_service.core.service.IUnitNameService;
import huy.project.accommodation_service.ui.resource.Resource;
import huy.project.accommodation_service.ui.resource.constant.Constant;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/v1/unit_names")
@RequiredArgsConstructor
public class UnitNameController {
    private final IUnitNameService unitNameService;

    @PostMapping
    public ResponseEntity<Resource<UnitNameEntity>> createUnitName(
            @RequestBody CreateUnitNameDto req
    ) {
        return ResponseEntity.ok(new Resource<>(unitNameService.createUnitName(req)));
    }

    @GetMapping
    public ResponseEntity<Resource<ListDataResponse<UnitNameEntity>>> getUnitNames(
            @RequestParam(name = "page", defaultValue = Constant.DEFAULT_PAGE) Integer page,
            @RequestParam(name = "pageSize", defaultValue = Constant.DEFAULT_SIZE) Integer pageSize,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "sortBy", required = false) String sortBy,
            @RequestParam(name = "sortType", required = false) String sortType
    ) {
        UnitNameParams params = UnitNameParams.builder()
                .page(page).pageSize(pageSize)
                .sortBy(sortBy).sortType(sortType)
                .name(name)
                .build();
        var result = unitNameService.getUnitNames(params);
        ListDataResponse<UnitNameEntity> response = ListDataResponse.of(result.getFirst(), result.getSecond());
        return ResponseEntity.ok(new Resource<>(response));
    }

}
