package run.ice.zero.base.controller;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import run.ice.zero.api.base.model.captcha.CaptchaData;
import run.ice.zero.base.BaseApplicationTest;
import run.ice.zero.common.model.No;
import run.ice.zero.common.model.Request;
import run.ice.zero.common.model.Response;
import tools.jackson.core.type.TypeReference;

@Slf4j
class CaptchaControllerTest extends BaseApplicationTest {

    @Test
    void captchaCode() {

        String api = "captcha-code";

        No param = new No();
        Request<No> request = new Request<>(param);

        Response<CaptchaData> response = mockMvc(api, request, new TypeReference<>() {
        });

        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.isOk());

        CaptchaData data = response.getData();

        Assertions.assertNotNull(data);
        Assertions.assertNotNull(data.getId());
        Assertions.assertNotNull(data.getImage());

    }

}