package huy.project.file_service.core.processor;

import huy.project.file_service.core.domain.constant.ErrorCode;
import huy.project.file_service.core.domain.constant.ImageSize;
import huy.project.file_service.core.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
@Slf4j
public class ImageProcessor {

    public boolean isImage(String contentType) {
        return contentType != null && contentType.startsWith("image/");
    }

    /**
     * Xử lý ảnh và tạo ra các phiên bản với kích thước khác nhau
     * @return Map với key là ImageSize và value là dữ liệu byte[] của ảnh
     */
    public Map<ImageSize, byte[]> processImage(MultipartFile file) throws IOException {
        // Kiểm tra xem file có phải là ảnh không
        if (!isImage(file.getContentType())) {
            log.error("Image is not image");
            throw new AppException(ErrorCode.FILE_IS_NOT_IMAGE);
        }

        // Đọc ảnh gốc
        BufferedImage originalImage = ImageIO.read(file.getInputStream());
        if (originalImage == null) {
            throw new IOException("Không thể đọc file ảnh");
        }

        // Lấy định dạng ảnh từ file gốc
        String formatName = getFormatName(Objects.requireNonNull(file.getOriginalFilename()));

        // Map lưu kết quả
        Map<ImageSize, byte[]> imageVersions = new HashMap<>();

        // Lưu phiên bản gốc
        ByteArrayOutputStream originalOutput = new ByteArrayOutputStream();
        ImageIO.write(originalImage, formatName, originalOutput);
        imageVersions.put(ImageSize.ORIGINAL, originalOutput.toByteArray());

        // Tạo các phiên bản cho từng kích thước
        for (ImageSize size : ImageSize.values()) {
            if (size == ImageSize.ORIGINAL) {
                continue;
            }

            byte[] resizedImage = resize(originalImage, size, formatName);
            imageVersions.put(size, resizedImage);
        }

        return imageVersions;
    }

    /**
     * Tạo ra phiên bản ảnh với kích thước mới
     */
    private byte[] resize(BufferedImage original, ImageSize size, String formatName) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        if (size == ImageSize.THUMBNAIL) {
            // Đối với thumbnail, cắt ảnh để giữ tỷ lệ vuông
            Thumbnails.of(original)
                    .size(size.getWidth(), size.getHeight())
                    .crop(Positions.CENTER)
                    .outputFormat(formatName)
                    .toOutputStream(outputStream);
        } else {
            // Đối với các kích thước khác, giữ tỷ lệ gốc của ảnh
            Thumbnails.of(original)
                    .size(size.getWidth(), size.getHeight())
                    .keepAspectRatio(true)
                    .outputFormat(formatName)
                    .toOutputStream(outputStream);
        }

        return outputStream.toByteArray();
    }

    /**
     * Xác định định dạng file ảnh từ tên file
     */
    public String getFormatName(String filename) {
        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();

        return switch (extension) {
            case "jpg", "jpeg" -> "jpeg";
            case "png" -> "png";
            case "gif" -> "gif";
            case "bmp" -> "bmp";
            case "webp" -> "webp";
            default -> "jpeg"; // Mặc định
        };
    }

    /**
     * Trích xuất metadata của ảnh
     */
    public Map<String, Object> extractMetadata(BufferedImage image) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("width", image.getWidth());
        metadata.put("height", image.getHeight());
        metadata.put("aspectRatio", (double) image.getWidth() / image.getHeight());
        return metadata;
    }
}
