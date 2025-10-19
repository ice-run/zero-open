package run.ice.zero.api.auth.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import run.ice.zero.api.auth.model.oauth2.Login;
import run.ice.zero.api.auth.model.oauth2.OAuth2;
import run.ice.zero.common.constant.AppConstant;
import run.ice.zero.common.model.*;

/**
 * @author DaoDao
 */
@Tag(name = "OAuth2", description = "OAuth2")
@HttpExchange(url = AppConstant.API)
public interface OAuth2Api {

    @Operation(summary = "登录", description = "传入 username & password 信息，获取 token")
    @PostExchange(url = "login")
    Response<OAuth2> login(@RequestBody @Valid Request<Login> request);

    @Operation(summary = "id 登录", description = "传入 id 信息，获取 token，此接口仅允许受信任的后端内部服务调用")
    @PostExchange(url = "id-login")
    Response<OAuth2> idLogin(@RequestBody @Valid Request<IdParam> request);

    @Operation(summary = "退出", description = "header 中传入 token 信息，退出登录")
    @PostExchange(url = "logout")
    Response<Ok> logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization, @RequestBody @Valid Request<No> request);

}
