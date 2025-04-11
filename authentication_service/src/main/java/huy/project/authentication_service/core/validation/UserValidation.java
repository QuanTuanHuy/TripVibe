package huy.project.authentication_service.core.validation;

import huy.project.authentication_service.core.domain.constant.ErrorCode;
import huy.project.authentication_service.core.domain.dto.request.UpdateUserRequestDto;
import huy.project.authentication_service.core.domain.entity.UserEntity;
import huy.project.authentication_service.core.port.IUserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserValidation {
    private final IUserPort userPort;

    public boolean isEmailExist(String email) {
        return userPort.getUserByEmail(email) != null;
    }

    public Pair<Boolean, ErrorCode> validateUpdateUserReq(UserEntity existedUser, UpdateUserRequestDto req) {
        UserEntity emailUser = userPort.getUserByEmail(req.getEmail());
        if (emailUser != null && !emailUser.getId().equals(existedUser.getId())) {
            return Pair.of(false, ErrorCode.USER_EMAIL_EXISTED);
        }

        return Pair.of(true, ErrorCode.SUCCESS);
    }
}
