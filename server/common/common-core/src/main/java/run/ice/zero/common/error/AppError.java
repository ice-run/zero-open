package run.ice.zero.common.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author DaoDao
 */
@Getter
@AllArgsConstructor
public enum AppError implements ErrorEnum {

    /**
     * OK
     * 现在约定 OK 0000 为请求成功，前 2 个数字区分服务，后 2 个数字区分响应码
     */
    OK("0000", "OK"),

    /**
     * ERROR
     */
    ERROR("9999", "ERROR"),

    /**
     * TOKEN_ERROR
     */
    TOKEN_ERROR("1111", "token 错误"),

    /**
     * HTTP_STATUS_IS_NOT_2XX
     */
    HTTP_STATUS_IS_NOT_2XX("2222", "http 状态码不是 2XX"),

    /**
     * HTTP_RESPONSE_IS_NULL
     */
    HTTP_RESPONSE_IS_NULL("3333", "http 响应为空"),

    /**
     * HTTP_CONNECT_TIMEOUT
     */
    HTTP_CONNECT_TIMEOUT("4444", "http 连接超时"),

    /**
     * HTTP_READ_TIMEOUT
     */
    HTTP_READ_TIMEOUT("5555", "读取超时"),

    REQUEST_PARAM_ERROR("6666", "请求参数错误"),

    ;

    /**
     * 错误编码
     */
    public final String code;

    /**
     * 错误信息
     */
    public final String message;

}
