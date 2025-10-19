package run.ice.zero.common.annotation;

import java.lang.annotation.*;

/**
 * Hash 注解
 *
 * @author DaoDao
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Hash {

    /**
     * 属性
     */
    String property() default "";

    /**
     * Hash 算法
     */
    Algorithm algorithm() default Algorithm.SHA_256;

    /**
     * Hash 算法枚举
     */
    enum Algorithm {
        SHA_256,
    }

}
