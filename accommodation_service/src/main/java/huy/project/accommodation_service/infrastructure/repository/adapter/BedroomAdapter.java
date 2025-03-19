package huy.project.accommodation_service.infrastructure.repository.adapter;

import huy.project.accommodation_service.core.domain.entity.BedroomEntity;
import huy.project.accommodation_service.core.port.IBedroomPort;
import huy.project.accommodation_service.infrastructure.repository.IBedroomRepository;
import huy.project.accommodation_service.infrastructure.repository.mapper.BedroomMapper;
import huy.project.accommodation_service.infrastructure.repository.model.BedroomModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BedroomAdapter implements IBedroomPort {
    private final IBedroomRepository bedroomRepository;

    @Override
    public BedroomEntity save(BedroomEntity bedroom) {
        BedroomModel bedroomModel = BedroomMapper.INSTANCE.toModel(bedroom);
        return BedroomMapper.INSTANCE.toEntity(bedroomRepository.save(bedroomModel));
    }

    @Override
    public List<BedroomEntity> getBedroomsByUnitIds(List<Long> unitIds) {
        return BedroomMapper.INSTANCE.toListEntity(bedroomRepository.findByUnitIdIn(unitIds));
    }

    @Override
    public List<BedroomEntity> getBedroomsByUnitId(Long unitId) {
        return BedroomMapper.INSTANCE.toListEntity(bedroomRepository.findByUnitId(unitId));
    }
}
