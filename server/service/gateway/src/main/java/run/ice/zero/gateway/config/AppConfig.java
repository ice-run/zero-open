package run.ice.zero.gateway.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * @author DaoDao
 */
@Data
@Configuration
public class AppConfig {

    @Value("${spring.application.name:}")
    private String application;

    @Value(value = "${zero.print-body-size-limit:1024}")
    private Integer printBodySizeLimit;

    @Value(value = "${zero.request-body-size-limit:1024000}")
    private Integer requestBodySizeLimit;

    @Value(value = "${zero.response-body-size-limit:1024000}")
    private Integer responseBodySizeLimit;

    @Value("${zero.x-time.duration:120s}")
    private Duration xTimeDuration;

    @Value("${zero.x-trace.duration:60s}")
    private Duration xTraceDuration;

}
