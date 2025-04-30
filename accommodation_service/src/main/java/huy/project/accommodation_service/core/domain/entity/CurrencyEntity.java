package huy.project.accommodation_service.core.domain.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CurrencyEntity {
    Long id;
    String name;
    String code;
    String symbol;
}