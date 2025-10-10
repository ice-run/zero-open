package run.ice.zero.api.auth.model.rbac;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import run.ice.zero.common.model.Serializer;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

/**
 * @author DaoDao
 */
@Data
@Schema(title = "RolePermUpsert", description = "修改角色权限")
public class RolePermUpsert implements Serializer {

    @Schema(title = "roleId", description = "角色 id", example = "1")
    @NotNull
    @Min(value = 1)
    @Max(value = Long.MAX_VALUE)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long roleId;

    @Schema(title = "permId", description = "权限 id", example = "1")
    @NotNull
    @Min(value = 1)
    @Max(value = Long.MAX_VALUE)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long permId;

    @Schema(title = "valid", description = "是否有效，如果置为有效，则会将本参数对象中的 角色 id 和 权限 id 组成关联关系，并设置为有效，即赋予角色这个权限。反之无效，则取消这个权限。", example = "true")
    @NotNull
    private Boolean valid;

}
