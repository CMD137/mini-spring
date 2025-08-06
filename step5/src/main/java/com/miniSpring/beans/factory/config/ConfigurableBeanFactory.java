package com.miniSpring.beans.factory.config;

import com.miniSpring.beans.factory.HierarchicalBeanFactory;

/**
 * 配置型 Bean 工厂接口，通常由大多数 BeanFactory 实现。
 *
 * 它在 BeanFactory 的基础上，扩展了对 Bean 工厂的配置能力，例如：
 * - 设置作用域（singleton / prototype）
 * - 注册单例 Bean
 * - 支持层级结构（父子容器）
 *
 * 继承了：
 * - HierarchicalBeanFactory：提供父容器支持，可通过 getParentBeanFactory() 获取父容器。
 * - SingletonBeanRegistry：允许手动注册/获取单例对象。
 */
public interface ConfigurableBeanFactory extends HierarchicalBeanFactory, SingletonBeanRegistry {

    /** 单例作用域：容器中只创建一个共享的 Bean 实例 */
    String SCOPE_SINGLETON = "singleton";

    /** 原型作用域：每次获取 Bean 都会创建一个新的实例 */
    String SCOPE_PROTOTYPE = "prototype";

}

