package run.ice.zero.auth.authorization;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationGrantAuthenticationToken;
import run.ice.zero.auth.constant.OAuth2Constant;

import java.util.Map;
import java.util.Set;

@Getter
@Setter
public class OAuth2IdAuthenticationToken extends OAuth2AuthorizationGrantAuthenticationToken {

    private String id;

    private Set<String> scopes;

    private Map<String, Object> additionalParameters;

    public OAuth2IdAuthenticationToken(String id,
                                       Set<String> scopes,
                                       Authentication clientPrincipal,
                                       @Nullable Map<String, Object> additionalParameters) {
        super(new AuthorizationGrantType(OAuth2Constant.ID), clientPrincipal, additionalParameters);
        this.id = id;
        this.scopes = scopes;
        this.additionalParameters = additionalParameters;
        super.setAuthenticated(true);
    }

}
