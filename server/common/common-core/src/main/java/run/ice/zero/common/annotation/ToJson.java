package run.ice.zero.common.annotation;

import java.lang.annotation.*;

/**
 * 使用 javax.annotation.processing.AbstractProcessor 在编译期补充一个 toJson 方法
 * 暂时不做实现
 *
 * @author DaoDao
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface ToJson {

}
