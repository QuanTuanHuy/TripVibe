package huy.project.profile_service.ui.controller;

import huy.project.profile_service.core.domain.dto.request.CreateCreditCardDto;
import huy.project.profile_service.core.domain.dto.request.UpdateTouristDto;
import huy.project.profile_service.core.domain.entity.CreditCardEntity;
import huy.project.profile_service.core.domain.entity.TouristEntity;
import huy.project.profile_service.core.service.ITouristService;
import huy.project.profile_service.kernel.utils.AuthenUtils;
import huy.project.profile_service.ui.resource.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("profile_service/api/public/v1/tourists")
@RequiredArgsConstructor
@Slf4j
public class TouristController {
    private final ITouristService touristService;

    @GetMapping("/me")
    public ResponseEntity<Resource<TouristEntity>> getDetailTourist() {
        Long userId = AuthenUtils.getCurrentUserId();
        return ResponseEntity.ok(new Resource<>(touristService.getDetailTourist(userId)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Resource<TouristEntity>> updateTourist(
            @PathVariable("id") Long id,
            @RequestBody UpdateTouristDto req
    ) {
        return ResponseEntity.ok(new Resource<>(touristService.updateTourist(id, req)));
    }

    @PostMapping("/{touristId}/credit_cards")
    public ResponseEntity<Resource<CreditCardEntity>> addCreditCardToTourist(
            @PathVariable("touristId") Long touristId,
            @RequestBody CreateCreditCardDto req
    ) {
        Long userId = AuthenUtils.getCurrentUserId();
        if (!userId.equals(touristId)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(new Resource<>(touristService.addCreditCardToTourist(touristId, req)));
    }
}
