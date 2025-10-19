package run.ice.zero.auth.config;

import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.oauth2.server.resource.introspection.SpringOpaqueTokenIntrospector;
import org.springframework.security.web.SecurityFilterChain;
import run.ice.zero.auth.authorization.OAuth2IdAuthenticationConverter;
import run.ice.zero.auth.authorization.OAuth2IdAuthenticationProvider;
import run.ice.zero.auth.authorization.OAuth2PasswordAuthenticationConverter;
import run.ice.zero.auth.authorization.OAuth2PasswordAuthenticationProvider;
import run.ice.zero.auth.constant.OAuth2Constant;
import run.ice.zero.auth.service.UserDetailsServiceImpl;
import run.ice.zero.auth.util.OAuth2ConfigurerUtils;

import java.time.Duration;
import java.time.Year;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

@Configuration
@EnableWebSecurity
public class OAuth2Config {

    @Value("${spring.security.oauth2.resourceserver.opaque-token.introspection-uri:http://oauth2.zero/oauth2/introspect}")
    private String introspectionUri;

    @Value("${spring.security.oauth2.resourceserver.opaque-token.client-id:zero}")
    private String clientId;

    @Value("${spring.security.oauth2.resourceserver.opaque-token.client-secret:zero}")
    private String clientSecret;

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Resource
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/",
                                "/favicon.ico",
                                "/actuator/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/swagger-ui/index.html",
                                "/doc.html",
                                "/webjars/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .opaqueToken(opaqueToken -> opaqueToken
                                .introspector(introspector())
                        )
                )
                .with(new OAuth2AuthorizationServerConfigurer(), configurer -> configurer
                        .tokenEndpoint(tokenEndpoint -> tokenEndpoint
                                .accessTokenRequestConverters((converters) -> {
                                    converters.add(new OAuth2PasswordAuthenticationConverter());
                                    converters.add(new OAuth2IdAuthenticationConverter());
                                })
                                .authenticationProviders((providers) -> {
                                    OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator = OAuth2ConfigurerUtils.getTokenGenerator(http);
                                    providers.add(new OAuth2PasswordAuthenticationProvider(oAuth2AuthorizationService(), userDetailsServiceImpl, tokenGenerator, passwordEncoder()));
                                    providers.add(new OAuth2IdAuthenticationProvider(oAuth2AuthorizationService(), userDetailsServiceImpl, tokenGenerator));
                                })
                        )
                        .authorizationServerMetadataEndpoint(metadataEndpoint -> metadataEndpoint
                                .authorizationServerMetadataCustomizer((metadata) -> {
                                    metadata.grantType(new AuthorizationGrantType(OAuth2Constant.PASSWORD).getValue());
                                    metadata.grantType(new AuthorizationGrantType(OAuth2Constant.ID).getValue());
                                })
                        )
                )
                .userDetailsService(userDetailsServiceImpl)
        ;
        return http.build();
    }

    @Bean
    public OpaqueTokenIntrospector introspector() {
        return SpringOpaqueTokenIntrospector.withIntrospectionUri(introspectionUri)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .build();
    }

    // @Bean
    public RegisteredClientRepository registeredClientRepository() {

        RegisteredClient registeredClient = RegisteredClient.withId("zero")
                .clientId("zero")
                .clientIdIssuedAt(Year.of(2025).atDay(1).atStartOfDay().atZone(ZoneOffset.systemDefault()).toInstant())
                .clientSecret("{noop}zero")
                .clientSecretExpiresAt(null)
                .clientName("zero")
                .clientAuthenticationMethods((methods) -> {
                    methods.add(ClientAuthenticationMethod.CLIENT_SECRET_BASIC);
                    methods.add(ClientAuthenticationMethod.CLIENT_SECRET_POST);
                    methods.add(ClientAuthenticationMethod.CLIENT_SECRET_JWT);
                    methods.add(ClientAuthenticationMethod.PRIVATE_KEY_JWT);
                    methods.add(ClientAuthenticationMethod.NONE);
                    methods.add(ClientAuthenticationMethod.TLS_CLIENT_AUTH);
                    methods.add(ClientAuthenticationMethod.SELF_SIGNED_TLS_CLIENT_AUTH);
                })
                .authorizationGrantTypes((grantTypes) -> {
                    grantTypes.add(AuthorizationGrantType.AUTHORIZATION_CODE);
                    grantTypes.add(AuthorizationGrantType.REFRESH_TOKEN);
                    grantTypes.add(AuthorizationGrantType.CLIENT_CREDENTIALS);
                    // grantTypes.add(AuthorizationGrantType.PASSWORD);
                    grantTypes.add(AuthorizationGrantType.JWT_BEARER);
                    grantTypes.add(AuthorizationGrantType.DEVICE_CODE);
                    grantTypes.add(AuthorizationGrantType.TOKEN_EXCHANGE);
                    grantTypes.add(new AuthorizationGrantType(OAuth2Constant.ID));
                    grantTypes.add(new AuthorizationGrantType(OAuth2Constant.PASSWORD));
                })
                .redirectUris((redirectUris) -> redirectUris.add("http://oauth2.zero/api/code"))
                .postLogoutRedirectUris((postLogoutRedirectUris) -> postLogoutRedirectUris.add("http://oauth2.zero/"))
                .scopes((scopes) -> {
                    scopes.add("openid");
                    scopes.add("profile");
                })
                .clientSettings(ClientSettings.builder()
                        .requireAuthorizationConsent(true)
                        .build())
                .tokenSettings(TokenSettings.builder()
                        .accessTokenFormat(OAuth2TokenFormat.REFERENCE)
                        // .accessTokenTimeToLive(Duration.ofDays(30L))
                        // .refreshTokenTimeToLive(Duration.ofDays(365L))
                        .build())
                .build();

        JdbcRegisteredClientRepository registeredClientRepository = new JdbcRegisteredClientRepository(jdbcTemplate);
        registeredClientRepository.save(registeredClient);

        return registeredClientRepository;
    }

    @Resource
    private RegisteredClientRepository registeredClientRepository;

    @Bean
    OAuth2AuthorizationService oAuth2AuthorizationService() {
        // 等待发布正式版本
         return new InMemoryOAuth2AuthorizationService();
        // return new JdbcOAuth2AuthorizationService(jdbcTemplate, registeredClientRepository());
//        return new JdbcOAuth2AuthorizationService(jdbcTemplate, registeredClientRepository);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

}
