package run.ice.zero.api.auth.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import run.ice.zero.api.auth.model.user.UserData;
import run.ice.zero.api.auth.model.user.UserSearch;
import run.ice.zero.api.auth.model.user.UserUpsert;
import run.ice.zero.common.constant.AppConstant;
import run.ice.zero.common.model.*;

/**
 * @author DaoDao
 */
@Tag(name = "用户", description = "用户接口")
@HttpExchange(url = AppConstant.API)
public interface UserApi {

    @Operation(summary = "用户信息", description = "header 中传入 token 信息，获取用户信息")
    @PostExchange(url = "user-info")
    Response<UserData> userInfo(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization, @RequestBody @Valid Request<No> request);

    @Operation(summary = "查询用户", description = "传入 id 查询用户信息")
    @PostExchange(url = "user-select")
    Response<UserData> userSelect(@RequestBody @Valid Request<IdParam> request);

    @Operation(summary = "写入用户", description = "传入用户信息，新增或更新一个用户")
    @PostExchange(url = "user-upsert")
    Response<UserData> userUpsert(@RequestBody @Valid Request<UserUpsert> request);

    @Operation(summary = "搜索用户", description = "传入用户信息，搜索用户列表")
    @PostExchange(url = "user-search")
    Response<PageData<UserData>> userSearch(@RequestBody @Valid Request<PageParam<UserSearch>> request);

}
