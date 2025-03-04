package huy.project.accommodation_service.infrastructure.repository.adapter;

import huy.project.accommodation_service.core.domain.entity.BedEntity;
import huy.project.accommodation_service.core.port.IBedPort;
import huy.project.accommodation_service.infrastructure.repository.IBedRepository;
import huy.project.accommodation_service.infrastructure.repository.mapper.BedMapper;
import huy.project.accommodation_service.infrastructure.repository.model.BedModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BedAdapter implements IBedPort {
    private final IBedRepository bedRepository;

    @Override
    public List<BedEntity> saveAll(List<BedEntity> beds) {
        List<BedModel> bedModels = BedMapper.INSTANCE.toListModel(beds);
        return BedMapper.INSTANCE.toListEntity(bedRepository.saveAll(bedModels));
    }
}
