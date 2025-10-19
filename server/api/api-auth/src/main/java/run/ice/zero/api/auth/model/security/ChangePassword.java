package run.ice.zero.api.auth.model.security;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import run.ice.zero.common.annotation.Sensitive;
import run.ice.zero.common.model.Serializer;

/**
 * @author DaoDao
 */
@Data
@Schema(title = "ChangePassword", description = "修改密码")
public class ChangePassword implements Serializer {

    @Schema(title = "oldPassword", description = "旧密码", example = "1")
    @NotEmpty
    @Size(min = 1, max = 32)
    private String oldPassword;

    @Schema(title = "newPassword", description = "新密码", example = "1")
    @NotEmpty
    @Size(min = 4, max = 32)
    // @Pattern(regexp = "^(?=.*\\d)(?=.*[A-Z])(?=.*[a-z])[\\dA-Za-z_-]{4,32}$")
    @Pattern(regexp = "^[0-9A-Za-z_-]{4,32}$")
    @Sensitive(type = Sensitive.Type.PASSWORD)
    private String newPassword;

}
