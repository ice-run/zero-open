package run.ice.zero.gateway.controller;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import run.ice.zero.common.constant.AppConstant;
import run.ice.zero.gateway.GatewayApplicationTest;

@Slf4j
class IndexControllerTest extends GatewayApplicationTest {

    @Resource
    private WebTestClient webTestClient;

    @Test
    void index() {

        String api = "/";

        EntityExchangeResult<String> result = webTestClient.get().uri(api).exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class)
                .returnResult();
        String responseBody = result.getResponseBody();

        Assertions.assertEquals(AppConstant.SLOGAN, responseBody);

    }

}