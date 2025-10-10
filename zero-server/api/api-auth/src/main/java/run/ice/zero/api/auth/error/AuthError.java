package run.ice.zero.api.auth.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import run.ice.zero.common.constant.ServiceCode;
import run.ice.zero.common.error.ErrorEnum;

/**
 * @author DaoDao
 */
@Getter
@AllArgsConstructor
public enum AuthError implements ErrorEnum {

    ERROR(code("01"), "auth 服务异常"),
    USER_NOT_EXIST(code("02"), "用户不存在"),
    USERNAME_ALREADY_EXIST(code("03"), "用户名已存在"),
    USER_PASSWORD_INCORRECT(code("04"), "密码错误，请输入正确的密码"),

    OLD_AND_NEW_PASSWORDS_CANNOT_BE_THE_SAME(code("05"), "新旧密码不可设置为一致"),

    LOGIN_CAPTCHA_ERROR(code("06"), "登录验证码错误"),
    INVOKE_AUTH_EXCEPTION(code("07"), "调用 auth 服务异常"),

    ROLE_NOT_EXIST(code("08"), "角色不存在"),
    ROLE_NAME_ALREADY_EXIST(code("09"), "角色名称已经存在"),
    ROLE_CODE_ALREADY_EXIST(code("10"), "角色代码已经存在"),

    PERM_NOT_EXIST(code("11"), "权限不存在"),
    PERM_NAME_ALREADY_EXIST(code("12"), "权限名称已经存在"),
    PERM_CODE_ALREADY_EXIST(code("13"), "权限代码已经存在"),
    USER_INVALID(code("14"), "用户无效"),
    OLD_PASSWORD_INCORRECT(code("15"), "原密码错误，请输入正确的密码"),
    INVOKE_DICT_EXCEPTION(code("16"), "调用 dict 服务异常"),
    INVOKE_USER_EXCEPTION(code("17"), "调用 user 服务异常"),
    NICKNAME_ALREADY_EXIST(code("18"), "昵称已存在"),
    INVOKE_FILE_EXCEPTION(code("19"), "调用 file 服务异常"),
    AVATAR_NOT_EXIST(code("20"), "头像不存在"),
    USERNAME_NOT_NULL(code("21"), "用户名不能为空"),
    INVOKE_MESSAGE_EXCEPTION(code("22"), "调用 message 服务异常"),

    INVOKE_BASE_ERROR(code("23"), "调用基础服务异常"),
    USERNAME_NOT_EXIST(code("24"), "用户名不存在"),
    USER_ID_NOT_EXIST(code("25"), "用户 ID 不存在"),

    GROUP_NOT_EXIST(code("26"), "组织不存在"),
    GROUP_NAME_ALREADY_EXIST(code("27"), "组织名称已经存在"),

    ;

    /**
     * 错误编码
     */
    public final String code;

    /**
     * 错误信息
     */
    public final String message;

    private static String code(String code) {
        return ServiceCode.AUTH + code;
    }

}
