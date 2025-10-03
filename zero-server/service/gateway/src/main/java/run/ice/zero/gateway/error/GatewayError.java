package run.ice.zero.gateway.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import run.ice.zero.common.constant.AppConstant;
import run.ice.zero.common.constant.ServiceCode;
import run.ice.zero.common.error.ErrorEnum;

/**
 * @author DaoDao
 */
@Getter
@AllArgsConstructor
public enum GatewayError implements ErrorEnum {

    ERROR(code("01"), "ERROR : 网关错误"),

    TOKEN_ERROR("1111", "TOKEN_ERROR : 请传递有效的 token"),

    ILLEGAL_REQUEST(code("02"), "ILLEGAL_REQUEST : 非法请求，请检查白名单配置"),

    INNER_SERVICE_RESPONSE_HTTP_STATUS_IS_ERROR(code("03"), "内部服务响应 http 状态错误"),
    INNER_SERVICE_RESPONSE_BODY_IS_NOT_FLUX(code("04"), "内部服务响应数据不是 Flux"),
    INNER_SERVICE_RESPONSE_BODY_RELEASE_EXCEPTION(code("05"), "内部服务响应数据发布异常"),
    INNER_SERVICE_RESPONSE_BODY_DESERIALIZE_EXCEPTION(code("06"), "内部服务响应数据反序列化异常"),
    RESPONSE_BODY_SERIALIZE_EXCEPTION(code("10"), "响应 body 数据序列化异常"),
    REQUEST_BODY_TOO_LARGE(code("11"), "请求 body 参数太大"),
    RESPONSE_BODY_TOO_LARGE(code("12"), "响应 body 数据太大"),
    RESPONSE_BODY_ENCRYPT_ERROR(code("24"), "响应 body 数据加密异常"),

    REQUIRED_X_SECURITY(code("10"), "http header 中需要传递: " + AppConstant.X_SECURITY),
    REQUIRED_X_SIGN(code("11"), "http header 中需要传递: " + AppConstant.X_SIGN),
    X_SECURITY_ERROR(code("12"), "http header 中的 " + AppConstant.X_SECURITY + " 错误"),
    X_SIGN_ERROR(code("14"), "http header 中的 " + AppConstant.X_SIGN + " 错误"),
    REQUEST_BODY_SERIALIZE_ERROR(code("15"), "请求 body 参数序列化异常"),
    REQUEST_BODY_PARAM_CIPHER_ERROR(code("17"), "请求 body 参数密文错误"),
    REQUEST_BODY_PARAM_CIPHER_DECRYPT_EXCEPTION(code("20"), "请求 body 参数密文解密异常"),
    REQUEST_BODY_PARAM_PLAINS_DESERIALIZE_ERROR(code("21"), "请求 body 参数明文反序列化异常"),
    REQUIRED_X_TIME(code("44"), "http header 中需要传递: " + AppConstant.X_TIME),
    X_TIME_FORMAT_ERROR(code("45"), "http header 中的 " + AppConstant.X_TIME + " 格式错误"),
    X_TIME_EXPIRED(code("46"), "http header 中的 " + AppConstant.X_TIME + " 已过期"),
    REQUEST_BODY_REWRITE_ERROR(code("52"), "请求 body 参数重写异常"),
    REQUIRED_X_TRACE(code("58"), "http header 中需要传递: " + AppConstant.X_TRACE),
    X_TRACE_DUPLICATE(code("59"), "http header 中的 " + AppConstant.X_TRACE + " 重复"),
    FIND_CLIENT_AES_KEY_ERROR(code("60"), "查询客户端 AES 密钥失败"),
    FIND_CLIENT_RSA_KEY_ERROR(code("61"), "查询客户端 RSA 密钥失败"),
    REQUEST_BODY_PARAM_CAN_NOT_BE_NULL(code("64"), "请求 body 数据中的 param 不可为 null"),
    REQUIRED_X_HASH(code("62"), "http header 中需要传递: " + AppConstant.X_HASH),
    X_HASH_ERROR(code("63"), AppConstant.X_HASH + " 错误"),
    REQUIRED_X_CLIENT(code("65"), "http header 中需要传递: " + AppConstant.X_CLIENT),
    FIND_AGENCY_SECRET_KEY_ERROR(code("66"), "查找机构密钥异常"),
    INNER_SERVICE_RESPONSE_BODY_DATA_SERIALIZE_EXCEPTION(code("67"), "内部服务响应数据序列化异常"),
    RESPONSE_BODY_DATA_PLAINS_ENCRYPT_EXCEPTION(code("68"), "响应 body 数据明文加密异常"),
    RESPONSE_BODY_DATA_CIPHER_SIGN_EXCEPTION(code("69"), "响应 body 数据密文签名异常"),
    REQUEST_BODY_DESERIALIZE_ERROR(code("70"), "请求 body 数据反序列化异常"),
    REQUEST_BODY_REQUIRED_PARAM_PROPERTIES(code("71"), "请求 body 数据缺少必要的属性"),
    X_SIGN_VERIFY_EXCEPTION(code("72"), "http header 中的 " + AppConstant.X_SIGN + " 验证异常"),
    X_SIGN_VERIFY_ERROR(code("73"), "http header 中的 " + AppConstant.X_SIGN + " 验证错误"),
    REQUEST_REWRITE_ERROR(code("74"), "请求重写异常"),
    X_TRACE_FORMAT_ERROR(code("75"), "http header 中的 " + AppConstant.X_TRACE + " 格式错误"),
    INNER_SERVICE_RESPONSE_HTTP_STATUS_IS_NULL(code("76"), "内部服务响应 http 状态为空"),
    REQUEST_HEADER_HASH_CIPHER_DECRYPT_EXCEPTION(code("20"), "请求 header 中的 " + AppConstant.X_HASH + " 密文解密异常"),
    REQUIRED_CONTENT_TYPE(code("77"), "http header 中需要传递: Content-Type"),
    REQUEST_HEADER_HASH_SIGNATURE_VERIFY_EXCEPTION(code("78"), "请求 header 中的 " + AppConstant.X_HASH + " 签名验证异常"),
    REQUEST_HEADER_HASH_SIGNATURE_VERIFY_ERROR(code("79"), "请求 header 中的 " + AppConstant.X_HASH + " 签名验证错误"),
    RESPONSE_HASH_ENCRYPT_ERROR(code("80"), "响应 header 中的 " + AppConstant.X_HASH + " 加密异常"),
    RESPONSE_HASH_SIGN_ERROR(code("81"), "响应 header 中的 " + AppConstant.X_HASH + " 签名异常"),

    ;

    /**
     * 响应编码
     */
    public final String code;

    /**
     * 响应说明
     */
    public final String message;

    private static String code(String code) {
        return ServiceCode.GATEWAY + code;
    }

}
