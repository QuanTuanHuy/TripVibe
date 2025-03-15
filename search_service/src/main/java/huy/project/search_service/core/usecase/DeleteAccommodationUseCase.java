package huy.project.search_service.core.usecase;

import huy.project.search_service.core.port.IAccommodationPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeleteAccommodationUseCase {
    private final IAccommodationPort accommodationPort;
    private final GetAccommodationUseCase getAccommodationUseCase;

    public void deleteAccommodation(Long id) {
        // check acc existed
        getAccommodationUseCase.getAccById(id);

        accommodationPort.deleteAccById(id);
    }
}
