package huy.project.profile_service.core.domain.mapper;

import huy.project.profile_service.core.domain.dto.request.UpdateTouristDto;
import huy.project.profile_service.core.domain.entity.TouristEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.util.StringUtils;

@Mapper
public abstract class TouristMapper {
    public static final TouristMapper INSTANCE = Mappers.getMapper(TouristMapper.class);

    public void toEntity(TouristEntity existedTourist, UpdateTouristDto req) {
        if (StringUtils.hasText(req.getName())) {
            existedTourist.setName(req.getName());
        }
        if (StringUtils.hasText(req.getPhone())) {
            existedTourist.setPhone(req.getPhone());
        }
        if (StringUtils.hasText(req.getAvatarUrl())) {
            existedTourist.setAvatarUrl(req.getAvatarUrl());
        }
        if (StringUtils.hasText(req.getGender())) {
            existedTourist.setGender(req.getGender());
        }
        if (req.getDateOfBirth() != null) {
            existedTourist.setDateOfBirth(req.getDateOfBirth());
        }
    }
}
