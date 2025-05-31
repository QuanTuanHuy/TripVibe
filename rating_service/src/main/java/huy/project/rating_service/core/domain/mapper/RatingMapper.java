package huy.project.rating_service.core.domain.mapper;

import huy.project.rating_service.core.domain.dto.request.CreateRatingDto;
import huy.project.rating_service.core.domain.dto.request.CreateRatingResponseDto;
import huy.project.rating_service.core.domain.dto.response.RatingDto;
import huy.project.rating_service.core.domain.dto.response.RatingResponseDto;
import huy.project.rating_service.core.domain.entity.RatingEntity;
import huy.project.rating_service.core.domain.entity.RatingResponseEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class RatingMapper {
    public static final RatingMapper INSTANCE = Mappers.getMapper(RatingMapper.class);

    @Mapping(target = "ratingDetails", ignore = true)
    public abstract RatingEntity toEntity(CreateRatingDto req);

    public RatingDto toDto(RatingEntity rating) {
        return RatingDto.builder()
                .id(rating.getId())
                .value(rating.getValue())
                .comment(rating.getComment())
                .languageId(rating.getLanguageId())
                .createdAt(rating.getCreatedAt())
                .numberOfHelpful(rating.getNumberOfHelpful())
                .numberOfUnhelpful(rating.getNumberOfUnhelpful())
                .build();
    }

    public RatingResponseEntity toRatingResponseEntity(Long ownerId, CreateRatingResponseDto req) {
        return RatingResponseEntity.builder()
                .ratingId(req.getRatingId())
                .ownerId(ownerId)
                .content(req.getContent())
                .build();
    }

    public RatingResponseDto toResponseDto(RatingResponseEntity entity) {
        return RatingResponseDto.builder()
                .id(entity.getId())
                .ratingId(entity.getRatingId())
                .content(entity.getContent())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
