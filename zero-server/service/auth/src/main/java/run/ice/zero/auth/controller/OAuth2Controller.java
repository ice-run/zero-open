package run.ice.zero.auth.controller;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RestController;
import run.ice.zero.api.auth.api.OAuth2Api;
import run.ice.zero.api.auth.model.oauth2.Login;
import run.ice.zero.api.auth.model.oauth2.OAuth2;
import run.ice.zero.auth.service.OAuth2Service;
import run.ice.zero.common.model.*;

/**
 * @author DaoDao
 */
@RestController
public class OAuth2Controller implements OAuth2Api {

    @Resource
    private OAuth2Service oAuth2Service;

    @Override
    public Response<OAuth2> login(Request<Login> request) {
        OAuth2 data = oAuth2Service.login(request.getParam());
        return new Response<>(data);
    }

    @Override
    public Response<OAuth2> idLogin(Request<IdParam> request) {
        OAuth2 data = oAuth2Service.idLogin(request.getParam());
        return new Response<>(data);
    }

    @Override
    public Response<Ok> logout(String authorization, Request<No> request) {
        oAuth2Service.logout(authorization);
        return Response.ok();
    }

}
