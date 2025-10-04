package run.ice.zero.auth.helper;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import run.ice.zero.api.auth.error.AuthError;
import run.ice.zero.api.auth.model.oauth2.OAuth2;
import run.ice.zero.common.error.AppError;
import run.ice.zero.common.error.AppException;

import java.net.URI;
import java.util.Map;

@Slf4j
@Component
public class AuthHelper {

    @Value("${zero.service.zero.auth:auth.zero}")
    private String oAuth2;

    @Value("${spring.security.oauth2.resourceserver.opaque-token.client-id:zero}")
    private String clientId;

    @Value("${spring.security.oauth2.resourceserver.opaque-token.client-secret:zero}")
    private String clientSecret;

    @Value("${spring.security.oauth2.resourceserver.opaque-token.introspection-uri:http://auth.zero/oauth2/introspect}")
    private String introspectionUri;

    @Resource
    private RestTemplate restTemplate;

    public String username(String token) {
        Map<String, Object> body = introspect(token);
        Object active = body.get("active");
        if (!Boolean.TRUE.equals(active)) {
            throw new AppException(AppError.TOKEN_ERROR, body.toString());
        }
        String sub = (String) body.get("sub");
        if (null == sub) {
            throw new AppException(AuthError.INVOKE_AUTH_EXCEPTION, body.toString());
        }
        return sub;
    }

    public OAuth2 password(String username, String password) {
        String url = (oAuth2.matches("^https?://.*") ? oAuth2 : "http://" + oAuth2) + "/oauth2/token";
        URI uri = URI.create(url);
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(clientId, clientSecret);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "password");
        requestBody.add("username", username);
        requestBody.add("password", password);
        requestBody.add("scope", "openid");
        RequestEntity<?> requestEntity = new RequestEntity<>(requestBody, headers, HttpMethod.POST, uri);
        ParameterizedTypeReference<OAuth2> typeReference = new ParameterizedTypeReference<>() {
        };
        ResponseEntity<OAuth2> responseEntity;
        try {
            responseEntity = restTemplate.exchange(requestEntity, typeReference);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new AppException(AuthError.INVOKE_AUTH_EXCEPTION, e.getMessage());
        }
        HttpStatusCode httpStatusCode = responseEntity.getStatusCode();
        if (!httpStatusCode.is2xxSuccessful()) {
            throw new AppException(AuthError.INVOKE_AUTH_EXCEPTION, httpStatusCode.toString());
        }
        OAuth2 responseBody = responseEntity.getBody();
        if (null == responseBody) {
            throw new AppException(AuthError.INVOKE_AUTH_EXCEPTION, responseEntity.toString());
        }
        return responseBody;
    }

    public OAuth2 id(Long id) {
        String url = (oAuth2.matches("^https?://.*") ? oAuth2 : "http://" + oAuth2) + "/oauth2/token";
        URI uri = URI.create(url);
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(clientId, clientSecret);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "id");
        requestBody.add("id", id.toString());
        requestBody.add("scope", "openid");
        RequestEntity<?> requestEntity = new RequestEntity<>(requestBody, headers, HttpMethod.POST, uri);
        ParameterizedTypeReference<OAuth2> typeReference = new ParameterizedTypeReference<>() {
        };
        ResponseEntity<OAuth2> responseEntity;
        try {
            responseEntity = restTemplate.exchange(requestEntity, typeReference);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new AppException(AuthError.INVOKE_AUTH_EXCEPTION, e.getMessage());
        }
        HttpStatusCode httpStatusCode = responseEntity.getStatusCode();
        if (!httpStatusCode.is2xxSuccessful()) {
            throw new AppException(AuthError.INVOKE_AUTH_EXCEPTION, httpStatusCode.toString());
        }
        OAuth2 responseBody = responseEntity.getBody();
        if (null == responseBody) {
            throw new AppException(AuthError.INVOKE_AUTH_EXCEPTION, responseEntity.toString());
        }
        return responseBody;
    }

    public void revoke(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(clientId, clientSecret);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        String url = (oAuth2.matches("^https?://.*") ? oAuth2 : "http://" + oAuth2) + "/oauth2/revoke";
        URI uri = URI.create(url);
        MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("token", token);
        RequestEntity<?> requestEntity = new RequestEntity<>(requestBody, headers, HttpMethod.POST, uri);
        ParameterizedTypeReference<Void> typeReference = new ParameterizedTypeReference<>() {
        };
        ResponseEntity<Void> responseEntity;
        try {
            responseEntity = restTemplate.exchange(requestEntity, typeReference);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new AppException(AuthError.INVOKE_AUTH_EXCEPTION, e.getMessage());
        }
        HttpStatusCode httpStatusCode = responseEntity.getStatusCode();
        if (!httpStatusCode.is2xxSuccessful()) {
            throw new AppException(AuthError.INVOKE_AUTH_EXCEPTION, httpStatusCode.toString());
        }
    }

    public Map<String, Object> introspect(String token) {
        URI uri = URI.create(introspectionUri);
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(clientId, clientSecret);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("token", token);
        RequestEntity<?> requestEntity = new RequestEntity<>(requestBody, headers, HttpMethod.POST, uri);
        ParameterizedTypeReference<Map<String, Object>> typeReference = new ParameterizedTypeReference<>() {
        };
        ResponseEntity<Map<String, Object>> responseEntity;
        try {
            responseEntity = restTemplate.exchange(requestEntity, typeReference);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new AppException(AuthError.INVOKE_AUTH_EXCEPTION, e.getMessage());
        }
        HttpStatusCode httpStatusCode = responseEntity.getStatusCode();
        if (!httpStatusCode.is2xxSuccessful()) {
            throw new AppException(AuthError.INVOKE_AUTH_EXCEPTION, httpStatusCode.toString());
        }
        Map<String, Object> responseBody = responseEntity.getBody();
        if (null == responseBody) {
            throw new AppException(AuthError.INVOKE_AUTH_EXCEPTION, responseEntity.toString());
        }
        return responseBody;
    }

}
