package huy.project.inventory_service.core.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccommodationLockResponse {
    private String lockId;
    private boolean success;
    private List<String> errors;
    private Long expiresAt;
}
