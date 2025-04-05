package huy.project.profile_service.infrastructure.repository.adapter;

import huy.project.profile_service.core.domain.entity.TouristEntity;
import huy.project.profile_service.core.port.ITouristPort;
import huy.project.profile_service.infrastructure.repository.ITouristRepository;
import huy.project.profile_service.infrastructure.repository.mapper.TouristMapper;
import huy.project.profile_service.infrastructure.repository.model.TouristModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TouristAdapter implements ITouristPort {
    private final ITouristRepository touristRepository;

    @Override
    public TouristEntity save(TouristEntity tourist) {
        TouristModel touristModel = TouristMapper.INSTANCE.toModel(tourist);
        return TouristMapper.INSTANCE.toEntity(touristRepository.save(touristModel));
    }

    @Override
    public TouristEntity getTouristById(Long id) {
        return TouristMapper.INSTANCE.toEntity(touristRepository.findById(id).orElse(null));
    }

    @Override
    public List<TouristEntity> getTouristsByIds(List<Long> ids) {
        return TouristMapper.INSTANCE.toListEntity(touristRepository.findByIdIn(ids));
    }
}
