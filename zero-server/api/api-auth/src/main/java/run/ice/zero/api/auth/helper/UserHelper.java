package run.ice.zero.api.auth.helper;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import run.ice.zero.api.auth.api.UserApi;
import run.ice.zero.api.auth.error.AuthError;
import run.ice.zero.api.auth.model.user.UserData;
import run.ice.zero.common.error.AppError;
import run.ice.zero.common.error.AppException;
import run.ice.zero.common.model.Request;
import run.ice.zero.common.model.Response;

import java.net.URI;
import java.util.LinkedHashMap;

@Slf4j
@Component
public class UserHelper {

    @Value("${zero.service.zero.auth:auth.zero}")
    private String auth;

    @Resource
    private RestTemplate restTemplate;

    @Lazy
    @Resource
    private UserApi userApi;

    public UserData user() {
        String authorization = getHeader(HttpHeaders.AUTHORIZATION);
        if (null == authorization || authorization.isEmpty()) {
            log.error("header authorization is null");
            throw new AppException(AppError.TOKEN_ERROR);
        }
        if (!authorization.startsWith("Bearer")) {
            authorization = String.format("Bearer %s", authorization);
        }
        Response<UserData> response;
        try {
            response = userApi.userInfo(authorization, Request.no());
        } catch (Exception e) {
            log.error("userApi.user error", e);
            throw new AppException(AuthError.INVOKE_USER_EXCEPTION, e);
        }
        if (!AppError.OK.code.equals(response.getCode())) {
            log.error("userApi.user error : {}", response.toJson());
            throw new AppException(AuthError.INVOKE_USER_EXCEPTION, response.toJson());
        }
        return response.getData();
    }

    public String token(Long id) {
        String url = (auth.matches("^https?://.*") ? auth : "http://" + auth) + "/oauth2/token";
        URI uri = URI.create(url);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "id");
        requestBody.add("client_id", "zero");
        requestBody.add("client_secret", "zero");
        requestBody.add("scope", "openid");
        requestBody.add("id", id);
        RequestEntity<?> requestEntity = new RequestEntity<>(requestBody, headers, HttpMethod.POST, uri);
        ParameterizedTypeReference<LinkedHashMap<String, Object>> typeReference = new ParameterizedTypeReference<>() {
        };
        ResponseEntity<LinkedHashMap<String, Object>> responseEntity;
        try {
            responseEntity = restTemplate.exchange(requestEntity, typeReference);
        } catch (Exception e) {
            log.error("restTemplate.exchange error", e);
            throw new AppException(AuthError.INVOKE_AUTH_EXCEPTION, e);
        }
        if (!responseEntity.getStatusCode().is2xxSuccessful() || null == responseEntity.getBody()) {
            log.error("restTemplate.exchange error : {}", responseEntity);
            throw new AppException(AuthError.INVOKE_AUTH_EXCEPTION, responseEntity.toString());
        }
        return (String) responseEntity.getBody().get("access_token");
    }

    private HttpServletRequest getHttpServletRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert attributes != null;
        return attributes.getRequest();
    }

    private String getHeader(String name) {
        return getHttpServletRequest().getHeader(name);
    }

}
