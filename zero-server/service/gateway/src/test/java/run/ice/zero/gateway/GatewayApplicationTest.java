package run.ice.zero.gateway;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import run.ice.zero.common.constant.AppConstant;
import run.ice.zero.common.model.Request;
import run.ice.zero.common.model.Response;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author DaoDao
 */
@Slf4j
@Rollback
//@Transactional
@ActiveProfiles("local")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GatewayApplicationTest {

    private String uri;

    @Resource
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        uri = "/" + AppConstant.API + "/";
    }

    @AfterEach
    void tearDown() {
        assert true;
    }

    public <P, D> Response<D> webTest(String api, Request<P> request, ParameterizedTypeReference<Response<D>> typeReference) {

        String url = uri + api;
        log.debug("{}", url);

        log.debug("{}", request.toJson());

        EntityExchangeResult<Response<D>> result = webTestClient.post()
                .uri(url)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(typeReference)
                .returnResult();

        Response<D> response = result.getResponseBody();

        assert response != null;
        log.debug("{}", response.toJson());

        return response;
    }

}