package huy.project.profile_service.ui.controller.internal;

import huy.project.profile_service.core.domain.dto.response.UserProfileDto;
import huy.project.profile_service.core.domain.mapper.TouristMapper;
import huy.project.profile_service.core.service.ITouristService;
import huy.project.profile_service.ui.resource.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/internal/v1/profiles")
@RequiredArgsConstructor
@Slf4j
public class InternalUserProfileController {
    private final ITouristService touristService;

    @GetMapping
    public ResponseEntity<Resource<List<UserProfileDto>>> getTouristProfilesByIds(
            @RequestParam List<Long> touristIds
    ) {
        var tourists = touristService.getTouristsByIds(touristIds);
        var result = tourists.stream().map(TouristMapper.INSTANCE::toDto).toList();
        return ResponseEntity.ok(new Resource<>(result));
    }
}
