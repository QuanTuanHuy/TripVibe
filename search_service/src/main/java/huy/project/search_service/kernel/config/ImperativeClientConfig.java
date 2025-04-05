package huy.project.search_service.kernel.config;

import huy.project.search_service.kernel.property.ElasticsearchProperty;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;

@Configuration
@RequiredArgsConstructor
public class ImperativeClientConfig extends ElasticsearchConfiguration {
    private final ElasticsearchProperty elasticsearchProperty;

    @Override
    @NonNull
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo(elasticsearchProperty.getUrl())
                .build();
    }
}
