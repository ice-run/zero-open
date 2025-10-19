package run.ice.zero.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import run.ice.zero.api.demo.constant.DemoConstant;
import run.ice.zero.api.demo.model.Cat;
import run.ice.zero.api.demo.model.Dog;
import run.ice.zero.common.constant.AppConstant;
import run.ice.zero.common.model.Request;
import run.ice.zero.common.model.Response;
import run.ice.zero.demo.DemoApplicationTest;
import tools.jackson.core.type.TypeReference;

/**
 * @author DaoDao
 */
@Slf4j
class DemoControllerTest extends DemoApplicationTest {

    @Test
    void demo() {

        String api = DemoConstant.DEMO;

        Cat param = new Cat();
        param.setName(AppConstant.SLOGAN);
        Request<Cat> request = new Request<>(param);

        Response<Dog> response = mockMvc(api, request, new TypeReference<>() {
        });

        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.isOk());

        Dog data = response.getData();

        Assertions.assertNotNull(data);
        Assertions.assertNotNull(data.getName());

    }

}