package run.ice.zero.api.demo.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import run.ice.zero.api.demo.constant.DemoConstant;
import run.ice.zero.api.demo.model.Cat;
import run.ice.zero.api.demo.model.Dog;
import run.ice.zero.common.constant.AppConstant;
import run.ice.zero.common.model.Request;
import run.ice.zero.common.model.Response;

/**
 * @author DaoDao
 */
@Tag(name = "DemoApi", description = "示例")
@HttpExchange(url = AppConstant.API)
public interface DemoApi {

    @Operation(summary = "demo", description = "示例接口 @DaoDao")
    @PostExchange(url = DemoConstant.DEMO)
    Response<Dog> demo(@RequestBody @Valid Request<Cat> request);

}
