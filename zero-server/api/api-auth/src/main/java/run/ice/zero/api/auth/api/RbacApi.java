package run.ice.zero.api.auth.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import run.ice.zero.api.auth.model.permission.PermissionData;
import run.ice.zero.api.auth.model.rbac.RolePermissionData;
import run.ice.zero.api.auth.model.rbac.RolePermissionUpsert;
import run.ice.zero.api.auth.model.rbac.UserRoleUpsert;
import run.ice.zero.api.auth.model.role.RoleData;
import run.ice.zero.common.constant.AppConstant;
import run.ice.zero.common.model.*;

import java.util.List;

/**
 * @author DaoDao
 */
@Tag(name = "RBAC", description = "基于角色的权限控制接口")
@HttpExchange(url = AppConstant.API)
public interface RbacApi {

    @Operation(summary = "自己的角色权限对象集合", description = "header 中传入 token 信息，获取用户自己的角色权限信息，常用于前端获取数据加载到状态管理中。返回完整的角色和权限对象集合")
    @PostExchange(url = "role-permission")
    Response<RolePermissionData> rolePermission(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization, @RequestBody @Valid Request<No> request);

    @Operation(summary = "用户的角色列表", description = "查询用户的角色列表")
    @PostExchange(url = "user-role-list")
    Response<List<RoleData>> userRoleList(@RequestBody @Valid Request<IdParam> request);

    @Operation(summary = "角色的权限列表", description = "查询角色的权限列表")
    @PostExchange(url = "role-permission-list")
    Response<List<PermissionData>> rolePermissionList(@RequestBody @Valid Request<IdParam> request);

    @Operation(summary = "变更用户的角色", description = "传入用户的 id，角色的 id，赋予状态，变更用户的角色")
    @PostExchange(url = "user-role-upsert")
    Response<Ok> userRoleUpsert(@RequestBody @Valid Request<UserRoleUpsert> request);

    @Operation(summary = "变更角色的权限", description = "传入角色的 id，权限的 id，赋予状态，变更角色的权限")
    @PostExchange(url = "role-permission-upsert")
    Response<Ok> rolePermissionUpsert(@RequestBody @Valid Request<RolePermissionUpsert> request);

}
