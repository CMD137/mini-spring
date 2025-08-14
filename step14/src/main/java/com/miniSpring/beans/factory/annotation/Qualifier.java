package com.miniSpring.beans.factory.annotation;

import java.lang.annotation.*;

/**
 * 限定符注解。
 * <p>与 @Autowired 配合使用，用于按名称精确指定注入的 Bean。
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Qualifier {

    /**
     * 指定注入的 Bean 名称。
     */
    String value() default "";
}
