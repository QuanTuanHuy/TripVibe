package huy.project.accommodation_service.core.usecase;

import huy.project.accommodation_service.core.domain.entity.ImageEntity;
import huy.project.accommodation_service.core.port.IImagePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetImageUseCase {
    private final IImagePort imagePort;;

    public List<ImageEntity> getImagesByEntityIdAndType(Long entityId, String entityType) {
        return imagePort.getImagesByEntityIdAndType(entityId, entityType);
    }
}
