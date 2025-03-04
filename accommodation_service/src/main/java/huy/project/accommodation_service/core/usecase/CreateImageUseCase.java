package huy.project.accommodation_service.core.usecase;

import huy.project.accommodation_service.core.domain.entity.ImageEntity;
import huy.project.accommodation_service.core.port.IImagePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateImageUseCase {
    private final IImagePort imagePort;

    @Transactional(rollbackFor = Exception.class)
    public List<ImageEntity> createImages(List<ImageEntity> images) {
        return imagePort.saveAll(images);
    }
}
