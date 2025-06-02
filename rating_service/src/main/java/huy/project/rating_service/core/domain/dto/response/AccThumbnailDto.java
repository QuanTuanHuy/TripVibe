package huy.project.rating_service.core.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccThumbnailDto {
    private Long id;
    private String name;
    private String thumbnailUrl;

    private List<Unit> units;
    private Location location;

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Unit {
        private Long id;
        private String name;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Location {
        private String address;
    }
}
