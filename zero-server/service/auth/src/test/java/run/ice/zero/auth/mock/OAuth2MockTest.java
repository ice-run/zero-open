package run.ice.zero.auth.mock;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import run.ice.zero.auth.AuthApplicationTest;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Slf4j
public class OAuth2MockTest extends AuthApplicationTest {

    private static final String CLIENT_ID = "zero";
    private static final String CLIENT_SECRET = "zero";

    private static final String USERNAME = "admin";

    private static final String PASSWORD = "admin";

    private static final String USER_ID = "1";

    private static final String SCOPE = "openid";

    @Resource
    private MockMvc mockMvc;

    @Resource
    private ObjectMapper objectMapper;

    @Test
    void index() {

    }

    @Test
    void login() {

    }

    @Test
    void password() {
        String url = "/oauth2/token";
        String response;
        try {
            response = mockMvc.perform(MockMvcRequestBuilders
                            .post(url)
                            .param("grant_type", "password")
                            .param("client_id", CLIENT_ID)
                            .param("client_secret", CLIENT_SECRET)
                            .param("username", USERNAME)
                            .param("password", PASSWORD)
                            .param("scope", SCOPE)
                    ).andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                    .andDo(MockMvcResultHandlers.print())
                    .andReturn()
                    .getResponse()
                    .getContentAsString(Charset.defaultCharset());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        log.info(response);
        TypeReference<Map<String, Object>> typeReference = new TypeReference<>() {
        };
        Map<String, Object> oAuth2Map;
        try {
            oAuth2Map = objectMapper.readValue(response, typeReference);
        } catch (JacksonException e) {
            throw new RuntimeException(e);
        }
        String access_token = (String) oAuth2Map.get("access_token");
        String refresh_token = (String) oAuth2Map.get("refresh_token");
        String id_token = (String) oAuth2Map.get("id_token");
        String scope = (String) oAuth2Map.get("scope");
        String token_type = (String) oAuth2Map.get("token_type");
        Integer expires_in = (Integer) oAuth2Map.get("expires_in");

        Assertions.assertNotNull(access_token);
        Assertions.assertFalse(access_token.isEmpty());
        Assertions.assertNotNull(refresh_token);
        Assertions.assertFalse(refresh_token.isEmpty());
         Assertions.assertNotNull(id_token);
         Assertions.assertFalse(id_token.isEmpty());
        Assertions.assertNotNull(scope);
        Assertions.assertFalse(scope.isEmpty());
        Assertions.assertNotNull(token_type);
        Assertions.assertFalse(token_type.isEmpty());
        Assertions.assertNotNull(expires_in);
        Assertions.assertTrue(expires_in > 0L);

        String basic = new String(Base64.getEncoder().encode((CLIENT_ID+":"+ CLIENT_SECRET).getBytes(StandardCharsets.UTF_8)));

        url = "/oauth2/introspect";
        try {
            response = mockMvc.perform(MockMvcRequestBuilders
                            .post(url)
                            .header("Authorization", "Basic " + basic)
                            .param("token", access_token)
                    ).andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                    .andDo(MockMvcResultHandlers.print())
                    .andReturn()
                    .getResponse()
                    .getContentAsString(Charset.defaultCharset());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        log.info(response);

        try {
            oAuth2Map = objectMapper.readValue(response, typeReference);
        } catch (JacksonException e) {
            throw new RuntimeException(e);
        }
        Boolean active = (Boolean) oAuth2Map.get("active");
        Assertions.assertTrue(active);

        try {
            response = mockMvc.perform(MockMvcRequestBuilders
                            .post(url)
                            .header("Authorization", "Basic " + basic)
                            .param("token", id_token)
                    ).andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                    .andDo(MockMvcResultHandlers.print())
                    .andReturn()
                    .getResponse()
                    .getContentAsString(Charset.defaultCharset());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        log.info(response);
        try {
            oAuth2Map = objectMapper.readValue(response, typeReference);
        } catch (JacksonException e) {
            throw new RuntimeException(e);
        }
        active = (Boolean) oAuth2Map.get("active");
        Assertions.assertTrue(active);

    }

}
