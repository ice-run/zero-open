package run.ice.zero.gateway.filter;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import run.ice.zero.gateway.helper.ResponseHelper;

/**
 * 重写 response body 过滤器
 *
 * @author DaoDao
 */
@Slf4j
@Component
public class ResponseFilter implements GlobalFilter, Ordered {

    @Resource
    private ResponseHelper responseHelper;

    @Override
    public int getOrder() {
        return -2;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        return responseHelper.filterX(exchange, chain);

    }

}
