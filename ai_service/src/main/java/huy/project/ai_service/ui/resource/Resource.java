package huy.project.ai_service.ui.resource;

import lombok.*;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Resource<T> {
    private Integer code;
    private String message;
    private T data;

    public Resource(T data) {
        this.code = HttpStatus.OK.value();
        this.message = "Success";
        this.data = data;
    }

    public Resource(Integer code, String message){
        this.code = code;
        this.message = message;
        this.data = null;
    }
}