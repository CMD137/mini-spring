package com.miniSpring.beans.factory.support;

import com.miniSpring.beans.BeansException;
import com.miniSpring.beans.factory.FactoryBean;
import com.miniSpring.beans.factory.config.BeanDefinition;
import com.miniSpring.beans.factory.config.BeanPostProcessor;
import com.miniSpring.beans.factory.config.ConfigurableBeanFactory;
import com.miniSpring.util.ClassUtils;

import java.util.ArrayList;
import java.util.List;


public abstract class AbstractBeanFactory extends FactoryBeanRegistrySupport implements ConfigurableBeanFactory {

    // 存储所有注册的 BeanPostProcessor，用于在 Bean 初始化前后进行扩展处理
    private final List<BeanPostProcessor> beanPostProcessors = new ArrayList<>();

    /**
     * 用于加载 Bean 类名时所使用的 ClassLoader（如果需要的话）
     */
    private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();


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
        Object sharedInstance = getSingleton(name);
        if (sharedInstance != null) {
            // 如果是 FactoryBean，则需要调用 FactoryBean#getObject
            return (T) getObjectForBeanInstance(sharedInstance, name);
        }

        BeanDefinition beanDefinition = getBeanDefinition(name);
        Object bean = createBean(name, beanDefinition, args);
        return (T) getObjectForBeanInstance(bean, name);
    }

    private Object getObjectForBeanInstance(Object beanInstance, String beanName) {

        // 普通 Bean，直接返回
        if (!(beanInstance instanceof FactoryBean)) {
            return beanInstance;
        }

        // FactoryBean 产物缓存中获取已有对象
        Object object = getCachedObjectForFactoryBean(beanName);

        // 缓存中没有，则调用 FactoryBean 创建产物
        if (object == null) {
            FactoryBean<?> factoryBean = (FactoryBean<?>) beanInstance;
            object = getObjectFromFactoryBean(factoryBean, beanName);
        }

        return object;
    }

    protected abstract BeanDefinition getBeanDefinition(String beanName) throws BeansException;

    protected abstract Object createBean(String beanName, BeanDefinition beanDefinition, Object[] args) throws BeansException;

    public ClassLoader getBeanClassLoader() {
        return this.beanClassLoader;
    }
}

