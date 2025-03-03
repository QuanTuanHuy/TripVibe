package huy.project.accommodation_service.core.service;

import huy.project.accommodation_service.core.domain.dto.request.CreateUnitNameDto;
import huy.project.accommodation_service.core.domain.dto.request.UnitNameParams;
import huy.project.accommodation_service.core.domain.dto.response.PageInfo;
import huy.project.accommodation_service.core.domain.entity.UnitNameEntity;
import org.springframework.data.util.Pair;

import java.util.List;

public interface IUnitNameService {
    UnitNameEntity createUnitName(CreateUnitNameDto req);

    Pair<PageInfo, List<UnitNameEntity>> getUnitNames(UnitNameParams params);
}
