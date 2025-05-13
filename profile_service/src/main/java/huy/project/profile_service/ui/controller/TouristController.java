package huy.project.profile_service.ui.controller;

import huy.project.profile_service.core.domain.dto.request.CreateCreditCardDto;
import huy.project.profile_service.core.domain.dto.request.UpdateTouristDto;
import huy.project.profile_service.core.domain.entity.CreditCardEntity;
import huy.project.profile_service.core.domain.entity.TouristEntity;
import huy.project.profile_service.core.service.ITouristService;
import huy.project.profile_service.kernel.utils.AuthenUtils;
import huy.project.profile_service.ui.resource.Resource;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/public/v1/tourists")
@RequiredArgsConstructor
@Slf4j
public class TouristController {
    private final ITouristService touristService;

    @GetMapping("/me")
    public ResponseEntity<Resource<TouristEntity>> getDetailTourist() {
        Long userId = AuthenUtils.getCurrentUserId();
        return ResponseEntity.ok(new Resource<>(touristService.getDetailTourist(userId)));
    }

    @PutMapping("/me")
    public ResponseEntity<Resource<TouristEntity>> updateTourist(
            @Valid @RequestBody UpdateTouristDto req
    ) {
        Long touristId = AuthenUtils.getCurrentUserId();
        return ResponseEntity.ok(new Resource<>(touristService.updateTourist(touristId, req)));
    }

    @PostMapping("/me/credit_cards")
    public ResponseEntity<Resource<CreditCardEntity>> addCreditCardToTourist(
            @RequestBody CreateCreditCardDto req
    ) {
        Long touristId = AuthenUtils.getCurrentUserId();
        return ResponseEntity.ok(new Resource<>(touristService.addCreditCardToTourist(touristId, req)));
    }

    @PostMapping("/me/avatar")
    public ResponseEntity<Resource<?>> updateAvatar(
            @RequestPart(value = "file") MultipartFile file
    ) {
        Long touristId = AuthenUtils.getCurrentUserId();
        touristService.updateAvatar(touristId, file);
        return ResponseEntity.ok(new Resource<>(null));
    }
}
