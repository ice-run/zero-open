package run.ice.zero.api.auth.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import run.ice.zero.api.auth.model.role.RoleData;
import run.ice.zero.api.auth.model.role.RoleSearch;
import run.ice.zero.api.auth.model.role.RoleUpsert;
import run.ice.zero.common.constant.AppConstant;
import run.ice.zero.common.model.*;

/**
 * @author DaoDao
 */
@Tag(name = "角色", description = "用户服务 角色")
@HttpExchange(url = AppConstant.API)
public interface RoleApi {

    @Operation(summary = "角色查询", description = "传入角色 ID，查询一条角色数据")
    @PostExchange(url = "role-select")
    Response<RoleData> roleSelect(@RequestBody @Valid Request<IdParam> request);

    @Operation(summary = "角色写入", description = "传入角色新增或修改参数 ，写入角色数据")
    @PostExchange(url = "role-upsert")
    Response<RoleData> roleUpsert(@RequestBody @Valid Request<RoleUpsert> request);

    @Operation(summary = "角色搜索", description = "传入角色查询参数，查询分页角色数据")
    @PostExchange(url = "role-search")
    Response<PageData<RoleData>> roleSearch(@RequestBody @Valid Request<PageParam<RoleSearch>> request);

}
