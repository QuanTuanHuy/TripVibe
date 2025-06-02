package huy.project.accommodation_service.core.init;

import huy.project.accommodation_service.core.domain.dto.request.CreateLanguageRequestDto;
import huy.project.accommodation_service.core.service.ILanguageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class LanguageInitializer implements CommandLineRunner {
    private final ILanguageService languageService;

    @Override
    public void run(String... args) {
        log.info("Initializing language data...");
        initializeLanguages();
        log.info("Language data initialization completed.");
    }

    private void initializeLanguages() {
        List<CreateLanguageRequestDto> languages = Arrays.asList(
                new CreateLanguageRequestDto("Vietnamese", "vi", "Tiếng Việt"),
                new CreateLanguageRequestDto("English", "en", "English"),
                new CreateLanguageRequestDto("Chinese (Simplified)", "zh-CN", "中文（简体）"),
                new CreateLanguageRequestDto("Chinese (Traditional)", "zh-TW", "中文（繁體）"),
                new CreateLanguageRequestDto("Japanese", "ja", "日本語"),
                new CreateLanguageRequestDto("Korean", "ko", "한국어"),
                new CreateLanguageRequestDto("French", "fr", "Français"),
                new CreateLanguageRequestDto("Russian", "ru", "Русский"),
                new CreateLanguageRequestDto("German", "de", "Deutsch"),
                new CreateLanguageRequestDto("Spanish", "es", "Español"),
                new CreateLanguageRequestDto("Thai", "th", "ไทย"),
                new CreateLanguageRequestDto("Indonesian", "id", "Bahasa Indonesia"),
                new CreateLanguageRequestDto("Malay", "ms", "Bahasa Melayu"),
                new CreateLanguageRequestDto("Italian", "it", "Italiano"),
                new CreateLanguageRequestDto("Portuguese", "pt", "Português"),
                new CreateLanguageRequestDto("Arabic", "ar", "العربية"),
                new CreateLanguageRequestDto("Hindi", "hi", "हिन्दी"),
                new CreateLanguageRequestDto("Dutch", "nl", "Nederlands"),
                new CreateLanguageRequestDto("Swedish", "sv", "Svenska"),
                new CreateLanguageRequestDto("Norwegian", "no", "Norsk")
        );

        try {
            languageService.createIfNotExists(languages);
            log.info("Successfully initialized {} languages", languages.size());
        } catch (Exception e) {
            log.error("Failed to initialize languages. Error: {}", e.getMessage(), e);
        }
    }
}
