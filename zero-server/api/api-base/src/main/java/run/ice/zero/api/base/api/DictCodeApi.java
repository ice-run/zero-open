package run.ice.zero.api.base.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import run.ice.zero.api.base.model.code.DictCodeData;
import run.ice.zero.api.base.model.code.DictCodeSearch;
import run.ice.zero.api.base.model.code.DictCodeUpsert;
import run.ice.zero.common.constant.AppConstant;
import run.ice.zero.common.model.*;

/**
 * @author DaoDao
 **/
@Tag(name = "代码映射", description = "代码映射接口")
@HttpExchange(url = AppConstant.API)
public interface DictCodeApi {

    @Operation(summary = "代码映射查询", description = "代码映射查询")
    @PostExchange(url = "dict-code-select")
    Response<DictCodeData> dictCodeSelect(@RequestBody @Valid Request<IdParam> request);

    @Operation(summary = "代码映射写入", description = "代码映射写入")
    @PostExchange(url = "dict-code-upsert")
    Response<DictCodeData> dictCodeUpsert(@RequestBody @Valid Request<DictCodeUpsert> request);

    @Operation(summary = "代码映射搜索", description = "代码映射搜索")
    @PostExchange(url = "dict-code-search")
    Response<PageData<DictCodeData>> dictCodeSearch(@RequestBody @Valid Request<PageParam<DictCodeSearch>> request);

}
