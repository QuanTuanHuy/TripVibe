package huy.project.accommodation_service.infrastructure.repository.specification;

import huy.project.accommodation_service.core.domain.dto.request.PriceTypeParams;
import huy.project.accommodation_service.infrastructure.repository.model.PriceTypeModel;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;

public class PriceTypeSpecification {
    public static Specification<PriceTypeModel> getPriceTypes(PriceTypeParams params) {
        return (root, query, criteriaBuilder) -> {
            var predicates = new ArrayList<Predicate>();

            if (StringUtils.hasText(params.getName())) {
                predicates.add(criteriaBuilder.like(root.get("name"), "%" + params.getName() + "%"));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
