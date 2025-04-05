package huy.project.accommodation_service.core.service;

import huy.project.accommodation_service.core.domain.dto.request.BedTypeParams;
import huy.project.accommodation_service.core.domain.dto.request.CreateBedTypeDto;
import huy.project.accommodation_service.core.domain.dto.response.PageInfo;
import huy.project.accommodation_service.core.domain.entity.BedTypeEntity;
import org.springframework.data.util.Pair;

import java.util.List;

public interface IBedTypeService {
    BedTypeEntity createBedType(CreateBedTypeDto req);
    Pair<PageInfo, List<BedTypeEntity>> getBedTypes(BedTypeParams params);
}
