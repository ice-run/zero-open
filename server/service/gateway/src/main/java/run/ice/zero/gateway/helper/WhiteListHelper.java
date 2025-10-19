package run.ice.zero.gateway.helper;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.server.RequestPath;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import run.ice.zero.common.constant.AppConstant;
import run.ice.zero.common.error.AppException;
import run.ice.zero.gateway.config.AppConfig;
import run.ice.zero.gateway.config.WhiteListConfig;
import run.ice.zero.gateway.constant.FilterConstant;
import run.ice.zero.gateway.error.GatewayError;

import java.net.URI;
import java.util.LinkedHashSet;
import java.util.List;

@Slf4j
@Component
public class WhiteListHelper {

    @Resource
    private AppConfig appConfig;

    @Resource
    private WhiteListConfig whiteListConfig;

    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        if (Boolean.TRUE.equals(exchange.getAttribute(FilterConstant.NO_FILTER))) {
            return chain.filter(exchange);
        }

        LinkedHashSet<URI> uris = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ORIGINAL_REQUEST_URL_ATTR);
        String uri = null;
        if (uris != null) {
            for (URI u : uris) {
                if ("lb".equals(u.getScheme())) {
                    uri = "/" + u.getHost() + u.getPath();
                    break;
                }
            }
        } else {
            RequestPath requestPath = exchange.getRequest().getPath();
            uri = requestPath.value();
        }

        if (null != uri && !uri.isEmpty()) {
            exchange.getAttributes().put(FilterConstant.URI, uri);
        }

        List<String> patterns = whiteListConfig.getUri();
        if (null != patterns && !patterns.isEmpty()) {
            // 使用 AntPathMatcher 进行 Ant Path 匹配，相比 equals 或 startWith 或 regex 更灵活
            PathMatcher pathMatcher = new AntPathMatcher();
            for (String pattern : patterns) {
                if (null != uri && pathMatcher.match(pattern, uri)) {
                    exchange.getAttributes().put(FilterConstant.IN_WHITE_LIST, Boolean.TRUE);
                    break;
                }
            }
        }

        Boolean inWhiteList = exchange.getAttribute(FilterConstant.IN_WHITE_LIST);
        if (!Boolean.TRUE.equals(inWhiteList)) {
            throw new AppException(GatewayError.ILLEGAL_REQUEST, AppConstant.NAMESPACE + " : " + appConfig.getApplication() + " : " + uri);
        }

        return chain.filter(exchange);

    }

}
