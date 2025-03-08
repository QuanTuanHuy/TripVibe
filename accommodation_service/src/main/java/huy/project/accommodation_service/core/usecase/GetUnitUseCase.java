package huy.project.accommodation_service.core.usecase;

import huy.project.accommodation_service.core.domain.constant.ImageEntityType;
import huy.project.accommodation_service.core.domain.entity.*;
import huy.project.accommodation_service.core.port.IUnitPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetUnitUseCase {
    private final IUnitPort unitPort;

    private final GetUnitNameUseCase getUnitNameUseCase;
    private final GetImageUseCase getImageUseCase;
    private final GetUnitAmenityUseCase getUnitAmenityUseCase;
    private final GetBedroomUseCase getBedroomUseCase;
    private final GetUnitPriceTypeUseCase getUnitPriceTypeUseCase;
    private final GetUnitPriceGroupUseCase getUnitPriceGroupUseCase;

    public List<UnitEntity> getUnitsByAccommodationId(Long accommodationId) {
        List<UnitEntity> units = unitPort.getUnitsByAccommodationId(accommodationId);

        // get unit names
        List<Long> unitNameIds = units.stream().map(UnitEntity::getUnitNameId).distinct().toList();
        List<UnitNameEntity> unitNames = getUnitNameUseCase.getUnitNamesByIds(unitNameIds);
        var unitNameMap = unitNames.stream().collect(Collectors.toMap(UnitNameEntity::getId, Function.identity()));

        // get images
        List<Long> unitIds = units.stream().map(UnitEntity::getId).toList();
        List<ImageEntity> images = getImageUseCase.getImagesByEntityIdsAndType(unitIds, ImageEntityType.UNIT.getType());
        var imageGroup = images.stream().collect(Collectors.groupingBy(ImageEntity::getEntityId));

        // get bedrooms
        List<BedroomEntity> bedrooms = getBedroomUseCase.getBedroomsByUnitIds(unitIds);
        var bedroomGroup = bedrooms.stream().collect(Collectors.groupingBy(BedroomEntity::getUnitId));

        // get amenities
        List<UnitAmenityEntity> unitAmenities = getUnitAmenityUseCase.getUnitAmenitiesByUnitIds(unitIds);
        var unitAmenityGroup = unitAmenities.stream()
                .collect(Collectors.groupingBy(UnitAmenityEntity::getUnitId));

        // get price types
        List<UnitPriceTypeEntity> unitPriceTypes = getUnitPriceTypeUseCase.getUnitPriceTypesByUnitIds(unitIds);
        var unitPriceTypeGroup = unitPriceTypes.stream()
                .collect(Collectors.groupingBy(UnitPriceTypeEntity::getUnitId));

        // get prices group
        List<UnitPriceGroupEntity> priceGroups = getUnitPriceGroupUseCase.getUnitPriceGroupsByUnitIds(unitIds);
        var priceGroup = priceGroups.stream()
                .collect(Collectors.groupingBy(UnitPriceGroupEntity::getUnitId));

        return units.stream()
                .peek(unit -> {
                    unit.setUnitName(unitNameMap.get(unit.getId()));
                    unit.setAmenities(unitAmenityGroup.get(unit.getId()));
                    unit.setBedrooms(bedroomGroup.get(unit.getId()));
                    unit.setImages(imageGroup.get(unit.getId()));
                    unit.setPriceTypes(unitPriceTypeGroup.get(unit.getId()));
                    unit.setPriceGroups(priceGroup.get(unit.getId()));
                }).toList();
    }

    public UnitEntity getUnitByAccIdAndId(Long accId, Long unitId) {
        return unitPort.getUnitByAccIdAndId(accId, unitId);
    }
}
