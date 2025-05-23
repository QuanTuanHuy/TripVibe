package huy.project.inventory_service.core.domain.entity;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Accommodation {
    private Long id;

    private String name;

    private String description;

    private String address;

    private List<Unit> units;
}
