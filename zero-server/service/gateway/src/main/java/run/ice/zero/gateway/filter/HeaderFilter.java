package run.ice.zero.gateway.filter;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import run.ice.zero.gateway.helper.HeaderHelper;

/**
 * header 过滤器
 *
 * @author DaoDao
 */
@Slf4j
@Component
public class HeaderFilter implements GlobalFilter, Ordered {

    @Resource
    private HeaderHelper headerHelper;

    @Override
    public int getOrder() {
        return -9;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        return headerHelper.filterX(exchange, chain);

    }

}
