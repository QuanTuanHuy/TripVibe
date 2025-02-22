package huy.project.authentication_service.core.service;

import huy.project.authentication_service.core.domain.dto.request.CreatePrivilegeRequestDto;
import huy.project.authentication_service.core.domain.dto.request.UpdatePrivilegeRequestDto;
import huy.project.authentication_service.core.domain.entity.PrivilegeEntity;

import java.util.List;

public interface IPrivilegeService {
    PrivilegeEntity createPrivilege(CreatePrivilegeRequestDto req);
    List<PrivilegeEntity> getAllPrivileges();
    PrivilegeEntity updatePrivilege(Long id, UpdatePrivilegeRequestDto req);
}
