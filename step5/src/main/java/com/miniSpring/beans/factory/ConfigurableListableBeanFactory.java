package com.miniSpring.beans.factory;

import com.miniSpring.beans.BeansException;
import com.miniSpring.beans.factory.config.AutowireCapableBeanFactory;
import com.miniSpring.beans.factory.config.BeanDefinition;
import com.miniSpring.beans.factory.config.ConfigurableBeanFactory;

/**
 * 可配置且可列举的 Bean 工厂接口。
 *
 * 此接口通常由支持列出所有 Bean 定义的 Bean 工厂实现。
 * 相较于 {@link ConfigurableBeanFactory}，它还提供了分析和修改 BeanDefinition、
 * 以及预实例化单例 Bean 的能力。
 *
 * Spring 容器刷新（refresh）过程中，通常会用这个接口对 BeanDefinition 做统一处理。
 */
public interface ConfigurableListableBeanFactory
        extends ListableBeanFactory,                   // 可以按类型、名称列出 Bean
        AutowireCapableBeanFactory,                     // 提供自动装配已存在对象的能力
        ConfigurableBeanFactory {                       // 提供作用域、自定义类型转换器、注册钩子等能力

    /**
     * 获取指定名称的 BeanDefinition 对象。
     *
     * @param beanName Bean 的名称
     * @return BeanDefinition 定义信息（如：class、作用域、依赖属性等）
     * @throws BeansException 如果未找到对应的定义
     */
    BeanDefinition getBeanDefinition(String beanName) throws BeansException;

}
