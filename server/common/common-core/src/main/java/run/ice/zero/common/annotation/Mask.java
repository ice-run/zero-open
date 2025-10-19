package run.ice.zero.common.annotation;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.annotation.*;

/**
 * 掩码注解
 *
 * @author DaoDao
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Mask {

    /**
     * 属性
     */
    String property() default "";

    /**
     * 掩码类型
     */
    Type type() default Type.NONE;

    /**
     * 敏感类型枚举
     *
     * @author DaoDao
     */
    @Getter
    @AllArgsConstructor
    public enum Type {

        NONE("", ""),

        /**
         * 姓名，默认规则保留第一个字符，其余按照字符个数替换掩码 *
         * 大猫        : 大*
         * 大脸猫      : 大**
         * 大脸猫喵喵喵 : 大*****
         */
        // NAME("([\u4E00-\u9FA5]{1})([\u4E00-\u9FA5]{1})([\u4E00-\u9FA5]{0,})", "*$2$3"),
        NAME("(?<=.).", "*"),

        /**
         * 手机
         */
        PHONE("(1\\d{2})(\\d{4})(\\d{4})", "$1****$3"),

        /**
         * 电话
         */
        TEL("(\\d{3})(\\d{0,4})(\\d{4})", "$1****$3"),

        /**
         * 邮箱
         */
        EMAIL("([a-zA-Z0-9_-]{2})([a-zA-Z0-9_-]+)(@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+)", "$1****$3"),

        /**
         * 身份证
         */
        ID_CARD("(\\d{6})(\\d{5,8})(\\d{4}|\\d{3}[Xx]{1})", "$1****$3"),

        /**
         * 银行卡
         */
        BANK_CARD("(\\d{6})(\\d{2,22})(\\d{4})", "$1****$3"),

        /**
         * 证
         */
        LICENCE("^([\\s\\S]{6})([\\s\\S]*)([\\s\\S]{4})$", "$1****$3"),

        /**
         * 密码
         */
        PASSWORD("^[\\s\\S]*$", "******"),

        ;

        /**
         * 正则
         */
        final String regex;

        /**
         * 替换
         */
        final String replace;

    }

}
