package run.ice.zero.auth.controller;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import run.ice.zero.api.auth.model.oauth2.Login;
import run.ice.zero.api.auth.model.oauth2.OAuth2;
import run.ice.zero.auth.AuthApplicationTest;
import run.ice.zero.common.model.Request;
import run.ice.zero.common.model.Response;
import tools.jackson.core.type.TypeReference;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class OAuth2ControllerTest extends AuthApplicationTest {

    @Test
    void login() {
        String api = "login";
        Login param = new Login();
        param.setUsername("admin");
        param.setPassword("admin");
        param.setCaptchaId("1990");
        param.setCaptchaCode("0607");
        Request<Login> request = new Request<>(param);
        Response<OAuth2> response = mockMvc(api, request, new TypeReference<>() {
        });
        assertNotNull(response);
        assertNotNull(response.getData());
    }

    @Test
    void idLogin() {

    }

    @Test
    void logout() {

    }

}