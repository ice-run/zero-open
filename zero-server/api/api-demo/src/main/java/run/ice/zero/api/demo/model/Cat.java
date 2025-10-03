package run.ice.zero.api.demo.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import run.ice.zero.common.model.Serializer;

/**
 * @author DaoDao
 */
@Data
@Schema(title = "Cat", description = "猫")
public class Cat implements Serializer {

    @Schema(title = "名字", description = "漂亮的喵", example = "喵喵")
    @NotEmpty
    @Size(min = 1, max = 255)
    private String name;

}
