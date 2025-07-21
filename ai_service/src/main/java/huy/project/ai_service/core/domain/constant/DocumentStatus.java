package huy.project.ai_service.core.domain.constant;

import lombok.Getter;

@Getter
public enum DocumentStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    FAILED,
    DELETED
}