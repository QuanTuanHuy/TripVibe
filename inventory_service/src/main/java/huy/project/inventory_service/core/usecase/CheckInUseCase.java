package huy.project.inventory_service.core.usecase;

import huy.project.inventory_service.core.domain.dto.request.CheckInRequest;
import huy.project.inventory_service.core.domain.dto.response.CheckInResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckInUseCase {
    public CheckInResponse checkIn(CheckInRequest request) {
        return null;
    }
}
