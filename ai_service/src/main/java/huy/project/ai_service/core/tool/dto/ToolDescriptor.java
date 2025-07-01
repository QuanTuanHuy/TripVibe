package huy.project.ai_service.core.tool.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToolDescriptor {
    private String name;
    private String description;
    private Class<?> toolClass;

    public String getClassName() {
        return toolClass != null ? toolClass.getSimpleName() : "Unknown";
    }
}
