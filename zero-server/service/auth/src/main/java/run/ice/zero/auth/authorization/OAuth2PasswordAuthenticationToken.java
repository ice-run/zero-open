package run.ice.zero.auth.authorization;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationGrantAuthenticationToken;
import org.springframework.util.Assert;
import run.ice.zero.auth.constant.OAuth2Constant;

import java.util.Map;
import java.util.Set;

@Getter
@Setter
public class OAuth2PasswordAuthenticationToken extends OAuth2AuthorizationGrantAuthenticationToken {

    private String username;

    private String password;

    private Set<String> scopes;

    public OAuth2PasswordAuthenticationToken(@Nullable String username,
                                             @Nullable String password,
                                             @Nullable Set<String> scopes,
                                             Authentication clientPrincipal,
                                             @Nullable Map<String, Object> additionalParameters) {
        super(new AuthorizationGrantType(OAuth2Constant.PASSWORD), clientPrincipal, additionalParameters);
        Assert.hasText(username, "username cannot be empty");
        Assert.hasText(password, "password cannot be empty");
        this.username = username;
        this.password = password;
        this.scopes = scopes;
    }

    protected OAuth2PasswordAuthenticationToken(AuthorizationGrantType authorizationGrantType, Authentication clientPrincipal, Map<String, Object> additionalParameters) {
        super(authorizationGrantType, clientPrincipal, additionalParameters);
    }

}
