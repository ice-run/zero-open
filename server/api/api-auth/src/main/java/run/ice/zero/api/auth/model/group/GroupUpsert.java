package run.ice.zero.api.auth.model.group;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import run.ice.zero.common.model.Serializer;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

/**
 * @author DaoDao
 */
@Data
@Schema(title = "GroupUpsert", description = "组织写入")
public class GroupUpsert implements Serializer {

    @Schema(title = "id", description = "id", example = "1")
    @Min(value = 1)
    @Max(value = Long.MAX_VALUE)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @Schema(title = "parentId", description = "父组织id", example = "1")
    @Min(value = 1)
    @Max(value = Long.MAX_VALUE)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long parentId;

    @Schema(title = "name", description = "组织名称", example = "管理组")
    @NotEmpty
    @Size(min = 1, max = 64)
    private String name;

    @Schema(title = "adminId", description = "管理员id", example = "1")
    @Min(value = 1)
    @Max(value = Long.MAX_VALUE)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long adminId;

    @Schema(title = "valid", description = "是否有效", example = "true")
    private Boolean valid;

}
