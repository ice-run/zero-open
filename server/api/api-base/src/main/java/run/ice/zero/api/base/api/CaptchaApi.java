package run.ice.zero.api.base.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import run.ice.zero.api.base.model.captcha.CaptchaData;
import run.ice.zero.api.base.model.captcha.CaptchaParam;
import run.ice.zero.common.constant.AppConstant;
import run.ice.zero.common.model.No;
import run.ice.zero.common.model.Ok;
import run.ice.zero.common.model.Request;
import run.ice.zero.common.model.Response;

/**
 * @author DaoDao
 */
@Tag(name = "验证码", description = "CaptchaApi")
@HttpExchange(url = AppConstant.API)
public interface CaptchaApi {

    @Operation(summary = "获取验证码", description = "此接口在白名单中，不需要验证 token")
    @PostExchange(url = "captcha-code")
    Response<CaptchaData> captchaCode(@RequestBody @Valid Request<No> request);

    @Operation(summary = "校验验证码", description = "此接口应当只允许后端微服务之间互相校验认证，不允许前端和 APP 与服务端之间进行校验。前端获取验证码，将用户输入的验证码，与后续接口的请求数据，一起传送给后端。由后端校验验证码。")
    @PostExchange(url = "captcha-check")
    Response<Ok> captchaCheck(@RequestBody @Valid Request<CaptchaParam> request);

}
