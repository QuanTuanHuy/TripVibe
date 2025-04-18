package huy.project.rating_service.infrastructure.client.adapter;

import huy.project.rating_service.core.domain.constant.ErrorCode;
import huy.project.rating_service.core.domain.dto.response.AccommodationDto;
import huy.project.rating_service.core.domain.dto.response.UnitDto;
import huy.project.rating_service.core.domain.exception.AppException;
import huy.project.rating_service.core.port.IAccommodationPort;
import huy.project.rating_service.infrastructure.client.IAccommodationClient;
import huy.project.rating_service.ui.resource.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class IAccommodationClientAdapter implements IAccommodationPort {
    private final IAccommodationClient accommodationClient;

    @Override
    public List<UnitDto> getUnitsByIds(List<Long> unitIds) {
        try {
            Resource<List<UnitDto>> units = accommodationClient.getUnitsByIds(unitIds);
            if (units.getMeta().getMessage().equals("Success") && units.getData() != null) {
                return units.getData();
            } else {
                return List.of();
            }
        } catch (Exception e) {
            log.error("error when calling accommodation service: ", e);
            throw new AppException(ErrorCode.SERVICE_UNAVAILABLE);
        }
    }

    @Override
    public AccommodationDto getAccById(Long id) {
        try {
            Resource<AccommodationDto> response = accommodationClient.getAccommodationById(id);
            if (response.getMeta().getMessage().equals("Success") && response.getData() != null) {
                return response.getData();
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error("error when calling accommodation service: ", e);
            throw new AppException(ErrorCode.SERVICE_UNAVAILABLE);
        }
    }
}
