package run.ice.zero.api.base.model.captcha;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import run.ice.zero.common.model.Serializer;

/**
 * 获取验证码响应数据
 *
 * @author DaoDao
 */
@Data
@Schema(title = "CaptchaData", description = "获取验证码响应数据")
public class CaptchaData implements Serializer {

    /**
     * 验证码 ID
     */
    @Schema(title = "id", example = "1990", description = "验证码 ID")
    @NotEmpty
    @Size(min = 1, max = 64)
    private String id;

    /**
     * 验证码图片 base64 code
     */
    @Schema(title = "image", example = "0607", description = "验证码图片 base64 code")
    @NotEmpty
    @Size(min = 4, max = 10240)
    private String image;

}
