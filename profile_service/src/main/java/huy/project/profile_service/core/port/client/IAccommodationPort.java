package huy.project.profile_service.core.port.client;

import java.util.List;

public interface IAccommodationPort {
    List<Object> getAccommodationsByIds(List<Long> ids);
}
