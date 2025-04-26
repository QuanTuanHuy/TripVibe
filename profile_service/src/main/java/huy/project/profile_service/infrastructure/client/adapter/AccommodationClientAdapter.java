package huy.project.profile_service.infrastructure.client.adapter;

import huy.project.profile_service.core.port.client.IAccommodationPort;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AccommodationClientAdapter implements IAccommodationPort {
    @Override
    public List<Object> getAccommodationsByIds(List<Long> ids) {
        return List.of();
    }
}
