package run.ice.zero.auth.controller;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RestController;
import run.ice.zero.api.auth.api.RbacApi;
import run.ice.zero.api.auth.model.permission.PermissionData;
import run.ice.zero.api.auth.model.rbac.RolePermissionData;
import run.ice.zero.api.auth.model.rbac.RolePermissionUpsert;
import run.ice.zero.api.auth.model.rbac.UserRoleUpsert;
import run.ice.zero.api.auth.model.role.RoleData;
import run.ice.zero.common.model.*;
import run.ice.zero.auth.service.RbacService;

import java.util.List;

/**
 * @author DaoDao
 */
@RestController
public class RbacController implements RbacApi {

    @Resource
    private RbacService rbacService;

    @Override
    public Response<RolePermissionData> rolePermission(String authorization, Request<No> request) {
        RolePermissionData data = rbacService.rolePermission(authorization);
        return new Response<>(data);
    }

    @Override
    public Response<List<RoleData>> userRoleList(Request<IdParam> request) {
        List<RoleData> data = rbacService.userRoleList(request.getParam());
        return new Response<>(data);
    }

    @Override
    public Response<List<PermissionData>> rolePermissionList(Request<IdParam> request) {
        List<PermissionData> data = rbacService.rolePermissionList(request.getParam());
        return new Response<>(data);
    }

    @Override
    public Response<Ok> userRoleUpsert(Request<UserRoleUpsert> request) {
        rbacService.userRoleUpsert(request.getParam());
        return Response.ok();
    }

    @Override
    public Response<Ok> rolePermissionUpsert(Request<RolePermissionUpsert> request) {
        rbacService.rolePermissionUpsert(request.getParam());
        return Response.ok();
    }

}
