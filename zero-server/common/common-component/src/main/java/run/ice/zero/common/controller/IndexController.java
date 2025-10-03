package run.ice.zero.common.controller;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RestController;
import run.ice.zero.common.api.IndexApi;
import run.ice.zero.common.config.CommonConfig;
import run.ice.zero.common.constant.AppConstant;

/**
 * @author DaoDao
 */
@RestController
public class IndexController implements IndexApi {

    @Resource
    private CommonConfig commonConfig;

    public String index() {
        String slogan = commonConfig.getSlogan();
        return (null != slogan && !slogan.isEmpty()) ? slogan : AppConstant.SLOGAN;
    }

}
