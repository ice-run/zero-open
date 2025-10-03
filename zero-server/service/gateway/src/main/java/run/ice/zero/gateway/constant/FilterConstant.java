package run.ice.zero.gateway.constant;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * 过滤器常量类
 *
 * @author DaoDao
 */
@Data
@Component
public class FilterConstant {

    public static final String NO_FILTER = "NoFilter";

    public static final String IN_WHITE_LIST = "InWhitelist";

    public static final String REQUEST_CONTENT_TYPE = "requestContentType";

    public static final String REQUEST_HEADER_MAP = "requestHeaderMap";

    public static final String REQUEST_BODY_BYTES = "requestBodyBytes";

    public static final String REQUEST_BODY_MAP = "requestBodyMap";

    public static final String URI = "URI";

    public static final String IN_NO_AUTH = "InNoAuth";

    public static final String OAUTH2_TOKEN = "OAuth2Token";

    public static final String AES_KEY = "AES_KEY";
    public static final String AES_IV = "AES_IV";

    public static final String CLIENT_PUBLIC_KEY = "clientPublicKey";
    public static final String SERVER_PRIVATE_KEY = "serverPrivateKey";

}
