package run.ice.zero.api.auth.model.role;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import run.ice.zero.common.model.Serializer;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

/**
 * @author DaoDao
 */
@Data
@Schema(title = "RoleSearch", description = "角色搜索")
public class RoleSearch implements Serializer {

    @Schema(title = "ID", description = "ID", example = "1")
    @Min(value = 1)
    @Max(value = Long.MAX_VALUE)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @Schema(title = "name", description = "角色名称 : 编码单词使用 - 隔开，层级分组使用 : 隔开", example = "name")
    @Size(min = 1, max = 64)
    private String name;

    @Schema(title = "code", description = "角色代码：编码单词使用 - 隔开，层级分组使用 : 隔开", example = "code")
    @Size(min = 1, max = 255)
    @Pattern(regexp = "^[a-z0-9\\-:]+$")
    private String code;

    @Schema(title = "valid", description = "是否有效", example = "true")
    private Boolean valid;

}
