package run.ice.zero.gateway.helper;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.PathMatcher;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import run.ice.zero.common.error.AppError;
import run.ice.zero.common.error.AppException;
import run.ice.zero.gateway.config.NoAuthConfig;
import run.ice.zero.gateway.constant.FilterConstant;
import run.ice.zero.gateway.error.GatewayError;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class OAuth2Helper {

    @Value("${spring.security.oauth2.resourceserver.opaque-token.client-id:zero}")
    private String clientId;

    @Value("${spring.security.oauth2.resourceserver.opaque-token.client-secret:zero}")
    private String clientSecret;

    @Value("${spring.security.oauth2.resourceserver.opaque-token.introspection-uri:http://oauth2.zero/oauth2/introspect}")
    private String introspectionUri;

    @Resource
    private NoAuthConfig noAuthConfig;

    @Resource
    private WebClient webClient;

    /**
     * 新版 web 和 app 网关
     */
    public Mono<Void> filterY(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (Boolean.TRUE.equals(exchange.getAttribute(FilterConstant.NO_FILTER))) {
            return chain.filter(exchange);
        }

        String uri = exchange.getAttribute(FilterConstant.URI);

        List<String> patterns = noAuthConfig.getUri();
        if (null != patterns && !patterns.isEmpty()) {
            // 使用 AntPathMatcher 进行 Ant Path 匹配，相比 equals 或 startWith 或 regex 更灵活
            PathMatcher pathMatcher = new AntPathMatcher();
            for (String pattern : patterns) {
                if (null != uri && pathMatcher.match(pattern, uri)) {
                    exchange.getAttributes().put(FilterConstant.IN_NO_AUTH, Boolean.TRUE);
                    break;
                }
            }
        }

        Boolean inNoAuth = exchange.getAttribute(FilterConstant.IN_NO_AUTH);
        if (!Boolean.TRUE.equals(inNoAuth)) {
            /*
             * 获取 token
             */
            String oAuth2Token = exchange.getAttribute(FilterConstant.OAUTH2_TOKEN);

            if (null == oAuth2Token || oAuth2Token.isEmpty()) {
                throw new AppException(GatewayError.TOKEN_ERROR, oAuth2Token);
            } else {
                return introspect(oAuth2Token)
                        .flatMap(isValid -> {
                            if (!isValid) {
                                return Mono.error(new AppException(AppError.TOKEN_ERROR, oAuth2Token));
                            }
                            return chain.filter(exchange);
                        });
            }
        }

        return chain.filter(exchange);
    }

    public Mono<Boolean> introspect(String token) {
        URI uri = URI.create(introspectionUri);
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(clientId, clientSecret);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("token", token);
        log.info("C > : {}", requestBody);

        return webClient.post()
                .uri(uri)
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .map(responseBody -> {
                    Object active = responseBody.get("active");
                    return active != null && (Boolean) active;
                })
                .onErrorReturn(Boolean.FALSE);
    }

}
