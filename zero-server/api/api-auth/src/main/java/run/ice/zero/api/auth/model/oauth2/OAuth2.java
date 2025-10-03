package run.ice.zero.api.auth.model.oauth2;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import run.ice.zero.common.model.Serializer;

/**
 * @author DaoDao
 */
@Schema(title = "OAuth2", description = "OAuth 2.1 协议 : https://datatracker.ietf.org/doc/draft-ietf-oauth-v2-1/")
@Data
public class OAuth2 implements Serializer {

    @Schema(title = "access_token", description = "访问令牌", example = "xxx")
    private String access_token;

    @Schema(title = "refresh_token", description = "刷新令牌", example = "yyy")
    private String refresh_token;

    @Schema(title = "id_token", description = "ID 令牌", example = "zzz")
    private String id_token;

    @Schema(title = "scope", description = "授权范围", example = "openid")
    private String scope;

    @Schema(title = "token_type", description = "令牌类型", example = "bearer")
    private String token_type;

    @Schema(title = "expires_in", description = "过期时间", example = "123")
    private Integer expires_in;

}
