package run.ice.zero.gateway.helper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.ice.zero.gateway.constant.FilterConstant;

import java.util.Map;

@Slf4j
@Component
public class RequestHelper {

    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        if (Boolean.TRUE.equals(exchange.getAttribute(FilterConstant.NO_FILTER))) {
            return chain.filter(exchange);
        }

        Map<String, String> headerMap = (Map<String, String>) exchange.getAttributes().get(FilterConstant.REQUEST_HEADER_MAP);
        /*
         * 1. 尝试从 exchange 的自定义属性中取出缓存到的 body
         */
        byte[] body = exchange.getAttribute(FilterConstant.REQUEST_BODY_BYTES);
        if (null == body && (null == headerMap || headerMap.isEmpty())) {
            /*
             * 为空，说明已经读过，或者 request body 原本即为空，不做操作，传递到下一个过滤器链
             */
            return chain.filter(exchange);
        }

        if (null != body) {
            log(body);
        }

        /*
         * 3. 将缓存中的 body 数据写入 ServerWebExchange
         */
        DataBufferFactory dataBufferFactory = exchange.getResponse().bufferFactory();
        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders headers = request.getHeaders();
        ServerHttpRequestDecorator decorator = new ServerHttpRequestDecorator(request) {
            /**
             * 使用 ServerHttpRequestDecorator 重新修饰 request body
             * @return Flux<DataBuffer>
             */
            @Override
            public Flux<DataBuffer> getBody() {
                if (null != body) {
                    if (body.length > 0) {
                        return Flux.just(dataBufferFactory.wrap(body));
                    }
                    return Flux.empty();
                } else {
                    return super.getBody();
                }
            }

            /**
             * 重写后的 body 长度有改动，必须重新设置 header 中的 ContentLength
             * @return HttpHeaders
             */
            @Override
            public HttpHeaders getHeaders() {
                long contentLength = headers.getContentLength();
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.putAll(headers);
                if (null != body) {
                    if (contentLength > 0 && body.length > 0) {
                        httpHeaders.setContentLength(body.length);
                    } else {
                        // TODO: this causes a 'HTTP/1.1 411 Length Required' on httpbin.org
                        httpHeaders.set(HttpHeaders.TRANSFER_ENCODING, "chunked");
                    }
                }
                if (null != headerMap && !headerMap.isEmpty()) {
                    headerMap.forEach(httpHeaders::set);
                }
                return httpHeaders;
            }
        };

        /*
         * 4. 将修饰器 mutate 到原 ServerWebExchange 中
         */
        return chain.filter(exchange.mutate().request(decorator).build());

    }

    private void log(byte[] body) {
        int limit = 1024 * 10;
        String requestBody = body.length > limit ? (new String(body, 0, limit) + " ...") : new String(body);
        log.info("X < : {}", requestBody);
    }

}
