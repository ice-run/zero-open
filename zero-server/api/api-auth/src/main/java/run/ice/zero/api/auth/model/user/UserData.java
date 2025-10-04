package run.ice.zero.api.auth.model.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import run.ice.zero.common.constant.AppConstant;
import run.ice.zero.common.model.Serializer;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.time.LocalDateTime;

/**
 * @author DaoDao
 */
@Data
@Schema(title = "UserData", description = "用户响应信息")
public class UserData implements Serializer {

    @Schema(title = "id", description = "id", example = "1")
    @NotNull
    @Min(value = 1)
    @Max(value = Long.MAX_VALUE)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @Schema(title = "username", description = "用户名", example = "admin")
    @NotEmpty
    @Size(min = 4, max = 32)
    @Pattern(regexp = "^[0-9A-Za-z_-]{4,32}$")
    private String username;

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

    @Schema(title = "groupId", description = "组id", example = "1")
    @Min(value = 1)
    @Max(value = Long.MAX_VALUE)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long groupId;

    @Schema(title = "createTime", description = "创建时间", example = AppConstant.DATE_TIME_EXAMPLE)
    @NotNull
    private LocalDateTime createTime;

    @Schema(title = "updateTime", description = "更新时间", example = AppConstant.DATE_TIME_EXAMPLE)
    @NotNull
    private LocalDateTime updateTime;

    @Schema(title = "valid", description = "状态 true 有效（启用） false 无效（停用）", example = "true")
    @NotNull
    private Boolean valid;

}
