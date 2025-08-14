package com.miniSpring.context.support;

import com.miniSpring.beans.BeansException;
import com.miniSpring.beans.factory.ConfigurableListableBeanFactory;
import com.miniSpring.beans.factory.support.DefaultListableBeanFactory;

/**
 * 抽象的可刷新的应用上下文实现类。
 * 获取了 DefaultListableBeanFactory 的实例化
 * 定义了对资源配置的加载操作 loadBeanDefinitions(beanFactory)。
 */
public abstract class AbstractRefreshableApplicationContext extends AbstractApplicationContext {

    // 持有当前上下文使用的 BeanFactory 实例
    private DefaultListableBeanFactory beanFactory;

    /**
     * 刷新 BeanFactory：每次 refresh() 时都会创建新的 BeanFactory 并加载 BeanDefinition。
     */
    @Override
    protected void refreshBeanFactory() throws BeansException {
        // 创建一个新的 BeanFactory 实例
        DefaultListableBeanFactory beanFactory = createBeanFactory();

        // 将配置信息（如 XML 或注解）加载到新创建的 BeanFactory 中
        loadBeanDefinitions(beanFactory);

        // 用新 BeanFactory 替换旧的（相当于完成一次“上下文刷新”）
        this.beanFactory = beanFactory;
    }

    /**
     * 创建一个新的 BeanFactory 实例。
     */
    private DefaultListableBeanFactory createBeanFactory() {
        return new DefaultListableBeanFactory();
    }

    /**
     * 抽象方法，由子类实现，用于加载 BeanDefinition（即 Bean 的配置信息）。
     * 例如：XmlApplicationContext 会从 XML 中解析 BeanDefinition。
     */
    protected abstract void loadBeanDefinitions(DefaultListableBeanFactory beanFactory);

    /**
     * 获取当前应用上下文持有的 BeanFactory。
     */
    @Override
    protected ConfigurableListableBeanFactory getBeanFactory() {
        return beanFactory;
    }
}

