package run.ice.zero.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;

/**
 * @author DaoDao
 */
@Slf4j
@Configuration
public class GatewayConfig {

    @Bean
    public WebClient webClient() {
        WebClient.Builder builder = WebClient.builder();
        builder.filter(restLoggerFilter());
        return builder.build();
    }

    private static ExchangeFilterFunction restLoggerFilter() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            URI uri = clientRequest.url();
            HttpMethod method = clientRequest.method();
            int limit = 1024 * 10;

            log.info("C > : {} {}", method, uri);

            HttpHeaders requestHeaders = clientRequest.headers();
            log.info("C > : {}", requestHeaders);

            if (HttpMethod.POST.equals(method)) {

                // todo 记录 body

            }
            return Mono.just(clientRequest);
        }).andThen(ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            HttpMethod method = clientResponse.request().getMethod();
            HttpStatusCode statusCode = clientResponse.statusCode();

            if (statusCode.isError()) {
                String statusText = clientResponse.statusCode().toString();
                log.error("C < : {} {}", statusCode.value(), statusText);
            }

            HttpHeaders responseHeaders = clientResponse.headers().asHttpHeaders();
            log.info("C < : {}", responseHeaders);

            if (HttpMethod.POST.equals(method)) {
                return clientResponse.bodyToMono(DataBuffer.class).map(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    String responseBody = bytes.length > 1024 * 10 ? (new String(bytes, 0, 1024 * 10) + " ...") : new String(bytes);
                    log.info("C < : {}", responseBody);

                    return clientResponse.mutate().body(new String(bytes)).build();
                });
            }
            return Mono.just(clientResponse);
        }));
    }

}
