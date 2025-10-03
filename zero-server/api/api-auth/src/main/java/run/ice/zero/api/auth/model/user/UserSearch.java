package run.ice.zero.api.auth.model.user;

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
@Schema(title = "UserSearch", description = "用户搜索")
public class UserSearch implements Serializer {

    @Schema(title = "ID", description = "ID", example = "1")
    @Min(value = 1)
    @Max(value = Long.MAX_VALUE)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @Schema(title = "username", description = "用户名", example = "admin")
    @Size(min = 4, max = 32)
    @Pattern(regexp = "^[0-9A-Za-z_-]{4,32}$")
    private String username;

    @Schema(title = "valid", description = "是否有效", example = "true")
    private Boolean valid;

}
