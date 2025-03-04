package huy.project.accommodation_service.core.port;

import huy.project.accommodation_service.core.domain.entity.BedroomEntity;

public interface IBedroomPort {
    BedroomEntity save(BedroomEntity bedroom);
}
