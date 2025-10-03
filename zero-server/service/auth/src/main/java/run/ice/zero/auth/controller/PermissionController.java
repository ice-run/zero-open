package run.ice.zero.auth.controller;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RestController;
import run.ice.zero.api.auth.api.PermissionApi;
import run.ice.zero.api.auth.model.permission.PermissionData;
import run.ice.zero.api.auth.model.permission.PermissionSearch;
import run.ice.zero.api.auth.model.permission.PermissionUpsert;
import run.ice.zero.common.model.*;
import run.ice.zero.auth.service.PermissionService;

/**
 * @author DaoDao
 */
@RestController
public class PermissionController implements PermissionApi {

    @Resource
    private PermissionService permissionService;

    @Override
    public Response<PermissionData> permissionSelect(Request<IdParam> request) {
        PermissionData data = permissionService.permissionSelect(request.getParam());
        return new Response<>(data);
    }

    @Override
    public Response<PermissionData> permissionUpsert(Request<PermissionUpsert> request) {
        PermissionData data = permissionService.permissionUpsert(request.getParam());
        return new Response<>(data);
    }

    @Override
    public Response<PageData<PermissionData>> permissionSearch(Request<PageParam<PermissionSearch>> request) {
        PageData<PermissionData> data = permissionService.permissionSearch(request.getParam());
        return new Response<>(data);
    }

}
