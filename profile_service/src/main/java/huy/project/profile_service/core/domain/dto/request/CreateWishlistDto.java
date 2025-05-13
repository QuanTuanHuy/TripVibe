package huy.project.profile_service.core.domain.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateWishlistDto {
    @NotBlank(message = "Tên danh sách yêu thích không được để trống")
    @Size(max = 100, message = "Tên danh sách yêu thích không được vượt quá 100 ký tự")
    String name;

    @Valid
    List<CreateWishlistItemDto> items;
}
