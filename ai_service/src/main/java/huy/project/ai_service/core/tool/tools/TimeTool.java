package huy.project.ai_service.core.tool.tools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;

@Component
@Slf4j
public class TimeTool {
    @Tool(description = "Get the current time in ISO-8601 format")
    public Instant getCurrentTime() {
        log.info("Tool called: getCurrentTime");
        return Instant.now();
    }

    @Tool(description = "Get the current date in ISO-8601 format")
    public LocalDate getCurrentDate() {
        log.info("Tool called: getCurrentDate");
        return LocalDate.now();
    }
}
