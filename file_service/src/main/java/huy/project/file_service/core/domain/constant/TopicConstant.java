package huy.project.file_service.core.domain.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TopicConstant {
    @UtilityClass
    public class FileCommand {
        public static final String TOPIC = "file_service.file_storage";
        public static final String DELETE_FILE = "delete_file";
    }

}
