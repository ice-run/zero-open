package run.ice.zero.auth.controller;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RestController;
import run.ice.zero.api.auth.api.GroupApi;
import run.ice.zero.api.auth.model.group.GroupData;
import run.ice.zero.api.auth.model.group.GroupSearch;
import run.ice.zero.api.auth.model.group.GroupUpsert;
import run.ice.zero.auth.service.GroupService;
import run.ice.zero.common.model.*;

/**
 * @author DaoDao
 */
@RestController
public class GroupController implements GroupApi {

    @Resource
    private GroupService groupService;

    @Override
    public Response<GroupData> groupSelect(Request<IdParam> request) {
        GroupData data = groupService.groupSelect(request.getParam());
        return new Response<>(data);
    }

    @Override
    public Response<GroupData> groupUpsert(Request<GroupUpsert> request) {
        GroupData data = groupService.groupUpsert(request.getParam());
        return new Response<>(data);
    }

    @Override
    public Response<PageData<GroupData>> groupSearch(Request<PageParam<GroupSearch>> request) {
        PageData<GroupData> data = groupService.groupSearch(request.getParam());
        return new Response<>(data);
    }

}
