package run.ice.zero.gateway.filter;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import run.ice.zero.gateway.helper.WhiteListHelper;

/**
 * 白名单过滤器
 *
 * @author DaoDao
 */
@Slf4j
@Component
public class WhitelistFilter implements GlobalFilter, Ordered {

    @Resource
    private WhiteListHelper whiteListHelper;

    @Override
    public int getOrder() {
        return -7;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        return whiteListHelper.filter(exchange, chain);

    }

}
