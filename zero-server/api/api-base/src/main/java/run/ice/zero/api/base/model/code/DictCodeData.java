package run.ice.zero.api.base.model.code;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import run.ice.zero.common.constant.AppConstant;
import run.ice.zero.common.model.Serializer;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.time.LocalDateTime;

/**
 * @author DaoDao
 */
@Schema(title = "DictCodeData", description = "代码映射数据")
@Data
public class DictCodeData implements Serializer {

    @Schema(title = "id", description = "ID", example = "1")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @Schema(title = "code", description = "编码", example = "demo")
    private String code;

    @Schema(title = "name", description = "名称", example = "示例")
    private String name;

    @Schema(title = "key", description = "键", example = "abc")
    private String key;

    @Schema(title = "value", description = "值", example = "123")
    private String value;

    @Schema(title = "sort", description = "排序", example = "1")
    private Integer sort;

    @Schema(title = "note", description = "注释")
    private String note;

    @Schema(title = "createTime", description = "创建时间", example = AppConstant.DATE_TIME_EXAMPLE)
    private LocalDateTime createTime;

    @Schema(title = "updateTime", description = "更新时间", example = AppConstant.DATE_TIME_EXAMPLE)
    private LocalDateTime updateTime;

    @Schema(title = "valid", description = "有效性", example = "true")
    private Boolean valid;

}
