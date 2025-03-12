package huy.project.profile_service.core.domain.constant;

import lombok.Getter;

@Getter
public enum MemberLevel {
    LEVEL_1 (1),
    LEVEL_2 (2),
    LEVEL_3 (3),
    ;

    MemberLevel(int level) {
        this.level = level;
    }
    private final int level;
}
