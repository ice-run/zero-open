package run.ice.zero.api.auth.model.rbac;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import run.ice.zero.api.auth.model.permission.PermissionData;
import run.ice.zero.api.auth.model.role.RoleData;

import java.util.List;

/**
 * @author DaoDao
 */
@Schema(title = "RolePermissionData", description = "角色权限数据")
@Data
public class RolePermissionData {

    @Schema(title = "roleDataList", description = "角色列表")
    @NotNull
    private List<RoleData> roleDataList;

    @Schema(title = "permissionDataList", description = "权限列表")
    @NotNull
    private List<PermissionData> permissionDataList;

}
