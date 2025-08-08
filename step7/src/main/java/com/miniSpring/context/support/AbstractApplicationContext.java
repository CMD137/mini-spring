package com.miniSpring.context.support;

import com.miniSpring.beans.BeansException;
import com.miniSpring.beans.factory.ConfigurableListableBeanFactory;
import com.miniSpring.beans.factory.config.BeanFactoryPostProcessor;
import com.miniSpring.beans.factory.config.BeanPostProcessor;
import com.miniSpring.context.ConfigurableApplicationContext;
import com.miniSpring.core.io.DefaultResourceLoader;

import java.util.Map;

/**
 * 抽象的应用上下文，实现了容器的刷新机制，是容器启动的核心入口。
 * 继承 DefaultResourceLoader，具备资源加载能力；实现 ConfigurableApplicationContext，可配置上下文。
 */
public abstract class AbstractApplicationContext extends DefaultResourceLoader implements ConfigurableApplicationContext {

    @Override
    public void refresh() throws BeansException {

        // 1. 创建 BeanFactory，并加载 BeanDefinition（从配置文件或注解中解析出 Bean 定义）
        refreshBeanFactory();

        // 2. 获取当前上下文使用的 BeanFactory
        ConfigurableListableBeanFactory beanFactory = getBeanFactory();

        // 3. 执行所有 BeanFactoryPostProcessor，允许修改 BeanDefinition
        // （此时还未实例化任何 Bean）
        invokeBeanFactoryPostProcessors(beanFactory);

        // 4. 注册所有 BeanPostProcessor，为后续 Bean 实例化过程添加扩展逻辑（如 AOP、依赖注入等）
        registerBeanPostProcessors(beanFactory);

        // 5. 提前实例化所有非懒加载的单例 Bean（触发完整的 Bean 创建流程）
        beanFactory.preInstantiateSingletons();
    }

    /**
     * 刷新 BeanFactory 的抽象方法，由子类实现，负责创建 BeanFactory 和加载 BeanDefinition。
     */
    protected abstract void refreshBeanFactory() throws BeansException;

    /**
     * 获取当前上下文持有的 BeanFactory，由子类实现。
     */
    protected abstract ConfigurableListableBeanFactory getBeanFactory();

    /**
     * 执行所有注册的 BeanFactoryPostProcessor，允许用户在 Bean 实例化前修改 BeanDefinition。
     */
    private void invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory beanFactory) {
        // 获取所有实现了 BeanFactoryPostProcessor 的 Bean
        Map<String, BeanFactoryPostProcessor> beanFactoryPostProcessorMap =
                beanFactory.getBeansOfType(BeanFactoryPostProcessor.class);

        // 依次执行每个 BeanFactoryPostProcessor 的后置处理方法
        for (BeanFactoryPostProcessor beanFactoryPostProcessor : beanFactoryPostProcessorMap.values()) {
            beanFactoryPostProcessor.postProcessBeanFactory(beanFactory);
        }
    }

    /**
     * 注册所有 BeanPostProcessor，在 Bean 创建过程中进行扩展处理（如 AOP、属性注入、代理包装等）。
     * BeanPostProcessor 会作用于每一个后续创建的 Bean。
     */
    private void registerBeanPostProcessors(ConfigurableListableBeanFactory beanFactory) {
        // 获取所有 BeanPostProcessor 类型的 Bean
        Map<String, BeanPostProcessor> beanPostProcessorMap =
                beanFactory.getBeansOfType(BeanPostProcessor.class);

        // 将它们注册到 BeanFactory 中，便于后续在 Bean 创建过程中回调
        for (BeanPostProcessor beanPostProcessor : beanPostProcessorMap.values()) {
            beanFactory.addBeanPostProcessor(beanPostProcessor);
        }
    }

    // 以下是对 ApplicationContext 中部分 getBean 方法的默认实现，均委托给 BeanFactory

    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException {
        return getBeanFactory().getBeansOfType(type);
    }

    @Override
    public String[] getBeanDefinitionNames() {
        return getBeanFactory().getBeanDefinitionNames();
    }

    @Override
    public Object getBean(String name) throws BeansException {
        return getBeanFactory().getBean(name);
    }

    @Override
    public Object getBean(String name, Object... args) throws BeansException {
        return getBeanFactory().getBean(name, args);
    }

    @Override
    public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
        return getBeanFactory().getBean(name, requiredType);
    }

    @Override
    public void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::close));
    }

    @Override
    public void close() {
        getBeanFactory().destroySingletons();
    }
}

