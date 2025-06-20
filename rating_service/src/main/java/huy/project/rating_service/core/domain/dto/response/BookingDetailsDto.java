package huy.project.rating_service.core.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookingDetailsDto {
    private Long id;
    private Long touristId;
    private Long accommodationId;
    private List<BookingUnit> units;

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class BookingUnit {
        private Long unitId;
    }
}
