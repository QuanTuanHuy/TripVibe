package huy.project.profile_service.core.port.client;

import huy.project.profile_service.core.domain.dto.response.AccommodationDto;

import java.util.List;

public interface IAccommodationPort {
    List<AccommodationDto> getAccommodations(List<Long> ids);
}
