package huy.project.ai_service.core.tool;

import huy.project.ai_service.core.tool.dto.ToolDescriptor;
import huy.project.ai_service.core.tool.tools.BookingTool;
import huy.project.ai_service.core.tool.tools.TimeTool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class ToolManager {
    private final Map<String, ToolCallback[]> toolRegistry = new ConcurrentHashMap<>();
    private final Map<String, ToolDescriptor> toolDescriptors = new ConcurrentHashMap<>();

    private final BookingTool bookingTool;
    private final TimeTool timeTool;

    @EventListener(ContextRefreshedEvent.class)
    public void initializeTools() {
        log.info("Initializing tools...");

        try {
            registerToolClass("bookingTools", bookingTool);
            registerToolClass("timeTools", timeTool);
        } catch (Exception e) {
            log.error("Failed to register booking tools", e);
            throw new RuntimeException("Failed to register booking tools", e);
        }
    }

    private void registerToolClass(String categoryName, Object toolInstance) {
        try {
            ToolCallback[] toolCallbacks = ToolCallbacks.from(toolInstance);

            if (toolCallbacks.length > 0) {
                toolRegistry.put(categoryName, toolCallbacks);

                ToolDescriptor toolDescriptor = ToolDescriptor.builder()
                        .name(categoryName)
                        .description("Tools from " + toolInstance.getClass().getSimpleName())
                        .toolClass(toolInstance.getClass())
                        .build();
                toolDescriptors.put(categoryName, toolDescriptor);
            }
        } catch (Exception e) {
            log.error("Failed to register tool class: {}", categoryName, e);
            throw new RuntimeException("Failed to register tool class: " + categoryName, e);
        }
    }

    public List<ToolCallback> getAllTools() {
        List<ToolCallback> allTools = new ArrayList<>();
        toolRegistry.values().forEach(toolArray -> allTools.addAll(Arrays.asList(toolArray)));
        return Collections.unmodifiableList(allTools);
    }

    public Optional<ToolCallback[]> getToolsByCategory(String categoryName) {
        return Optional.ofNullable(toolRegistry.get(categoryName));
    }

    public Set<String> getToolCategoryNames() {
        return Collections.unmodifiableSet(toolRegistry.keySet());
    }

    public Collection<ToolDescriptor> getToolDescriptors() {
        return Collections.unmodifiableCollection(toolDescriptors.values());
    }

    public boolean isToolsReady() {
        return !toolRegistry.isEmpty();
    }

    public int getToolsCount() {
        return toolRegistry.values().stream()
                .mapToInt(toolArray -> toolArray.length)
                .sum();
    }

    public ToolCallback[] getBookingTools() {
        return getToolsByCategory("bookingTools").orElse(new ToolCallback[0]);
    }
}
