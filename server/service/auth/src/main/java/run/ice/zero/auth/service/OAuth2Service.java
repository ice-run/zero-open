package run.ice.zero.auth.service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import run.ice.zero.api.auth.error.AuthError;
import run.ice.zero.api.auth.model.oauth2.Login;
import run.ice.zero.api.auth.model.oauth2.OAuth2;
import run.ice.zero.api.base.model.captcha.CaptchaParam;
import run.ice.zero.auth.entity.User;
import run.ice.zero.auth.helper.AuthHelper;
import run.ice.zero.auth.helper.BaseHelper;
import run.ice.zero.auth.repository.UserRepository;
import run.ice.zero.common.annotation.Mask;
import run.ice.zero.common.error.AppException;
import run.ice.zero.common.model.IdParam;
import run.ice.zero.common.util.security.MaskUtil;

import java.util.Optional;

@Slf4j
@Service
@Transactional
public class OAuth2Service {

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private AuthHelper authHelper;

    @Resource
    private UserRepository userRepository;

    @Resource
    private BaseHelper baseHelper;

    public OAuth2 login(Login param) {
        String captchaId = param.getCaptchaId();
        String captchaCode = param.getCaptchaCode();
        CaptchaParam captchaParam = new CaptchaParam();
        captchaParam.setId(captchaId);
        captchaParam.setCode(captchaCode);
        baseHelper.captchaCheck(captchaParam);

        String username = param.getUsername();
        String password = param.getPassword();
        Optional<User> optional = userRepository.findByUsername(username);
        if (optional.isEmpty()) {
            throw new AppException(AuthError.USER_NOT_EXIST, username);
        }
        User user = optional.get();
        if (Boolean.FALSE.equals(user.getValid())) {
            throw new AppException(AuthError.USER_INVALID, user.toJson());
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new AppException(AuthError.USER_PASSWORD_INCORRECT, MaskUtil.mask(Mask.Type.PASSWORD, password));
        }
        OAuth2 oAuth2 = authHelper.password(username, password);
        assert null != oAuth2;
        return oAuth2;
    }

    public OAuth2 idLogin(IdParam param) {
        Long id = param.getId();
        Optional<User> optional = userRepository.findById(id);
        if (optional.isEmpty()) {
            throw new AppException(AuthError.USER_NOT_EXIST, String.valueOf(id));
        }
        User user = optional.get();
        if (Boolean.FALSE.equals(user.getValid())) {
            throw new AppException(AuthError.USER_INVALID, user.toJson());
        }
        return authHelper.id(id);
    }

    public void logout(String authorization) {
        // String authorization = requestHelper.header(HttpHeaders.AUTHORIZATION);
        if (null != authorization && !authorization.isEmpty()) {
            String token = authorization.replace("Bearer ", "");
            authHelper.revoke(token);
        }
    }

}
