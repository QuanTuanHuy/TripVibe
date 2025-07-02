package huy.project.ai_service.infrastructure.adapter;

import huy.project.ai_service.core.port.IChatPort;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatAdapter implements IChatPort {
    private final ChatClient chatClient;

    @Override
    public String getResponse(String prompt) {
        return chatClient
                .prompt(prompt)
                .call()
                .content();
    }
}
