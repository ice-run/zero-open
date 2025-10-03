package run.ice.zero.auth.service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import run.ice.zero.api.auth.error.AuthError;
import run.ice.zero.api.auth.model.security.ChangePassword;
import run.ice.zero.api.auth.model.security.ResetPassword;
import run.ice.zero.common.error.AppException;
import run.ice.zero.auth.entity.User;
import run.ice.zero.auth.helper.AuthHelper;
import run.ice.zero.auth.repository.UserRepository;

import java.util.Optional;

@Slf4j
@Service
@Transactional
public class SecurityService {

    @Resource
    private UserRepository userRepository;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private AuthHelper authHelper;

    public void resetPassword(ResetPassword param) {
        Long id = param.getId();
        String password = param.getPassword();
        Optional<User> optional = userRepository.findById(id);
        if (optional.isEmpty()) {
            throw new AppException(AuthError.USER_NOT_EXIST, String.valueOf(id));
        }
        User user = optional.get();
        if (null == password || password.isEmpty()) {
            password = user.getUsername();
        }
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    public void changePassword(String authorization, ChangePassword param) {
        String username = authHelper.username(authorization.replace("Bearer ", ""));
        Optional<User> optional = userRepository.findByUsername(username);
        if (optional.isEmpty()) {
            throw new AppException(AuthError.USER_NOT_EXIST, username);
        }
        User user = optional.get();
        String oldPassword = param.getOldPassword();
        String newPassword = param.getNewPassword();
        if (oldPassword.equals(newPassword)) {
            throw new AppException(AuthError.OLD_AND_NEW_PASSWORDS_CANNOT_BE_THE_SAME, param.toJson());
        }
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new AppException(AuthError.OLD_PASSWORD_INCORRECT, oldPassword);
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

}
