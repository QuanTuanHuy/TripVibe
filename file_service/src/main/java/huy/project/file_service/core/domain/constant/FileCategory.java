package huy.project.file_service.core.domain.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FileCategory {
    HOTEL_COVER("hotel_cover", "Hình ảnh bìa khách sạn"),
    HOTEL_GALLERY("hotel_gallery", "Hình ảnh trong bộ sưu tập của khách sạn"),
    ROOM_COVER("room_cover", "Hình ảnh bìa phòng"),
    ROOM_GALLERY("room_gallery", "Hình ảnh trong bộ sưu tập của phòng"),
    USER_AVATAR("user_avatar", "Ảnh đại diện người dùng"),
    USER_BACKGROUND("user_background", "Ảnh nền người dùng"),
    BLOG_COVER("blog_cover", "Ảnh bìa bài viết"),
    BLOG_CONTENT("blog_content", "Ảnh trong nội dung bài viết"),
    REVIEW_PHOTO("review_photo", "Ảnh trong đánh giá"),
    DOCUMENT("document", "Tài liệu"),
    OTHER("other", "Khác");

    private final String code;
    private final String description;
}