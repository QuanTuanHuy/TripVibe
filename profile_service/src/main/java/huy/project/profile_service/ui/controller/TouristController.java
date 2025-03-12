package huy.project.profile_service.ui.controller;

import huy.project.profile_service.core.domain.dto.request.UpdateTouristDto;
import huy.project.profile_service.core.domain.entity.TouristEntity;
import huy.project.profile_service.core.service.ITouristService;
import huy.project.profile_service.ui.resource.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("profile_service/api/public/v1/tourists")
@RequiredArgsConstructor
public class TouristController {
    private final ITouristService touristService;

    @PutMapping("/{id}")
    public ResponseEntity<Resource<TouristEntity>> updateTourist(
            @PathVariable("id") Long id,
            @RequestBody UpdateTouristDto req
    ) {
        return ResponseEntity.ok(new Resource<>(touristService.updateTourist(id, req)));
    }
}
