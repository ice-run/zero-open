package run.ice.zero.auth.controller;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RestController;
import run.ice.zero.api.auth.api.SecurityApi;
import run.ice.zero.api.auth.model.security.ChangePassword;
import run.ice.zero.api.auth.model.security.ResetPassword;
import run.ice.zero.common.model.Ok;
import run.ice.zero.common.model.Request;
import run.ice.zero.common.model.Response;
import run.ice.zero.auth.service.SecurityService;

/**
 * @author DaoDao
 */
@RestController
public class SecurityController implements SecurityApi {

    @Resource
    private SecurityService securityService;

    @Override
    public Response<Ok> resetPassword(Request<ResetPassword> request) {
        securityService.resetPassword(request.getParam());
        return Response.ok();
    }

    @Override
    public Response<Ok> changePassword(String authorization, Request<ChangePassword> request) {
        securityService.changePassword(authorization, request.getParam());
        return Response.ok();
    }

}
