package run.ice.zero.common.annotation;

import java.lang.annotation.*;

/**
 * 加密注解
 *
 * @author DaoDao
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Cipher {

    /**
     * 属性
     */
    String property() default "";

    /**
     * 加密算法
     */
    Algorithm algorithm() default Algorithm.AES;

    /**
     * 加密算法枚举
     */
    enum Algorithm {
        AES,
    }

}
