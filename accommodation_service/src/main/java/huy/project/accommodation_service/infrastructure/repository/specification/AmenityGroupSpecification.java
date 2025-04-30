package huy.project.accommodation_service.infrastructure.repository.specification;

import huy.project.accommodation_service.core.domain.constant.AmenityGroupType;
import huy.project.accommodation_service.core.domain.dto.request.AmenityGroupParams;
import huy.project.accommodation_service.infrastructure.repository.model.AmenityGroupModel;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;

public class AmenityGroupSpecification {
    public static Specification<AmenityGroupModel> getAmenityGroups(AmenityGroupParams params) {
        return (root, query, criteriaBuilder) -> {
            var predicates = new ArrayList<Predicate>();

            if (StringUtils.hasText(params.getType())) {
                predicates.add(criteriaBuilder.equal(root.get("type"), AmenityGroupType.of(params.getType())));
            }
            if (params.getIsPopular() != null) {
                predicates.add(criteriaBuilder.equal(root.get("isPopular"), params.getIsPopular()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
