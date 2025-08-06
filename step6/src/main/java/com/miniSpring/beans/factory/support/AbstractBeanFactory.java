package com.miniSpring.beans.factory.support;

import com.miniSpring.beans.BeansException;
import com.miniSpring.beans.factory.BeanFactory;
import com.miniSpring.beans.factory.config.BeanDefinition;
import com.miniSpring.beans.factory.config.BeanPostProcessor;
import com.miniSpring.beans.factory.config.ConfigurableBeanFactory;

import java.util.ArrayList;
import java.util.List;


public abstract class AbstractBeanFactory extends DefaultSingletonBeanRegistry implements ConfigurableBeanFactory {

    // 存储所有注册的 BeanPostProcessor，用于在 Bean 初始化前后进行扩展处理
    private final List<BeanPostProcessor> beanPostProcessors = new ArrayList<>();

    /**
     * 注册一个 BeanPostProcessor。
     * 若已存在相同实例，则先移除后再添加，确保顺序性（后添加的在最后执行）。
     */
    @Override
    public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
        // 避免重复注册同一个处理器
        this.beanPostProcessors.remove(beanPostProcessor);
        // 添加到列表末尾，确保调用顺序
        this.beanPostProcessors.add(beanPostProcessor);
    }


    /**
     *返回将在该工厂创建的 Bean 上应用的 BeanPostProcessor 列表。
     */
    public List<BeanPostProcessor> getBeanPostProcessors() {
        return this.beanPostProcessors;
    }

    @Override
    public Object getBean(String name) throws BeansException {
        return doGetBean(name, null);
    }

    @Override
    public Object getBean(String name, Object... args) throws BeansException {
        return doGetBean(name, args);
    }

    @Override
    public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
        return (T) getBean(name);
    }

    protected <T> T doGetBean(final String name, final Object[] args) {
        Object bean = getSingleton(name);
        if (bean != null) {
            return (T) bean;
        }
        BeanDefinition beanDefinition = getBeanDefinition(name);
        return (T) createBean(name, beanDefinition, args);
    }

    protected abstract BeanDefinition getBeanDefinition(String beanName) throws BeansException;

    protected abstract Object createBean(String beanName, BeanDefinition beanDefinition, Object[] args) throws BeansException;
}

