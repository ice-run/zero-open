package run.ice.zero.api.auth.model.oauth2;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import run.ice.zero.common.annotation.Sensitive;
import run.ice.zero.common.model.Serializer;

/**
 * 获取验证码请求参数
 *
 * @author DaoDao
 */
@Schema(title = "Login", description = "登录")
@Data
public class Login implements Serializer {

    /**
     * 用户名
     */
    @Schema(title = "username", description = "用户名", example = "DaoDao")
    @NotEmpty
    @Size(min = 4, max = 32)
    @Pattern(regexp = "^[0-9A-Za-z_-]{4,32}$")
    private String username;

    /**
     * 密码
     */
    @Schema(title = "password", description = "密码", example = "1")
    @NotEmpty
    @Size(min = 4, max = 32)
    // @Pattern(regexp = "^(?=.*\\d)(?=.*[A-Z])(?=.*[a-z])[\\dA-Za-z_-]{4,32}$")
    @Pattern(regexp = "^[0-9A-Za-z_-]{4,32}$")
    @Sensitive(type = Sensitive.Type.PASSWORD)
    private String password;

    /**
     * 验证码 ID
     */
    @Schema(title = "captchaId", description = "验证码 ID", example = "1990")
    @NotEmpty
    @Size(min = 1, max = 64)
    @Pattern(regexp = "^[0-9A-Za-z_-]{1,64}$")
    private String captchaId;

    /**
     * 验证码
     */
    @Schema(title = "captchaCode", description = "验证码", example = "0607")
    @NotEmpty
    @Size(min = 4, max = 6)
    @Pattern(regexp = "^[0-9A-Za-z]{4,6}$")
    private String captchaCode;

}
