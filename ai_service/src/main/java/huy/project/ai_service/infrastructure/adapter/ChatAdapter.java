package huy.project.ai_service.infrastructure.adapter;

import huy.project.ai_service.core.port.IChatPort;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.content.Media;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

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

    @Override
    public String getResponseWithImage(String prompt, MultipartFile file) {
        Media media = Media.builder()
                .mimeType(MimeTypeUtils.parseMimeType(Objects.requireNonNull(file.getContentType())))
                .data(file.getResource())
                .build();

        ChatOptions chatOptions = ChatOptions.builder()
                .temperature(0D)
                .build();

        return chatClient.prompt()
                .options(chatOptions)
                .user(promptUserSpec -> promptUserSpec
                        .media(media)
                        .text(prompt))
                .call()
                .content();
    }

    @Override
    public <T> T getStructureResponse(String prompt, Class<T> clazz) {
        return chatClient
                .prompt(prompt)
                .call()
                .entity(clazz);
    }

    @Override
    public <T> T getStructureResponse(String prompt, ParameterizedTypeReference<T> typeReference) {
        return chatClient
                .prompt(prompt)
                .call()
                .entity(typeReference);
    }
}

