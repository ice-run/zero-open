package run.ice.zero.gateway.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import run.ice.zero.common.constant.AppConstant;

@Tag(name = "Index", description = "Index")
@HttpExchange(url = "")
public interface IndexApi {

    @Operation(summary = "index", description = AppConstant.SLOGAN)
    @GetExchange(url = "")
    String index();

}
