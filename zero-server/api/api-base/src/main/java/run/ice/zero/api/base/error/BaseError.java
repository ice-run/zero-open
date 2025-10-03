package run.ice.zero.api.base.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import run.ice.zero.common.constant.ServiceCode;
import run.ice.zero.common.error.ErrorEnum;

/**
 * @author DaoDao
 */
@Getter
@AllArgsConstructor
public enum BaseError implements ErrorEnum {

    ERROR(code("00"), "base 服务异常"),

    DICT_CODE_ALREADY_EXIST(code("01"), "编码已存在"),
    DICT_CODE_NOT_EXIST(code("02"), "编码不存在"),

    FILE_READ_WRITE_ERROR(code("08"), "文件读写异常"),
    FILE_CODE_ERROR(code("09"), "文件编码错误"),
    FILE_NOT_EXIST(code("10"), "文件不存在"),
    INVOKE_FILE_ERROR(code("11"), "调用文件服务异常"),

    GENERATE_CAPTCHA_ERROR(code("12"), "生成验证码失败"),
    CAPTCHA_NOT_NULL(code("13"), "验证码不能为空"),
    CAPTCHA_EXPIRE(code("14"), "验证码已过期"),
    CAPTCHA_ERROR(code("15"), "验证码错误"),

    MAIL_REGULAR_ERROR(code("16"), "邮件格式不正确"),

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
        return ServiceCode.BASE + code;
    }

}

