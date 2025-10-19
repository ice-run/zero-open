package run.ice.zero.api.auth.model.group;

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
@Schema(title = "GroupData", description = "组织响应信息")
public class GroupData implements Serializer {

    @Schema(title = "id", description = "id", example = "1")
    @NotNull
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

    @Schema(title = "adminName", description = "管理员名称", example = "DaoDao")
    @Size(min = 1, max = 64)
    private String adminName;

    @Schema(title = "createTime", description = "创建时间", example = AppConstant.DATE_TIME_EXAMPLE)
    @NotNull
    private LocalDateTime createTime;

    @Schema(title = "updateTime", description = "更新时间", example = AppConstant.DATE_TIME_EXAMPLE)
    @NotNull
    private LocalDateTime updateTime;

    @Schema(title = "valid", description = "是否有效", example = "true")
    @NotNull
    private Boolean valid;

}
