package run.ice.zero.gateway.handler;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.webflux.error.ErrorWebExceptionHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import run.ice.zero.gateway.helper.ErrorHelper;

/**
 * @author DaoDao
 */
@Slf4j
@Component
public class GlobalErrorWebExceptionHandler implements ErrorWebExceptionHandler {

    @Resource
    private ErrorHelper errorHelper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {

        return errorHelper.handleX(exchange, ex);

    }

}
