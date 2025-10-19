package run.ice.zero.common.annotation;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.annotation.*;

/**
 * 敏感数据对象注解
 * 暂时不做全局注解捆绑：JacksonAnnotationsInside
 * 注解在 class 上面表示当前类序列化时，需要脱敏
 * 注解在 field 上面表示当前属性序列化时，需要按照指定的参数脱敏
 *
 * @author DaoDao
 */
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Sensitive {

    /**
     * 脱敏规则类型
     */
    Type type() default Type.CUSTOM;

    /**
     * 正则
     */
    String regex() default "";

    /**
     * 替换
     */
    String replace() default "****";


    /**
     * 敏感类型枚举
     *
     * @author DaoDao
     */
    @Getter
    @AllArgsConstructor
    public enum Type {

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
         * 邮箱
         */
        EMAIL("([a-zA-Z0-9_-]{2})([a-zA-Z0-9_-]+)(@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+)", "$1****$3"),

        /**
         * 身份证
         */
        ID_CARD("(\\d{4})(\\d{7,10})(\\d{4}|\\d{3}[Xx]{1})", "$1****$3"),

        /**
         * 银行卡
         */
        BANK_CARD("(\\d{4})(\\d{2,22})(\\d{4})", "$1****$3"),

        /**
         * 证
         */
        LICENCE("^([\\s\\S]{4})([\\s\\S]*)([\\s\\S]{4})$", "$1****$3"),

        /**
         * 密码
         */
        PASSWORD("^[\\s\\S]*$", "******"),

        /**
         * 自定义
         */
        CUSTOM("", ""),
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
