package run.ice.zero.demo.controller;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RestController;
import run.ice.zero.api.demo.api.DemoApi;
import run.ice.zero.api.demo.model.Cat;
import run.ice.zero.api.demo.model.Dog;
import run.ice.zero.common.model.Request;
import run.ice.zero.common.model.Response;
import run.ice.zero.demo.service.DemoService;

/**
 * @author DaoDao
 */
@RestController
public class DemoController implements DemoApi {

    @Resource
    private DemoService demoService;

    @Override
    public Response<Dog> demo(Request<Cat> request) {
        Dog dog = demoService.demo(request.getParam());
        return new Response<>(dog);
    }

}
