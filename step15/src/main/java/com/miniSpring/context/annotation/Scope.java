package com.miniSpring.context.annotation;

import java.lang.annotation.*;

/**
 * 用于配置作用域的自定义注解，方便通过配置Bean对象注解的时候，拿到Bean对象的作用域。不过一般都使用默认的 singleton
 */
@Target({ElementType.TYPE, ElementType.METHOD}) // 可作用于类和方法
@Retention(RetentionPolicy.RUNTIME)             // 运行时可通过反射读取
@Documented                                     // 生成 Javadoc 时包含该注解信息
public @interface Scope {
    /**
     * 作用域值，默认为 singleton。
     * @return 作用域名称
     */
    String value() default "singleton";
}
