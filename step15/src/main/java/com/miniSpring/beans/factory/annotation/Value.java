package com.miniSpring.beans.factory.annotation;

import java.lang.annotation.*;

/**
 * 值注入注解。
 * 用于将外部配置或表达式的值注入到字段、方法参数等位置。
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Value {

    /**
     * 要注入的实际值或表达式，例如 "#{systemProperties.myProp}"。
     */
    String value();
}
