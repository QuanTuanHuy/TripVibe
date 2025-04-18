package huy.project.file_service.core.domain.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ImageSize {
    THUMBNAIL(320, 240, "thumbnail"),
    GALLERY(1024, 768, "gallery"),
    BANNER(1920, 1080, "cover"),
    ORIGINAL(-1, -1, "original"); // Kích thước gốc

    private final int width;
    private final int height;
    private final String suffix;
}
