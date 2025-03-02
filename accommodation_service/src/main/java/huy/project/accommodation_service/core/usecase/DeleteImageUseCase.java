package huy.project.accommodation_service.core.usecase;

import huy.project.accommodation_service.core.port.IImagePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteImageUseCase {
    private final IImagePort imagePort;

    @Transactional(rollbackFor = Exception.class)
    public void deleteImageByEntityIdAndType(Long entityId, String entityType) {
        imagePort.deleteImagesByEntityIdAndType(entityId, entityType);
    }
}
