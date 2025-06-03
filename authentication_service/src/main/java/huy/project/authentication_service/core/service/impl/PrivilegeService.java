package huy.project.authentication_service.core.service.impl;

import huy.project.authentication_service.core.domain.dto.request.CreatePrivilegeRequestDto;
import huy.project.authentication_service.core.domain.dto.request.UpdatePrivilegeRequestDto;
import huy.project.authentication_service.core.domain.entity.PrivilegeEntity;
import huy.project.authentication_service.core.service.IPrivilegeService;
import huy.project.authentication_service.core.usecase.CreatePrivilegeUseCase;
import huy.project.authentication_service.core.usecase.GetPrivilegeUseCase;
import huy.project.authentication_service.core.usecase.UpdatePrivilegeUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PrivilegeService implements IPrivilegeService {
    private final CreatePrivilegeUseCase createPrivilegeUseCase;
    private final GetPrivilegeUseCase getPrivilegeUseCase;
    private final UpdatePrivilegeUseCase updatePrivilegeUseCase;

    @Override
    public PrivilegeEntity createPrivilege(CreatePrivilegeRequestDto req) {
        return createPrivilegeUseCase.createPrivilege(req);
    }

    @Override
    public List<PrivilegeEntity> getAllPrivileges() {
        return getPrivilegeUseCase.getAllPrivileges();
    }

    @Override
    public PrivilegeEntity updatePrivilege(Long id, UpdatePrivilegeRequestDto req) {
        return updatePrivilegeUseCase.updatePrivilege(id, req);
    }

    @Override
    public void createIfNotExists(List<CreatePrivilegeRequestDto> privileges) {
        createPrivilegeUseCase.createIfNotExists(privileges);
    }
}
