package run.ice.zero.api.base.model.code;

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
@Schema(title = "DictCodeSearch", description = "代码映射搜索")
@Data
public class DictCodeSearch implements Serializer {

    @Schema(title = "ID", description = "ID", example = "1")
    @Min(value = 1)
    @Max(value = Long.MAX_VALUE)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @Schema(title = "code", description = "编码", example = "demo")
    @Size(min = 1, max = 32)
    private String code;

    @Schema(title = "name", description = "名称", example = "示例")
    @Size(min = 1, max = 32)
    private String name;

    @Schema(title = "key", description = "键", example = "abc")
    @Size(min = 1, max = 32)
    private String key;

    @Schema(title = "value", description = "值", example = "123")
    @Size(min = 1, max = 64)
    private String value;

    @Schema(title = "sort", description = "排序", example = "1")
    @Min(value = 1)
    @Max(value = Integer.MAX_VALUE)
    private Integer sort;

    @Schema(title = "note", description = "注释", example = "注释")
    private String note;

    @Schema(title = "valid", description = "有效", example = "true")
    private Boolean valid;

}
