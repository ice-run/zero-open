package run.ice.zero.base.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author DaoDao
 */
@Data
@Configuration
public class AppConfig {

    @Value("${spring.application.name:}")
    private String application;

    @Value("${zero.file.path:/data/file/}")
    private String filePath;

    /**
     * 是否启用验证码万能密钥
     */
    @Value("${zero.captcha.master-key.enabled:false}")
    private Boolean captchaMasterKeyEnabled;

    /**
     * 验证码万能密钥默认值
     */
    @Value("${zero.captcha.master-key.value:0607}")
    private String captchaMasterKeyValue;

}
