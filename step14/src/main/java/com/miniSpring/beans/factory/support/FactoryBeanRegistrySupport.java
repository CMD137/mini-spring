package com.miniSpring.beans.factory.support;

import com.miniSpring.beans.BeansException;
import com.miniSpring.beans.factory.FactoryBean;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * FactoryBeanRegistrySupport
 *
 * 该类是 Spring 容器中用于支持 FactoryBean 产物对象（getObject() 返回值）缓存和获取的抽象基类，
 * 继承自 DefaultSingletonBeanRegistry，因此具备单例对象注册与缓存的能力。
 *
 * 核心职责：
 * 1. 缓存由 FactoryBean 创建的单例产物对象（与工厂对象本身分开管理）
 * 2. 根据 FactoryBean 的 singleton/prototype 特性，决定是否使用缓存
 * 3. 统一处理 FactoryBean 产物对象的创建异常
 *
 * 设计背景：
 * - 在 Spring 中，FactoryBean 本身也是一个 Bean，但我们更多关心它产出的对象（getObject()）。
 * - 如果 FactoryBean 的产物是单例，需要在容器中缓存；如果是原型，每次都重新调用 getObject()。
 * - 此类提供了一个专门的缓存 Map（factoryBeanObjectCache）来管理这些产物对象，避免与普通 Bean 缓存混淆。
 */
public abstract class FactoryBeanRegistrySupport extends DefaultSingletonBeanRegistry {

    /**
     * FactoryBean 产物对象的单例缓存：
     * key   = FactoryBean 的 beanName
     * value = FactoryBean 创建的对象实例（getObject() 的返回值）
     *
     * 注意：
     * - 这里缓存的是产物对象，而不是 FactoryBean 本身
     * - 如果 getObject() 返回 null，会使用 NULL_OBJECT 占位符避免重复调用
     */
    private final Map<String, Object> factoryBeanObjectCache = new ConcurrentHashMap<>();

    /**
     * 从 FactoryBean 产物缓存中获取对象（不触发创建）。
     */
    protected Object getCachedObjectForFactoryBean(String beanName) {
        Object object = this.factoryBeanObjectCache.get(beanName);
        return (object != NULL_OBJECT ? object : null);
    }

    /**
     * 根据 FactoryBean 获取对象，带有缓存逻辑。
     * - 如果 FactoryBean 是单例：
     *   1. 先从缓存获取
     *   2. 缓存没有则调用 doGetObjectFromFactoryBean 创建，并放入缓存
     * - 如果是原型：
     *   每次都调用 doGetObjectFromFactoryBean 创建新对象，不缓存
     */
    protected Object getObjectFromFactoryBean(FactoryBean factory, String beanName) {
        if (factory.isSingleton()) {
            Object object = this.factoryBeanObjectCache.get(beanName);
            if (object == null) {
                object = doGetObjectFromFactoryBean(factory, beanName);
                this.factoryBeanObjectCache.put(beanName, (object != null ? object : NULL_OBJECT));
            }
            return (object != NULL_OBJECT ? object : null);
        } else {
            return doGetObjectFromFactoryBean(factory, beanName);
        }
    }

    /**
     * 真正调用 FactoryBean.getObject() 创建产物对象的方法。
     * 对异常进行统一封装为 BeansException。
     */
    private Object doGetObjectFromFactoryBean(final FactoryBean factory, final String beanName) {
        try {
            return factory.getObject();
        } catch (Exception e) {
            throw new BeansException("FactoryBean threw exception on object[" + beanName + "] creation", e);
        }
    }
}
