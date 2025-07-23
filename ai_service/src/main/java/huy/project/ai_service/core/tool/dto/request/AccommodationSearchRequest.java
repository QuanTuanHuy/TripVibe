package huy.project.ai_service.core.tool.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccommodationSearchRequest {
    String location;
    String checkInDate;
    String checkOutDate;
    Integer numberOfAdults;
    Integer numberOfChildren;
}
