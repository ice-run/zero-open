package run.ice.zero.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import run.ice.zero.auth.constant.OAuth2Constant;
import run.ice.zero.common.constant.AppConstant;

@Tag(name = "API", description = "API")
@RestController
@RequestMapping(path = AppConstant.API)
public class ApiController {

    @Operation(summary = "code", description = "code")
    @GetMapping(path = OAuth2Constant.CODE)
    public String code(@RequestParam(value = "code", required = false) String code) {
        return code;
    }

}
