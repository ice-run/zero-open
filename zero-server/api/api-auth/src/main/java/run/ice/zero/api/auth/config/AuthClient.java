package run.ice.zero.api.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import run.ice.zero.api.auth.api.*;

@Configuration
public class AuthClient {

    private final HttpServiceProxyFactory httpServiceProxyFactory;

    @Autowired
    public AuthClient(@Value("${zero.service.zero.auth:auth.zero}") String service, RestClient.Builder builder) {
        String url = (service.matches("^https?://.*") ? service : "http://" + service) + "/";
        this.httpServiceProxyFactory = HttpServiceProxyFactory.builderFor(RestClientAdapter.create(builder.baseUrl(url).build())).build();
    }

    @Bean
    public OAuth2Api oAuth2Api() {
        return httpServiceProxyFactory.createClient(OAuth2Api.class);
    }

    @Bean
    public PermissionApi permissionApi() {
        return httpServiceProxyFactory.createClient(PermissionApi.class);
    }

    @Bean
    public RbacApi rbacApi() {
        return httpServiceProxyFactory.createClient(RbacApi.class);
    }

    @Bean
    public RoleApi roleApi() {
        return httpServiceProxyFactory.createClient(RoleApi.class);
    }

    @Bean
    public SecurityApi securityApi() {
        return httpServiceProxyFactory.createClient(SecurityApi.class);
    }

    @Bean
    public UserApi userApi() {
        return httpServiceProxyFactory.createClient(UserApi.class);
    }

}
