package run.ice.zero.api.auth.model.role;

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
@Schema(title = "RoleData", description = "角色数据")
public class RoleData implements Serializer {

    @Schema(title = "id", description = "id", example = "1")
    @NotNull
    @Min(value = 1)
    @Max(value = Long.MAX_VALUE)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @Schema(name = "name", description = "角色名称 : 名命中的层级分组使用 -- 隔开", example = "超级管理员")
    @NotEmpty
    @Size(min = 1, max = 64)
    private String name;

    @Schema(name = "code", description = "角色代码：编码单词使用 - 隔开，层级分组使用 : 隔开", example = "admin")
    @NotEmpty
    @Size(min = 1, max = 64)
    @Pattern(regexp = "^(?!:)[a-z0-9\\-:]+(?<!:)$")
    private String code;

    @Schema(title = "createTime", description = "创建时间", example = AppConstant.DATE_TIME_EXAMPLE)
    private LocalDateTime createTime;

    @Schema(title = "updateTime", description = "更新时间", example = AppConstant.DATE_TIME_EXAMPLE)
    private LocalDateTime updateTime;

    @Schema(title = "valid", description = "是否有效", example = "true")
    private Boolean valid;

}
