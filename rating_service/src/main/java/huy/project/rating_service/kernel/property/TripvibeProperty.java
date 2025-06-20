package huy.project.rating_service.kernel.property;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "app.tripvibe")
public class TripvibeProperty {
    private String email;
    private String password;
}
