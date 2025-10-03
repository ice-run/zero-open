package run.ice.zero.api.base.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import run.ice.zero.api.base.api.CaptchaApi;
import run.ice.zero.api.base.api.DictCodeApi;
import run.ice.zero.api.base.api.FileApi;
import run.ice.zero.api.base.api.MailApi;

/**
 * @author DaoDao
 */
@Configuration
public class BaseClient {

    private final HttpServiceProxyFactory httpServiceProxyFactory;

    @Autowired
    public BaseClient(@Value("${zero.service.zero.base:base.zero}") String service, RestClient.Builder builder) {
        String url = (service.matches("^https?://.*") ? service : "http://" + service) + "/";
        this.httpServiceProxyFactory = HttpServiceProxyFactory.builderFor(RestClientAdapter.create(builder.baseUrl(url).build())).build();
    }

    @Bean
    public DictCodeApi dictCodeApi() {
        return httpServiceProxyFactory.createClient(DictCodeApi.class);
    }

    @Bean
    public FileApi fileApi() {
        return httpServiceProxyFactory.createClient(FileApi.class);
    }

    @Bean
    public CaptchaApi captchaApi() {
        return httpServiceProxyFactory.createClient(CaptchaApi.class);
    }

    @Bean
    public MailApi mailApi() {
        return httpServiceProxyFactory.createClient(MailApi.class);
    }

}
