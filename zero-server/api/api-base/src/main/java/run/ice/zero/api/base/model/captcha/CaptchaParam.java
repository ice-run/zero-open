package run.ice.zero.api.base.model.captcha;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import run.ice.zero.common.model.Serializer;

/**
 * 获取验证码请求参数
 *
 * @author DaoDao
 */
@Data
@Schema(title = "CaptchaParam", description = "获取验证码请求")
public class CaptchaParam implements Serializer {

    /**
     * 验证码 ID
     */
    @Schema(title = "id", description = "验证码 ID", example = "1990")
    @NotEmpty
    @Size(min = 1, max = 64)
    private String id;

    /**
     * 图形验证码
     */
    @Schema(title = "code", description = "图形验证码", example = "0607")
    @NotEmpty
    @Size(min = 4, max = 6)
    private String code;

}
