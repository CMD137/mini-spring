package com.miniSpring.context.support;

import com.miniSpring.beans.BeansException;
import com.miniSpring.beans.factory.ConfigurableListableBeanFactory;
import com.miniSpring.beans.factory.config.BeanFactoryPostProcessor;
import com.miniSpring.beans.factory.config.BeanPostProcessor;
import com.miniSpring.context.ApplicationEvent;
import com.miniSpring.context.ApplicationListener;
import com.miniSpring.context.ConfigurableApplicationContext;
import com.miniSpring.context.event.ApplicationEventMulticaster;
import com.miniSpring.context.event.ContextClosedEvent;
import com.miniSpring.context.event.ContextRefreshedEvent;
import com.miniSpring.context.event.SimpleApplicationEventMulticaster;
import com.miniSpring.core.io.DefaultResourceLoader;

import java.util.Collection;
import java.util.Map;

/**
 * 抽象的应用上下文，实现了容器的刷新机制，是容器启动的核心入口。
 * 继承 DefaultResourceLoader，具备资源加载能力；实现 ConfigurableApplicationContext，可配置上下文。
 */
public abstract class AbstractApplicationContext extends DefaultResourceLoader implements ConfigurableApplicationContext {

    /**
     * 事件广播器在容器中的默认 Bean 名称。
     * 通过该名称可以在容器中查找或注册事件广播器实例。
     */
    public static final String APPLICATION_EVENT_MULTICASTER_BEAN_NAME = "applicationEventMulticaster";

    /**
     * 事件广播器实例，负责将事件分发给所有注册的监听器。
     */
    private ApplicationEventMulticaster applicationEventMulticaster;


    @Override
    public void refresh() throws BeansException {

        // 1. 创建 BeanFactory，并加载 BeanDefinition（从配置文件或注解中解析出 Bean 定义）
        refreshBeanFactory();

        // 2. 获取当前上下文使用的 BeanFactory
        ConfigurableListableBeanFactory beanFactory = getBeanFactory();

        // 3. 添加 ApplicationContextAwareProcessor，这是一个特殊的 BeanPostProcessor，
        // 用于在 Bean 初始化前自动回调实现了 ApplicationContextAware 接口的 Bean 的 setApplicationContext 方法，
        // 使这些 Bean 能够感知并持有当前 ApplicationContext 实例。
        // 该处理器必须提前注册，确保后续创建的所有相关 Bean 都能正确获得 ApplicationContext。
        beanFactory.addBeanPostProcessor(new ApplicationContextAwareProcessor(this));


        // 4. 执行所有 BeanFactoryPostProcessor，允许修改 BeanDefinition
        // （此时还未实例化任何 Bean）
        invokeBeanFactoryPostProcessors(beanFactory);

        // 5. 注册所有 BeanPostProcessor，为后续 Bean 实例化过程添加扩展逻辑（如 AOP、依赖注入等）
        registerBeanPostProcessors(beanFactory);

        // 6. 初始化事件发布者
        initApplicationEventMulticaster();

        // 7. 注册事件监听器
        registerListeners();

        // 8. 提前实例化所有非懒加载的单例 Bean（触发完整的 Bean 创建流程）
        beanFactory.preInstantiateSingletons();

        // 9. 发布容器刷新完成事件
        finishRefresh();
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

    /**
     * 初始化事件广播器。
     * 从 BeanFactory 获取或创建一个 SimpleApplicationEventMulticaster 实例，
     * 并将其注册为单例 Bean，供容器管理和事件发布使用。
     */
    private void initApplicationEventMulticaster() {
        ConfigurableListableBeanFactory beanFactory = getBeanFactory();
        applicationEventMulticaster = new SimpleApplicationEventMulticaster(beanFactory);
        beanFactory.registerSingleton(APPLICATION_EVENT_MULTICASTER_BEAN_NAME, applicationEventMulticaster);
    }

    /**
     * 注册所有已定义的事件监听器。
     * 从容器中获取所有实现了 ApplicationListener 接口的 Bean，
     * 并将它们添加到事件广播器的监听器集合中。
     */
    private void registerListeners() {
        Collection<ApplicationListener> applicationListeners = getBeansOfType(ApplicationListener.class).values();
        for (ApplicationListener listener : applicationListeners) {
            applicationEventMulticaster.addApplicationListener(listener);
        }
    }

    /**
     * 完成容器刷新操作后的收尾工作。
     * 发布一个 ContextRefreshedEvent 事件，通知所有监听器容器已刷新完成。
     */
    private void finishRefresh() {
        publishEvent(new ContextRefreshedEvent(this));
    }

    /**
     * 发布事件接口实现。
     * 将事件交由事件广播器进行多播，通知所有匹配的监听器。
     *
     * @param event 要发布的应用事件
     */
    @Override
    public void publishEvent(ApplicationEvent event) {
        applicationEventMulticaster.multicastEvent(event);
    }


    @Override
    public void close() {

        // 发布容器关闭事件
        publishEvent(new ContextClosedEvent(this));

        // 执行销毁单例bean的销毁方法
        getBeanFactory().destroySingletons();
    }
}

