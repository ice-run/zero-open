package run.ice.zero.gateway.config;

import io.micrometer.observation.ObservationPredicate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.observation.ServerRequestObservationContext;

import java.net.URI;

/**
 * @author DaoDao
 */
@Configuration
public class ObservationConfig {

    @Bean
    public ObservationPredicate ignoreObservationPredicate() {
        return (name, context) -> {
            if ((name.equals("http.server.requests") || name.equals("http.client.requests")) && context instanceof ServerRequestObservationContext serverContext) {
                ServerHttpRequest request = serverContext.getCarrier();
                // HttpMethod method = request.getMethod();
                // return !HttpMethod.OPTIONS.equals(method) && !HttpMethod.HEAD.equals(method) && !HttpMethod.GET.equals(method);
                URI uri = request.getURI();
                // return !uri.getPath().startsWith("/actuator");
                String regex = "^/actuator.*$";
                return !uri.getPath().matches(regex);
            }
            return true;
        };
    }

}
