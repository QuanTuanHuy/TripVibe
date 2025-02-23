package huy.project.authentication_service.core.validation;

import huy.project.authentication_service.core.port.IUserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserValidation {
    private final IUserPort userPort;

    public boolean isUsernameExist(String username) {
        return userPort.getUserByUsername(username) != null;
    }

    public boolean isEmailExist(String email) {
        return userPort.getUserByEmail(email) != null;
    }
}
