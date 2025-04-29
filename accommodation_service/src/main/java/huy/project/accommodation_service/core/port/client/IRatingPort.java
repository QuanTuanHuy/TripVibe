package huy.project.accommodation_service.core.port.client;

import huy.project.accommodation_service.core.domain.dto.response.RatingSummaryDto;

import java.util.List;

public interface IRatingPort {
    List<RatingSummaryDto> getRatingSummariesByAccIds(List<Long> ids);
}
