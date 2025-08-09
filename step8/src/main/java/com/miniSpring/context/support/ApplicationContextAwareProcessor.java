package com.miniSpring.context.support;

import com.miniSpring.beans.BeansException;
import com.miniSpring.beans.factory.config.BeanPostProcessor;
import com.miniSpring.context.ApplicationContext;
import com.miniSpring.context.ApplicationContextAware;

/**
 * 负责处理实现了 ApplicationContextAware 接口的 Bean，
 * 在 Bean 初始化前将当前的 ApplicationContext 注入到 Bean 中。
 */
public class ApplicationContextAwareProcessor implements BeanPostProcessor {

    private final ApplicationContext applicationContext;

    public ApplicationContextAwareProcessor(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * 在 Bean 初始化前调用，判断是否实现了 ApplicationContextAware，
     * 若是则注入 ApplicationContext。
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof ApplicationContextAware) {
            ((ApplicationContextAware) bean).setApplicationContext(applicationContext);
        }
        return bean;
    }

    /**
     * 初始化后处理，默认直接返回 Bean 不做任何处理。
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}

