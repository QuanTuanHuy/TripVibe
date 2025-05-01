package huy.project.search_service.core.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AccommodationParams extends BaseParams {
    private Integer page;
    private Integer pageSize;
    private Long provinceId;
    private Date startDate;
    private Date endDate;
    private Integer numAdults;
    private Integer numChildren;
    private Double minBudget;
    private Double maxBudget;
    private Long accTypeId;
    private List<Long> accAmenityIds;
    private List<Long> unitAmenityIds;
    private List<Long> bookingPolicyIds;
    private Double minRatingStar;
    private Double longitude;
    private Double latitude;
    private Double radius;

    private String sortBy;
}
