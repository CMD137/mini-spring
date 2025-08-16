package com.miniSpring.context.annotation;

import java.lang.annotation.*;

/**
 * 组件注解，用于标识一个类为Spring容器管理的Bean
 * 被此注解标记的类会被自动扫描并注册到Spring容器中
 */
@Target(ElementType.TYPE)          // 仅可作用于类、接口（包括注解类型）、枚举
@Retention(RetentionPolicy.RUNTIME) // 运行时可通过反射读取
@Documented                         // 生成 Javadoc 时包含该注解信息
public @interface Component {
    /**
     * Bean 名称，可选。
     * @return Bean 名称
     */
    String value() default "";
}
