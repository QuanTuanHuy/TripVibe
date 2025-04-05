package huy.project.accommodation_service.infrastructure.repository.specification;

import huy.project.accommodation_service.core.domain.dto.request.UnitNameParams;
import huy.project.accommodation_service.infrastructure.repository.model.UnitNameModel;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;

public class UnitNameSpecification {
    public static Specification<UnitNameModel> getUnitNames(UnitNameParams params) {
        return (root, query, criteriaBuilder) -> {
            var predicates = new ArrayList<Predicate>();

            if (StringUtils.hasText(params.getName())) {
                predicates.add(criteriaBuilder.like(root.get("name"), "%" + params.getName() + "%"));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
