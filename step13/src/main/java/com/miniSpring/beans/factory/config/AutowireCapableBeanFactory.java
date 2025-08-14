package com.miniSpring.beans.factory.config;

import com.miniSpring.beans.BeansException;
import com.miniSpring.beans.factory.BeanFactory;

/**
 * AutowireCapableBeanFactory（自动装配能力的 Bean 工厂）
 *
 * 这是 beans.factory.BeanFactory 的扩展接口，
 * 用于具备“自动装配现有 Bean 实例”能力的工厂。
 *
 * 通常用于：
 * - 将一个**已经存在的对象**（不是容器创建的）注入其依赖项（属性、注解等）。
 * - 在运行时对对象进行依赖注入、生命周期管理（如调用初始化方法）。
 *
 * 举例场景：
 * - 手动 new 出一个对象，然后交给 Spring 去完成依赖注入（而不是由容器创建）
 *
 * 在 Spring 中的实现类：
 * - DefaultListableBeanFactory 就实现了这个接口。
 */
public interface AutowireCapableBeanFactory extends BeanFactory {
    /**
     * 执行 BeanPostProcessors 接口实现类的 postProcessBeforeInitialization 方法
     *
     * @param existingBean
     * @param beanName
     * @return
     * @throws BeansException
     */
    Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName) throws BeansException;

    /**
     * 执行 BeanPostProcessors 接口实现类的 postProcessorsAfterInitialization 方法
     *
     * @param existingBean
     * @param beanName
     * @return
     * @throws BeansException
     */
    Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName) throws BeansException;
}
