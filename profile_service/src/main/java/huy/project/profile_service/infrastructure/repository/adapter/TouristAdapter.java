package huy.project.profile_service.infrastructure.repository.adapter;

import huy.project.profile_service.core.domain.entity.TouristEntity;
import huy.project.profile_service.core.port.ITouristPort;
import huy.project.profile_service.infrastructure.cache.MultiLevelCache;
import huy.project.profile_service.infrastructure.cache.MultiLevelCacheFactory;
import huy.project.profile_service.infrastructure.repository.ITouristRepository;
import huy.project.profile_service.infrastructure.repository.mapper.TouristMapper;
import huy.project.profile_service.infrastructure.repository.model.TouristModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class TouristAdapter implements ITouristPort {
    private final ITouristRepository touristRepository;
    private final MultiLevelCache<Long, TouristEntity> touristCache;

    public TouristAdapter(ITouristRepository touristRepository, MultiLevelCacheFactory cacheFactory) {
        this.touristRepository = touristRepository;
        this.touristCache = cacheFactory.createProfileCache(TouristEntity.class);
    }

    @Override
    public TouristEntity save(TouristEntity tourist) {
        touristCache.invalidate(tourist.getId());

        TouristModel touristModel = TouristMapper.INSTANCE.toModel(tourist);
        return TouristMapper.INSTANCE.toEntity(touristRepository.save(touristModel));
    }

    @Override
    public TouristEntity getTouristById(Long id) {
        return touristCache.get(id, k ->
                TouristMapper.INSTANCE.toEntity(touristRepository.findById(k).orElse(null)));
    }

    @Override
    public List<TouristEntity> getTouristsByIds(List<Long> ids) {
        return TouristMapper.INSTANCE.toListEntity(touristRepository.findByIdIn(ids));
    }
}
