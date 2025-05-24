package huy.project.inventory_service.core.usecase;

import huy.project.inventory_service.core.domain.dto.request.CheckOutRequest;
import huy.project.inventory_service.core.domain.dto.response.CheckOutResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckOutUseCase {
    public CheckOutResponse checkOut(CheckOutRequest request) {
        return null;
    }
}
