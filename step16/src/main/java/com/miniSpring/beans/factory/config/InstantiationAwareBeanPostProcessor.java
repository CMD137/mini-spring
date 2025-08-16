package com.miniSpring.beans.factory.config;

import com.miniSpring.beans.BeansException;
import com.miniSpring.beans.PropertyValues;

/**
 * 这个接口在 Spring AOP 自动代理中非常关键，
 * 用于实现自动创建代理对象，拦截 Bean 的实例化流程，
 * 从而无侵入地为目标对象生成代理。
 */
public interface InstantiationAwareBeanPostProcessor extends BeanPostProcessor {

    /**
     * 在目标 Bean 实例化之前执行此方法。
     *
     * 该方法可以返回一个对象，该对象会替代原本容器默认创建的 Bean 实例。
     * 换句话说，可以通过返回代理对象等，阻止默认的 Bean 实例化过程。
     *
     * 这对于实现 AOP 自动代理非常重要，因为可以在此阶段返回代理对象，
     * 从而避免后续再去创建原始的目标对象。
     */
    Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException;

    /**
     * 在工厂将属性值应用到给定 Bean 之前，对这些属性值进行后置处理。
     * 例如，可以在这里检查所有依赖是否已满足，
     * 也可以基于 Bean 属性的 setter 方法上的 “@Required” 注解进行依赖校验。
     *
     * 在 Bean 实例化完成后、执行属性填充操作之前调用此方法。
     *
     */
    PropertyValues postProcessPropertyValues(PropertyValues pvs, Object bean, String beanName) throws BeansException;

}

