package run.ice.zero.auth.controller;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RestController;
import run.ice.zero.api.auth.api.RbacApi;
import run.ice.zero.api.auth.model.perm.PermData;
import run.ice.zero.api.auth.model.rbac.RolePermData;
import run.ice.zero.api.auth.model.rbac.RolePermUpsert;
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
    public Response<RolePermData> rolePerm(String authorization, Request<No> request) {
        RolePermData data = rbacService.rolePerm(authorization);
        return new Response<>(data);
    }

    @Override
    public Response<List<RoleData>> userRoleList(Request<IdParam> request) {
        List<RoleData> data = rbacService.userRoleList(request.getParam());
        return new Response<>(data);
    }

    @Override
    public Response<List<PermData>> rolePermList(Request<IdParam> request) {
        List<PermData> data = rbacService.rolePermList(request.getParam());
        return new Response<>(data);
    }

    @Override
    public Response<Ok> userRoleUpsert(Request<UserRoleUpsert> request) {
        rbacService.userRoleUpsert(request.getParam());
        return Response.ok();
    }

    @Override
    public Response<Ok> rolePermUpsert(Request<RolePermUpsert> request) {
        rbacService.rolePermUpsert(request.getParam());
        return Response.ok();
    }

}
