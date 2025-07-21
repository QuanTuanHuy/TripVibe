package huy.project.ai_service.kernel.config;

import huy.project.ai_service.kernel.properties.ChatMemoryProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ChatMemoryConfig {
    private final ChatMemoryProperty chatMemoryProperty;

    @Bean
    public ChatMemory chatMemory(ChatMemoryRepository repository) {
        ChatMemoryRepository memoryRepository = repository != null ?
                repository : new InMemoryChatMemoryRepository();

        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(memoryRepository)
                .maxMessages(chatMemoryProperty.getMaxMessages())
                .build();
    }
}
