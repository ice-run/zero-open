package run.ice.zero.api.auth.model.permission;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;
import run.ice.zero.common.model.Serializer;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

/**
 * @author DaoDao
 */
@Data
@Schema(title = "PermissionSearch", description = "权限搜索")
public class PermissionSearch implements Serializer {

    @Schema(title = "ID", description = "ID", example = "1")
    @Min(value = 1)
    @Max(value = Long.MAX_VALUE)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @Schema(name = "name", description = "权限名称：名命中的层级分组使用 -- 隔开", example = "超级管理")
    @Size(min = 1, max = 64)
    private String name;

    @Schema(name = "code", description = "权限代码：编码单词使用 - 隔开，层级分组使用 : 隔开", example = "admin")
    @Size(min = 1, max = 64)
    private String code;

    @Schema(title = "valid", description = "是否有效", example = "true")
    private Boolean valid;

}
