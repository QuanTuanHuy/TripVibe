package huy.project.profile_service.infrastructure.repository.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "tourists")
public class TouristModel extends AuditTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "gender")
    private String gender;
    @Column(name = "phone")
    private String phone;
    @Column(name = "email")
    private String email;
    @Column(name = "avatar_url")
    private String avatarUrl;
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;
    @Column(name = "location_id")
    private Long locationId;
    @Column(name = "passport_id")
    private Long passportId;
    @Column(name = "credit_card_id")
    private Long creditCardId;
    @Column(name = "member_level")
    private Integer memberLevel;
    @Column(name = "is_active")
    private Boolean isActive;
}
