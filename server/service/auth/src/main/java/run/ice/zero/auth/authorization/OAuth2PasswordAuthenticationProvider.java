package run.ice.zero.auth.authorization;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.context.AuthorizationServerContextHolder;
import org.springframework.security.oauth2.server.authorization.token.DefaultOAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.util.Assert;
import run.ice.zero.auth.constant.OAuth2Constant;
import run.ice.zero.auth.util.OAuth2AuthenticationProviderUtils;
import run.ice.zero.auth.util.OAuth2EndpointUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@Getter
@Setter
public class OAuth2PasswordAuthenticationProvider implements AuthenticationProvider {

    private final MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

    private final PasswordEncoder passwordEncoder;

    private final UserDetailsService userDetailsService;

    private final OAuth2AuthorizationService oAuth2AuthorizationService;

    private final OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator;

    public OAuth2PasswordAuthenticationProvider(OAuth2AuthorizationService oAuth2AuthorizationService, UserDetailsService userDetailsService, OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator, PasswordEncoder passwordEncoder) {
        this.oAuth2AuthorizationService = oAuth2AuthorizationService;
        this.userDetailsService = userDetailsService;
        this.tokenGenerator = tokenGenerator;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.isInstanceOf(OAuth2PasswordAuthenticationToken.class, authentication, () -> this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.onlySupports", "Only UsernamePasswordAuthenticationToken is supported"));
        OAuth2PasswordAuthenticationToken oAuth2PasswordAuthenticationToken = (OAuth2PasswordAuthenticationToken) authentication;

        // 1. 客户端认证校验
        OAuth2ClientAuthenticationToken clientPrincipal = OAuth2AuthenticationProviderUtils.getAuthenticatedClientElseThrowInvalidClient(oAuth2PasswordAuthenticationToken);
        RegisteredClient registeredClient = clientPrincipal.getRegisteredClient();

        // 2. 当前客户端是否支持密码模式
        assert registeredClient != null;
        if (!registeredClient.getAuthorizationGrantTypes().contains(new AuthorizationGrantType(OAuth2Constant.PASSWORD))) {
            throw new OAuth2AuthenticationException(OAuth2ErrorCodes.UNAUTHORIZED_CLIENT);
        }

        // 3. 权限范围检查
        Set<String> requestedScopes = oAuth2PasswordAuthenticationToken.getScopes(); // 请求中的权限范围
        Set<String> allowedScopes = registeredClient.getScopes(); // 客户端被允许的权限范围
        if (!requestedScopes.isEmpty() && !allowedScopes.containsAll(requestedScopes)) {
            OAuth2EndpointUtils.throwError(OAuth2ErrorCodes.INVALID_SCOPE, OAuth2ParameterNames.SCOPE, OAuth2EndpointUtils.ACCESS_TOKEN_REQUEST_ERROR_URI);
        }

        // 4. 用户名查询用户并校验
        String username = oAuth2PasswordAuthenticationToken.getUsername();
        UserDetails userDetails;
        try {
            userDetails = this.userDetailsService.loadUserByUsername(username);
        } catch (UsernameNotFoundException ex) {
            log.debug("Failed to find user '" + username + "'");
            throw new BadCredentialsException(this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
        }
        Assert.notNull(userDetails, "retrieveUser returned null - a violation of the interface contract");

        this.checkPasswordAuthentication(userDetails, oAuth2PasswordAuthenticationToken);

        // 4.5 创建认证成功对象
        Authentication passwordAuthentication = this.createSuccessAuthentication(userDetails, authentication);

        OAuth2Authorization.Builder oAuth2AuthorizationBuilder = OAuth2Authorization.withRegisteredClient(registeredClient)
                .authorizedScopes(requestedScopes)
                .authorizationGrantType(new AuthorizationGrantType(OAuth2Constant.PASSWORD))
                .principalName(passwordAuthentication.getPrincipal().toString());

        // 5. 生成访问令牌
        // 令牌上下文
        DefaultOAuth2TokenContext.Builder tokenContextBuilder = DefaultOAuth2TokenContext.builder()
                .registeredClient(registeredClient)
                .principal(passwordAuthentication)
                .authorizationServerContext(AuthorizationServerContextHolder.getContext())
                .authorizedScopes(requestedScopes)
                .authorizationGrantType(new AuthorizationGrantType(OAuth2Constant.PASSWORD))
                .authorizationGrant(oAuth2PasswordAuthenticationToken);

        // 生成访问令牌
        DefaultOAuth2TokenContext oAuth2TokenContext = tokenContextBuilder.tokenType(OAuth2TokenType.ACCESS_TOKEN).build();
        OAuth2Token generateAccessToken = this.tokenGenerator.generate(oAuth2TokenContext);
        if (generateAccessToken == null) {
            throw new OAuth2AuthenticationException("生成访问令牌失败");
        }
        OAuth2AccessToken accessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER,
                generateAccessToken.getTokenValue(), generateAccessToken.getIssuedAt(),
                generateAccessToken.getExpiresAt(), oAuth2TokenContext.getAuthorizedScopes());
        oAuth2AuthorizationBuilder.accessToken(accessToken);

        // 6. 生成刷新令牌
        OAuth2RefreshToken refreshToken = null;
        if (registeredClient.getAuthorizationGrantTypes().contains(AuthorizationGrantType.REFRESH_TOKEN) &&
                // Do not issue refresh token to public client
                !clientPrincipal.getClientAuthenticationMethod().equals(ClientAuthenticationMethod.NONE)) {
            DefaultOAuth2TokenContext tokenContext = tokenContextBuilder.tokenType(OAuth2TokenType.REFRESH_TOKEN).build();
            OAuth2Token generatedRefreshToken = this.tokenGenerator.generate(tokenContext);
            assert generatedRefreshToken != null;
            refreshToken = new OAuth2RefreshToken(generatedRefreshToken.getTokenValue(),
                    generatedRefreshToken.getIssuedAt(),
                    generatedRefreshToken.getExpiresAt());

            if (!(generatedRefreshToken instanceof OAuth2RefreshToken)) {
                throw new OAuth2AuthenticationException("生成刷新令牌失败");
            }
        }
        oAuth2AuthorizationBuilder.refreshToken(refreshToken);

        OidcIdToken idToken;
        if (requestedScopes.contains(OidcScopes.OPENID)) {
            DefaultOAuth2TokenContext tokenContext = tokenContextBuilder
                    .tokenType(new OAuth2TokenType(OidcParameterNames.ID_TOKEN))
                    .authorization(oAuth2AuthorizationBuilder.build()) // ID token customizer may need access to the access token and/or refresh token
                    .build();
            // @formatter:on
            OAuth2Token generatedIdToken = this.tokenGenerator.generate(tokenContext);
            if (!(generatedIdToken instanceof Jwt)) {
                OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR,
                        "The token generator failed to generate the ID token.", OAuth2Constant.ERROR_URI);
                throw new OAuth2AuthenticationException(error);
            }

            log.trace("Generated id token");

//            idToken = new OidcIdToken(generatedIdToken.getTokenValue(), generatedIdToken.getIssuedAt(),
//                    generatedIdToken.getExpiresAt(), ((Jwt) generatedIdToken).getClaims());
            idToken = new OidcIdToken(generatedIdToken.getTokenValue(), generatedIdToken.getIssuedAt(),
                    generateAccessToken.getExpiresAt(), ((Jwt) generatedIdToken).getClaims());
            oAuth2AuthorizationBuilder.token(idToken, (metadata) ->
                    metadata.put(OAuth2Authorization.Token.CLAIMS_METADATA_NAME, idToken.getClaims())
            );
        } else {
            idToken = null;
        }

        OAuth2Authorization oAuth2Authorization = oAuth2AuthorizationBuilder.build();
        oAuth2AuthorizationService.save(oAuth2Authorization);

        Map<String, Object> additionalParameters = Collections.emptyMap();
        if (idToken != null) {
            additionalParameters = new HashMap<>();
            additionalParameters.put(OidcParameterNames.ID_TOKEN, idToken.getTokenValue());
        }

        OAuth2AccessTokenAuthenticationToken accessTokenAuthenticationResult = new OAuth2AccessTokenAuthenticationToken(
                registeredClient, clientPrincipal, accessToken, refreshToken, additionalParameters);
        accessTokenAuthenticationResult.setDetails(oAuth2PasswordAuthenticationToken.getDetails());
        return accessTokenAuthenticationResult;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return OAuth2PasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    public void checkPasswordAuthentication(UserDetails userDetails, OAuth2PasswordAuthenticationToken authentication) throws AuthenticationException {
        if (authentication.getCredentials() == null) {
            log.debug("Failed to authenticate since no credentials provided");
            throw new BadCredentialsException(this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
        } else {
            String presentedPassword = authentication.getPassword();
            if (!this.passwordEncoder.matches(presentedPassword, userDetails.getPassword())) {
                log.debug("Failed to authenticate since password does not match stored value");
                throw new BadCredentialsException(this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
            }
        }
    }

    public Authentication createSuccessAuthentication(UserDetails userDetails, Authentication authentication) {
        Object principal = userDetails.getUsername();
        UsernamePasswordAuthenticationToken result = UsernamePasswordAuthenticationToken.authenticated(principal, authentication.getCredentials(), userDetails.getAuthorities());
        result.setDetails(authentication.getDetails());
        log.debug("Authenticated user");
        return result;
    }

}
