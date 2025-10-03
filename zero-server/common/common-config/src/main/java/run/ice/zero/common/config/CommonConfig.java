package run.ice.zero.common.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author DaoDao
 */
@Data
@Configuration
public class CommonConfig {

    @Value("${spring.application.name:}")
    private String application;

    @Value("${zero.slogan:}")
    private String slogan;

    @Value("${zero.aes-key:0000000000000000}")
    private String aesKey;

    @Value("${zero.aes-iv:0000000000000000}")
    private String aesIv;

    @Value("${zero.proxy.host:squid}")
    private String proxyHost;

    @Value("${zero.proxy.port:3128}")
    private Integer proxyPort;

}
