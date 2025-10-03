package run.ice.zero.base.controller;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RestController;
import run.ice.zero.api.base.api.CaptchaApi;
import run.ice.zero.api.base.model.captcha.CaptchaData;
import run.ice.zero.api.base.model.captcha.CaptchaParam;
import run.ice.zero.base.service.CaptchaService;
import run.ice.zero.common.model.No;
import run.ice.zero.common.model.Ok;
import run.ice.zero.common.model.Request;
import run.ice.zero.common.model.Response;

/**
 * @author DaoDao
 */
@RestController
public class CaptchaController implements CaptchaApi {

    @Resource
    private CaptchaService captchaService;

    @Override
    public Response<CaptchaData> captchaCode(Request<No> request) {
        CaptchaData data = captchaService.captchaCode();
        return new Response<>(data);
    }

    @Override
    public Response<Ok> captchaCheck(Request<CaptchaParam> request) {
        captchaService.captchaCheck(request.getParam());
        return Response.ok();
    }

}
