package huy.project.profile_service.core.domain.entity;

import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class FriendEntity {
    private Long id;
    private Long touristId;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String gender;
}
