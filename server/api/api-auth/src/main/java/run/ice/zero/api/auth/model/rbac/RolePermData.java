package run.ice.zero.api.auth.model.rbac;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import run.ice.zero.api.auth.model.perm.PermData;
import run.ice.zero.api.auth.model.role.RoleData;
import run.ice.zero.common.model.Serializer;

import java.util.List;

/**
 * @author DaoDao
 */
@Schema(title = "RolePermData", description = "角色权限数据")
@Data
public class RolePermData implements Serializer {

    @Schema(title = "roleDataList", description = "角色列表")
    @NotNull
    private List<RoleData> roleDataList;

    @Schema(title = "permDataList", description = "权限列表")
    @NotNull
    private List<PermData> permDataList;

}
