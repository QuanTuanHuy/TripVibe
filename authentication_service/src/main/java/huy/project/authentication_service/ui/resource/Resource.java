package huy.project.authentication_service.ui.resource;

import lombok.*;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Resource<T> {
    private Object meta;
    private T data;

    public Resource(T data) {
        this.meta = new MetaResource(HttpStatus.OK.value(), "Success");
        this.data = data;
    }

    public Resource(Integer code, String message){
        this.meta = new MetaResource(code, message);
        this.data = null;
    }
}