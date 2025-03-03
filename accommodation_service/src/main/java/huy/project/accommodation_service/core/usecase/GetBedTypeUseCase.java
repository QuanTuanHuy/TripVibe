package huy.project.accommodation_service.core.usecase;

import huy.project.accommodation_service.core.domain.dto.request.BedTypeParams;
import huy.project.accommodation_service.core.domain.dto.response.PageInfo;
import huy.project.accommodation_service.core.domain.entity.BedTypeEntity;
import huy.project.accommodation_service.core.port.IBedTypePort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetBedTypeUseCase {
    private final IBedTypePort bedTypePort;

    public Pair<PageInfo, List<BedTypeEntity>> getBedTypes(BedTypeParams params) {
        return bedTypePort.getBedTypes(params);
    }
}
