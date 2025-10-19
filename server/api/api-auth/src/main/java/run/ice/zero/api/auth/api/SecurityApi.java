package run.ice.zero.api.auth.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import run.ice.zero.api.auth.model.security.ChangePassword;
import run.ice.zero.api.auth.model.security.ResetPassword;
import run.ice.zero.common.constant.AppConstant;
import run.ice.zero.common.model.Ok;
import run.ice.zero.common.model.Request;
import run.ice.zero.common.model.Response;

/**
 * @author DaoDao
 */
@Tag(name = "安全", description = "用户安全")
@HttpExchange(url = AppConstant.API)
public interface SecurityApi {

    @Operation(summary = "重置密码", description = "输入 用户 id 和 密码，重新设置密码")
    @PostExchange(url = "reset-password")
    Response<Ok> resetPassword(@RequestBody @Valid Request<ResetPassword> request);

    @Operation(summary = "变更密码", description = "输入 新密码和旧密码，设置用户密码")
    @PostExchange(url = "change-password")
    Response<Ok> changePassword(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization, @RequestBody @Valid Request<ChangePassword> request);

}
