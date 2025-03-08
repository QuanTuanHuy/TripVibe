package huy.project.accommodation_service.core.usecase;

import huy.project.accommodation_service.core.port.IAccommodationPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeleteAccommodationUseCase {
    private final IAccommodationPort accommodationPort;

    public void deleteAccommodation(Long id) {

    }
}
