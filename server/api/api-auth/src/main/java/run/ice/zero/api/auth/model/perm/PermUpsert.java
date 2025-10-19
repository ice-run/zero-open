package run.ice.zero.api.auth.model.perm;

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
@Schema(title = "PermUpsert", description = "权限写入")
public class PermUpsert implements Serializer {

    @Schema(title = "id", description = "id", example = "1")
    @Min(value = 1)
    @Max(value = Long.MAX_VALUE)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @Schema(title = "name", description = "权限名称：名命中的层级分组使用 -- 隔开", example = "超级管理")
    @NotEmpty
    @Size(min = 1, max = 64)
    private String name;

    @Schema(title = "code", description = "权限代码：编码单词使用 - 隔开，层级分组使用 : 隔开", example = "admin")
    @NotEmpty
    @Size(min = 1, max = 255)
    @Pattern(regexp = "^(?!:)[a-z0-9\\-:]+(?<!:)$")
    private String code;

    @Schema(title = "valid", description = "是否有效", example = "true")
    private Boolean valid;

}
