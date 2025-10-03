package run.ice.zero.auth.controller;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RestController;
import run.ice.zero.api.auth.api.RoleApi;
import run.ice.zero.api.auth.model.role.RoleData;
import run.ice.zero.api.auth.model.role.RoleSearch;
import run.ice.zero.api.auth.model.role.RoleUpsert;
import run.ice.zero.common.model.*;
import run.ice.zero.auth.service.RoleService;

/**
 * @author DaoDao
 */
@RestController
public class RoleController implements RoleApi {

    @Resource
    private RoleService roleService;

    @Override
    public Response<RoleData> roleSelect(Request<IdParam> request) {
        RoleData data = roleService.roleSelect(request.getParam());
        return new Response<>(data);
    }

    @Override
    public Response<RoleData> roleUpsert(Request<RoleUpsert> request) {
        RoleData data = roleService.roleUpsert(request.getParam());
        return new Response<>(data);
    }

    @Override
    public Response<PageData<RoleData>> roleSearch(Request<PageParam<RoleSearch>> request) {
        PageData<RoleData> data = roleService.roleSearch(request.getParam());
        return new Response<>(data);
    }

}
