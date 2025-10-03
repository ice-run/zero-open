package run.ice.zero.gateway.filter;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import run.ice.zero.gateway.helper.RewriteHelper;

/**
 * spring 官方提供了预言类：一个 ReadBodyPredicateFactory 谓词工厂，和 ModifyRequestBodyGatewayFilterFactory 过滤器工厂
 * 而且目前 2019-04-20 仍然是 bate 版
 * 并没有直接实现获取 request body 的 filter
 * 此 filter 为参考以上预言类作出的记录 request body 的全局过滤器工厂
 *
 * @author DaoDao
 */
@Slf4j
@Component
public class RewriteFilter implements GlobalFilter, Ordered {

    @Resource
    private RewriteHelper rewriteHelper;

    @Override
    public int getOrder() {
        return -5;
    }

    /**
     * 将 request body 中的内容 copy 一份，记录到 exchange 的一个自定义属性中
     *
     * @param exchange ServerWebExchange
     * @param chain    GatewayFilterChain
     * @return Mono<Void>
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        return rewriteHelper.filterX(exchange, chain);

    }

}
