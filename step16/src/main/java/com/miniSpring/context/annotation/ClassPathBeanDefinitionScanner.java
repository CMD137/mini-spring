package com.miniSpring.context.annotation;

import cn.hutool.core.util.StrUtil;
import com.miniSpring.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import com.miniSpring.beans.factory.config.BeanDefinition;
import com.miniSpring.beans.factory.support.BeanDefinitionRegistry;

import java.util.Set;

/**
 * 该类的核心作用是在指定包路径下扫描符合条件的组件（继承父类的扫描能力），
 * 并将这些组件的定义（BeanDefinition）注册到 Bean 定义注册表（BeanDefinitionRegistry）中，
 * 是连接 “组件扫描” 与 “容器注册” 的关键桥梁。
 */
public class ClassPathBeanDefinitionScanner extends ClassPathScanningCandidateComponentProvider {

    // BeanDefinition 注册器，用于将扫描到的 BeanDefinition 注册到容器
    private BeanDefinitionRegistry registry;

    public ClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry) {
        this.registry = registry;
    }

    /**
     * 扫描指定的基础包路径，并注册符合条件的 BeanDefinition
     */
    public void doScan(String... basePackages) {
        for (String basePackage : basePackages) {
            // 扫描该包下所有被 @Component 标注的类，转换成 BeanDefinition 集合
            Set<BeanDefinition> candidates = findCandidateComponents(basePackage);

            for (BeanDefinition beanDefinition : candidates) {
                // 解析 Bean 的作用域（singleton、prototype 等）
                String beanScope = resolveBeanScope(beanDefinition);
                if (StrUtil.isNotEmpty(beanScope)) {
                    beanDefinition.setScope(beanScope);
                }

                // 注册 BeanDefinition 到容器
                // determineBeanName() 用于解析 Bean 名称（@Component value 或类名首字母小写）
                registry.registerBeanDefinition(determineBeanName(beanDefinition), beanDefinition);
            }
        }
        // 注册处理注解的 BeanPostProcessor（@Autowired、@Value）
        registry.registerBeanDefinition("internalAutowiredAnnotationProcessor", new BeanDefinition(AutowiredAnnotationBeanPostProcessor.class));
    }

    /**
     * 根据 @Scope 注解解析 Bean 的作用域
     */
    private String resolveBeanScope(BeanDefinition beanDefinition) {
        Class<?> beanClass = beanDefinition.getBeanClass();
        Scope scope = beanClass.getAnnotation(Scope.class);
        if (null != scope) return scope.value(); // 如果有 @Scope 注解，返回其值
        return StrUtil.EMPTY; // 默认返回空字符串，表示使用默认作用域（singleton）
    }

    /**
     * 根据 @Component 注解解析 Bean 的名称
     * 如果 @Component value 为空，则使用类名首字母小写
     */
    private String determineBeanName(BeanDefinition beanDefinition) {
        Class<?> beanClass = beanDefinition.getBeanClass();
        Component component = beanClass.getAnnotation(Component.class);
        String value = component.value();
        if (StrUtil.isEmpty(value)) {
            // 例如 UserService -> userService
            value = StrUtil.lowerFirst(beanClass.getSimpleName());
        }
        return value;
    }
}

