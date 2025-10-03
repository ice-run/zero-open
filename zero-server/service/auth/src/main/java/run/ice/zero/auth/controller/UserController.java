package run.ice.zero.auth.controller;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RestController;
import run.ice.zero.api.auth.api.UserApi;
import run.ice.zero.api.auth.model.user.UserData;
import run.ice.zero.api.auth.model.user.UserSearch;
import run.ice.zero.api.auth.model.user.UserUpsert;
import run.ice.zero.common.model.*;
import run.ice.zero.auth.service.UserService;

/**
 * @author DaoDao
 */
@RestController
public class UserController implements UserApi {

    @Resource
    private UserService userService;

    @Override
    public Response<UserData> userInfo(String authorization, Request<No> request) {
        UserData data = userService.userInfo(authorization);
        return new Response<>(data);
    }

    @Override
    public Response<UserData> userSelect(Request<IdParam> request) {
        UserData data = userService.userSelect(request.getParam());
        return new Response<>(data);
    }

    @Override
    public Response<UserData> userUpsert(Request<UserUpsert> request) {
        UserData data = userService.userUpsert(request.getParam());
        return new Response<>(data);
    }

    @Override
    public Response<PageData<UserData>> userSearch(Request<PageParam<UserSearch>> request) {
        PageData<UserData> data = userService.userSearch(request.getParam());
        return new Response<>(data);
    }

}
