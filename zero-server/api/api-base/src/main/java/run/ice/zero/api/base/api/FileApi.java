package run.ice.zero.api.base.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import run.ice.zero.api.base.model.file.FileData;
import run.ice.zero.api.base.model.file.FileParam;
import run.ice.zero.common.constant.AppConstant;
import run.ice.zero.common.model.Request;
import run.ice.zero.common.model.Response;

/**
 * @author DaoDao
 */
@Tag(name = "文件", description = "文件接口")
@HttpExchange(url = AppConstant.API)
public interface FileApi {

    @Operation(summary = "info 文件信息", description = "传入文件 id，查询文件信息")
    @PostExchange(url = "file-info")
    Response<FileData> fileInfo(@RequestBody @Valid Request<FileParam> request);

}
