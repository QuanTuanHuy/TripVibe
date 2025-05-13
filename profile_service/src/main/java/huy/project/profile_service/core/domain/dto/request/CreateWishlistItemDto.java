package huy.project.profile_service.core.domain.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateWishlistItemDto {
    @NotNull(message = "Accommodation ID không được để trống")
    @Positive(message = "Accommodation ID phải là số dương")
    private Long accommodationId;
}
