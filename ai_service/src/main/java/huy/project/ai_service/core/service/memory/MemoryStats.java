package huy.project.ai_service.core.service.memory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemoryStats {
    private int totalTurns;
    private int totalTokens;
    private LocalDateTime firstTurn;
    private LocalDateTime lastTurn;
    private double averageTokensPerTurn;
    private String memoryType;
    private boolean isCompressed;
    private LocalDateTime lastCompression;
}
