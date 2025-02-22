package huy.project.authentication_service.core.usecase;

import huy.project.authentication_service.core.domain.constant.ErrorCode;
import huy.project.authentication_service.core.domain.dto.request.CreatePrivilegeRequestDto;
import huy.project.authentication_service.core.domain.entity.PrivilegeEntity;
import huy.project.authentication_service.core.domain.mapper.PrivilegeMapper;
import huy.project.authentication_service.core.exception.AppException;
import huy.project.authentication_service.core.port.IPrivilegePort;
import huy.project.authentication_service.core.validation.PrivilegeValidation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreatePrivilegeUseCase {
    private final IPrivilegePort privilegePort;
    private final PrivilegeValidation privilegeValidation;

    @Transactional(rollbackFor = Exception.class)
    public PrivilegeEntity createPrivilege(CreatePrivilegeRequestDto req) {
        if (privilegeValidation.isPrivilegeNameExist(req.getName())) {
            log.error("Privilege name existed");
            throw new AppException(ErrorCode.PRIVILEGE_NAME_EXISTED);
        }

        PrivilegeEntity privilege = PrivilegeMapper.INSTANCE.toEntity(req);
        return privilegePort.save(privilege);
    }
}
