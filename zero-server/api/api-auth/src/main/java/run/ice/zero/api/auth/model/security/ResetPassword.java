package run.ice.zero.api.auth.model.security;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import run.ice.zero.common.annotation.Sensitive;
import run.ice.zero.common.model.Serializer;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

/**
 * @author DaoDao
 */
@Data
@Schema(title = "ResetPassword", description = "重置密码")
public class ResetPassword implements Serializer {

    @Schema(title = "id", description = "id", example = "1")
    @NotNull
    @Min(value = 1)
    @Max(value = Long.MAX_VALUE)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @Schema(title = "password", description = "密码", example = "1")
    @Size(min = 4, max = 32)
    //@Pattern(regexp = "^(?=.*\\d)(?=.*[A-Z])(?=.*[a-z])[\\dA-Za-z_-]{4,32}$")
    @Pattern(regexp = "^[0-9A-Za-z_-]{4,32}$")
    @Sensitive(type = Sensitive.Type.PASSWORD)
    private String password;

}
