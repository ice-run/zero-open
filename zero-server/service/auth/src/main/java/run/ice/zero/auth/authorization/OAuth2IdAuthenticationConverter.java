package run.ice.zero.auth.authorization;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import run.ice.zero.auth.constant.OAuth2Constant;
import run.ice.zero.auth.util.OAuth2EndpointUtils;

import java.util.*;

@Slf4j
public class OAuth2IdAuthenticationConverter implements AuthenticationConverter {

    @Override
    public Authentication convert(HttpServletRequest request) {

        // grant_type (REQUIRED)
        String grantType = request.getParameter(OAuth2ParameterNames.GRANT_TYPE);
        if (!OAuth2Constant.ID.equals(grantType)) {
            return null;
        }

        // clientPrincipal (REQUIRED)
        Authentication clientPrincipal = SecurityContextHolder.getContext().getAuthentication();
        if (clientPrincipal == null || !clientPrincipal.isAuthenticated()) {
            OAuth2EndpointUtils.throwError(OAuth2ErrorCodes.UNAUTHORIZED_CLIENT, null, OAuth2EndpointUtils.ACCESS_TOKEN_REQUEST_ERROR_URI);
        }

        MultiValueMap<String, String> parameters = OAuth2EndpointUtils.getParameters(request);

        // id (REQUIRED)
        String id = parameters.getFirst(OAuth2Constant.ID);
        if (!StringUtils.hasText(id) || parameters.get(OAuth2Constant.ID).size() != 1) {
            OAuth2EndpointUtils.throwError(OAuth2ErrorCodes.INVALID_REQUEST, OAuth2Constant.ID, OAuth2EndpointUtils.ACCESS_TOKEN_REQUEST_ERROR_URI);
        }

        // scope (REQUIRED)
        String scope = parameters.getFirst(OAuth2ParameterNames.SCOPE);
        if (!StringUtils.hasText(scope) || parameters.get(OAuth2ParameterNames.SCOPE).size() != 1) {
            OAuth2EndpointUtils.throwError(OAuth2ErrorCodes.INVALID_REQUEST, OAuth2ParameterNames.SCOPE, OAuth2EndpointUtils.ACCESS_TOKEN_REQUEST_ERROR_URI);
        }
        Set<String> requestedScopes = new HashSet<>(Arrays.asList(StringUtils.delimitedListToStringArray(scope, " ")));

        Map<String, Object> additionalParameters = new HashMap<>();
        parameters.forEach((key, value) -> {
            if (!key.equals(OAuth2ParameterNames.SCOPE)
                    && !key.equals(OAuth2ParameterNames.CLIENT_ID)
                    && !key.equals(OAuth2ParameterNames.CLIENT_SECRET)
                    && !key.equals(OAuth2Constant.PASSWORD)
                    && !key.equals(OAuth2ParameterNames.GRANT_TYPE)
                    && !key.equals(OAuth2Constant.USERNAME)
            ) {
                additionalParameters.put(key, value.getFirst());
            }

        });

        return new OAuth2IdAuthenticationToken(id, requestedScopes, clientPrincipal, additionalParameters);
    }

}
