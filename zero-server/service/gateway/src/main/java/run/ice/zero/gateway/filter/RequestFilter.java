package run.ice.zero.gateway.filter;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import run.ice.zero.gateway.helper.RequestHelper;

/**
 * 重写请求 body 过滤器
 *
 * @author DaoDao
 */
@Slf4j
@Component
public class RequestFilter implements GlobalFilter, Ordered {

    @Resource
    private RequestHelper requestHelper;

    @Override
    public int getOrder() {
        return -3;
    }

    /**
     * 重写 request body
     *
     * @param exchange ServerWebExchange
     * @param chain    GatewayFilterChain
     * @return Mono<Void>
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        return requestHelper.filter(exchange, chain);

    }

}
