package run.ice.zero.api.auth.model.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import run.ice.zero.common.model.Serializer;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

/**
 * @author DaoDao
 */
@Data
@Schema(title = "UserUpsert", description = "用户写入")
public class UserUpsert implements Serializer {

    @Schema(title = "ID", example = "1", description = "用户 ID")
    @Min(value = 1)
    @Max(value = Long.MAX_VALUE)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @Schema(title = "username", description = "用户名", example = "admin")
    @Size(min = 4, max = 32)
    @Pattern(regexp = "^[0-9A-Za-z_-]{4,32}$")
    private String username;

    @Schema(title = "nickname", description = "昵称", example = "管理员")
    @Size(min = 2, max = 32)
    private String nickname;

    @Schema(title = "email", description = "邮箱", example = "admin@example.com")
    @Email
    private String email;

    @Schema(title = "phone", description = "手机号", example = "13800000000")
    @Pattern(regexp = "^1[3-9]\\d{9}$")
    private String phone;

    @Schema(title = "groupId", description = "组id", example = "1")
    @Min(value = 1)
    @Max(value = Long.MAX_VALUE)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long groupId;

    @Schema(title = "valid", description = "是否有效", example = "true")
    private Boolean valid;

}
