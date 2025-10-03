package run.ice.zero.common.config;

import io.micrometer.observation.ObservationPredicate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.observation.ServerRequestObservationContext;

/**
 * @author DaoDao
 */
@Configuration
public class ObservationConfig {

    @Bean
    public ObservationPredicate ignoreObservationPredicate() {
        return (name, context) -> {
            if ((name.equals("http.server.requests") || name.equals("http.client.requests")) && context instanceof ServerRequestObservationContext serverContext) {
                HttpServletRequest request = serverContext.getCarrier();
                // String method = request.getMethod();
                // return !"OPTIONS".equals(method) && !"HEAD".equals(method) && !"GET".equals(method);
                String uri = request.getRequestURI();
                // return !uri.startsWith("/actuator");
                String regex = "^/actuator.*$";
                return !uri.matches(regex);
            }
            return true;
        };
    }

}
