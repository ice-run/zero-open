package run.ice.zero.api.auth.model.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import run.ice.zero.common.model.Serializer;

/**
 * @author DaoDao
 */
@Data
@Schema(title = "UserUpdate", description = "用户更新请求参数")
public class UserUpdate implements Serializer {

    @Schema(title = "nickname", description = "昵称", example = "管理员")
    @Size(min = 2, max = 32)
    private String nickname;

    @Schema(title = "avatar", description = "头像", example = "1")
    @Size(min = 2, max = 32)
    private String avatar;

    @Schema(title = "email", description = "邮箱", example = "admin@example.com")
    @Email
    private String email;

    @Schema(title = "phone", description = "手机号", example = "13800000000")
    @Pattern(regexp = "^1[3-9]\\d{9}$")
    private String phone;

}
