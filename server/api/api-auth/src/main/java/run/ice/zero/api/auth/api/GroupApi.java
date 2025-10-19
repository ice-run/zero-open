package run.ice.zero.api.auth.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import run.ice.zero.api.auth.model.group.GroupData;
import run.ice.zero.api.auth.model.group.GroupSearch;
import run.ice.zero.api.auth.model.group.GroupUpsert;
import run.ice.zero.common.constant.AppConstant;
import run.ice.zero.common.model.*;

/**
 * @author DaoDao
 */
@Tag(name = "组织", description = "组织接口")
@HttpExchange(url = AppConstant.API)
public interface GroupApi {

    @Operation(summary = "组织查询", description = "传入组织 ID，查询一条组织数据")
    @PostExchange(url = "group-select")
    Response<GroupData> groupSelect(@RequestBody @Valid Request<IdParam> request);

    @Operation(summary = "组织写入", description = "传入组织新增或修改参数 ，写入组织数据")
    @PostExchange(url = "group-upsert")
    Response<GroupData> groupUpsert(@RequestBody @Valid Request<GroupUpsert> request);

    @Operation(summary = "组织搜索", description = "传入组织查询参数，查询分页组织数据")
    @PostExchange(url = "group-search")
    Response<PageData<GroupData>> groupSearch(@RequestBody @Valid Request<PageParam<GroupSearch>> request);

}
