package huy.project.rating_service.infrastructure.repository.specification;

import huy.project.rating_service.core.domain.dto.request.RatingParams;
import huy.project.rating_service.infrastructure.repository.model.RatingModel;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.ArrayList;

public class RatingSpecification {
    public static Specification<RatingModel> getAllRatings(RatingParams params) {
        return (root, query, criteriaBuilder) -> {
            var predicates = new ArrayList<Predicate>();

            if (params.getAccommodationId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("accommodationId"), params.getAccommodationId()));
            }
            if (params.getUnitId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("unitId"), params.getUnitId()));
            }

            if (params.getMinValue() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("value"), params.getMinValue()));
            }
            if (params.getMaxValue() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("value"), params.getMaxValue()));
            }
            if (params.getLanguageId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("languageId"), params.getLanguageId()));
            }

            if (params.getCreatedFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("createdAt"),
                        Instant.ofEpochMilli(params.getCreatedFrom())
                ));
            }
            if (params.getCreatedTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("createdAt"),
                        Instant.ofEpochMilli(params.getCreatedTo())
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
