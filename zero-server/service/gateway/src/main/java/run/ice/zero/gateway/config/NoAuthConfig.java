package run.ice.zero.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author DaoDao
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "zero.no-auth")
public class NoAuthConfig {

    /**
     * 白名单 uri 匹配前缀
     */
    private List<String> uri;

}
