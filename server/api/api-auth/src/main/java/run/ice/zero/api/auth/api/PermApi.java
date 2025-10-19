package run.ice.zero.api.auth.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import run.ice.zero.api.auth.model.perm.PermData;
import run.ice.zero.api.auth.model.perm.PermSearch;
import run.ice.zero.api.auth.model.perm.PermUpsert;
import run.ice.zero.common.constant.AppConstant;
import run.ice.zero.common.model.*;

/**
 * @author DaoDao
 */
@Tag(name = "权限", description = "权限接口")
@HttpExchange(url = AppConstant.API)
public interface PermApi {

    @Operation(summary = "权限查询", description = "传入权限 ID，查询一条权限数据")
    @PostExchange(url = "perm-select")
    Response<PermData> permSelect(@RequestBody @Valid Request<IdParam> request);

    @Operation(summary = "权限写入", description = "传入权限新增或修改参数 ，写入权限数据")
    @PostExchange(url = "perm-upsert")
    Response<PermData> permUpsert(@RequestBody @Valid Request<PermUpsert> request);

    @Operation(summary = "权限搜索", description = "传入权限查询参数，查询分页权限数据")
    @PostExchange(url = "perm-search")
    Response<PageData<PermData>> permSearch(@RequestBody @Valid Request<PageParam<PermSearch>> request);

}
